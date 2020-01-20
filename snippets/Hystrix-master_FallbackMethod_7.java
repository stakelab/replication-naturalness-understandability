public void validateReturnType(Method commandMethod) throws FallbackDefinitionException {
        if (isPresent()) {
            Class<?> commandReturnType = commandMethod.getReturnType();
            if (ExecutionType.OBSERVABLE == ExecutionType.getExecutionType(commandReturnType)) {
                if (ExecutionType.OBSERVABLE != getExecutionType()) {
                    Type commandParametrizedType = commandMethod.getGenericReturnType();

                    // basically any object can be wrapped into Completable, Completable itself ins't parametrized
                    if(Completable.class.isAssignableFrom(commandMethod.getReturnType())) {
                        validateCompletableReturnType(commandMethod, method.getReturnType());
                        return;
                    }

                    if (isReturnTypeParametrized(commandMethod)) {
                        commandParametrizedType = getFirstParametrizedType(commandMethod);
                    }
                    validateParametrizedType(commandParametrizedType, method.getGenericReturnType(), commandMethod, method);
                } else {
                    validateReturnType(commandMethod, method);
                }


            } else if (ExecutionType.ASYNCHRONOUS == ExecutionType.getExecutionType(commandReturnType)) {
                if (isCommand() && ExecutionType.ASYNCHRONOUS == getExecutionType()) {
                    validateReturnType(commandMethod, method);
                }
                if (ExecutionType.ASYNCHRONOUS != getExecutionType()) {
                    Type commandParametrizedType = commandMethod.getGenericReturnType();
                    if (isReturnTypeParametrized(commandMethod)) {
                        commandParametrizedType = getFirstParametrizedType(commandMethod);
                    }
                    validateParametrizedType(commandParametrizedType, method.getGenericReturnType(), commandMethod, method);
                }
                if (!isCommand() && ExecutionType.ASYNCHRONOUS == getExecutionType()) {
                    throw new FallbackDefinitionException(createErrorMsg(commandMethod, method, "fallback cannot return Future if the fallback isn't command when the command is async."));
                }
            } else {
                if (ExecutionType.ASYNCHRONOUS == getExecutionType()) {
                    throw new FallbackDefinitionException(createErrorMsg(commandMethod, method, "fallback cannot return Future if command isn't asynchronous."));
                }
                if (ExecutionType.OBSERVABLE == getExecutionType()) {
                    throw new FallbackDefinitionException(createErrorMsg(commandMethod, method, "fallback cannot return Observable if command isn't observable."));
                }
                validateReturnType(commandMethod, method);
            }

        }
    }