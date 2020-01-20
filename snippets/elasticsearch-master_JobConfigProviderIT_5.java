public void testCrud() throws InterruptedException {
        final String jobId = "crud-job";

        AtomicReference<IndexResponse> indexResponseHolder = new AtomicReference<>();
        AtomicReference<Exception> exceptionHolder = new AtomicReference<>();

        // Create job
        Job newJob = createJob(jobId, null).build(new Date());
        blockingCall(actionListener -> jobConfigProvider.putJob(newJob, actionListener), indexResponseHolder, exceptionHolder);
        assertNull(exceptionHolder.get());
        assertNotNull(indexResponseHolder.get());

        // Read Job
        AtomicReference<Job.Builder> getJobResponseHolder = new AtomicReference<>();
        blockingCall(actionListener -> jobConfigProvider.getJob(jobId, actionListener), getJobResponseHolder, exceptionHolder);
        assertNull(exceptionHolder.get());
        assertEquals(newJob, getJobResponseHolder.get().build());

        // Update Job
        indexResponseHolder.set(null);
        JobUpdate jobUpdate = new JobUpdate.Builder(jobId).setDescription("This job has been updated").build();

        AtomicReference<Job> updateJobResponseHolder = new AtomicReference<>();
        blockingCall(actionListener -> jobConfigProvider.updateJob(jobId, jobUpdate, new ByteSizeValue(32), actionListener),
                updateJobResponseHolder, exceptionHolder);
        assertNull(exceptionHolder.get());
        assertEquals("This job has been updated", updateJobResponseHolder.get().getDescription());

        getJobResponseHolder.set(null);
        blockingCall(actionListener -> jobConfigProvider.getJob(jobId, actionListener), getJobResponseHolder, exceptionHolder);
        assertNull(exceptionHolder.get());
        assertEquals("This job has been updated", getJobResponseHolder.get().build().getDescription());

        // Delete Job
        AtomicReference<DeleteResponse> deleteJobResponseHolder = new AtomicReference<>();
        blockingCall(actionListener -> jobConfigProvider.deleteJob(jobId, true, actionListener),
                deleteJobResponseHolder, exceptionHolder);
        assertNull(exceptionHolder.get());
        assertThat(deleteJobResponseHolder.get().getResult(), equalTo(DocWriteResponse.Result.DELETED));

        // Read deleted job
        getJobResponseHolder.set(null);
        blockingCall(actionListener -> jobConfigProvider.getJob(jobId, actionListener), getJobResponseHolder, exceptionHolder);
        assertNull(getJobResponseHolder.get());
        assertEquals(ResourceNotFoundException.class, exceptionHolder.get().getClass());

        // Delete deleted job
        deleteJobResponseHolder.set(null);
        exceptionHolder.set(null);
        blockingCall(actionListener -> jobConfigProvider.deleteJob(jobId, true, actionListener),
                deleteJobResponseHolder, exceptionHolder);
        assertNull(deleteJobResponseHolder.get());
        assertEquals(ResourceNotFoundException.class, exceptionHolder.get().getClass());

        // and again with errorIfMissing set false
        deleteJobResponseHolder.set(null);
        exceptionHolder.set(null);
        blockingCall(actionListener -> jobConfigProvider.deleteJob(jobId, false, actionListener),
                deleteJobResponseHolder, exceptionHolder);
        assertEquals(DocWriteResponse.Result.NOT_FOUND, deleteJobResponseHolder.get().getResult());
    }