public static void main(String[] args) {
    try {
      if (args.length != 1) {
        System.err.println("Usage: <URL>");
        System.exit(-1);
      }
      AuthenticatedURL.Token token = new AuthenticatedURL.Token();
      URL url = new URL(args[0]);
      HttpURLConnection conn = new AuthenticatedURL().openConnection(url, token);
      System.out.println();
      System.out.println("Token value: " + token);
      System.out.println("Status code: " + conn.getResponseCode() + " " + conn.getResponseMessage());
      System.out.println();
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                conn.getInputStream(), Charset.forName("UTF-8")));
        String line = reader.readLine();
        while (line != null) {
          System.out.println(line);
          line = reader.readLine();
        }
        reader.close();
      }
      System.out.println();
    }
    catch (Exception ex) {
      System.err.println("ERROR: " + ex.getMessage());
      System.exit(-1);
    }
  }