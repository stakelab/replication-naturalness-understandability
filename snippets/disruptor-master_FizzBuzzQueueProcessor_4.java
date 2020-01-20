@Override
    public void run()
    {
        running = true;
        while (true)
        {
            try
            {
                switch (fizzBuzzStep)
                {
                    case FIZZ:
                    {
                        Long value = fizzInputQueue.take();
                        fizzOutputQueue.put(Boolean.valueOf(0 == (value.longValue() % 3)));
                        break;
                    }

                    case BUZZ:
                    {
                        Long value = buzzInputQueue.take();
                        buzzOutputQueue.put(Boolean.valueOf(0 == (value.longValue() % 5)));
                        break;
                    }

                    case FIZZ_BUZZ:
                    {
                        final boolean fizz = fizzOutputQueue.take().booleanValue();
                        final boolean buzz = buzzOutputQueue.take().booleanValue();
                        if (fizz && buzz)
                        {
                            ++fizzBuzzCounter;
                        }
                        break;
                    }
                }

                if (null != latch && sequence++ == count)
                {
                    latch.countDown();
                }
            }
            catch (InterruptedException ex)
            {
                if (!running)
                {
                    break;
                }
            }
        }
    }