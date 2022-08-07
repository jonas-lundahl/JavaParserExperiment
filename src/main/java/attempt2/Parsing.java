package attempt2;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parsing {
  public static void main(String[] args) {
    CompilationUnit cu;
    try {
      cu = StaticJavaParser.parse(new File("C:\\Users\\Ani\\IdeaProjects\\ParsingTheParsers\\src\\main\\resources\\parser.java"));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    var methodDeclList = cu.findAll(MethodDeclaration.class);
    List<XMLAttribute> attributeList = new ArrayList<>();
    for (var methodDecl : methodDeclList) {
      var methodCallList = methodDecl.findAll(MethodCallExpr.class);
      var getAttrValCalls = methodCallList.stream()
        .filter(methodCallExpr -> methodCallExpr.toString().contains("getAttributeValue"))
        .filter(methodCallExpr -> !methodCallExpr.toString().startsWith("getAttributeValue"))
        .collect(Collectors.toList());
      getAttrValCalls.forEach(k -> attributeList.add(new XMLAttribute(k)));
    }
    attributeList.forEach(System.out::println);
  }
}
