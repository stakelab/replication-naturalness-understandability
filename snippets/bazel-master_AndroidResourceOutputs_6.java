public static Path archiveCompiledResources(
      final Path archiveOut,
      final Path databindingResourcesRoot,
      final Path compiledRoot,
      final List<Path> compiledArtifacts)
      throws IOException {
    final Path relativeDatabindingProcessedResources =
        databindingResourcesRoot.getRoot().relativize(databindingResourcesRoot);

    try (ZipBuilder builder = ZipBuilder.createFor(archiveOut)) {
      for (Path artifact : compiledArtifacts) {
        Path relativeName = artifact;

        // remove compiled resources prefix
        if (artifact.startsWith(compiledRoot)) {
          relativeName = compiledRoot.relativize(relativeName);
        }
        // remove databinding prefix
        if (relativeName.startsWith(relativeDatabindingProcessedResources)) {
          relativeName =
              relativeName.subpath(
                  relativeDatabindingProcessedResources.getNameCount(),
                  relativeName.getNameCount());
        }

        builder.addEntry(
            relativeName.toString(),
            Files.readAllBytes(artifact),
            ZipEntry.STORED,
            ResourceCompiler.getCompiledType(relativeName.toString()).asComment());
      }
    }
    return archiveOut;
  }