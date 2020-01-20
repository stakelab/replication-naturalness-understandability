@Override
    protected AbstractOperation createOperation(
            Project project, HttpServletRequest request, EngineConfig engineConfig) throws Exception {
        
        String columnName = request.getParameter("columnName");
        String similarValue = request.getParameter("similarValue");
        Judgment judgment = Recon.stringToJudgment(request.getParameter("judgment"));
        
        ReconCandidate match = null;
        String id = request.getParameter("id");
        if (id != null) {
            String scoreString = request.getParameter("score");
            
            match = new ReconCandidate(
                id,
                request.getParameter("name"),
                request.getParameter("types").split(","),
                scoreString != null ? Double.parseDouble(scoreString) : 100
            );
        }
        
        String shareNewTopics = request.getParameter("shareNewTopics");
        
        return new ReconJudgeSimilarCellsOperation(
            engineConfig, 
            columnName,
            similarValue,
            judgment,
            match,
            "true".equals(shareNewTopics)
        );
    }