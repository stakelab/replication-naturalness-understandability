@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProjectManager.singleton.setBusy(true);
        try {
            Properties options = ParsingUtilities.parseUrlParameters(request);

            long projectID = Project.generateID();
            logger.info("Importing existing project using new ID {}", projectID);

            internalImport(request, options, projectID);

            ProjectManager.singleton.loadProjectMetadata(projectID);

            ProjectMetadata pm = ProjectManager.singleton.getProjectMetadata(projectID);
            if (pm != null) {
                if (options.containsKey("project-name")) {
                    String projectName = options.getProperty("project-name");
                    if (projectName != null && projectName.length() > 0) {
                        pm.setName(projectName);
                    }
                }

                redirect(response, "/project?project=" + projectID);
            } else {
                respondWithErrorPage(request, response, "Failed to import project. Reason unknown.", null);
            }
        } catch (Exception e) {
            respondWithErrorPage(request, response, "Failed to import project", e);
        } finally {
            ProjectManager.singleton.setBusy(false);
        }
    }