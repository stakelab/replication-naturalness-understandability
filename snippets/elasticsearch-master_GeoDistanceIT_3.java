@Override
    public void setupSuiteScopeCluster() throws Exception {
        Settings settings = Settings.builder().put(IndexMetaData.SETTING_VERSION_CREATED, version).build();
        prepareCreate("idx").setSettings(settings)
                .addMapping("type", "location", "type=geo_point", "city", "type=keyword")
                .get();

        prepareCreate("idx-multi")
                .addMapping("type", "location", "type=geo_point", "city", "type=keyword")
                .get();

        createIndex("idx_unmapped");

        List<IndexRequestBuilder> cities = new ArrayList<>();
        cities.addAll(Arrays.asList(
                // below 500km
                indexCity("idx", "utrecht", "52.0945, 5.116"),
                indexCity("idx", "haarlem", "52.3890, 4.637"),
                // above 500km, below 1000km
                indexCity("idx", "berlin", "52.540, 13.409"),
                indexCity("idx", "prague", "50.097679, 14.441314"),
                // above 1000km
                indexCity("idx", "tel-aviv", "32.0741, 34.777")));

        // random cities with no location
        for (String cityName : Arrays.asList("london", "singapour", "tokyo", "milan")) {
            if (randomBoolean()) {
                cities.add(indexCity("idx", cityName));
            }
        }
        indexRandom(true, cities);

        cities.clear();
        cities.addAll(Arrays.asList(
                // first point is within the ~17.5km, the second is ~710km
                indexCity("idx-multi", "city1", "52.3890, 4.637", "50.097679,14.441314"),
                // first point is ~576km, the second is within the ~35km
                indexCity("idx-multi", "city2", "52.540, 13.409", "52.0945, 5.116"),
                // above 1000km
                indexCity("idx-multi", "city3", "32.0741, 34.777")));

        // random cities with no location
        for (String cityName : Arrays.asList("london", "singapour", "tokyo", "milan")) {
            cities.add(indexCity("idx-multi", cityName));
        }
        indexRandom(true, cities);
        prepareCreate("empty_bucket_idx")
                .addMapping("type", "value", "type=integer", "location", "type=geo_point").get();
        List<IndexRequestBuilder> builders = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            builders.add(client().prepareIndex("empty_bucket_idx", "type", "" + i).setSource(jsonBuilder()
                    .startObject()
                    .field("value", i * 2)
                    .field("location", "52.0945, 5.116")
                    .endObject()));
        }
        indexRandom(true, builders.toArray(new IndexRequestBuilder[builders.size()]));
        ensureSearchable();
    }