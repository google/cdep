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

import static io.cdep.cdep.ast.finder.ExpressionBuilder.archive;
import static org.junit.Assert.fail;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.ast.finder.Expression;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.ast.finder.ParameterExpression;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.Invariant;
import io.cdep.cdep.yml.CDepManifestYmlGenerator;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import org.junit.Test;

public class TestInterpretingVisitor {
  @Test
  public void testNullInclude() throws Exception {
    new InterpretingVisitor().visit(archive(new URL("https://google.com"), "sha256",
        192L, null, null, new String[0], new Expression[0], new CxxLanguageFeatures[0]));
  }

  @Test
  public void fuzzTest() {
    // for (int i = 0; i < 100; ++i)
    QuickCheck.forAll(new CDepManifestYmlGenerator(), new AbstractCharacteristic<CDepManifestYml>() {
      @Override
      protected void doSpecify(CDepManifestYml any) throws Throwable {
        try {
          Invariant.pushScope();
          String capture = CDepManifestYmlUtils.convertManifestToString(any);
          CDepManifestYml readAny = CDepManifestYmlUtils.convertStringToManifest(capture);
          BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
          builder.addManifest(new ResolvedManifest(new URL("https://google.com"), readAny));
          FunctionTableExpression function = builder.build();
          visitAndroid(function);
          visitiOS(function);
          visitLinux(function);
        } finally {
          Invariant.popScope();
        }
      }
    });
  }

  private void visitiOS(final FunctionTableExpression function) {
    new InterpretingVisitor() {
      @Override
      protected Object visitParameterExpression(@NotNull ParameterExpression expr) {
        if (function.globals.cdepExplodedRoot == expr) {
          return "exploded/root";
        }
        if (function.globals.buildSystemTargetSystem == expr) {
          return "Darwin";
        }
        if (function.globals.buildSystemTargetPlatform == expr) {
          return 21;
        }
        if (function.globals.cdepDeterminedAndroidAbi == expr) {
          return "x86";
        }
        if (function.globals.cdepDeterminedAndroidRuntime == expr) {
          return "c++_static";
        }
        if (function.globals.cmakeOsxSysroot == expr) {
          return "/iPhoneOS10.2.sdk";
        }
        if (function.globals.cmakeOsxArchitectures == expr) {
          return new String[]{"i386"};
        }
        return super.visitParameterExpression(expr);
      }
    }.visit(function);
  }

