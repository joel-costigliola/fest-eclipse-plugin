package org.fest.eclipse.assertions.generator.internal.converter.util;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

public class AstParserFactory {

  public static ASTParser astParserFor(ICompilationUnit cu) {
    ASTParser parser = ASTParser.newParser(AST.JLS4);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(cu);
    parser.setResolveBindings(true);
    return parser;
  }

  public static ASTParser astParserFor(IJavaProject project) {
    ASTParser parser = ASTParser.newParser(AST.JLS4);
    parser.setProject(project);
    parser.setResolveBindings(true);
    return parser;
  }
}
