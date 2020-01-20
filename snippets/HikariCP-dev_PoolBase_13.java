private void setupConnection(final Connection connection) throws ConnectionSetupException
   {
      try {
         if (networkTimeout == UNINITIALIZED) {
            networkTimeout = getAndSetNetworkTimeout(connection, validationTimeout);
         }
         else {
            setNetworkTimeout(connection, validationTimeout);
         }

         if (connection.isReadOnly() != isReadOnly) {
            connection.setReadOnly(isReadOnly);
         }

         if (connection.getAutoCommit() != isAutoCommit) {
            connection.setAutoCommit(isAutoCommit);
         }

         checkDriverSupport(connection);

         if (transactionIsolation != defaultTransactionIsolation) {
            connection.setTransactionIsolation(transactionIsolation);
         }

         if (catalog != null) {
            connection.setCatalog(catalog);
         }

         if (schema != null) {
            connection.setSchema(schema);
         }

         executeSql(connection, config.getConnectionInitSql(), true);

         setNetworkTimeout(connection, networkTimeout);
      }
      catch (SQLException e) {
         throw new ConnectionSetupException(e);
      }
   }