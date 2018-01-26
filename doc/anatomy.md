## Anatomy of a CDep Package
A CDep package is a manifest file called cdep-manifest.yml along with zipped source and compiled libraries. You can see a live example [here](https://github.com/jomof/re2/releases/download/17.3.1-rev18/cdep-manifest.yml). Here's a fragment of cdep-manifest.yml.

```
coordinate:
  groupId: com.github.jomof
  artifactId: re2
  version: 17.3.1-rev18
license:
  name: "BSD 3-Clause"
  url: "https://raw.githubusercontent.com/google/re2/master/LICENSE"
interfaces:
  headers:
    file: re2-headers.zip
    sha256: 773c6c106e68e1494b8b73f46db4e98dfda901a18c2fbcc5ec762f9f27b94094
    size: 43964
    include: include
    requires: [cxx_deleted_functions, cxx_variadic_templates]
android:
  archives:
  - file: re2-android-21-armeabi.zip
    sha256: 162e6fbe36b1b097c815a629d7c94492b491cf68f8cb2d079c6c97e23fd9785d
    size: 3063095
    platform: 21
    abi: armeabi
    libs: [libre2.a]
  - file: re2-android-21-armeabi-v7a.zip
    sha256: 18e88a1c4e4450346f04d20315a4b251e1181cba7424178a4bd975673658f942
    size: 3027841
    platform: 21
    abi: armeabi-v7a
    libs: [libre2.a]
    ...
example: |
  #include <re2/re2.h>
  void test() {
    RE2::FullMatch("hello", "h.*o");
  }
```

### coordinate section
CDep uses a three part coordinate to identify the package in a unique way. Once you have published a package with a particular coordinate then you should not modify it later.
```
coordinate:
  groupId: com.github.jomof
  artifactId: re2
  version: 17.3.1-rev18
```
* 'groupId' identifies the author of the package. It also tells CDep to search on Github for the package.
* 'artifactId' names the library.
* 'version' is the package. In this case, 17.3.1 is the version of RE2 that is in the package. The 'rev18' part indicates that this is the 18th attempt at packaging RE2 for CDep.

### license section
There are two fields here--'name' and 'url'. These are freeform text and CDep mostly ignores them. It is a best practice to indicate the license of your library so users can know what they can do with it (commercial use, etc).

### interfaces section
This is the source-code entry point into your library. This is where header files (.h, .hpp, etc.) should go.
```
interfaces:
  headers:
    file: re2-headers.zip
    sha256: 773c6c106e68e1494b8b73f46db4e98dfda901a18c2fbcc5ec762f9f27b94094
    size: 43964
    include: include
    requires: [cxx_deleted_functions, cxx_variadic_templates]
```
* The 'file' field indicates the name of the archive that CDep should download when needed. This file must be right next to cdep-manifest.yml.
* 'sha256' is the the hash of re2-headers.zip. CDep will not consume the package if this hash doesn't match the download file.
* 'size' is the expected size of re2-headers.zip.
* The field 'include' is the name of the relative folder within the .zip that holds the header files.
* The field 'requires' indicates which C++ language features the header files require. A list of possible values can be found here: https://cmake.org/cmake/help/v3.9/prop_gbl/CMAKE_CXX_KNOWN_FEATURES.html#prop_gbl:CMAKE_CXX_KNOWN_FEATURES.


### android section
This section contains the android static libraries that are referenced by the package. There are usually many of these in the manifest. Archives are broken down in a granular way so that CDep can download only what is needed.
```
android:
  archives:
  - file: re2-android-21-armeabi.zip
    sha256: 162e6fbe36b1b097c815a629d7c94492b491cf68f8cb2d079c6c97e23fd9785d
    size: 3063095
    platform: 21
    abi: armeabi
    libs: [libre2.a]
```
* The 'file' field indicates the name of the archive that CDep should download when needed. This file must be right next to cdep-manifest.yml.
* 'sha256' is the the hash of re2-android-21-armeabi.zip. CDep will not consume the package if this hash doesn't match the download file.
* 'size' is the expected size of re2-android-21-armeabi.zip.
* 'platform' is the Android platform that the library targets.
* 'abi' is the Android ABI that the library targets.
* 'libs' is a list of the libraries that is contained within re2-android-21-armeabi.zip. Often there is only one, but multiple libs is allowed.

### example section
This section contains a working example of how the user should get started using the code.

```
example: |
  #include <re2/re2.h>
  void test() {
    RE2::FullMatch("hello", "h.*o");
  }
```

The purpose of this is to help the user get started and also so that tools can automatically prove that the package can link.


## Optional
### Dependencies
This section contains any external dependencies the package might need in order to link.

```
dependencies:
- compile: "com.github.gpx1000:zlib:1.2.11"
  sha256: cbdb96db3b4e07f41cbbe0407863b6ae3cecfaf34821b6b252c816791d70196a
- compile: "com.github.gpx1000:boringssl:0.0.2"
  sha256: f83f6197d8191c06fdb99d7bb7c3d7e1dad5915d7fa1d73e7cdb2aed938d0985
```

The above is taken from curl's cdep manifest, which can be found [here](https://github.com/gpx1000/curl/releases/download/7.56.2/cdep-manifest.yml).  In this example, Curl was minimally built with zlib and boringssl as dependencies.

