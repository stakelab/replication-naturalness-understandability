private ReentrantLock getOrCreateMonitor(Object obj, boolean createIfNotExisting) {
        final DynamicHub hub = ObjectHeader.readDynamicHubFromObject(obj);
        final int monitorOffset = hub.getMonitorOffset();
        if (monitorOffset != 0) {
            /* The common case: memory for the monitor reserved in the object. */
            final ReentrantLock existingMonitor = KnownIntrinsics.convertUnknownValue(BarrieredAccess.readObject(obj, monitorOffset), ReentrantLock.class);
            if (existingMonitor != null || !createIfNotExisting) {
                return existingMonitor;
            }
            /* Atomically put a new lock in place of the null at the monitorOffset. */
            final ReentrantLock newMonitor = new ReentrantLock();
            if (UnsafeAccess.UNSAFE.compareAndSwapObject(obj, monitorOffset, null, newMonitor)) {
                return newMonitor;
            }
            /* We lost the race, use the lock some other thread installed. */
            return KnownIntrinsics.convertUnknownValue(BarrieredAccess.readObject(obj, monitorOffset), ReentrantLock.class);
        } else {
            /* No memory reserved for a lock in the object, fall back to our secondary storage. */
            /*
             * Lock the monitor map and maybe add a monitor for this object. This serialization
             * might be a scalability problem.
             */
            additionalMonitorsLock.lock();
            try {
                final ReentrantLock existingEntry = additionalMonitors.get(obj);
                if (existingEntry != null || !createIfNotExisting) {
                    return existingEntry;
                }
                final ReentrantLock newEntry = new ReentrantLock();
                final ReentrantLock previousEntry = additionalMonitors.put(obj, newEntry);
                VMError.guarantee(previousEntry == null, "MonitorSupport.getOrCreateMonitor: Replaced monitor");
                return newEntry;
            } finally {
                additionalMonitorsLock.unlock();
            }
        }
    }