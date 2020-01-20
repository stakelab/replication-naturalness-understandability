public static SimplePropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
    Map<String, SimplePropertyDescriptor> properties = new HashMap<>();

    properties.put(
        "class",
        new SimplePropertyDescriptor("class", GET_CLASS_NAME, null));

    for (Method m : clazz.getMethods()) {
      if (Class.class.equals(m.getDeclaringClass()) ||
          Object.class.equals(m.getDeclaringClass())) {
        continue;
      }

      String methodName = m.getName();
      String propertyName = null;

      Method readMethod = null;
      Method writeMethod = null;

      if (hasPrefix("is", methodName)) {
        readMethod = m;
        propertyName = uncapitalize(methodName.substring(2));
      } else if (hasPrefix("get", methodName) || hasPrefix("has", methodName)) {
        readMethod = m;
        propertyName = uncapitalize(methodName.substring(3));
      } else if (hasPrefix("set", methodName)) {
        if (m.getParameterCount() == 1) {
          writeMethod = m;
          propertyName = uncapitalize(methodName.substring(3));
        }
      }

      if (readMethod != null && readMethod.getParameterCount() != 0) {
        readMethod = null;
      }

      Function<Object, Object> read = null;

      if (readMethod != null) {
        final Method finalReadMethod = readMethod;

        read = obj -> {
          try {
            finalReadMethod.setAccessible(true);
            return finalReadMethod.invoke(obj);
          } catch (ReflectiveOperationException e) {
            throw new JsonException(e);
          }
        };
      }

      if (readMethod != null || writeMethod != null) {
        SimplePropertyDescriptor descriptor = properties.getOrDefault(propertyName, new SimplePropertyDescriptor(propertyName, null, null));

        properties.put(
            propertyName,
            new SimplePropertyDescriptor(
                propertyName,
                read != null ? read : descriptor.getReadMethod(),
                writeMethod != null ? writeMethod : descriptor.getWriteMethod()));
      }
    }

    SimplePropertyDescriptor[] pdsArray = new SimplePropertyDescriptor[properties.size()];
    return properties.values().toArray(pdsArray);
  }