public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.out.println("Arguments: <WORKDIR> <MINIKDCPROPERTIES> " +
              "<KEYTABFILE> [<PRINCIPALS>]+");
      System.exit(1);
    }
    File workDir = new File(args[0]);
    if (!workDir.exists()) {
      throw new RuntimeException("Specified work directory does not exists: "
              + workDir.getAbsolutePath());
    }
    Properties conf = createConf();
    File file = new File(args[1]);
    if (!file.exists()) {
      throw new RuntimeException("Specified configuration does not exists: "
              + file.getAbsolutePath());
    }
    Properties userConf = new Properties();
    InputStreamReader r = null;
    try {
      r = new InputStreamReader(new FileInputStream(file),
          StandardCharsets.UTF_8);
      userConf.load(r);
    } finally {
      if (r != null) {
        r.close();
      }
    }
    for (Map.Entry<?, ?> entry : userConf.entrySet()) {
      conf.put(entry.getKey(), entry.getValue());
    }
    final MiniKdc miniKdc = new MiniKdc(conf, workDir);
    miniKdc.start();
    File krb5conf = new File(workDir, "krb5.conf");
    if (miniKdc.getKrb5conf().renameTo(krb5conf)) {
      File keytabFile = new File(args[2]).getAbsoluteFile();
      String[] principals = new String[args.length - 3];
      System.arraycopy(args, 3, principals, 0, args.length - 3);
      miniKdc.createPrincipal(keytabFile, principals);
      System.out.println();
      System.out.println("Standalone MiniKdc Running");
      System.out.println("---------------------------------------------------");
      System.out.println("  Realm           : " + miniKdc.getRealm());
      System.out.println("  Running at      : " + miniKdc.getHost() + ":" +
              miniKdc.getHost());
      System.out.println("  krb5conf        : " + krb5conf);
      System.out.println();
      System.out.println("  created keytab  : " + keytabFile);
      System.out.println("  with principals : " + Arrays.asList(principals));
      System.out.println();
      System.out.println(" Do <CTRL-C> or kill <PID> to stop it");
      System.out.println("---------------------------------------------------");
      System.out.println();
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          miniKdc.stop();
        }
      });
    } else {
      throw new RuntimeException("Cannot rename KDC's krb5conf to "
              + krb5conf.getAbsolutePath());
    }
  }