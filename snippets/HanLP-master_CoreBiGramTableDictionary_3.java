static boolean loadDat(String path)
    {
//        ByteArray byteArray = ByteArray.createByteArray(path);
//        if (byteArray == null) return false;
//
//        int size = byteArray.nextInt(); // 这两个数组从byte转为int竟然要花4秒钟
//        start = new int[size];
//        for (int i = 0; i < size; ++i)
//        {
//            start[i] = byteArray.nextInt();
//        }
//
//        size = byteArray.nextInt();
//        pair = new int[size];
//        for (int i = 0; i < size; ++i)
//        {
//            pair[i] = byteArray.nextInt();
//        }

        try
        {
            ObjectInputStream in = new ObjectInputStream(IOUtil.newInputStream(path));
            start = (int[]) in.readObject();
            if (CoreDictionary.trie.size() != start.length - 1)     // 目前CoreNatureDictionary.ngram.txt的缓存依赖于CoreNatureDictionary.txt的缓存
            {                                                       // 所以这里校验一下二者的一致性，不然可能导致下标越界或者ngram错乱的情况
                in.close();
                return false;
            }
            pair = (int[]) in.readObject();
            in.close();
        }
        catch (Exception e)
        {
            logger.warning("尝试载入缓存文件" + path + "发生异常[" + e + "]，下面将载入源文件并自动缓存……");
            return false;
        }
        return true;
    }