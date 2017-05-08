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
package io.cdep;

import com.google.common.io.Files;
import io.cdep.annotations.NotNull;
import io.cdep.cdep.utils.FileUtils;
import io.cdep.cdep.yml.cdep.CDepYml;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TestCDep {
  @NotNull
  private static String main(@NotNull String... args) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    new CDep(ps, ps, false).go(args, true);
    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }

  @Test
  public void lintUsage() throws Exception {
    assertThat(main("lint")).contains("Usage:");
  }

  @Test
  public void startupInfo() throws Exception {
    String result = main("startup-info");
    System.out.printf(result);
  }

  @Test
  public void mergeTwo() throws Exception {
    File output = new File(".test-files/mergeTwo/merged-manifest.yml");
    output.delete();
    String text = main("merge", "com.github.jomof:sqlite/iOS:3.16.2-rev33", "com.github.jomof:sqlite/android:3.16.2-rev33",
        output.toString());
    System.out.printf(text);
  }

  @Test
  public void mergeTwoWithDifferentHash() throws Exception {
    File left = new File(".test-files/mergeTwo/merged-manifest-left.yml");
    File right = new File(".test-files/mergeTwo/merged-manifest-right.yml");
    File merged = new File(".test-files/mergeTwo/merged-manifest-merged.yml");
    left.delete();
    right.delete();
    merged.delete();
    merged.getParentFile().mkdirs();

    Files.write("coordinate:\n" +
        "  groupId: com.github.jomof\n" +
        "  artifactId: sdl2\n" +
        "  version: 2.0.5-rev26\n" +
        "interfaces:\n" +
        "  headers:\n" +
        "    file: headers.zip\n" +
        "    sha256: e7e61f29f9480209f1cad71c4d4f7cec75d63e7a457bbe9da0ac26a64ad5751b-left\n" +
        "    size: 338812\n" +
        "    include: include", left, StandardCharsets.UTF_8);

    Files.write("coordinate:\n" +
        "  groupId: com.github.jomof\n" +
        "  artifactId: sdl2\n" +
        "  version: 2.0.5-rev26\n" +
        "interfaces:\n" +
        "  headers:\n" +
        "    file: headers.zip\n" +
        "    sha256: e7e61f29f9480209f1cad71c4d4f7cec75d63e7a457bbe9da0ac26a64ad5751-right\n" +
        "    size: 338812\n" +
        "    include: include", right, StandardCharsets.UTF_8);
    merged.getParentFile().mkdirs();
    String text = main("merge",
        left.toString(),
        right.toString(),
        merged.toString());
    System.out.printf(text);
    String mergedText = FileUtils.readAllText(merged);
    System.out.printf(mergedText);
    assertThat(mergedText).contains("-right");
  }

  @Test
  public void fullfill() throws Exception {
    String text = main("fullfill",
        "../third_party/stb",
        "1.0.0",
        "../third_party/stb/cdep/cdep-manifest-divide.yml",
        "../third_party/stb/cdep/cdep-manifest-c_lexer.yml");
    System.out.printf(text);
  }

  @Test
  public void mergeHeaders() throws Exception {
    File output = new File(".test-files/mergeHeaders/merged-manifest.yml");
    File zip = new File(".test-files/mergeHeaders/headers.zip");
    zip.getParentFile().mkdirs();
    Files.write("xyz", zip, StandardCharsets.UTF_8);
    output.delete();
    String text = main("merge", "headers",
        "com.github.jomof:sqlite:3.16.2-rev48",
        zip.toString(),
        "include",
        output.toString());
    assertThat(text).doesNotContain("Usage");
    assertThat(text).contains("Merged com.github.jomof:sqlite:3.16.2-rev48 and ");
    System.out.printf(text);
    System.out.printf(FileUtils.readAllText(output));
  }

  @Test
  public void mergeFirstMissing() throws Exception {
    File output = new File(".test-files/mergeFirstMissing/merged-manifest.yml");
    output.delete();
    assertThat(main("merge", "non:existing:1.2.3", "com.github.jomof:firebase/admob:2.1.3-rev8", output.toString())).contains
        ("Manifest for 'non:existing:1.2.3' didn't exist");
  }

  @Test
  public void mergeTwo1() throws Exception {
    File output = new File(".test-files/mergeTwo1/merged-manifest.yml");
    output.delete();
    assertThat(main("merge", "com.github.jomof:sqlite/iOS:3.16.2-rev26", "com.github.jomof:sqlite/android:3.16.2-rev26", output
        .toString())).contains("Merged 2 manifests into");
  }

  @Test
  public void mergeTwo2() throws Exception {
    File output = new File(".test-files/mergeTwo2/merged-manifest.yml");
    output.delete();
    assertThat(main("merge", "com.github.jomof:cmakeify/iOS:0.0.219", "com.github.jomof:cmakeify/android:0.0.219", output
        .toString())).contains("Merged 2 manifests into");
  }

  @Test
  public void lintBoost() throws Exception {
    main(main("lint",
        "com.github.jomof:boost:1.0.63-rev18"
    ));
  }

  @Test
  public void lintSomeKnownLibraries() throws Exception {
    main(main("lint",
        //        "com.github.jomof:firebase/admob:2.1.3-rev11",
        //        "com.github.jomof:firebase/analytics:2.1.3-rev11",
        //        "com.github.jomof:firebase/auth:2.1.3-rev11",
        //        "com.github.jomof:firebase/database:2.1.3-rev11",
        //        "com.github.jomof:firebase/invites:2.1.3-rev11",
        //        "com.github.jomof:firebase/messaging:2.1.3-rev11",
        //        "com.github.jomof:firebase/remote_config:2.1.3-rev11",
        //        "com.github.jomof:firebase/storage:2.1.3-rev11",
        "com.github.jomof:sqlite:3.16.2-rev25",
        "com.github.jomof:boost:1.0.63-rev18",
        //"com.github.jomof:vectorial:0.0.0-rev11",
        //"com.github.jomof:mathfu:1.0.2-rev7",
        "com.github.jomof:sdl2:2.0.5-rev11"));
  }

  @Test
  public void testVersion() throws Exception {
    assertThat(main("--version")).contains(BuildInfo.PROJECT_VERSION);
  }

  @Test
  public void missingConfigurationFile() throws Exception {
    new File(".test-files/empty-folder").mkdirs();
    assertThat(main("-wf", ".test-files/empty-folder")).contains("configuration file");
  }

  @Test
  public void workingFolderFlag() throws Exception {
    assertThat(main("--working-folder", "non-existing-blah")).contains("non-existing-blah");
  }

  @Test
  public void wfFlag() throws Exception {
    assertThat(main("-wf", "non-existing-blah")).contains("non-existing-blah");
  }

  //  @Test
  //  public void runVectorial() throws Exception {
  //    CDepYml config = new CDepYml();
  //    System.out.printf(new Yaml().dump(config));
  //    File yaml = new File(".test-files/runVectorial/cdep.yml");
  //    yaml.getParentFile().mkdirs();
  //    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: com.github" +
  //        ".jomof:vectorial:0.0.0-rev13\n", yaml, StandardCharsets.UTF_8);
  //    String result = main("-wf", yaml.getParent());
  //    System.out.printf(result);
  //  }

  //  @Test
  //  public void runMathfu() throws Exception {
  //    CDepYml config = new CDepYml();
  //    System.out.printf(new Yaml().dump(config));
  //    File yaml = new File(".test-files/runMathfu/cdep.yml");
  //    yaml.getParentFile().mkdirs();
  //    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: com.github.jomof:mathfu:1.0.2-rev7\n",
  //        yaml, StandardCharsets.UTF_8);
  //    String result = main("-wf", yaml.getParent());
  //    System.out.printf(result);
  //  }

  @Test
  public void testMissingGithubCoordinate() throws Exception {
    CDepYml config = new CDepYml();
    System.out.printf(new Yaml().dump(config));
    File yaml = new File(".test-files/runMathfu/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: com.github.jomof:mathfoo:1.0.2-rev7\n",
        yaml, StandardCharsets.UTF_8);
    try {
      String result = main("-wf", yaml.getParent());
      System.out.printf(result);
      fail("Expected an exception");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("Could not resolve 'com.github.jomof:mathfoo:1.0.2-rev7'. It doesn't exist.");
    }
  }

  //  @Test
  //  public void emptyCdepSha256() throws Exception {
  //    CDepYml config = new CDepYml();
  //    System.out.printf(new Yaml().dump(config));
  //    File yaml = new File(".test-files/emptyCdepSha256/cdep.yml");
  //    File yamlSha256 = new File(".test-files/emptyCdepSha256/cdep.sha256");
  //    yaml.getParentFile().mkdirs();
  //    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: com.github.jomof:mathfu:1.0.2-rev7\n",
  //        yaml, StandardCharsets.UTF_8);
  //    Files.write("# This file is automatically maintained by CDep.\n#\n#     MANUAL EDITS WILL BE LOST ON THE NEXT " +
  //            "CDEP RUN\n#\n# This file contains a list of CDep coordinates along with the SHA256 hash of their\n"
  //            + "# manifest file. This is to ensure that a manifest hasn't changed since the last\n# time CDep ran.\n"
  //            + "# The recommended best practice is to check this file into source control so that\n# anyone else " +
  //            "who builds this project is guaranteed to get the same dependencies.\n\n\n",
  //        yamlSha256,
  //        StandardCharsets.UTF_8);
  //    String result = main("-wf", yaml.getParent());
  //    System.out.printf(result);
  //  }

  @Test
  public void unfindableLocalFile() throws Exception {
    CDepYml config = new CDepYml();
    System.out.printf(new Yaml().dump(config));
    File yaml = new File(".test-files/unfindableLocalFile/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: ../not-a-file/cdep-manifest.yml\n",
        yaml, StandardCharsets.UTF_8);

    try {
      main("-wf", yaml.getParent());
      fail("Expected failure");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("Could not resolve '../not-a-file/cdep-manifest.yml'. It doesn't exist.");
    }
  }

  //  @Test
  //  public void someKnownUrls() throws Exception {
  //    CDepYml config = new CDepYml();
  //    System.out.printf(new Yaml().dump(config));
  //    File yaml = new File(".test-files/someKnownUrls/cdep.yml");
  //    yaml.getParentFile().mkdirs();
  //    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n"
  //        //                + "- compile: com.github.jomof:boost:1.0.63-rev12\n"
  //        //                + "- compile: com.github.jomof:cmakeify:0.0.70\n"
  //        + "- compile: com.github.jomof:mathfu:1.0.2-rev7\n- compile: https://github" +
  //        ".com/jomof/cmakeify/releases/download/0.0.81/cdep-manifest.yml\n" +
  //        "- compile: com.github.jomof:low-level-statistics:0.0.16\n", yaml, StandardCharsets.UTF_8);
  //    String result1 = main("show", "manifest", "-wf", yaml.getParent());
  //    yaml.delete();
  //    Files.write(result1, yaml, StandardCharsets.UTF_8);
  //    System.out.print(result1);
  //    String result2 = main("show", "manifest", "-wf", yaml.getParent());
  //    assertThat(result2).isEqualTo(result1);
  //    assertThat(result2).contains("0.0.81");
  //    String result3 = main("-wf", yaml.getParent());
  //  }

  @Test
  public void sqlite() throws Exception {
    CDepYml config = new CDepYml();
    System.out.printf(new Yaml().dump(config));
    File yaml = new File(".test-files/firebase/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\n"
            + "dependencies:\n"
            + "- compile: com.github.jomof:sqlite:3.16.2-rev45\n",
        yaml, StandardCharsets.UTF_8);
    String result1 = main("show", "manifest", "-wf", yaml.getParent());
    yaml.delete();
    Files.write(result1, yaml, StandardCharsets.UTF_8);
    System.out.print(result1);
    String result = main("-wf", yaml.getParent());
    System.out.printf(result);
  }

  @Test
  public void redownload() throws Exception {
    CDepYml config = new CDepYml();
    File yaml = new File(".test-files/simpleDependency/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\n" +
            "dependencies:\n" +
            "- compile: com.github.jomof:low-level-statistics:0.0.16\n", yaml,
        StandardCharsets.UTF_8);
    // Download first.
    main("-wf", yaml.getParent());
    // Redownload
    String result = main("redownload", "-wf", yaml.getParent());
    System.out.printf(result);
    assertThat(result).contains("Redownload");
  }

  @Test
  public void fetch() throws Exception {
    File folder = new File(".test-files/fetchArchive");
    folder.mkdirs();
    String result = main("fetch", "com.github.jomof:low-level-statistics:0.0.16", "com.github" +
        ".jomof:low-level-statistics:0.0.16", "-wf", folder.toString());
    System.out.printf(result);
    assertThat(result).contains("Fetch complete");
  }

  @Test
  public void fetchArchive() throws Exception {
    File folder = new File(".test-files/fetch");
    folder.mkdirs();
    String result = main("fetch-archive",
        "com.github.jomof:low-level-statistics:0.0.22",
        "https://github.com/jomof/low-level-statistics/releases/download/0.0.22/low-level-statistics-android-platform-21-armeabi.zip",
        "2035",
        "1661c899dee3b7cf1bb3e376c1cd504a156a4658904d8554a84db4e5a71ade49",
        "-wf", folder.toString());
    System.out.printf(result);
  }

  //  @Test
  //  public void fetchThree() throws Exception {
  //    File folder = new File(".test-files/fetch");
  //    folder.mkdirs();
  //    String result = main("fetch",
  //        "com.github.jomof:sqlite/iOS:3.16.2-rev43",
  //        "com.github.jomof:sqlite/linux:3.16.2-rev43",
  //        "com.github.jomof:sqlite/android:3.16.2-rev43",
  //        "-wf", folder.toString());
  //    System.out.printf(result);
  //    assertThat(result).contains("Fetch complete");
  //  }

  @Test
  public void fetchNotFound() throws Exception {
    File folder = new File(".test-files/fetch");
    folder.mkdirs();
    try {
      main("fetch", "com.github.jomof:low-level-statistics-x:0.0.16", "-wf", folder.toString());
      fail("Expected failure");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("Could not resolve 'com.github.jomof:low-level-statistics-x:0.0.16'. It doesn't exist.");
    }
  }

  @Test
  public void createHashes() throws Exception {
    File yaml = new File(".test-files/simpleDependency/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: com.github" +
        ".jomof:low-level-statistics:0.0.16\n", yaml, StandardCharsets.UTF_8);
    String text = main("create", "hashes", "-wf", yaml.getParent());
    assertThat(text).contains("Created cdep.sha256");
    File hashFile = new File(".test-files/simpleDependency/cdep.sha256");
    assertThat(hashFile.isFile());
  }

  @Test
  public void checkThatHashesWork() throws Exception {
    File yaml = new File(".test-files/checkThatHashesWork/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: com.github" +
        ".jomof:low-level-statistics:0.0.16\n", yaml, StandardCharsets.UTF_8);
    File hashes = new File(".test-files/checkThatHashesWork/cdep.sha256");
    Files.write("- coordinate: com.github.jomof:low-level-statistics:0.0.16\n  " +
        "sha256: dogbone", hashes, StandardCharsets.UTF_8);
    try {
      main("-wf", yaml.getParent());
      fail("Expected failure");
    } catch (RuntimeException e) {
      assertThat(e).hasMessage("SHA256 of cdep-manifest.yml for package 'com.github.jomof:low-level-statistics:0.0.16' " +
          "does not agree with constant in cdep.sha256. Something changed.");
    }
  }

  @Test
  public void noDependencies() throws Exception {
    CDepYml config = new CDepYml();
    System.out.printf(new Yaml().dump(config));
    File yaml = new File(".test-files/simpleDependency/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n", yaml, StandardCharsets.UTF_8);
    String result1 = main("-wf", yaml.getParent());
    System.out.printf(result1);
    assertThat(result1).contains("Nothing");
  }

  @Test
  public void dumpIsSelfHost() throws Exception {
    System.out.printf("%s\n", System.getProperty("user.home"));
    CDepYml config = new CDepYml();
    System.out.printf(new Yaml().dump(config));
    File yaml = new File(".test-files/simpleConfiguration/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]", yaml, StandardCharsets.UTF_8);
    String result1 = main("show", "manifest", "-wf", yaml.getParent());
    yaml.delete();
    Files.write(result1, yaml, StandardCharsets.UTF_8);
    System.out.print(result1);
    String result2 = main("show", "manifest", "-wf", yaml.getParent());
    assertThat(result2).isEqualTo(result1);
  }

  @Test
  public void testNakedCall() throws Exception {
    main();
  }

  @Test
  public void showFolders() throws Exception {
    String result = main("show", "folders");
    System.out.printf(result);
  }

  @Test
  public void showInclude() throws Exception {
    String result = main("show", "include", "com.github.jomof:boost:1.0.63-rev21");
    //assertThat(result).doesNotContain(".yml");
    assertThat(result).contains(".zip");
    System.out.printf(result);
  }

  @Test
  public void showIncludeNoCoordinate() throws Exception {
    String result = main("show", "include");
    assertThat(result).contains("Usage: show include {coordinate}");
    System.out.printf(result);
  }

  @Test
  public void help() throws Exception {
    String result = main("--help");
    System.out.printf(result);
    assertThat(result).contains("show folders");
  }

  @Test
  public void testWrapper() throws Exception {
    File testFolder = new File(".test-files/testWrapper");
    File redistFolder = new File(testFolder, "redist");
    File workingFolder = new File(testFolder, "working");
    File cdepFile = new File(redistFolder, "cdep");
    File cdepBatFile = new File(redistFolder, "cdep.bat");
    File cdepYmlFile = new File(redistFolder, "cdep.yml");
    File bootstrapJar = new File(redistFolder, "bootstrap/wrapper/bootstrap.jar");
    redistFolder.mkdirs();
    workingFolder.mkdirs();
    bootstrapJar.getParentFile().mkdirs();
    Files.write("cdepFile content", cdepFile, Charset.defaultCharset());
    Files.write("cdepBatFile content", cdepBatFile, Charset.defaultCharset());
    Files.write("cdepYmlFile content", cdepYmlFile, Charset.defaultCharset());
    Files.write("bootstrapJar content", bootstrapJar, Charset.defaultCharset());
    System.setProperty("io.cdep.appname", new File(redistFolder, "cdep.bat").getAbsolutePath());
    String result;
    try {
      result = main("wrapper", "-wf", workingFolder.toString());
    } finally {
      System.setProperty("io.cdep.appname", "rando-test-folder");
    }

    System.out.print(result);
    assertThat(result).contains("Installing cdep");
    File cdepToFile = new File(workingFolder, "cdep");
    File cdepBatToFile = new File(workingFolder, "cdep.bat");
    File cdepYmlToFile = new File(workingFolder, "cdep.yml");
    File bootstrapJarToFile = new File(workingFolder, "bootstrap/wrapper/bootstrap.jar");
    assertThat(cdepToFile.isFile()).isTrue();
    assertThat(cdepBatToFile.isFile()).isTrue();
    assertThat(cdepYmlToFile.isFile()).isTrue();
    assertThat(bootstrapJarToFile.isFile()).isTrue();
  }

  @Test
  public void localPathsWork() throws Exception {
    File yaml = new File(".test-files/localPathsWork/cdep.yml");
    yaml.getParentFile().mkdirs();
    Files.write("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: com.github" +
        ".jomof:low-level-statistics:0.0.16\n", yaml, StandardCharsets.UTF_8);
    // Download everything
    String resultRemote = main("-wf", yaml.getParent());
    // Ask for the local path to the manifest.
    String localPath = main("show", "local", "com.github.jomof:low-level-statistics:0.0.16");
    assertThat(localPath).contains("cdep-manifest.yml");
    // Write a new manifest with the local path.
    Files.write(String.format("builders: [cmake, cmakeExamples]\ndependencies:\n- compile: %s\n", localPath), yaml,
        StandardCharsets.UTF_8);
    String resultLocal = main("-wf", yaml.getParent(), "-df", new File(yaml.getParent(), "downloads").getPath());
    System.out.print(resultLocal);
    resultLocal = main("-wf", yaml.getParent());
    System.out.print(resultLocal);
  }
}