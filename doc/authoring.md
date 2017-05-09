# CDep Package Author's Guide
This guide will show how to author your own CDep package and host it on github. When you're done you'll have a package that any CDep user can reference in their project.

Prequisites: Github account, read [Anatomy of a CDep Package](https://github.com/google/cdep/blob/master/doc/anatomy.md)

In this tutorial we'll author a BoringSSL package and host it on Github. This tutorial runs on Ubuntu but it should work with simple modifications on MacOS and Windows.

## Step 1 -- Fork BoringSSL on Github
Navigate to https://github.com/google/boringssl and click the Fork button.
After the fork is complete, go to settings and rename the repository to "boringssl-tutorial".

## Step 2 -- Install CMake
This step installs CMake in a new folder. You can skip this step if you already have CMake 3.7.1 or later but you will need to modifiy subsequent steps to reference that CMake instead.
```
cd openssl-tutorial
wget https://cmake.org/files/v3.8/cmake-3.8.1-Linux-x86_64.tar.gz
tar xvzf cmake-3.8.1-Linux-x86_64.tar.gz
```

## Step 3 -- Install Android NDK
This step installs a recent Android NDK. This has the libraries and compilers needed to target Android with C++.
```
wget https://dl.google.com/android/repository/android-ndk-r14-linux-x86_64.zip
unzip android-ndk-r14-linux-x86_64.zip 
```

## Step 4 -- Build BoringSSL for a single ABI
This step builds BoringSSL for a armeabi. You can repeat this step for other ABIs to get them but that's not necessary for this demo.
```
cmake-3.8.1-Linux-x86_64/bin/cmake --install \
  -H. \
  -Bbuild/armeabi \
  -DCMAKE_ANDROID_NDK_TOOLCHAIN_VERSION=clang \
  -DCMAKE_SYSTEM_NAME=Android \
  -DCMAKE_SYSTEM_VERSION=16 \
  -DCMAKE_ANDROID_STL_TYPE=c++_static \
  -DCMAKE_ANDROID_NDK=`pwd`/android-ndk-r14 \
  -DCMAKE_ANDROID_ARCH_ABI=armeabi
cmake-3.8.1-Linux-x86_64/bin/cmake --build build/armeabi
```
At this point CMake should have built a couple of static libraries.
```
build/armeabi/ssl/libssl.a
build/armeabi/crypto/libcrypto.a
```


## Step 5 -- Prepare a staging folder to place the manifest and archive files
This folder is going to hold the files that we will eventually host on github as a CDep package
```
mkdir -p staging/armeabi
```
## Step 6 -- Prepare a stag












