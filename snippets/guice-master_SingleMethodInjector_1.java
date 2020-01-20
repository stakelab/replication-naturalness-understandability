private MethodInvoker createMethodInvoker(final Method method) {

    /*if[AOP]*/
    try {
      final net.sf.cglib.reflect.FastClass fastClass = BytecodeGen.newFastClassForMember(method);
      if (fastClass != null) {
        final int index = fastClass.getMethod(method).getIndex();

        return new MethodInvoker() {
          @Override
          public Object invoke(Object target, Object... parameters)
              throws IllegalAccessException, InvocationTargetException {
            return fastClass.invoke(index, target, parameters);
          }
        };
      }
    } catch (net.sf.cglib.core.CodeGenerationException e) {
      /* fall-through */
    }
    /*end[AOP]*/

    int modifiers = method.getModifiers();
    if (!Modifier.isPublic(modifiers)
        || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
      method.setAccessible(true);
    }

    return new MethodInvoker() {
      @Override
      public Object invoke(Object target, Object... parameters)
          throws IllegalAccessException, InvocationTargetException {
        return method.invoke(target, parameters);
      }
    };
  }