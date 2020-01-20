@Override
  public void kill(DataSegment segment) throws SegmentLoadingException
  {
    final File path = getPath(segment);
    log.info("killing segment[%s] mapped to path[%s]", segment.getIdentifier(), path);

    try {
      if (path.getName().endsWith(".zip")) {
        // path format -- > .../dataSource/interval/version/partitionNum/xxx.zip
        // or .../dataSource/interval/version/partitionNum/UUID/xxx.zip

        File parentDir = path.getParentFile();
        FileUtils.deleteDirectory(parentDir);

        // possibly recursively delete empty parent directories up to 'dataSource'
        parentDir = parentDir.getParentFile();
        int maxDepth = 4; // if for some reason there's no datasSource directory, stop recursing somewhere reasonable
        while (parentDir != null && --maxDepth >= 0) {
          if (!parentDir.delete() || segment.getDataSource().equals(parentDir.getName())) {
            break;
          }

          parentDir = parentDir.getParentFile();
        }
      } else {
        throw new SegmentLoadingException("Unknown file type[%s]", path);
      }
    }
    catch (IOException e) {
      throw new SegmentLoadingException(e, "Unable to kill segment");
    }
  }