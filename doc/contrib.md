## Build Requirements:
* OSX or Linux.  Windows support requires travis_cdep script rewrite (planned to turn this into a cross platform python script).
* Android Studio
 1. Alternatively can install gradle, ninja, and cmake
    * Rest of document assumes SDK path exists at the $ANDROID_SDK environment variable.
* NDK
  * Rest of document assumes NDK path exists at the $ANDROID_NDK environment variable.
* optional for iOS builds:
  * XCode
  * XCode command line tools

## How to build a local CDep
* With command line:
  1. ./gradlew assemble
  2. ./gradlew check
  3. ./gradlew packageArtifacts

* With Android Studio
  1. build project
  2. gradle packageArtifacts task

## How to run smoke-test
* pushd smoke-test
* edit the cdep.yml as necessary for any test.
* ../travis_cdep

### Build with CMake
* $ANDROID_SDK/cmake/3.6.4111459/bin/cmake -H.cdep/examples/cmake/ -Bbuild/examples -DCMAKE_BUILD_TYPE=RelWithDebInfo -DCMAKE_ANDROID_NDK_TOOLCHAIN_VERSION=clang -DCMAKE_SYSTEM_NAME=Android -DANDROID_SYSTEM_VERSION=21 -DANDROID_PLATFORM=android-21 -DANDROID_ABI=arm64-v8a -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake -DCMAKE_ANDROID_STL_TYPE=c++_static -DCMAKE_ANDROID_NDK=$ANDROID_NDK -DCMAKE_ANDROID_ARCH_ABI=arm64-v8a
   * NB: CMake can be used with any cmake binary above 3.6.  I prefer to test with the Android studio version of CMake to accurately replicate what devs will experience with Android Studio.

### Build with ndk-build
* pushd .cdep/smoke-test/.cdep/examples/ndk-build/jni
* $ANDROID_NDK/ndk-build

