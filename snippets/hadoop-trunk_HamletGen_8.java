void genAttributeMethod(String className, Method method, int indent) {
    String methodName = method.getName();
    String attrName = methodName.substring(1).replace('_', '-');
    Type[] params = method.getGenericParameterTypes();
    echo(indent, "\n",
         "@Override\n",
         "public ", className, topMode ? " " : "<T> ", methodName, "(");
    if (params.length == 0) {
      puts(0, ") {");
      puts(indent,
           "  addAttr(\"", attrName, "\", null);\n",
           "  return this;\n", "}");
    } else if (params.length == 1) {
      String typeName = getTypeName(params[0]);
      puts(0, typeName, " value) {");
      if (typeName.equals("EnumSet<LinkType>")) {
        puts(indent,
             "  addRelAttr(\"", attrName, "\", value);\n",
             "  return this;\n", "}");
      } else if (typeName.equals("EnumSet<Media>")) {
        puts(indent,
             "  addMediaAttr(\"", attrName, "\", value);\n",
             "  return this;\n", "}");
      } else {
        puts(indent,
             "  addAttr(\"", attrName, "\", value);\n",
             "  return this;\n", "}");
      }
    } else {
      throwUnhandled(className, method);
    }
  }