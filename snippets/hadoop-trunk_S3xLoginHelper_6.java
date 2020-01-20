public static void checkPath(Configuration conf,
      URI fsUri,
      Path path,
      int defaultPort) {
    URI pathUri = path.toUri();
    String thatScheme = pathUri.getScheme();
    if (thatScheme == null) {
      // fs is relative
      return;
    }
    URI thisUri = canonicalizeUri(fsUri, defaultPort);
    String thisScheme = thisUri.getScheme();
    //hostname and scheme are not case sensitive in these checks
    if (equalsIgnoreCase(thisScheme, thatScheme)) {// schemes match
      String thisHost = thisUri.getHost();
      String thatHost = pathUri.getHost();
      if (thatHost == null &&                // path's host is null
          thisHost != null) {                // fs has a host
        URI defaultUri = FileSystem.getDefaultUri(conf);
        if (equalsIgnoreCase(thisScheme, defaultUri.getScheme())) {
          pathUri = defaultUri; // schemes match, so use this uri instead
        } else {
          pathUri = null; // can't determine auth of the path
        }
      }
      if (pathUri != null) {
        // canonicalize uri before comparing with this fs
        pathUri = canonicalizeUri(pathUri, defaultPort);
        thatHost = pathUri.getHost();
        if (thisHost == thatHost ||       // hosts match
            (thisHost != null &&
                 equalsIgnoreCase(thisHost, thatHost))) {
          return;
        }
      }
    }
    // make sure the exception strips out any auth details
    throw new IllegalArgumentException(
        "Wrong FS " + S3xLoginHelper.toString(pathUri)
            + " -expected " + fsUri);
  }