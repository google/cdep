package io.cdep.cdep.generator;

import static io.cdep.cdep.utils.StringUtils.safeFormat;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.ReadonlyVisitor;
import io.cdep.cdep.ast.finder.ConstantExpression;

public class AbstractNdkBuildGenerator extends ReadonlyVisitor {
  @NotNull
  final GeneratorEnvironment environment;

  @NotNull
  StringBuilder sb = new StringBuilder();

  protected AbstractNdkBuildGenerator(@NotNull GeneratorEnvironment environment) {
    this.environment = environment;
  }

  @Override
  protected void visitConstantExpression(ConstantExpression expr) {
    append("%s", expr.value);
  }

  protected void append(String format, Object... args) {
    sb.append(safeFormat(format, args));
  }
}
