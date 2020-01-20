public static boolean equals(Type a, Type b) {
    if (a == b) {
      // also handles (a == null && b == null)
      return true;

    } else if (a instanceof Class) {
      // Class already specifies equals().
      return a.equals(b);

    } else if (a instanceof ParameterizedType) {
      if (!(b instanceof ParameterizedType)) {
        return false;
      }

      // TODO: save a .clone() call
      ParameterizedType pa = (ParameterizedType) a;
      ParameterizedType pb = (ParameterizedType) b;
      return Objects.equal(pa.getOwnerType(), pb.getOwnerType())
          && pa.getRawType().equals(pb.getRawType())
          && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());

    } else if (a instanceof GenericArrayType) {
      if (!(b instanceof GenericArrayType)) {
        return false;
      }

      GenericArrayType ga = (GenericArrayType) a;
      GenericArrayType gb = (GenericArrayType) b;
      return equals(ga.getGenericComponentType(), gb.getGenericComponentType());

    } else if (a instanceof WildcardType) {
      if (!(b instanceof WildcardType)) {
        return false;
      }

      WildcardType wa = (WildcardType) a;
      WildcardType wb = (WildcardType) b;
      return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
          && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

    } else if (a instanceof TypeVariable) {
      if (!(b instanceof TypeVariable)) {
        return false;
      }
      TypeVariable<?> va = (TypeVariable) a;
      TypeVariable<?> vb = (TypeVariable) b;
      return va.getGenericDeclaration().equals(vb.getGenericDeclaration())
          && va.getName().equals(vb.getName());

    } else {
      // This isn't a type we support. Could be a generic array type, wildcard type, etc.
      return false;
    }
  }