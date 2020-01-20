public final boolean isSubtypeOf(Type supertype) {
    checkNotNull(supertype);
    if (supertype instanceof WildcardType) {
      // if 'supertype' is <? super Foo>, 'this' can be:
      // Foo, SubFoo, <? extends Foo>.
      // if 'supertype' is <? extends Foo>, nothing is a subtype.
      return any(((WildcardType) supertype).getLowerBounds()).isSupertypeOf(runtimeType);
    }
    // if 'this' is wildcard, it's a suptype of to 'supertype' if any of its "extends"
    // bounds is a subtype of 'supertype'.
    if (runtimeType instanceof WildcardType) {
      // <? super Base> is of no use in checking 'from' being a subtype of 'to'.
      return any(((WildcardType) runtimeType).getUpperBounds()).isSubtypeOf(supertype);
    }
    // if 'this' is type variable, it's a subtype if any of its "extends"
    // bounds is a subtype of 'supertype'.
    if (runtimeType instanceof TypeVariable) {
      return runtimeType.equals(supertype)
          || any(((TypeVariable<?>) runtimeType).getBounds()).isSubtypeOf(supertype);
    }
    if (runtimeType instanceof GenericArrayType) {
      return of(supertype).isSupertypeOfArray((GenericArrayType) runtimeType);
    }
    // Proceed to regular Type subtype check
    if (supertype instanceof Class) {
      return this.someRawTypeIsSubclassOf((Class<?>) supertype);
    } else if (supertype instanceof ParameterizedType) {
      return this.isSubtypeOfParameterizedType((ParameterizedType) supertype);
    } else if (supertype instanceof GenericArrayType) {
      return this.isSubtypeOfArrayType((GenericArrayType) supertype);
    } else { // to instanceof TypeVariable
      return false;
    }
  }