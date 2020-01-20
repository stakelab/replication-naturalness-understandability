public static Constructor<?> getCreatorConstructor(Constructor[] constructors) {
        Constructor<?> creatorConstructor = null;

        for (Constructor<?> constructor : constructors) {
            JSONCreator annotation = constructor.getAnnotation(JSONCreator.class);
            if (annotation != null) {
                if (creatorConstructor != null) {
                    throw new JSONException("multi-JSONCreator");
                }

                creatorConstructor = constructor;
                // 不应该break，否则多个构造方法上存在 JSONCreator 注解时，并不会触发上述异常抛出
            }
        }
        if (creatorConstructor != null) {
            return creatorConstructor;
        }

        for (Constructor constructor : constructors) {
            Annotation[][] paramAnnotationArrays = constructor.getParameterAnnotations();
            if (paramAnnotationArrays.length == 0) {
                continue;
            }
            boolean match = true;
            for (Annotation[] paramAnnotationArray : paramAnnotationArrays) {
                boolean paramMatch = false;
                for (Annotation paramAnnotation : paramAnnotationArray) {
                    if (paramAnnotation instanceof JSONField) {
                        paramMatch = true;
                        break;
                    }
                }
                if (!paramMatch) {
                    match = false;
                    break;
                }
            }

            if (match) {
                if (creatorConstructor != null) {
                    throw new JSONException("multi-JSONCreator");
                }

                creatorConstructor = constructor;
            }
        }

        if (creatorConstructor != null) {
            return creatorConstructor;
        }

        return creatorConstructor;
    }