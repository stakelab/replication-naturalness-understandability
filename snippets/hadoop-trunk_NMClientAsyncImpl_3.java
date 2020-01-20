@Override
  protected void serviceStop() throws Exception {
    if (stopped.getAndSet(true)) {
      // return if already stopped
      return;
    }
    if (eventDispatcherThread != null) {
      eventDispatcherThread.interrupt();
      try {
        eventDispatcherThread.join();
      } catch (InterruptedException e) {
        LOG.error("The thread of " + eventDispatcherThread.getName() +
                  " didn't finish normally.", e);
      }
    }
    if (threadPool != null) {
      threadPool.shutdownNow();
    }
    if (client != null) {
      // If NMClientImpl doesn't stop running containers, the states doesn't
      // need to be cleared.
      if (!(client instanceof NMClientImpl) ||
          ((NMClientImpl) client).getCleanupRunningContainers().get()) {
        if (containers != null) {
          containers.clear();
        }
      }
      client.stop();
    }
    super.serviceStop();
  }