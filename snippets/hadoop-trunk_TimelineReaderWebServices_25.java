@GET
  @Path("/users/{userid}/flows/{flowname}/runs/{flowrunid}/apps/")
  @Produces(MediaType.APPLICATION_JSON + "; " + JettyUtils.UTF_8)
  public Set<TimelineEntity> getFlowRunApps(
      @Context HttpServletRequest req,
      @Context HttpServletResponse res,
      @PathParam("userid") String userId,
      @PathParam("flowname") String flowName,
      @PathParam("flowrunid") String flowRunId,
      @QueryParam("limit") String limit,
      @QueryParam("createdtimestart") String createdTimeStart,
      @QueryParam("createdtimeend") String createdTimeEnd,
      @QueryParam("relatesto") String relatesTo,
      @QueryParam("isrelatedto") String isRelatedTo,
      @QueryParam("infofilters") String infofilters,
      @QueryParam("conffilters") String conffilters,
      @QueryParam("metricfilters") String metricfilters,
      @QueryParam("eventfilters") String eventfilters,
      @QueryParam("confstoretrieve") String confsToRetrieve,
      @QueryParam("metricstoretrieve") String metricsToRetrieve,
      @QueryParam("fields") String fields,
      @QueryParam("metricslimit") String metricsLimit,
      @QueryParam("metricstimestart") String metricsTimeStart,
      @QueryParam("metricstimeend") String metricsTimeEnd,
      @QueryParam("fromid") String fromId) {
    return getEntities(req, res, null, null,
        TimelineEntityType.YARN_APPLICATION.toString(), userId, flowName,
        flowRunId, limit, createdTimeStart, createdTimeEnd, relatesTo,
        isRelatedTo, infofilters, conffilters, metricfilters, eventfilters,
        confsToRetrieve, metricsToRetrieve, fields, metricsLimit,
        metricsTimeStart, metricsTimeEnd, fromId);
  }