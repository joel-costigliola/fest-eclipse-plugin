package org.fest.eclipse.test;

public enum JavaTypeKind {

  CLASS, ENUM;

  String toJavaCode() {
    return name().toLowerCase();
  }
}
