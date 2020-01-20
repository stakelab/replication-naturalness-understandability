@Override
  @LifecycleStart
  public void start()
  {
    writeLock.lock();
    try {
      if (isStarted()) {
        return;
      }

      startCount++;
      currentStartOrder = startCount;
      final long localStartOrder = currentStartOrder;

      exec = Execs.scheduledSingleThreaded("DatabaseSegmentManager-Exec--%d");

      final Duration delay = config.get().getPollDuration().toStandardDuration();
      exec.scheduleWithFixedDelay(
          new Runnable()
          {
            @Override
            public void run()
            {
              // poll() is synchronized together with start(), stop() and isStarted() to ensure that when stop() exists,
              // poll() won't actually run anymore after that (it could only enter the syncrhonized section and exit
              // immediately because the localStartedOrder doesn't match the new currentStartOrder). It's needed
              // to avoid flakiness in SQLMetadataSegmentManagerTest.
              // See https://github.com/apache/incubator-druid/issues/6028
              readLock.lock();
              try {
                if (localStartOrder == currentStartOrder) {
                  poll();
                }
              }
              catch (Exception e) {
                log.makeAlert(e, "uncaught exception in segment manager polling thread").emit();

              }
              finally {
                readLock.unlock();
              }
            }
          },
          0,
          delay.getMillis(),
          TimeUnit.MILLISECONDS
      );
    }
    finally {
      writeLock.unlock();
    }
  }