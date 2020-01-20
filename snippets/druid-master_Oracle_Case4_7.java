private void p0(final DataSource dataSource, String name, int threadCount) throws Exception {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            
                            int mod = i % 500;
                            
                            String sql = SQL; // + " AND ROWNUM <= " + (mod + 1);
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, 61);
                            ResultSet rs = stmt.executeQuery();
                            int rowCount = 0;
                            while (rs.next()) {
                                rowCount++;
                            }
                            // Assert.isTrue(!rs.isClosed());
                            rs.close();
                            // Assert.isTrue(!stmt.isClosed());
                            stmt.close();
                            Assert.isTrue(stmt.isClosed());
                            conn.close();
                            Assert.isTrue(conn.isClosed());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    endLatch.countDown();
                }
            };
            thread.start();
        }
        long startMillis = System.currentTimeMillis();
        long startYGC = TestUtil.getYoungGC();
        long startFullGC = TestUtil.getFullGC();
        startLatch.countDown();
        endLatch.await();

        long millis = System.currentTimeMillis() - startMillis;
        long ygc = TestUtil.getYoungGC() - startYGC;
        long fullGC = TestUtil.getFullGC() - startFullGC;

        System.out.println("thread " + threadCount + " " + name + " millis : "
                           + NumberFormat.getInstance().format(millis) + ", YGC " + ygc + " FGC " + fullGC);
    }