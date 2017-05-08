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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import io.cdep.annotations.NotNull;
import io.cdep.cdep.InterpretingVisitor.ModuleArchive;
import io.cdep.cdep.ast.finder.FunctionTableExpression;
import io.cdep.cdep.generator.CMakeGenerator;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.resolver.ResolvedManifest;
import io.cdep.cdep.resolver.Resolver;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.ExpressionUtils;
import io.cdep.cdep.utils.Invariant;
import io.cdep.cdep.yml.CDepManifestYmlGenerator;
import io.cdep.cdep.yml.cdep.SoftNameDependency;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import org.junit.Test;

@SuppressWarnings("ConstantConditions")
public class TestFindModuleFunctionTableBuilder {

  private final GeneratorEnvironment environment = new GeneratorEnvironment(
      new File("./test-files/TestFindModuleFunctionTableBuilder/working"),
      null,
      false,
      false);
  final private Resolver resolver = new Resolver(environment);

  @NotNull
  private static SoftNameDependency createReference(@NotNull String compile) {
    return new SoftNameDependency(compile);

  }

  @Test
  public void testSimple() throws Exception {
    ResolvedManifest resolved = resolver.resolveAny(
        createReference(
            "https://github.com/jomof/cmakeify/releases/download/0.0.81/cdep-manifest.yml"));
    assert resolved != null;
    assertThat(resolved.cdepManifestYml.coordinate.groupId).isEqualTo("com.github.jomof");
    assertThat(resolved.cdepManifestYml.coordinate.artifactId).isEqualTo("cmakeify");
    assertThat(resolved.cdepManifestYml.coordinate.version.value).isEqualTo("0.0.81");
    assert resolved.cdepManifestYml.android != null;
    assertThat(resolved.cdepManifestYml.android.archives.length).isEqualTo(8);

    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    System.out.printf(CreateStringVisitor.convert(table));
    String zip = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86").remote.getPath();
    assertThat(zip).endsWith("cmakeify-android-platform-21.zip");
    new CMakeGenerator(environment, table).generate();
  }

