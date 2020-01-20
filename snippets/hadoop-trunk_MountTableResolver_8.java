@VisibleForTesting
  public void refreshEntries(final Collection<MountTable> entries) {
    // The tree read/write must be atomic
    writeLock.lock();
    try {
      // New entries
      Map<String, MountTable> newEntries = new ConcurrentHashMap<>();
      for (MountTable entry : entries) {
        String srcPath = entry.getSourcePath();
        newEntries.put(srcPath, entry);
      }

      // Old entries (reversed to sort from the leaves to the root)
      Set<String> oldEntries = new TreeSet<>(Collections.reverseOrder());
      for (MountTable entry : getTreeValues("/")) {
        String srcPath = entry.getSourcePath();
        oldEntries.add(srcPath);
      }

      // Entries that need to be removed
      for (String srcPath : oldEntries) {
        if (!newEntries.containsKey(srcPath)) {
          this.tree.remove(srcPath);
          invalidateLocationCache(srcPath);
          LOG.info("Removed stale mount point {} from resolver", srcPath);
        }
      }

      // Entries that need to be added
      for (MountTable entry : entries) {
        String srcPath = entry.getSourcePath();
        if (!oldEntries.contains(srcPath)) {
          // Add node, it does not exist
          this.tree.put(srcPath, entry);
          invalidateLocationCache(srcPath);
          LOG.info("Added new mount point {} to resolver", srcPath);
        } else {
          // Node exists, check for updates
          MountTable existingEntry = this.tree.get(srcPath);
          if (existingEntry != null && !existingEntry.equals(entry)) {
            LOG.info("Entry has changed from \"{}\" to \"{}\"",
                existingEntry, entry);
            this.tree.put(srcPath, entry);
            invalidateLocationCache(srcPath);
            LOG.info("Updated mount point {} in resolver", srcPath);
          }
        }
      }
    } finally {
      writeLock.unlock();
    }
    this.init = true;
  }