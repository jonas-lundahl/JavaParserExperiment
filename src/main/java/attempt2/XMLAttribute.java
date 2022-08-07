package attempt2;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.StringJoiner;

public class XMLAttribute {
  private final String xmlNodeVariable;
  private final String type;
  private final boolean required;

  public XMLAttribute(MethodCallExpr methodCallExpr) {
    String parseFunc = methodCallExpr.getNameAsString();
    if (!parseFunc.startsWith("parse")) throw new RuntimeException(parseFunc + " is not a parseFunc!");
    this.type = initType(methodCallExpr);
    this.required = initRequired(parseFunc);
    this.xmlNodeVariable = initXMLNodeVariable(methodCallExpr);
  }

  private String initXMLNodeVariable(MethodCallExpr methodCallExpr) {
    var firstArg = methodCallExpr.getArgument(0);
    if (firstArg.isMethodCallExpr()) return initXMLNodeVariable(firstArg.asMethodCallExpr());
    return methodCallExpr.getArgument(0).toString();
  }

  private boolean initRequired(String parseFunc) {
    return !parseFunc.endsWith("Optional");
  }

  @SuppressWarnings("unchecked")
  private String initType(MethodCallExpr methodCallExpr) {
    MethodDeclaration decl = methodCallExpr.findAncestor(MethodDeclaration.class).orElseThrow();
    return decl.getType().asString();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", XMLAttribute.class.getSimpleName() + "[", "]")
      .add("xmlNodeVariable='" + xmlNodeVariable + "'")
      .add("type='" + type + "'")
      .add("required=" + required)
      .toString();
  }
}
