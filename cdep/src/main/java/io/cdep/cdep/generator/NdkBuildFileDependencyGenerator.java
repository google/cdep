package io.cdep.cdep.generator;

import io.cdep.API;
import io.cdep.annotations.NotNull;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ast.finder.AssignmentExpression;
import io.cdep.cdep.ast.finder.FindModuleExpression;
import io.cdep.cdep.ast.finder.IfSwitchExpression;
import io.cdep.cdep.ast.finder.ModuleArchiveExpression;
import io.cdep.cdep.ast.finder.StatementExpression;
import io.cdep.cdep.utils.StringUtils;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Generate gnu make style file dependencies that call cdep to download relevant files
 */
public class NdkBuildFileDependencyGenerator extends AbstractNdkBuildGenerator {

  private Coordinate coordinate = null;
  private Set<String> seen = new HashSet<>();

  private NdkBuildFileDependencyGenerator(GeneratorEnvironment environment) {
    super(environment);
  }

  public static String create(GeneratorEnvironment environment, StatementExpression expr) {
    NdkBuildFileDependencyGenerator generator = new NdkBuildFileDependencyGenerator(environment);
    generator.visit(expr);
    return generator.sb.toString();
  }

  @Override
  protected void visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    this.coordinate = expr.coordinate;
    super.visitFindModuleExpression(expr);
  }

  @Override
  protected void visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    for (int i = 0; i < expr.libraryPaths.length; ++i) {
      StringBuilder old = sb;
      sb = new StringBuilder();
      visit(expr.libraryPaths[i]);
      String path = sb.toString();
      sb = old;

      if (!seen.contains(path)) {
        seen.add(path);
        sb.append("\r\n");
        sb.append(String.format("%s:\r\n", path));
        sb.append(String.format("\t%s", generateCDepCall(
            "fetch-archive", this.coordinate.toString(),
            expr.file.toString(),
            expr.size.toString(),
            expr.sha256)));
      }
    }
  }

  @Override
  protected void visitAssignmentExpression(@NotNull AssignmentExpression expr) {
  }

  @Override
  protected void visitIfSwitchExpression(@NotNull IfSwitchExpression expr) {
    visitArray(expr.expressions);
    visit(expr.elseExpression);
  }

  private String generateCDepCall(String... args) {
    try {
      return String.format("$(shell %s)",
          StringUtils.joinOn(" ", API.generateCDepCall(environment, args)));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
