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
package io.cdep.cdep.yml.cdepmanifest;

import io.cdep.annotations.NotNull;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.Version;

@SuppressWarnings("ConstantConditions")
public class CDepManifestYmlRewriter {

  @NotNull
  public CDepManifestYml visitCDepManifestYml(@NotNull CDepManifestYml value) {
    assert value.coordinate != null;
    return new CDepManifestYml(
        visitDepManifestYmlVersion(value.sourceVersion),
        visitCoordinate(value.coordinate),
        visitHardNameDependencyArray(value.dependencies),
        visitLicense(value.license),
        visitInterfaces(value.interfaces),
        visitAndroid(value.android),
        visitiOS(value.iOS),
        visitLinux(value.linux),
        visitExample(value.example));
  }

  @NotNull
  License visitLicense(@NotNull  License license) {
    return new License(
        visitString(license.name),
        visitString(license.url)
    );
  }

  @Nullable
  private String visitExample(String example) {
    return visitString(example);
  }

  @Nullable
  protected String visitString(String example) {
    return example;
  }

  @Nullable
  protected Linux visitLinux(@Nullable Linux linux) {
    if (linux == null) {
      return null;
    }
    return new Linux(
        visitLinuxArchiveArray(linux.archives)
    );
  }

  @Nullable
  protected LinuxArchive[] visitLinuxArchiveArray(@Nullable LinuxArchive[] archives) {
    if (archives == null) {
      return null;
    }
    LinuxArchive[] result = new LinuxArchive[archives.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = visitLinuxArchive(archives[i]);
    }
    return result;
  }

  @NotNull
  protected LinuxArchive visitLinuxArchive(@NotNull LinuxArchive archive) {
    return new LinuxArchive(
        visitFile(archive.file),
        visitSha256(archive.sha256),
        visitSize(archive.size),
        visitLibArray(archive.libs),
        visitInclude(archive.include));
  }

  @Nullable
  private String[] visitLibArray(@Nullable String[] libs) {
    if (libs == null) {
      return null;
    }
    String result[] = new String[libs.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = visitLib(libs[i]);
    }
    return result;
  }

  @Nullable
  private String visitInclude(String include) {
    return visitString(include);
  }

  @Nullable
  private String visitLib(String lib) {
    return visitString(lib);
  }

  private Long visitSize(Long size) {
    return visitLong(size);
  }

  private Long visitLong(Long value) {
    return value;
  }

  @Nullable
  private String visitSha256(String sha256) {
    return visitString(sha256);
  }

  @Nullable
  private String visitFile(String file) {
    return visitString(file);
  }

  @Nullable
  private iOS visitiOS(@Nullable iOS iOS) {
    if (iOS == null) {
      return null;
    }
    return new iOS(
        visitHardNameDependencyArray(iOS.dependencies),
        visitiOSArchiveArray(iOS.archives)
    );
  }

  @Nullable
  private iOSArchive[] visitiOSArchiveArray(@Nullable iOSArchive[] archives) {
    if (archives == null) {
      return null;
    }
    iOSArchive[] result = new iOSArchive[archives.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = visitiOSArchive(archives[i]);
    }
    return result;
  }

  @NotNull
  protected iOSArchive visitiOSArchive(@NotNull iOSArchive archive) {
    return new iOSArchive(
        visitFile(archive.file),
        visitSha256(archive.sha256),
        visitSize(archive.size),
        visitiOSPlatform(archive.platform),
        visitiOSArchitecture(archive.architecture),
        visitSdk(archive.sdk),
        visitInclude(archive.include),
        visitLibArray(archive.libs),
        visitFlavor(archive.flavor));
  }

  @Nullable
  private String visitFlavor(String flavor) {
    return visitString(flavor);
  }

  @Nullable
  private String visitSdk(String sdk) {
    return visitString(sdk);
  }

  private iOSArchitecture visitiOSArchitecture(iOSArchitecture architecture) {
    return architecture;
  }

  private iOSPlatform visitiOSPlatform(iOSPlatform platform) {
    return platform;
  }

