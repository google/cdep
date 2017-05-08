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
package io.cdep.cdep.fullfill;

import io.cdep.cdep.Coordinate;
import io.cdep.cdep.ResolvedManifests;
import io.cdep.cdep.Version;
import io.cdep.cdep.generator.GeneratorEnvironment;
import io.cdep.cdep.io.IO;
import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.FileUtils;
import io.cdep.cdep.utils.Invariant;
import io.cdep.cdep.yml.CDepManifestYmlGenerator;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.util.*;

import static com.google.common.truth.Truth.assertThat;
import static io.cdep.cdep.utils.Invariant.require;
import static io.cdep.cdep.yml.CDepManifestYmlSubject.assertThat;

public class TestFullfill {
  private final GeneratorEnvironment environment = new GeneratorEnvironment(
      new File("./test-files/TestFullfill/working"),
      null,
      false,
      false);

  private File[] templates(File... folders) {
    List<File> templates = new ArrayList<>();
    for (File folder : folders) {
      Collections.addAll(templates, folder.listFiles(new FileFilter() {
        @Override
        public boolean accept(File pathname) {
          return pathname.getName().startsWith("cdep-manifest");
        }
      }));
    }
    return templates.toArray(new File[templates.size()]);
  }

  @Test
  public void testBasicSTB() throws Exception {
    File templates[] = templates(
        new File("../third_party/stb/cdep/"));
    File output = new File(".test-files/testBasicSTB").getAbsoluteFile();
    Fullfill.multiple(environment, templates, output, new File("../third_party/stb"), "1.2.3");
  }

  @Test
  public void testBasicTinyDir() throws Exception {
    File templates[] = templates(
        new File("../third_party/tinydir/"));
    File output = new File(".test-files/testBasicTinyDir").getAbsoluteFile();
    List<File> result = Fullfill.multiple(environment, templates, output, new File("../third_party/tinydir"), "1.2.3");
    assertThat(result).hasSize(2);
  }

  @Test
  public void testBasicVectorial() throws Exception {
    File templates[] = templates(
        new File("../third_party/vectorial/cdep"));
    File output = new File(".test-files/testBasicVectorial").getAbsoluteFile();
    List<File> result = Fullfill.multiple(environment, templates, output, new File("../third_party/vectorial"), "1.2.3");
    assertThat(result).hasSize(2);
  }

  @Test
  public void testBasicMathFu() throws Exception {
    File templates[] = templates(
        new File("../third_party/mathfu/cdep"));
    File output = new File(".test-files/testBasicMathFu").getAbsoluteFile();
    List<File> result = Fullfill.multiple(environment, templates, output, new File("../third_party/mathfu"), "1.2.3");
    assertThat(result).hasSize(2);
    File manifestFile = new File(output, "layout");
    manifestFile = new File(manifestFile, "cdep-manifest.yml");
    CDepManifestYml manifest = CDepManifestYmlUtils.convertStringToManifest(FileUtils.readAllText(manifestFile));
    assertThat(manifest.dependencies[0].sha256).isNotNull();
    assertThat(manifest.dependencies[0].sha256).isNotEmpty();
  }

  @Test
  public void testMiniFirebase() throws Exception {
    File templates[] = new File[]{
        new File("../third_party/mini-firebase/cdep-manifest-app.yml"),
        new File("../third_party/mini-firebase/cdep-manifest-database.yml")
    };
    File output = new File(".test-files/testMiniFirebase").getAbsoluteFile();
    output.delete();
    List<File> result = Fullfill.multiple(environment, templates, output,
        new File("../third_party/mini-firebase/firebase_cpp_sdk"), "1.2.3");
    File manifestFile = new File(output, "layout");
    manifestFile = new File(manifestFile, "cdep-manifest-database.yml");
    CDepManifestYml manifest = CDepManifestYmlUtils.convertStringToManifest(FileUtils.readAllText(manifestFile));
    assertThat(manifest.dependencies[0].sha256).isNotNull();
    assertThat(manifest.dependencies[0].sha256).isNotEmpty();
    // Don't allow + in file name to escape.
    assertThat(manifest.android.archives[0].file.contains("+")).isFalse();
  }

  @Test
  public void testSqlite() throws Exception {
    ResolvedManifests.TestManifest manifest = ResolvedManifests.sqlite();
    File outputFolder = new File(".test-files/TestFullfill/testSqlite").getAbsoluteFile();
    outputFolder.delete();
    outputFolder.mkdirs();

    File manifestFile = new File(outputFolder, "cdep-manifest.yml");
    FileUtils.writeTextToFile(manifestFile, manifest.body);
    Fullfill.multiple(
        environment,
        new File[]{manifestFile},
        new File(outputFolder, "output"),
        new File(outputFolder, "source"),
        "1.2.3");
  }

