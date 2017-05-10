[![Build Status](https://travis-ci.org/jomof/cdep.svg?branch=master)](https://travis-ci.org/jomof/cdep)

# CDep
CDep is a decentralized native package dependency manager with a focus on Android. 
- Runs on Windows, Linux, and MacOS 
- Works with Android Studio, CMake, and ndk-build

Anyone can author a package and there is a growing list of useful packages like [Freetype 2.0](https://github.com/jomof/freetype), [SDL](https://github.com/jomof/sdl), [STB](https://github.com/jomof/stb), [RE2 Regular Expressions](https://github.com/jomof/re2), [Firebase](https://github.com/jomof/firebase), [MathFu](https://github.com/jomof/mathfu), [Vectorial](https://github.com/jomof/vectorial), [Boost](https://github.com/jomof/boost), [Yaml-CPP](https://github.com/jomof/yaml-cpp), [SQLite](https://github.com/jomof/sqlite).
   
CDep comes from members of the Android Studio team and is not an official Google product. It is a work in progress and subject to change over time. Backward compatibility with existing packages will be maintained.
   
## Get started with CDep
Here are some things you can do to get started with CDep.
* [Add CDep dependencies to an existing Android Studio CMake project](https://github.com/google/cdep/blob/master/doc/android-studio.md)
* [Author a new CDep package and host it on Github](https://github.com/google/cdep/blob/master/doc/authoring.md)
* [Learn about the structure of CDep packages](https://github.com/google/cdep/blob/master/doc/anatomy.md)
* [Contribute to CDep](https://github.com/google/cdep/blob/master/CONTRIBUTING.md)

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
     
This tell CDeps that this project depends on SQLite.

Next, run cdep command to download SQLite and build CMake modules for it.

    $ ./cdep
    Generating .cdep/modules/cdep-dependencies-config.cmake

## Getting started on Windows
Get started with CDep on Windows.

     > git clone https://github.com/jomof/cdep-redist.git  
     > cd my-project
     > ..\cdep-redist\cdep wrapper
     
After this, the instructions are the same as Linux and Mac.

