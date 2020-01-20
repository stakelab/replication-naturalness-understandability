@Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException
  {
    HttpServletRequest request;
    HttpServletResponse response;

    try {
      request = (HttpServletRequest) req;
      response = (HttpServletResponse) res;
    }
    catch (ClassCastException e) {
      throw new ServletException("non-HTTP request or response");
    }

    if (redirectInfo.doLocal(request.getRequestURI())) {
      chain.doFilter(request, response);
    } else {
      URL url = redirectInfo.getRedirectURL(request.getQueryString(), request.getRequestURI());
      log.debug("Forwarding request to [%s]", url);

      if (url == null) {
        // We apparently have nothing to redirect to, so let's do a Service Unavailable
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        return;
      }

      response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
      response.setHeader("Location", url.toString());
    }
  }