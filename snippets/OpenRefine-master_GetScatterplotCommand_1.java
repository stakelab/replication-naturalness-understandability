@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            long start = System.currentTimeMillis();
            
            Project project = getProject(request);
            Engine engine = getEngine(request, project);
            PlotterConfig conf = ParsingUtilities.mapper.readValue(
            		request.getParameter("plotter"),
            		PlotterConfig.class);
            
            response.setHeader("Content-Type", "image/png");
            
            ServletOutputStream sos = null;
            
            try {
                sos = response.getOutputStream();
                draw(sos, project, engine, conf);
            } finally {
                sos.close();
            }
            
            logger.trace("Drawn scatterplot in {} ms", Long.toString(System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
            respondException(response, e);
        }
    }