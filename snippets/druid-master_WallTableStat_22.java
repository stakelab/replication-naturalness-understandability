public void addSqlTableStat(WallSqlTableStat stat) {
        {
            long val = stat.getSelectCount();
            if (val > 0) {
                selectCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getSelectIntoCount();
            if (val > 0) {
                selectIntoCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getInsertCount();
            if (val > 0) {
                insertCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getUpdateCount();
            if (val > 0) {
                updateCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getDeleteCount();
            if (val > 0) {
                deleteCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getAlterCount();
            if (val > 0) {
                alterCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getTruncateCount();
            if (val > 0) {
                truncateCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getCreateCount();
            if (val > 0) {
                createCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getDropCount();
            if (val > 0) {
                dropCountUpdater.addAndGet(this, val);
            }
        }
        {
            long val = stat.getReplaceCount();
            if (val > 0) {
                replaceCountUpdater.addAndGet(this, val);
            }
        }
    }