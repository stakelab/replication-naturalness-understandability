RequestDispatcher getRequestDispatcher(String path) {
    final String newRequestUri = path;

    // TODO(dhanji): check servlet spec to see if the following is legal or not.
    // Need to strip query string if requested...

    for (final ServletDefinition servletDefinition : servletDefinitions) {
      if (servletDefinition.shouldServe(path)) {
        return new RequestDispatcher() {
          @Override
          public void forward(ServletRequest servletRequest, ServletResponse servletResponse)
              throws ServletException, IOException {
            Preconditions.checkState(
                !servletResponse.isCommitted(),
                "Response has been committed--you can only call forward before"
                    + " committing the response (hint: don't flush buffers)");

            // clear buffer before forwarding
            servletResponse.resetBuffer();

            ServletRequest requestToProcess;
            if (servletRequest instanceof HttpServletRequest) {
              requestToProcess = wrapRequest((HttpServletRequest) servletRequest, newRequestUri);
            } else {
              // This should never happen, but instead of throwing an exception
              // we will allow a happy case pass thru for maximum tolerance to
              // legacy (and internal) code.
              requestToProcess = servletRequest;
            }

            // now dispatch to the servlet
            doServiceImpl(servletDefinition, requestToProcess, servletResponse);
          }

          @Override
          public void include(ServletRequest servletRequest, ServletResponse servletResponse)
              throws ServletException, IOException {
            // route to the target servlet
            doServiceImpl(servletDefinition, servletRequest, servletResponse);
          }

          private void doServiceImpl(
              ServletDefinition servletDefinition,
              ServletRequest servletRequest,
              ServletResponse servletResponse)
              throws ServletException, IOException {
            servletRequest.setAttribute(REQUEST_DISPATCHER_REQUEST, Boolean.TRUE);

            try {
              servletDefinition.doService(servletRequest, servletResponse);
            } finally {
              servletRequest.removeAttribute(REQUEST_DISPATCHER_REQUEST);
            }
          }
        };
      }
    }

    //otherwise, can't process
    return null;
  }