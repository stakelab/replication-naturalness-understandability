public void testMultipleCreateAndDeletes() throws InterruptedException {
        String datafeedId = "df2";

        AtomicReference<IndexResponse> indexResponseHolder = new AtomicReference<>();
        AtomicReference<Exception> exceptionHolder = new AtomicReference<>();

        // Create datafeed config
        DatafeedConfig.Builder config = createDatafeedConfig(datafeedId, "j1");
        blockingCall(actionListener -> datafeedConfigProvider.putDatafeedConfig(config.build(), Collections.emptyMap(), actionListener),
                indexResponseHolder, exceptionHolder);
        assertNull(exceptionHolder.get());
        assertEquals(RestStatus.CREATED, indexResponseHolder.get().status());

        // cannot create another with the same id
        indexResponseHolder.set(null);
        blockingCall(actionListener -> datafeedConfigProvider.putDatafeedConfig(config.build(), Collections.emptyMap(), actionListener),
                indexResponseHolder, exceptionHolder);
        assertNull(indexResponseHolder.get());
        assertThat(exceptionHolder.get(), instanceOf(ResourceAlreadyExistsException.class));
        assertEquals("A datafeed with id [df2] already exists", exceptionHolder.get().getMessage());

        // delete
        exceptionHolder.set(null);
        AtomicReference<DeleteResponse> deleteResponseHolder = new AtomicReference<>();
        blockingCall(actionListener -> datafeedConfigProvider.deleteDatafeedConfig(datafeedId, actionListener),
                deleteResponseHolder, exceptionHolder);
        assertNull(exceptionHolder.get());
        assertEquals(DocWriteResponse.Result.DELETED, deleteResponseHolder.get().getResult());

        // error deleting twice
        deleteResponseHolder.set(null);
        blockingCall(actionListener -> datafeedConfigProvider.deleteDatafeedConfig(datafeedId, actionListener),
                deleteResponseHolder, exceptionHolder);
        assertNull(deleteResponseHolder.get());
        assertEquals(ResourceNotFoundException.class, exceptionHolder.get().getClass());
    }