  @Test
  public void testArchiveOnly() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.archiveOnly().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    System.out.printf(CreateStringVisitor.convert(table));
    String zip = FindModuleInterpreter.findiOS(table, resolved.cdepManifestYml.coordinate, environment.unzippedArchivesFolder
        .getAbsolutePath(), new String[]{"armv7"}, "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS" +
        ".platform/Developer/SDKs/iPhoneOS10.2.sdk").remote.getPath();
    assertThat(zip).endsWith("vectorial.zip");
    zip = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86").remote.getPath();
    assertThat(zip).endsWith("vectorial.zip");
    new CMakeGenerator(environment, table).generate();
  }

  @Test
  public void testSingleABISqlite() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.singleABISqlite().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    System.out.printf(CreateStringVisitor.convert(table));
    ModuleArchive found = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86_64");

    assertThat(found.remote.getPath()).endsWith("sqlite-android-cxx-platform-21-x86_64.zip");
    new CMakeGenerator(environment, table).generate();
  }

  @Test
  public void testTinyiOS() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.emptyAndroidArchive().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    System.out.printf(CreateStringVisitor.convert(table));
    String zip = FindModuleInterpreter.findiOS(table, resolved.cdepManifestYml.coordinate, environment.unzippedArchivesFolder
        .getAbsolutePath(), new String[]{"armv7"}, "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS" +
        ".platform/Developer/SDKs/iPhoneOS10.2.sdk").remote.getPath();
    assertThat(zip).endsWith("sqlite-ios-platform-iPhone.zip");
    new CMakeGenerator(environment, table).generate();
  }

  @Test
  public void testTinyAndroid() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.emptyiOSArchive().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    System.out.printf(CreateStringVisitor.convert(table));
    ModuleArchive found = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86");
    assertThat(found.remote.toString()).contains("sqlite-android-cxx-platform-12.zip");
    new CMakeGenerator(environment, table).generate();
  }

  @Test
  public void testiOS() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.sqlite().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();

    System.out.printf(table.toString());
    String zip = FindModuleInterpreter.findiOS(table, resolved.cdepManifestYml.coordinate, environment.unzippedArchivesFolder
        .getAbsolutePath(), new String[]{"armv7s"}, "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS" +
        ".platform/Developer/SDKs/iPhoneOS10.2.sdk").remote.getPath();
    assertThat(zip).endsWith("sqlite-ios-platform-iPhone.zip");

    zip = FindModuleInterpreter.findiOS(table, resolved.cdepManifestYml.coordinate, environment.unzippedArchivesFolder
        .getAbsolutePath(), new String[]{"i386"}, "/Applications/Xcode" +
        ".app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator10.2.sdk").remote.getPath();
    assertThat(zip).endsWith("sqlite-ios-platform-simulator.zip");

    new CMakeGenerator(environment, table).generate();
    ExpressionUtils.getAllFoundModuleExpressions(table);
  }

  @Test
  public void testRequires() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.simpleRequires().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    String text = CreateStringVisitor.convert(table);
    System.out.printf(text);
    assertThat(text).contains("requires: cxx_auto_type");
  }

  @Test
  public void testLinux() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.sqliteLinux().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    CreateStringVisitor.convert(table);
    System.out.printf(table.toString());
    String zip = FindModuleInterpreter.findLinux(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath()).remote.getPath();
    assertThat(zip).endsWith("sqlite-linux.zip");

    new CMakeGenerator(environment, table).generate();
    ExpressionUtils.getAllFoundModuleExpressions(table);
  }

  @Test
  public void testiOSNonSpecificSDK() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.sqlite().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    String zip = FindModuleInterpreter.findiOS(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder
            .getAbsolutePath(),
        new String[]{"armv7s"},
        "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS.platform/Developer/SDKs/iPhoneOS.sdk").remote
        .getPath();
    assertThat(zip).endsWith("sqlite-ios-platform-iPhone.zip");

    zip = FindModuleInterpreter.findiOS(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder
            .getAbsolutePath(),
        new String[]{"i386"},
        "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator" +
            ".sdk").remote
        .getPath();
    assertThat(zip).endsWith("sqlite-ios-platform-simulator.zip");

    new CMakeGenerator(environment, table).generate();
    ExpressionUtils.getAllFoundModuleExpressions(table);
  }

  @Test
  public void testiOSUnknownPlatform() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.sqlite().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    try {
      String zip = FindModuleInterpreter.findiOS(table,
          resolved.cdepManifestYml.coordinate,
          environment.unzippedArchivesFolder
              .getAbsolutePath(),
          new String[]{"armv7s"},
          "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/iPad10.2.sdk").remote
          .getPath();
      fail("Expected exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage(
          "Abort: OSX SDK iPad10.2 is not supported by com.github.jomof:sqlite:0.0.0 and architecture armv7s. Supported: " +
              "iPhoneOS10.2 ");
    }
  }

  @Test
  public void testEmptyiOSArchive() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.emptyiOSArchive().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
  }

  @Test
  public void testEmptyAndroidArchive() throws Exception {
    ResolvedManifest resolved = ResolvedManifests.emptyAndroidArchive().manifest;
    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
  }

  @Test
  public void testCheckPlatformSwitch() throws Exception {
    ResolvedManifest resolved = resolver.resolveAny(createReference("https://github" +
        ".com/jomof/cmakeify/releases/download/0.0.81/cdep-manifest.yml"));
    assertThat(resolved.cdepManifestYml.coordinate.groupId).isEqualTo("com.github.jomof");
    assertThat(resolved.cdepManifestYml.coordinate.artifactId).isEqualTo("cmakeify");
    assertThat(resolved.cdepManifestYml.coordinate.version.value).isEqualTo("0.0.81");
    assertThat(resolved.cdepManifestYml.android.archives.length).isEqualTo(8);

    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    assertThat(FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86").remote.getPath()).contains("platform-21");
    assertThat(FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "22",
        "c++_shared",
        "x86").remote.getPath()).contains("platform-21");
    assertThat(FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "20",
        "c++_shared",
        "x86").remote.getPath()).contains("platform-9");
  }

  @Test
  public void testArchivePathIsFull() throws Exception {
    ResolvedManifest resolved = resolver.resolveAny(createReference("https://github" +
        ".com/jomof/cmakeify/releases/download/0.0.81/cdep-manifest.yml"));

    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    ModuleArchive found = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86");
    assertThat(found.remote.toString()).isEqualTo("https://github.com/jomof/cmakeify/releases/download/0.0.81/" +
        "cmakeify-android-platform-21.zip");
  }

  @Test
  public void testFoundIncludeAndLib() throws Exception {
    ResolvedManifest resolved = resolver.resolveAny(createReference("https://github" +
        ".com/jomof/sqlite/releases/download/3.16.2-rev25/cdep-manifest.yml"));

    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    ModuleArchive found = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86");
    assertThat(found.fullLibraryNames[0].getName()).isEqualTo("libsqlite.a");
  }

  @Test
  public void fuzzTest() {
    //for (int i = 0; i < 1000; ++i)
    QuickCheck.forAll(new CDepManifestYmlGenerator(), new AbstractCharacteristic<CDepManifestYml>() {
      @Override
      protected void doSpecify(CDepManifestYml any) throws Throwable {
        String capture = CDepManifestYmlUtils.convertManifestToString(any);
        CDepManifestYml readAny = any;
        try {
          readAny = CDepManifestYmlUtils.convertStringToManifest(capture);
        } catch (RuntimeException e) {
          System.out.printf("%s", capture);
          throw e;
        }
        try {
          Invariant.pushScope();
          BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
          builder.addManifest(new ResolvedManifest(new URL("https://google.com"), readAny));
          builder.build();
        } finally {
          Invariant.popScope();
        }
      }
    });
  }

  @Test
  public void testHeaderOnly() throws Exception {
    ResolvedManifest resolved = resolver.resolveAny(createReference(
        "https://github.com/jomof/boost/releases/download/1.0.63-rev18/cdep-manifest.yml"));

    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    ModuleArchive found = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86");
    assertThat(found.fullLibraryNames).isEmpty();
    assertThat(found.remote.toString()).isEqualTo(
        "https://github.com/jomof/boost/releases/download/1.0.63-rev18/boost_1_63_0.zip");
  }

  @Test
  public void testHeaderOnlyGitHubCoordinate() throws Exception {
    ResolvedManifest resolved = resolver.resolveAny(createReference("com.github.jomof:boost:1.0.63-rev18"));

    BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
    builder.addManifest(resolved);
    FunctionTableExpression table = builder.build();
    ModuleArchive found = FindModuleInterpreter.findAndroid(table,
        resolved.cdepManifestYml.coordinate,
        environment.unzippedArchivesFolder.getAbsolutePath(),
        "Android",
        "21",
        "c++_shared",
        "x86");
    assertThat(found.fullLibraryNames).isEmpty();
    assertThat(found.remote.toString()).isEqualTo("https://github" +
        ".com/jomof/boost/releases/download/1.0.63-rev18/boost_1_63_0.zip");
  }

  @Test
  public void testAllResolvedManifests() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("admob", "Reference com.github.jomof:firebase/app:2.1.3-rev8 was not found");
    expected.put("fuzz1", "Could not parse main manifest coordinate []");
    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      BuildFindModuleFunctionTable builder = new BuildFindModuleFunctionTable();
      builder.addManifest(manifest.resolved);
      String expectedFailure = expected.get(manifest.name);
      try {
        FunctionTableExpression table = builder.build();
        if (expectedFailure != null) {
          fail("Expected failure");
        }
      } catch (RuntimeException e) {
        if (!e.getClass().equals(RuntimeException.class)) {
          throw e;
        }
        if (expectedFailure == null) {
          throw e;
        }
        assertThat(e.getMessage()).contains(expectedFailure);
      }
    }
  }
}