  @Test
  public void testAllResolvedManifestsLinux() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("archiveMissingSize", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("archiveMissingSha256", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("archiveMissingFile", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("sqliteiOS",
        "Abort: Target platform Linux is not supported by com.github.jomof:sqlite:3.16.2-rev33. Supported: Darwin");
    expected.put("sqlite",
        "Abort: Target platform Linux is not supported by com.github.jomof:sqlite:0.0.0. Supported: Android Darwin");
    expected.put("sqliteAndroid",
        "Abort: Target platform Linux is not supported by com.github.jomof:sqlite:3.16.2-rev33. Supported: Android");
    expected.put("admob",
        "Reference com.github.jomof:firebase/app:2.1.3-rev8 was not found, needed by com.github.jomof:firebase/admob:2.1.3-rev8");
    expected.put("singleABI",
        "Abort: Target platform Linux is not supported by com.github.jomof:sqlite:0.0.0. Supported: Android");
    expected.put("singleABISqlite",
        "Abort: Target platform Linux is not supported by com.github.jomof:sqlite:3.16.2-rev45. Supported: Android Darwin");
    expected.put("templateWithNullArchives", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("templateWithOnlyFile", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("indistinguishableAndroidArchives", "Abort: Target platform Linux is not supported by "
        + "com.github.jomof:firebase/app:0.0.0. Supported: Android");
    expected.put("re2", "Abort: Target platform Linux is not supported by com.github.jomof:re2:17.3.1-rev13. Supported: Android");
    expected.put("fuzz1", "Could not parse main manifest coordinate []");
    expected.put("fuzz2", "Abort: Archive file could not be converted to URL. It is likely an illegal path.");
    expected.put("openssl", "Abort: Target platform Linux is not supported by com.github.jomof:openssl:1.0.1-e-rev6. Supported: Android");

    boolean unexpectedFailures = false;
    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
      builder.addManifest(manifest.resolved);
      String expectedFailure = expected.get(manifest.name);
      try {
        final FunctionTableExpression function = builder.build();
        visitLinux(function);
        if (expectedFailure != null) {
          fail("Expected failure for " + manifest.name);
        }
      } catch (RuntimeException e) {
        if (!RuntimeException.class.equals(e.getClass())) {
          throw e;
        }
        if (expectedFailure == null || !expectedFailure.equals(e.getMessage())) {
          unexpectedFailures = true;
          System.out.printf("expected.put(\"%s\", \"%s\");\n", manifest.name, e.getMessage());
        }
      }

    }
    if (unexpectedFailures) {
      fail("Unexpected failures. See console.");
    }
  }

  private void visitLinux(final FunctionTableExpression function) {
    new InterpretingVisitor() {
      @Override
      protected Object visitParameterExpression(@NotNull ParameterExpression expr) {
        if (function.globals.cdepExplodedRoot == expr) {
          return "exploded/root";
        }
        if (function.globals.buildSystemTargetSystem == expr) {
          return "Linux";
        }
        if (function.globals.buildSystemTargetPlatform == expr) {
          return 21;
        }
        if (function.globals.cdepDeterminedAndroidAbi == expr) {
          return "x86";
        }
        if (function.globals.cdepDeterminedAndroidRuntime == expr) {
          return "c++_static";
        }
        if (function.globals.cmakeOsxSysroot == expr) {
          return "/iPhoneOS10.2.sdk";
        }
        if (function.globals.cmakeOsxArchitectures == expr) {
          return "i386";
        }
        return super.visitParameterExpression(expr);
      }
    }.visit(function);
  }

  @Test
  public void testAllResolvedManifestsAndroid() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("sqliteLinux",
        "Abort: Target platform Android is not supported by com.github.jomof:sqlite:0.0.0. Supported: Linux");
    expected.put("sqliteLinuxMultiple",
        "Abort: Target platform Android is not supported by com.github.jomof:sqlite:0.0.0. Supported: Linux");
    expected.put("archiveMissingFile", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("admob",
        "Reference com.github.jomof:firebase/app:2.1.3-rev8 was not found, needed by com.github.jomof:firebase"
            + "/admob:2.1.3-rev8");
    expected.put("archiveMissingSize", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("archiveMissingSha256", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("sqliteiOS",
        "Abort: Target platform Android is not supported by com.github.jomof:sqlite:3.16.2-rev33. Supported: Darwin");
    expected.put("templateWithNullArchives", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("templateWithOnlyFile", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("indistinguishableAndroidArchives", "Abort: Android ABI x86 is not supported by "
        + "com.github.jomof:firebase/app:0.0.0 for platform 21. Supported: arm64-v8a ");
    expected.put("fuzz1", "Could not parse main manifest coordinate []");
    expected.put("fuzz2", "Abort: Archive file could not be converted to URL. It is likely an illegal path.");
    boolean unexpectedFailures = false;
    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
      builder.addManifest(manifest.resolved);
      String expectedFailure = expected.get(manifest.name);
      try {
        final FunctionTableExpression function = builder.build();
        visitAndroid(function);
        if (expectedFailure != null) {
          fail("Expected failure");
        }
      } catch (RuntimeException e) {
        if (expectedFailure == null || !expectedFailure.equals(e.getMessage())) {
          //e.printStackTrace();
          unexpectedFailures = true;
          System.out.printf("expected.put(\"%s\", \"%s\");\n", manifest.name, e.getMessage());
        }
      }
    }
    if (unexpectedFailures) {
      fail("Unexpected failures. See console.");
    }
  }

  private void visitAndroid(final FunctionTableExpression function) {
    new InterpretingVisitor() {
      @Override
      protected Object visitParameterExpression(@NotNull ParameterExpression expr) {
        if (function.globals.cdepExplodedRoot == expr) {
          return "exploded/root";
        }
        if (function.globals.buildSystemTargetSystem == expr) {
          return "Android";
        }
        if (function.globals.buildSystemTargetPlatform == expr) {
          return 21;
        }
        if (function.globals.cdepDeterminedAndroidAbi == expr) {
          return "x86";
        }
        if (function.globals.cdepDeterminedAndroidRuntime == expr) {
          return "c++_static";
        }
        if (function.globals.cmakeOsxSysroot == expr) {
          return "/iPhoneOS10.2.sdk";
        }
        if (function.globals.cmakeOsxArchitectures == expr) {
          return "i386";
        }
        if (function.globals.buildSystemNoneRuntime == expr) {
          return "none";
        }
        return super.visitParameterExpression(expr);
      }
    }.visit(function);
  }

  @Test
  public void testAllResolvedManifestsiOS() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("sqliteLinuxMultiple",
        "Abort: Target platform Darwin is not supported by com.github.jomof:sqlite:0.0.0. Supported: Linux");
    expected.put("archiveMissingSha256", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("archiveMissingFile", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("archiveMissingSize", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("sqliteAndroid",
        "Abort: Target platform Darwin is not supported by com.github.jomof:sqlite:3.16.2-rev33. Supported: Android");
    expected.put("sqliteLinux",
        "Abort: Target platform Darwin is not supported by com.github.jomof:sqlite:0.0.0. Supported: Linux");
    expected.put("admob",
        "Reference com.github.jomof:firebase/app:2.1.3-rev8 was not found, needed by com.github.jomof:firebase/admob:2.1.3-rev8");
    expected.put("singleABI",
        "Abort: Target platform Darwin is not supported by com.github.jomof:sqlite:0.0.0. Supported: Android");
    expected.put("templateWithNullArchives", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("templateWithNullArchives", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("templateWithOnlyFile", "Abort: Archive in http://google.com/cdep-manifest.yml was malformed");
    expected.put("indistinguishableAndroidArchives",
        "Abort: Target platform Darwin is not supported by com.github.jomof:firebase/app:0.0.0. Supported: Android");
    expected.put("re2",
        "Abort: Target platform Darwin is not supported by com.github.jomof:re2:17.3.1-rev13. Supported: Android");
    expected.put("fuzz1", "Could not parse main manifest coordinate []");
    expected.put("fuzz2", "Abort: Archive file could not be converted to URL. It is likely an illegal path.");
    expected.put("openssl", "Abort: Target platform Darwin is not supported by com.github.jomof:openssl:1.0.1-e-rev6. Supported: Android");
    boolean unexpectedFailures = false;
    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      final BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
      builder.addManifest(manifest.resolved);
      String expectedFailure = expected.get(manifest.name);
      try {
        final FunctionTableExpression function = builder.build();
        visitiOS(function);
        if (expectedFailure != null) {
          fail("Expected failure");
        }
      } catch (RuntimeException e) {
        if (expectedFailure == null || !expectedFailure.equals(e.getMessage())) {
          unexpectedFailures = true;
          System.out.printf("expected.put(\"%s\", \"%s\");\n", manifest.name, e.getMessage());
        }
      }
    }
    if (unexpectedFailures) {
      fail("Unexpected failures. See console.");
    }
  }
}
