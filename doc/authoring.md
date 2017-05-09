# CDep Package Author's Guide
This guide will show how to author your own CDep package and host it on github. When you're done you'll have a package that any CDep user can reference in their project.

Prequisites: Have or create a Github account and read [Anatomy of a CDep Package](https://github.com/google/cdep/blob/master/doc/anatomy.md)

In this tutorial we'll author a BoringSSL package and host it on Github. This tutorial runs on Ubuntu and it will require some modifications to work on MacOS or Windows.

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
If these files aren't there then something went wrong in the steps above.

## Step 5 -- Prepare a staging folder for the libraries
The staging folder holds the library files in a convenient form for zipping 
```
mkdir -p staging/lib/armeabi
cp build/armeabi/ssl/libssl.a staging/lib/armeabi
cp build/armeabi/crypto/libcrypto.a staging/lib/armeabi
```
## Step 6 -- Create a zip file with the two libraries
The upload folder contains the files we will eventually upload to Github as a Release.
```
mkdir upload
cp build/armeabi/ssl/libssl.a staging/lib/armeabi
cp build/armeabi/crypto/crypto.a staging/lib/armeabi
pushd staging
zip -r ../upload/boringssl-tutorial-armeabi.zip .
popd
```

## Step 7 -- Create another zip file with include files
```
zip -r upload/boringssl-tutorial-headers.zip include/
```

## Step 8 -- Create a manifest file with coordinate for this package
This creates a file called cdep-manifest.yml that describes the package. You can also do this step in a text editor if you like.

Note that my name (jomof) below should be replaced by your Github user name. Also note that artifactId must match the name of the Github repo from the fork in Step 1. If it doesn't CDep won't be able to locate it later.

```
printf "%s\r\n" "coordinate:" > upload/cdep-manifest.yml
printf "  %s\r\n" "groupId: com.github.jomof" >> upload/cdep-manifest.yml
printf "  %s\r\n" "artifactId: boringssl-tutorial" >> upload/cdep-manifest.yml
printf "  %s\r\n" "version: 0.0.0"  >> upload/cdep-manifest.yml
```

BoringSSL doesn't have version numbers so we just use '0.0.0' as a starting version.


## Step 9 -- Add BoringSSL license information to the manifest
BoringSSL has a rather complicated license to just reference the LICENSE file in the original project so end users have access to it.

```
printf "%s\r\n" "license:" >> upload/cdep-manifest.yml
printf "  %s\r\n" "url: https://raw.githubusercontent.com/google/boringssl/master/LICENSE" \
  >> upload/cdep-manifest.yml
```

## Step 10 -- Add header file archive to the manifest
This step adds information to the manifest about the header files archive.

```
printf "%s\r\n" "interfaces:" >> upload/cdep-manifest.yml
printf "  %s\r\n" "headers:" >> upload/cdep-manifest.yml
printf "    %s\r\n" "file: boringssl-tutorial-headers.zip" >> upload/cdep-manifest.yml
printf "    %s\r\n" "include: include" >> upload/cdep-manifest.yml
printf "    sha256: " >> upload/cdep-manifest.yml
```

At this point, we need to compute the sha256 of the header zip and add it to the manifest.

```
shasum -a 256 upload/boringssl-tutorial-headers.zip | awk '{print $1}' >> upload/cdep-manifest.yml
```

Last, we need add the size of the zip file to the manifest

```
printf "    size: " >> upload/cdep-manifest.yml
ls -l upload/boringssl-tutorial-headers.zip | awk '{print $5}' >> upload/cdep-manifest.yml
```

## Step 11 -- Add library file archive to the manifest
This step is similar to step 10. It adds the zipped libraries archive along with size and sha256.
```
printf "%s\r\n" "android:" >> upload/cdep-manifest.yml
printf "  %s\r\n" "archives:" >> upload/cdep-manifest.yml
printf "    %s\r\n" "- file: boringssl-tutorial-armeabi.zip" >> upload/cdep-manifest.yml
printf "      sha256: " >> upload/cdep-manifest.yml
shasum -a 256 upload/boringssl-tutorial-armeabi.zip | awk '{print $1}' >> upload/cdep-manifest.yml
printf "      size: " >> upload/cdep-manifest.yml
ls -l upload/boringssl-tutorial-armeabi.zip | awk '{print $5}' >> upload/cdep-manifest.yml
```
Specify the ABI that this libraries target.
```
printf "    %s\r\n" "  abi: armeabi" >> upload/cdep-manifest.yml
```
Specify the Android platform that the libraries are built for
```
printf "    %s\r\n" "  platform: 16" >> upload/cdep-manifest.yml
```
Specify the libraries that the archive holds.
```
printf "      libs: [libssl.a, libcrypto.a]\r\n" >> upload/cdep-manifest.yml
```

## Step 12 -- Add an example to the manifest
CDep requires the manifest contain a small example of how to use the library. This is so that tools and the end-user can prove that the library links using only information contain in the package.
```
printf "%s\r\n" "example: |" >> upload/cdep-manifest.yml
printf "%s\r\n" "  #include <openssl/bio.h>" >> upload/cdep-manifest.yml
printf "%s\r\n" "  #include <openssl/ssl.h>" >> upload/cdep-manifest.yml
printf "%s\r\n" "  #include <openssl/err.h>" >> upload/cdep-manifest.yml
  
printf "%s\r\n" "  void example() {" >> upload/cdep-manifest.yml
printf "%s\r\n" "    SSL_load_error_strings();" >> upload/cdep-manifest.yml
printf "%s\r\n" "    ERR_load_BIO_strings();" >> upload/cdep-manifest.yml
printf "%s\r\n" "    OpenSSL_add_all_algorithms();" >> upload/cdep-manifest.yml
printf "%s\r\n" "  }" >> upload/cdep-manifest.yml
```

## Step 13 -- Test the package
At this point, there should be a valid CDep package in the upload folder. Use CDep to validate this.

First, install CDep in the current folder.
```
git clone https://github.com/jomof/cdep-redist.git
cdep-redist/cdep wrapper
```

Now use CDep to fetch the package from the upload folder.
```
./cdep fetch upload/cdep-manifest.yml
```
If the manifest is well formed then you should see a message like this.
```
Downloading boringssl-tutorial/upload/boringssl-tutorial-armeabi.zip
Fetch complete
```





