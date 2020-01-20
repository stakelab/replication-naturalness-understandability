private void fillAnnotatedSuperInfo(AnalysisType type, DynamicHub hub) {
        Class<?> javaClass = type.getJavaClass();

        AnnotatedType annotatedSuperclass;
        try {
            annotatedSuperclass = javaClass.getAnnotatedSuperclass();
        } catch (MalformedParameterizedTypeException | TypeNotPresentException t) {
            /*
             * Loading the annotated super class can fail due to missing types. Ignore the exception
             * and return null.
             */
            annotatedSuperclass = null;
        }
        if (annotatedSuperclass != null && !isTypeAllowed(annotatedSuperclass.getType())) {
            annotatedSuperclass = null;
        }

        AnnotatedType[] allAnnotatedInterfaces;
        try {
            allAnnotatedInterfaces = javaClass.getAnnotatedInterfaces();
        } catch (MalformedParameterizedTypeException | TypeNotPresentException t) {
            /*
             * Loading annotated interfaces can fail due to missing types. Ignore the exception and
             * return an empty array.
             */
            allAnnotatedInterfaces = new AnnotatedType[0];
        }

        AnnotatedType[] annotatedInterfaces = Arrays.stream(allAnnotatedInterfaces)
                        .filter(ai -> isTypeAllowed(ai.getType())).toArray(AnnotatedType[]::new);
        AnnotatedType[] cachedAnnotatedInterfaces = annotatedInterfacesMap.computeIfAbsent(
                        new AnnotatedInterfacesEncodingKey(annotatedInterfaces), k -> annotatedInterfaces);
        hub.setAnnotatedSuperInfo(AnnotatedSuperInfo.factory(annotatedSuperclass, cachedAnnotatedInterfaces));
    }