public static BufferedImage crop(BufferedImage src, int x, int y,
                                     int width, int height, BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        long t = -1;
        if (DEBUG)
            t = System.currentTimeMillis();

        if (src == null)
            throw new IllegalArgumentException("src cannot be null");
        if (x < 0 || y < 0 || width < 0 || height < 0)
            throw new IllegalArgumentException("Invalid crop bounds: x [" + x
                    + "], y [" + y + "], width [" + width + "] and height ["
                    + height + "] must all be >= 0");

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        if ((x + width) > srcWidth)
            throw new IllegalArgumentException(
                    "Invalid crop bounds: x + width [" + (x + width)
                            + "] must be <= src.getWidth() [" + srcWidth + "]");
        if ((y + height) > srcHeight)
            throw new IllegalArgumentException(
                    "Invalid crop bounds: y + height [" + (y + height)
                            + "] must be <= src.getHeight() [" + srcHeight
                            + "]");

        if (DEBUG)
            log(0,
                    "Cropping Image [width=%d, height=%d] to [x=%d, y=%d, width=%d, height=%d]...",
                    srcWidth, srcHeight, x, y, width, height);

        // Create a target image of an optimal type to render into.
        BufferedImage result = createOptimalImage(src, width, height);
        Graphics g = result.getGraphics();

        /*
         * Render the region specified by our crop bounds from the src image
         * directly into our result image (which is the exact size of the crop
         * region).
         */
        g.drawImage(src, 0, 0, width, height, x, y, (x + width), (y + height),
                null);
        g.dispose();

        if (DEBUG)
            log(0, "Cropped Image in %d ms", System.currentTimeMillis() - t);

        // Apply any optional operations (if specified).
        if (ops != null && ops.length > 0)
            result = apply(result, ops);

        return result;
    }