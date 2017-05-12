[![Build Status](https://travis-ci.org/google/cdep.svg?branch=master)](https://travis-ci.org/google/cdep)
[![Gitter](https://badges.gitter.im/google/cdep.svg)](https://gitter.im/google/cdep)

# CDep
CDep is a decentralized native package dependency manager with a focus on Android. 
- Runs on Windows, Linux, and MacOS 
- Works with Android Studio, CMake, and ndk-build. CMake support is for both Android Studio version of CMake and the built-in Android support that was added to CMake in version 3.7.1.

Anyone can author a package and there is a growing list of useful packages like [Freetype 2.0](https://github.com/jomof/freetype), [SDL](https://github.com/jomof/sdl), [STB](https://github.com/jomof/stb), [RE2 Regular Expressions](https://github.com/jomof/re2), [Firebase](https://github.com/jomof/firebase), [MathFu](https://github.com/jomof/mathfu), [Vectorial](https://github.com/jomof/vectorial), [Boost](https://github.com/jomof/boost), [Yaml-CPP](https://github.com/jomof/yaml-cpp), [SQLite](https://github.com/jomof/sqlite).
   
CDep comes from members of the Android Studio team and is not an official Google product. It is a work in progress and subject to change over time. Backward compatibility with existing packages will be maintained.
   
## Get started with CDep
Here are some things you can do to get started with CDep.
* [Add CDep dependencies to an existing Android Studio CMake project](https://github.com/google/cdep/blob/master/doc/android-studio-cmake.md)
* [Author a new CDep package and host it on Github](https://github.com/google/cdep/blob/master/doc/authoring.md)
* [Learn about the structure of CDep packages](https://github.com/google/cdep/blob/master/doc/anatomy.md)
* [Contribute to CDep](https://github.com/google/cdep/blob/master/CONTRIBUTING.md)
* [Request a new package by opening an issue](https://github.com/google/cdep/issues/new)

## Getting started on Linux and Mac
Get started with CDep on Linux and Mac.
 
     $ git clone https://github.com/jomof/cdep-redist.git  
     $ cd my-project
     $ ../cdep-redist/cdep wrapper

This created a four files in your local folder:

     cdep   
     cdep.bat
     cdep.yml
     bootstrap\wrapper\bootstrap.jar

These files are meant to be checked into source control.  
  
Now edit cdep.yml file to add a line like this.

     dependencies:
     - compile: com.github.jomof:sqlite:3.16.2-rev51
     
This tells CDep that this project depends on SQLite.

Next, run cdep command to download SQLite and build CMake modules for it.

    $ ./cdep
    Generating .cdep/modules/cdep-dependencies-config.cmake
    
Now, if you have a CMake project then open CMakeLists.txt and add the following code at the end of the file.
```
find_package(cdep-dependencies REQUIRED)
add_all_cdep_dependencies(native-lib)
```
This tells CMake to locate the module glue file and then to add all the dependencies in that file to the native-lib target. You'll need to change 'native-lib' to your own target name. 

When you call CMake to generate the project you'll need to tell it where to find the glue modules. So something like,
```
cmake -Dcdep-dependencies_DIR=.cdep/modules
```
For more details on setting up CMake build with CDep visit [Add CDep dependencies to an existing Android Studio CMake project](https://github.com/google/cdep/blob/master/doc/android-studio-cmake.md).

## Getting started on Windows
Get started with CDep on Windows.

     > git clone https://github.com/jomof/cdep-redist.git  
     > cd my-project
     > ..\cdep-redist\cdep wrapper
     
After this, the instructions are the same as Linux and Mac.

