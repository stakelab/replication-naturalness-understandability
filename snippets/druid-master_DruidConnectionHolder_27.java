public void reset() throws SQLException {
        // reset default settings
        if (underlyingReadOnly != defaultReadOnly) {
            conn.setReadOnly(defaultReadOnly);
            underlyingReadOnly = defaultReadOnly;
        }

        if (underlyingHoldability != defaultHoldability) {
            conn.setHoldability(defaultHoldability);
            underlyingHoldability = defaultHoldability;
        }

        if (underlyingTransactionIsolation != defaultTransactionIsolation) {
            conn.setTransactionIsolation(defaultTransactionIsolation);
            underlyingTransactionIsolation = defaultTransactionIsolation;
        }

        if (underlyingAutoCommit != defaultAutoCommit) {
            conn.setAutoCommit(defaultAutoCommit);
            underlyingAutoCommit = defaultAutoCommit;
        }

        connectionEventListeners.clear();
        statementEventListeners.clear();

        for (Object item : statementTrace.toArray()) {
            Statement stmt = (Statement) item;
            JdbcUtils.close(stmt);
        }
        statementTrace.clear();

        conn.clearWarnings();
    }