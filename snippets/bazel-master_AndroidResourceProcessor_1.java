public MergedAndroidData processResources(
      Path tempRoot,
      Path aapt,
      Path androidJar,
      @Nullable Revision buildToolsVersion,
      VariantType variantType,
      boolean debug,
      String customPackageForR,
      AaptOptions aaptOptions,
      Collection<String> resourceConfigs,
      Collection<String> splits,
      MergedAndroidData primaryData,
      List<DependencyAndroidData> dependencyData,
      @Nullable Path sourceOut,
      @Nullable Path packageOut,
      @Nullable Path proguardOut,
      @Nullable Path mainDexProguardOut,
      @Nullable Path publicResourcesOut,
      @Nullable Path dataBindingInfoOut)
      throws IOException, InterruptedException, LoggedErrorException, UnrecognizedSplitsException {
    Path androidManifest = primaryData.getManifest();
    final Path resourceDir =
        processDataBindings(
            primaryData.getResourceDir().resolveSibling("res_no_binding"),
            primaryData.getResourceDir(),
            dataBindingInfoOut,
            customPackageForR,
            /* shouldZipDataBindingInfo= */ true);

    final Path assetsDir = primaryData.getAssetDir();
    if (publicResourcesOut != null) {
      prepareOutputPath(publicResourcesOut.getParent());
    }
    runAapt(
        tempRoot,
        aapt,
        androidJar,
        buildToolsVersion,
        variantType,
        debug,
        customPackageForR,
        aaptOptions,
        resourceConfigs,
        splits,
        androidManifest,
        resourceDir,
        assetsDir,
        sourceOut,
        packageOut,
        proguardOut,
        mainDexProguardOut,
        publicResourcesOut);
    // The R needs to be created for each library in the dependencies,
    // but only if the current project is not a library.
    if (sourceOut != null && variantType != VariantType.LIBRARY) {
      writeDependencyPackageRJavaFiles(
          dependencyData, customPackageForR, androidManifest, sourceOut);
    }
    // Reset the output date stamps.
    if (packageOut != null) {
      if (!splits.isEmpty()) {
        renameSplitPackages(packageOut, splits);
      }
    }
    return new MergedAndroidData(resourceDir, assetsDir, androidManifest);
  }