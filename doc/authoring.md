# CDep Package Author's Guide
This guide will show how to author your own CDep package and host it on github. When you're done you'll have a package that any CDep user can reference in their project.

Prequisites: Github account, read [Anatomy of a CDep Package](https://github.com/google/cdep/blob/master/doc/anatomy.md)

In this tutorial we'll author an Open SSL package and host it on Github. This tutorial runs on Ubuntu but it should work with simple modifications on MacOS and Windows.

## Step 1 -- Install CMake
This step installs CMake in a new folder. You can skip this step if you already have CMake 3.7.1 or later but you will need to modifiy steps below to reference that CMake instead.
```
mkdir openssl-package
cd openssl-package
wget https://cmake.org/files/v3.8/cmake-3.8.1-Linux-x86_64.tar.gz
tar xvzf cmake-3.8.1-Linux-x86_64.tar.gz
```

## Step 2 -- Install Android NDK
This step installs a recent Android NDK. This has the libraries and compilers needed to target Android with C++.
```
wget https://dl.google.com/android/repository/android-ndk-r14-linux-x86_64.zip
unzip android-ndk-r14-linux-x86_64.zip 
```

## Step 3 -- Get the OpenSSL Source Code
This tutorial uses the source code CMake project found on [LaunchPad](https://launchpad.net/openssl-cmake/1.0.1e/1.0.1e-1). I'd like to thank the author Brian Sidebotham for making this available.
```
wget https://launchpad.net/openssl-cmake/1.0.1e/1.0.1e-1/+download/openssl-cmake-1.0.1e-src.tar.gz
tar xvzf openssl-cmake-1.0.1e-src.tar.gz
```

## Step 4 -- Build OpenSSL for a Single ABI
```
mkdir -p build/armeabi
cd build/armeabi
../../cmake-3.8.1-Linux-x86_64/bin/cmake --install \
  -H../../openssl-cmake-1.0.1e-src \
  -B. \
  -DCMAKE_ANDROID_NDK_TOOLCHAIN_DEBUG=1 \
  -DCMAKE_ANDROID_NDK_TOOLCHAIN_VERSION=clang \
  -DCMAKE_SYSTEM_NAME=Android \
  -DCMAKE_SYSTEM_VERSION=21 \
  -DCMAKE_ANDROID_STL_TYPE=c++_static \
  -DCMAKE_ANDROID_NDK=`pwd`/../../android-ndk-r14 \
  -DCMAKE_ANDROID_ARCH_ABI=armeabi
```