  @Nullable
  private Android visitAndroid(@Nullable Android android) {
    if (android == null) {
      return null;
    }
    return new Android(
        visitHardNameDependencyArray(android.dependencies),
        visitAndroidArchiveArray(android.archives)
    );
  }

  @Nullable
  protected AndroidArchive[] visitAndroidArchiveArray(@Nullable AndroidArchive[] archives) {
    if (archives == null) {
      return null;
    }
    AndroidArchive[] result = new AndroidArchive[archives.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = visitAndroidArchive(archives[i]);
    }
    return result;
  }

  @Nullable
  protected AndroidArchive visitAndroidArchive(@NotNull AndroidArchive archive) {
    return new AndroidArchive(
        visitFile(archive.file),
        visitSha256(archive.sha256),
        visitSize(archive.size),
        visitNdk(archive.ndk),
        visitCompiler(archive.compiler),
        visitRuntime(archive.runtime),
        visitPlatform(archive.platform),
        visitBuilder(archive.builder),
        visitAbi(archive.abi),
        visitInclude(archive.include),
        visitLibArray(archive.libs),
        visitFlavor(archive.flavor));
  }

  @Nullable
  private AndroidABI visitAbi(AndroidABI abi) {
    return new AndroidABI(visitString(abi.name));
  }

  @Nullable
  private String visitBuilder(String builder) {
    return visitString(builder);
  }

  @Nullable
  private String visitPlatform(String platform) {
    return visitString(platform);
  }

  @Nullable
  private String visitRuntime(String runtime) {
    return visitString(runtime);
  }

  @Nullable
  private String visitCompiler(String compiler) {
    return visitString(compiler);
  }

  @Nullable
  private String visitNdk(String ndk) {
    return visitString(ndk);
  }

  @Nullable
  private Interfaces visitInterfaces(@Nullable Interfaces interfaces) {
    if (interfaces == null) {
      return null;
    }
    return new Interfaces(visitArchive(interfaces.headers));
  }

  @Nullable
  protected Archive visitArchive(@Nullable Archive archive) {
    if (archive == null) {
      return null;
    }
    return new Archive(
        visitFile(archive.file),
        visitSha256(archive.sha256),
        visitLong(archive.size),
        visitInclude(archive.include),
        visitRequiresArray(archive.requires)
    );
  }

  @Nullable
  private CxxLanguageFeatures[] visitRequiresArray(@Nullable CxxLanguageFeatures[] requires) {
    if (requires == null) {
      return null;
    }
    CxxLanguageFeatures[] result = new CxxLanguageFeatures[requires.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = visitRequire(requires[i]);
    }
    return result;
  }

  private CxxLanguageFeatures visitRequire(CxxLanguageFeatures require) {
    return require;
  }

  @Nullable
  private HardNameDependency[] visitHardNameDependencyArray(@Nullable HardNameDependency[] dependencies) {
    if (dependencies == null) {
      return null;
    }
    HardNameDependency[] result = new HardNameDependency[dependencies.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = visitHardNameDependency(dependencies[i]);
    }
    return result;
  }

  @Nullable
  protected HardNameDependency visitHardNameDependency(@NotNull HardNameDependency dependency) {
    return new HardNameDependency(
        visitCompile(dependency.compile),
        visitSha256(dependency.sha256));
  }

  @Nullable
  private String visitCompile(String compile) {
    return visitString(compile);
  }

  @NotNull
  protected Coordinate visitCoordinate(@NotNull Coordinate coordinate) {
    return new Coordinate(
        visitGroupId(coordinate.groupId),
        visitArtifactId(coordinate.artifactId),
        visitVersion(coordinate.version)
    );
  }

  @NotNull
  private Version visitVersion(@NotNull Version version) {
    return new Version(visitString(version.value));
  }

  @Nullable
  private String visitArtifactId(String artifactId) {
    return visitString(artifactId);
  }

  @Nullable
  private String visitGroupId(String groupId) {
    return visitString(groupId);
  }

  private CDepManifestYmlVersion visitDepManifestYmlVersion(CDepManifestYmlVersion sourceVersion) {
    return sourceVersion;
  }
}
