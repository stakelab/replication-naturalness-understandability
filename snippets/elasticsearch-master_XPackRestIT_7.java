private void disableMonitoring() throws Exception {
        if (isMonitoringTest()) {
            final Map<String, Object> settings = new HashMap<>();
            settings.put("xpack.monitoring.collection.enabled", null);
            settings.put("xpack.monitoring.collection.interval", null);
            settings.put("xpack.monitoring.exporters._local.enabled", null);

            awaitCallApi("cluster.put_settings", emptyMap(),
                    singletonList(singletonMap("transient", settings)),
                    response -> {
                        Object acknowledged = response.evaluate("acknowledged");
                        return acknowledged != null && (Boolean) acknowledged;
                    },
                    () -> "Exception when disabling monitoring");

            awaitBusy(() -> {
                try {
                    ClientYamlTestResponse response =
                            callApi("xpack.usage", singletonMap("filter_path", "monitoring.enabled_exporters"), emptyList(),
                                    getApiCallHeaders());

                    @SuppressWarnings("unchecked")
                    final Map<String, ?> exporters = (Map<String, ?>) response.evaluate("monitoring.enabled_exporters");
                    if (exporters.isEmpty() == false) {
                        return false;
                    }

                    final Map<String, String> params = new HashMap<>();
                    params.put("node_id", "_local");
                    params.put("metric", "thread_pool");
                    params.put("filter_path", "nodes.*.thread_pool.write.active");
                    response = callApi("nodes.stats", params, emptyList(), getApiCallHeaders());

                    @SuppressWarnings("unchecked")
                    final Map<String, Object> nodes = (Map<String, Object>) response.evaluate("nodes");
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> node = (Map<String, Object>) nodes.values().iterator().next();

                    final Number activeWrites = (Number) extractValue("thread_pool.write.active", node);
                    return activeWrites != null && activeWrites.longValue() == 0L;
                } catch (Exception e) {
                    throw new ElasticsearchException("Failed to wait for monitoring exporters to stop:", e);
                }
            });
        }
    }