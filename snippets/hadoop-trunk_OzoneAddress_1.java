public OzoneClient createClient(OzoneConfiguration conf)
      throws IOException, OzoneClientException {
    OzoneClient client;
    String scheme = ozoneURI.getScheme();
    if (ozoneURI.getScheme() == null || scheme.isEmpty()) {
      scheme = OZONE_RPC_SCHEME;
    }
    if (scheme.equals(OZONE_HTTP_SCHEME)) {
      if (ozoneURI.getHost() != null && !ozoneURI.getAuthority()
          .equals(EMPTY_HOST)) {
        if (ozoneURI.getPort() == -1) {
          client = OzoneClientFactory.getRestClient(ozoneURI.getHost());
        } else {
          client = OzoneClientFactory
              .getRestClient(ozoneURI.getHost(), ozoneURI.getPort(), conf);
        }
      } else {
        client = OzoneClientFactory.getRestClient(conf);
      }
    } else if (scheme.equals(OZONE_RPC_SCHEME)) {
      if (ozoneURI.getHost() != null && !ozoneURI.getAuthority()
          .equals(EMPTY_HOST)) {
        if (ozoneURI.getPort() == -1) {
          client = OzoneClientFactory.getRpcClient(ozoneURI.getHost());
        } else {
          client = OzoneClientFactory
              .getRpcClient(ozoneURI.getHost(), ozoneURI.getPort(), conf);
        }
      } else {
        client = OzoneClientFactory.getRpcClient(conf);
      }
    } else {
      throw new OzoneClientException(
          "Invalid URI, unknown protocol scheme: " + scheme);
    }
    return client;
  }