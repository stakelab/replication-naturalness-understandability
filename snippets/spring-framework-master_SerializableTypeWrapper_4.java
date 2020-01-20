@Nullable
	static Type forTypeProvider(TypeProvider provider) {
		Type providedType = provider.getType();
		if (providedType == null || providedType instanceof Serializable) {
			// No serializable type wrapping necessary (e.g. for java.lang.Class)
			return providedType;
		}
		if (GraalDetector.inImageCode() || !Serializable.class.isAssignableFrom(Class.class)) {
			// Let's skip any wrapping attempts if types are generally not serializable in
			// the current runtime environment (even java.lang.Class itself, e.g. on Graal)
			return providedType;
		}

		// Obtain a serializable type proxy for the given provider...
		Type cached = cache.get(providedType);
		if (cached != null) {
			return cached;
		}
		for (Class<?> type : SUPPORTED_SERIALIZABLE_TYPES) {
			if (type.isInstance(providedType)) {
				ClassLoader classLoader = provider.getClass().getClassLoader();
				Class<?>[] interfaces = new Class<?>[] {type, SerializableTypeProxy.class, Serializable.class};
				InvocationHandler handler = new TypeProxyInvocationHandler(provider);
				cached = (Type) Proxy.newProxyInstance(classLoader, interfaces, handler);
				cache.put(providedType, cached);
				return cached;
			}
		}
		throw new IllegalArgumentException("Unsupported Type class: " + providedType.getClass().getName());
	}