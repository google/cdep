# CDep Package Author's Guide
This tutorial will show how to author your own CDep package and host it on Github. We'll package BoringSSL so that it can be used by CDep. When you're done you'll have a package that any CDep user can reference in their project.

Prerequisites: Have or create a Github account and read [Anatomy of a CDep Package](https://github.com/google/cdep/blob/master/doc/anatomy.md)

This tutorial runs on Ubuntu and it will require some modifications to work on MacOS or Windows.

## Step 1 -- Fork BoringSSL on Github
Navigate to https://github.com/google/boringssl and click the Fork button.
After the fork is complete, go to settings and rename the repository to "boringssl-tutorial". Now clone your "boringssl-tutorial" repository onto your local machine. 

## Step 2 -- Install CMake
This step installs CMake into your "boringssl-tutorial" folder. You can skip this step if you already have CMake 3.7.1 or later, but you will need to modifiy subsequent steps to reference that CMake instead. If you are not using Linux go to the [CMake downloads page](https://cmake.org/download/) to get the zip file URL for your OS. 
```
cd boringssl-tutorial
wget https://cmake.org/files/v3.8/cmake-3.8.1-Linux-x86_64.tar.gz
tar xvzf cmake-3.8.1-Linux-x86_64.tar.gz
```

## Step 3 -- Install Android NDK
This step installs a recent Android NDK. This has the libraries and compilers needed to target Android with C++.

Note: The latest NDK downloads are available on the [NDK website](https://developer.android.com/ndk/downloads/index.html).

```
wget https://dl.google.com/android/repository/android-ndk-r14b-linux-x86_64.zip
unzip android-ndk-r14b-linux-x86_64.zip 
```

## Step 4 -- Build BoringSSL for a single ABI
This step builds BoringSSL for armeabi. You can repeat this step for other ABIs, but that's not necessary for this tutorial.
```
cmake-3.8.1-Linux-x86_64/bin/cmake \
  -H. \
  -Bbuild/armeabi \
  -DCMAKE_BUILD_TYPE=RelWithDebInfo \
  -DCMAKE_ANDROID_NDK_TOOLCHAIN_VERSION=clang \
  -DCMAKE_SYSTEM_NAME=Android \
  -DCMAKE_SYSTEM_VERSION=16 \
  -DCMAKE_ANDROID_STL_TYPE=c++_static \
  -DCMAKE_ANDROID_NDK=`pwd`/android-ndk-r14b \
  -DCMAKE_ANDROID_ARCH_ABI=armeabi
cmake-3.8.1-Linux-x86_64/bin/cmake --build build/armeabi
```
At this point CMake should have built a couple of static libraries.
```
build/armeabi/ssl/libssl.a
build/armeabi/crypto/libcrypto.a
```
If these files aren't there then something went wrong in the steps above.

A few notes while we're here. 

1. CDep packages currently only support static libraries (.a files). For an app, you will need a shared library (.so file), but shipping a static library allows the user to decide whether to statically or dynamically link to BoringSSL. This also saves complexity in the CDep tool and reduces the size of package.

2. As a best practice, it is recommended that the libraries in the archive be optimized and have symbols (CMAKE_BUILD_TYPE=RelWithDebInfo). The Android gradle plugin will strip symbols so they don't end up in the APK.

## Step 5 -- Prepare a staging folder for the libraries
The staging folder holds the library files in a convenient form for zipping.
```
mkdir -p staging/lib/armeabi
cp build/armeabi/ssl/libssl.a staging/lib/armeabi
cp build/armeabi/crypto/libcrypto.a staging/lib/armeabi
```
## Step 6 -- Create a zip file with the two libraries
The upload folder contains the files we will eventually upload to Github as a Release.
```
mkdir upload
pushd staging
zip -r ../upload/boringssl-tutorial-armeabi.zip .
popd
```
NB: If your package has generated headers that are specific to the abi, then the process here needs to follow this simple modification.
```
mkdir upload
cp -Rp build/gen/armeabi/include staging/include
pushd staging
zip -r ../upload/boringssl-tutorial-armeabi.zip .
popd
```

## Step 7 -- Create another zip file with include files
```
zip -r upload/boringssl-tutorial-headers.zip include/
``` 
NB: In the event your package contains generated headers then they will need to be added during step 6.  The include files that go here are for includes that are common across all abi types.

## Step 8 -- Create a manifest file with coordinate for this package
This creates a file called cdep-manifest.yml that describes the package. You can also do this step in a text editor if you like.

Note that *yourname* below should be replaced by your Github user name. Also note that artifactId must match the name of the Github repo from the fork in Step 1. If it doesn't CDep won't be able to locate it later.

```
printf "%s\r\n" "coordinate:" > upload/cdep-manifest.yml
printf "  %s\r\n" "groupId: com.github.*yourname*" >> upload/cdep-manifest.yml
printf "  %s\r\n" "artifactId: boringssl-tutorial" >> upload/cdep-manifest.yml
printf "  %s\r\n" "version: 0.0.0"  >> upload/cdep-manifest.yml
```
BoringSSL doesn't have version numbers so we just use '0.0.0' as a starting version.

As a best practice it is strongly recommended that once you publish a package with a particular version you don't change it. In this tutorial publishing happens in step 15.

There are a few reasons packages should be immutable once they're published.
1. Most importantly, if you change a package after it is published then you can break user's build that depend on it.
2. CDep won't try to redownload the package if it already has a package with that name and version in the cache.
3. CDep stores the sha256 hash of the manifest and will refuse to use the package if the manifest has changed.

## Step 9 -- Add BoringSSL license information to the manifest
A CDep license section can have a 'name' or a 'url' or both. BoringSSL has a rather complicated license that is difficult to name so in this case we'll just use url to the LICENSE file in the original project.

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

Last, we need add the size of the zip file to the manifest.

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
Specify the ABI that the libraries target.
```
printf "    %s\r\n" "  abi: armeabi" >> upload/cdep-manifest.yml
```
Specify the Android platform that the libraries are built for.
```
printf "    %s\r\n" "  platform: 16" >> upload/cdep-manifest.yml
```
Specify the libraries that the archive holds.
```
printf "      libs: [libssl.a, libcrypto.a]\r\n" >> upload/cdep-manifest.yml
```

NB: If your project has generated headers that are abi specific.  Add the path to them here:
```
printf "      include: include\r\n"
```

## Step 12 -- Add an example to the manifest
CDep requires the manifest contain a small example of how to use the library. This is so that tools and the end-user can prove that the library links using only information contained in the package.
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

## Step 13 -- Test package integrity
Add the `upload` folder to your github repository. 

    git add -- upload
    git commit -m "Adding CDep package"
    git push

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

## Step 14 -- Prove the package can link
Now we'll use the example code from step 12 to prove that the package can be built into a final .so.
First, generate example projects.

```
printf "%s\r\n" "builders: [cmake, cmakeExamples, ndk-build]" > cdep.yml
printf "%s\r\n" "dependencies:" >> cdep.yml
printf "%s\r\n" "- compile: upload/cdep-manifest.yml" >> cdep.yml
./cdep
```

If that worked you should see a message like this.

```
Generating .cdep/modules/cdep-dependencies-config.cmake
Generating .cdep/examples/cmake/com.github.jomof/boringssl-tutorial/0.0.0/boringssl-tutorial.cpp
Generating .cdep/examples/cmake/com.github.jomof/boringssl-tutorial/0.0.0/CMakeLists.txt
Generating .cdep/examples/cmake/CMakeLists.txt
Generating .cdep/modules/ndk-build/cdep-dependencies/Android.mk
```

Now build the example project.

```
cmake-3.8.1-Linux-x86_64/bin/cmake \
  -H.cdep/examples/cmake/ \
  -Bbuild/examples \
  -DCMAKE_BUILD_TYPE=RelWithDebInfo \
  -DCMAKE_ANDROID_NDK_TOOLCHAIN_VERSION=clang \
  -DCMAKE_SYSTEM_NAME=Android \
  -DCMAKE_SYSTEM_VERSION=16 \
  -DCMAKE_ANDROID_STL_TYPE=c++_static \
  -DCMAKE_ANDROID_NDK=`pwd`/android-ndk-r14b \
  -DCMAKE_ANDROID_ARCH_ABI=armeabi
cmake-3.8.1-Linux-x86_64/bin/cmake --build build/examples
```
NB: To compile on OSX, use the cmake that comes with the Android SDK like this:

```
$ANDROID_HOME/cmake/3.6.3155560/bin/cmake \
   -H.cdep/examples/cmake/ \
   -Bbuild/examples \
   -DCMAKE_BUILD_TYPE=RelWithDebInfo \
   -DCMAKE_ANDROID_NDK_TOOLCHAIN_VERSION=clang \
   -DCMAKE_SYSTEM_NAME=Android \
   -DCMAKE_SYSTEM_VERSION=16 \
   -DANDROID_PLATFORM=android-21 \
   -DANDROID_ABI=armeabi \
   -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
   -DCMAKE_ANDROID_STL_TYPE=c++_static \
   -DCMAKE_ANDROID_ARCH_ABI=armeabi
$ANDROID_HOME/cmake/3.6.3155560/bin/cmake --build build/examples
```

If that builds successfully then you can be pretty confident you have a working package.

Now, let's release it on Github so other people can use it.

## Step 14 -- Create a Github release to hold this package
Go to https://github.com/*yourname*/boringssl-tutorial/releases and press 'Create new release'. Tag the release as '0.0.0'. This has to match the version from Step 8.

## Step 15 -- Create a Github personal access token 
This token is required to upload files from the local filesystem to Github. Go to https://github.com/settings/tokens and press 'Generate new token'. Name the token something like 'BoringSSL Tutorial'. Click the 'public_repo' checkbox. Then 'Generate token'.

IMPORTANT: Keep this token a secret. If someone has it then they can alter your projects on Github.

Press the 'Copy Token' button to copy the token to the clipboard.

Now, go back to the bash command-prompt and set an environment variable.

```
export GITHUB_TOKEN=<paste your token here>
```

## Step 15 -- Upload local files to Github
Get a tool that will help us upload. Again, if you're not using Linux go to [the releases page](https://github.com/aktau/github-release/releases) to choose the correct version for your OS.

```
wget https://github.com/aktau/github-release/releases/download/v0.7.2/linux-amd64-github-release.tar.bz2 -O linux-amd64-github-release.tar.bz2
tar xvjf linux-amd64-github-release.tar.bz2
```

Do the actual upload to Github.

```
bin/linux/amd64/github-release upload --user *yourname* --repo boringssl-tutorial --tag 0.0.0 \
  --file upload/cdep-manifest.yml --name cdep-manifest.yml
bin/linux/amd64/github-release upload --user *yourname* --repo boringssl-tutorial --tag 0.0.0 \
  --file upload/boringssl-tutorial-headers.zip --name boringssl-tutorial-headers.zip
bin/linux/amd64/github-release upload --user *yourname* --repo boringssl-tutorial --tag 0.0.0 \
  --file upload/boringssl-tutorial-armeabi.zip --name boringssl-tutorial-armeabi.zip
```

## Step 16 -- Fetch the package using coordinate
At this point, assuming your Github repo is public, the package should be available to any CDep user.
Let's prove this by fetching by coordinate.
```
./cdep fetch com.github.*yourname*:boringssl-tutorial:0.0.0
```
You should see something like this.
```
Downloading https://github.com/*yourname*/boringssl-tutorial/releases/download/0.0.0/cdep-manifest.yml
Fetch complete
```


## Congratulations!
At this point you have created and published a fully functioning CDep package. 

Now would be a good time to delete the personal access token from step 15.

