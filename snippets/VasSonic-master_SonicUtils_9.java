static String buildHtml(final String sessionId, JSONObject dataJson, String sha1, int dataMaxSize) {
        File templateFile = new File(SonicFileUtils.getSonicTemplatePath(sessionId));
        if (templateFile.exists()) {
            String templateString = SonicFileUtils.readFile(templateFile);
            if (!TextUtils.isEmpty(templateString)) {

                final String htmlString = buildHtml(templateString, dataJson, dataMaxSize);

                if (TextUtils.isEmpty(sha1) || sha1.equalsIgnoreCase(SonicUtils.getSHA1(htmlString))) {
                    return htmlString;
                }

                SonicEngine.getInstance().getRuntime().postTaskToThread(new Runnable() {
                    @Override
                    public void run() {
                        String path = SonicFileUtils.getSonicHtmlPath(sessionId) + ".tmp";
                        SonicFileUtils.writeFile(htmlString, path);
                    }
                }, 0);

                log(TAG, Log.ERROR, "buildHtml error: verify sha1 error.");
                return null;
            } else {
                log(TAG, Log.ERROR, "buildHtml error: template string is empty.");
            }
        } else {
            log(TAG, Log.ERROR, "buildHtml error: template file is not exists.");
        }
        return null;
    }