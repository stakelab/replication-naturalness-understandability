public List<InputSplit> getSplits(JobContext job) throws IOException {

    ResultSet results = null;  
    Statement statement = null;
    try {
      statement = connection.createStatement();

      results = statement.executeQuery(getCountQuery());
      results.next();

      long count = results.getLong(1);
      int chunks = job.getConfiguration().getInt(MRJobConfig.NUM_MAPS, 1);
      long chunkSize = (count / chunks);

      results.close();
      statement.close();

      List<InputSplit> splits = new ArrayList<InputSplit>();

      // Split the rows into n-number of chunks and adjust the last chunk
      // accordingly
      for (int i = 0; i < chunks; i++) {
        DBInputSplit split;

        if ((i + 1) == chunks)
          split = new DBInputSplit(i * chunkSize, count);
        else
          split = new DBInputSplit(i * chunkSize, (i * chunkSize)
              + chunkSize);

        splits.add(split);
      }

      connection.commit();
      return splits;
    } catch (SQLException e) {
      throw new IOException("Got SQLException", e);
    } finally {
      try {
        if (results != null) { results.close(); }
      } catch (SQLException e1) {}
      try {
        if (statement != null) { statement.close(); }
      } catch (SQLException e1) {}

      closeConnection();
    }
  }