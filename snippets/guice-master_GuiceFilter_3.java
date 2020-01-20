@Override
  public void doFilter(
      final ServletRequest servletRequest,
      final ServletResponse servletResponse,
      final FilterChain filterChain)
      throws IOException, ServletException {

    final FilterPipeline filterPipeline = getFilterPipeline();

    Context previous = GuiceFilter.localContext.get();
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    HttpServletRequest originalRequest =
        (previous != null) ? previous.getOriginalRequest() : request;
    try {
      RequestScoper.CloseableScope scope = new Context(originalRequest, request, response).open();
      try {
        //dispatch across the servlet pipeline, ensuring web.xml's filterchain is honored
        filterPipeline.dispatch(servletRequest, servletResponse, filterChain);
      } finally {
        scope.close();
      }
    } catch (IOException e) {
      throw e;
    } catch (ServletException e) {
      throw e;
    } catch (Exception e) {
      Throwables.propagate(e);
    }
  }