  @Test
  public void testRe2() throws Exception {
    ResolvedManifests.TestManifest manifest = ResolvedManifests.sqlite();
    File outputFolder = new File(".test-files/TestFullfill/re2").getAbsoluteFile();
    outputFolder.delete();
    outputFolder.mkdirs();

    File manifestFile = new File(outputFolder, "cdep-manifest.yml");
    FileUtils.writeTextToFile(manifestFile, manifest.body);
    List<File> results = Fullfill.multiple(
        environment,
        new File[]{manifestFile},
        new File(outputFolder, "output"),
        new File(outputFolder, "source"),
        "1.2.3");

    CDepManifestYml result = CDepManifestYmlUtils.convertStringToManifest(FileUtils.readAllText(results.get(0)));
    assertThat(result).hasCoordinate(new Coordinate("com.github.jomof", "sqlite", new Version("0.0.0")));
    assertThat(result).hasArchiveNamed("sqlite-android-gnustl-platform-21.zip");
  }

  @Test
  public void testAllResolvedManifests() throws Exception {
    Map<String, String> expected = new HashMap<>();
    expected.put("sqliteLinuxMultiple",
        "Package 'com.github.jomof:sqlite:0.0.0' has multiple linux archives. Only one is allowed.");
    expected.put("archiveMissingSize", "Archive com.github.jomof:vectorial:0.0.0 is missing size or it is zero");
    expected.put("indistinguishableAndroidArchives",
        "Android archive com.github.jomof:firebase/app:0.0.0 file archive2.zip is indistinguishable at build time from " +
            "archive1.zip given the information in the manifest");
    expected.put("archiveMissingSha256", "Could not hash file bob.zip because it didn't exist");
    expected.put("archiveMissingFile", "Package 'com.github.jomof:vectorial:0.0.0' does not contain any files");
    expected.put("admob", "Archive com.github.jomof:firebase/admob:2.1.3-rev8 is missing include");
    expected.put("fuzz1", "Dependency had no compile field");
    boolean unexpectedFailure = false;

    for (ResolvedManifests.NamedManifest manifest : ResolvedManifests.all()) {
      File outputFolder = new File(".test-files/TestFullfill/testAllResolvedManifests/"
          + manifest.name).getAbsoluteFile();
      outputFolder.delete();
      outputFolder.mkdirs();

      String key = manifest.name;
      String expectedFailure = expected.get(key);

      File manifestFile = new File(outputFolder, "cdep-manifest.yml");
      FileUtils.writeTextToFile(manifestFile, manifest.body);
      try {
        Fullfill.multiple(
            environment,
            new File[]{manifestFile},
            new File(outputFolder, "output"),
            new File(outputFolder, "source"),
            "1.2.3");
        if (expectedFailure != null) {
          require(false, "Expected failure in %s: '%s'", manifest.name, expectedFailure);
        }
      } catch (RuntimeException e) {
        if (!(e.getClass().equals(RuntimeException.class))) {
          throw e;
        }
        if (e.getMessage() == null) {
          throw e;
        }
        if (e.getMessage().contains("Could not zip file")) {
          continue;
        }
        if (!e.getMessage().equals(expectedFailure)) {
          //e.printStackTrace();
          System.out.printf("expected.put(\"%s\", \"%s\");\n", key, e.getMessage());
          unexpectedFailure = true;
        }
      }
    }
    if (unexpectedFailure) {
      throw new RuntimeException("Unexpected failures. See console.");
    }
  }

  @Test
  public void fuzzTest() {
    //for (int i = 0; i < 10000; ++i)
      QuickCheck.forAll(new CDepManifestYmlGenerator(), new AbstractCharacteristic<CDepManifestYml>() {
        @Override
        protected void doSpecify(CDepManifestYml any) throws Throwable {
          File outputFolder = new File(".test-files/TestFullfill/fuzzTest/"
              + "fuzzTest").getAbsoluteFile();
          outputFolder.delete();
          outputFolder.mkdirs();
          File manifestFile = new File(outputFolder, "cdep-manifest.yml");
          String body = CDepManifestYmlUtils.convertManifestToString(any);
          FileUtils.writeTextToFile(manifestFile, body);

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          PrintStream ps = new PrintStream(baos);
          PrintStream originalOut = null;
          PrintStream originalErr = null;
          try {
            Invariant.pushScope();
            originalOut = IO.setOut(ps);
            originalErr = IO.setErr(ps);
            Fullfill.multiple(
                environment,
                new File[]{manifestFile},
                new File(outputFolder, "output"),
                new File(outputFolder, "source"),
                "1.2.3");
          } catch(Throwable e) {
            System.out.print(body);
            throw e;
          } finally {
            IO.setOut(originalOut);
            IO.setErr(originalErr);
            Invariant.popScope();
          }
        }
      });
  }
}