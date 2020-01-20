@Override
    public void close() throws SQLException {
        if (isClosed()) {
            return;
        }

        boolean connectionClosed = this.conn.isClosed();
        // Reset the defaults
        if (pooled && !connectionClosed) {
            try {
                if (defaultMaxFieldSize != currentMaxFieldSize) {
                    stmt.setMaxFieldSize(defaultMaxFieldSize);
                    currentMaxFieldSize = defaultMaxFieldSize;
                }
                if (defaultMaxRows != currentMaxRows) {
                    stmt.setMaxRows(defaultMaxRows);
                    currentMaxRows = defaultMaxRows;
                }
                if (defaultQueryTimeout != currentQueryTimeout) {
                    stmt.setQueryTimeout(defaultQueryTimeout);
                    currentQueryTimeout = defaultQueryTimeout;
                }
                if (defaultFetchDirection != currentFetchDirection) {
                    stmt.setFetchDirection(defaultFetchDirection);
                    currentFetchDirection = defaultFetchDirection;
                }
                if (defaultFetchSize != currentFetchSize) {
                    stmt.setFetchSize(defaultFetchSize);
                    currentFetchSize = defaultFetchSize;
                }
            } catch (Exception e) {
                this.conn.handleException(e, null);
            }
        }

        conn.closePoolableStatement(this);
    }