/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package io.cdep.cdep;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.ast.finder.*;

import java.io.File;

import static io.cdep.cdep.utils.Invariant.fail;

/**
 * Locates every referenced local file and ensures that those files are present in the right
 * place on the local file system.
 */
public class CheckLocalFileSystemIntegrity extends InterpretingVisitor {

  final private File explodedRoot;

  public CheckLocalFileSystemIntegrity(File explodedRoot) {
    this.explodedRoot = explodedRoot;
  }

  @NotNull
  private ModuleArchive superVisitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    return super.visitModuleArchiveExpression(expr);
  }

  @NotNull
  @Override
  protected ModuleArchive visitModuleArchiveExpression(@NotNull ModuleArchiveExpression expr) {
    ModuleArchive archive = superVisitModuleArchiveExpression(expr);
    if (archive.fullIncludePath != null) {
      if (!archive.fullIncludePath.getParentFile().isDirectory()) {
        fail("Expected '%s' folder to be created but it wasn't.",
            archive.fullIncludePath.getParentFile());
      }
      if (!archive.fullIncludePath.isDirectory()) {
        fail("Downloaded '%s' did not contain include folder '%s' at its root.\n"
                + "Local path: %s\n"
                + " If you own this package you can add \"include:\" "
                + "to the archive entry in cdep-manifest.yml to indicate that there is no "
                + "include folder.",
            archive.remote,
            archive.fullIncludePath.getName(),
            archive.fullIncludePath);
      }
    }
    for(File fullLibraryName : archive.fullLibraryNames) {
      if (!fullLibraryName.getParentFile().isDirectory()) {
        fail("Expected '%s' folder to be created but it wasn't.",
            fullLibraryName.getParentFile());
      }
      if (!fullLibraryName.isFile()) {
        fail("Downloaded '%s' did not contain library '%s/%s' at its root.\nLocal path: %s",
            archive.remote,
            fullLibraryName.getParentFile().getName(),
            fullLibraryName.getName(),
            fullLibraryName);
      }
    }
    return archive;
  }

  @Override
  protected Object visitParameterExpression(@NotNull ParameterExpression expr) {
    if (expr.name.equals("cdep_exploded_root")) {
      return explodedRoot;
    }
    return super.visitParameterExpression(expr);
  }

  @Nullable
  @Override
  protected Object visitFindModuleExpression(@NotNull FindModuleExpression expr) {
    return visit(expr.body);
  }

  @Nullable
  @Override
  protected Object visitIfSwitchExpression(@NotNull IfSwitchExpression expr) {
    for (int i = 0; i < expr.conditions.length; ++i) {
      // Don't visit the condition. Instead, travel down all paths.
      visit(expr.expressions[i]);
    }
    return visit(expr.elseExpression);
  }

  @Nullable
  @Override
  protected Object visitAbortExpression(@NotNull AbortExpression expr) {
    return null;
  }
}
