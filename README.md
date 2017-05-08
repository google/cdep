[![Build Status](https://travis-ci.org/jomof/cdep.svg?branch=master)](https://travis-ci.org/jomof/cdep)

# CDep
CDep is a decentralized native package dependency manager with a focus on Android. Runs on Windows, Linux, and MacOS. Anyone can author a package and there is a growing list of useful packages like [Freetype 2.0](https://github.com/jomof/freetype), [SDL](https://github.com/jomof/sdl), [STB](https://github.com/jomof/stb), [re2 Regular Expressions](https://github.com/jomof/re2), [Firebase](https://github.com/jomof/firebase), [MathFu](https://github.com/jomof/mathfu), [Vectorial](https://github.com/jomof/vectorial), [Boost](https://github.com/jomof/boost), [Yaml-CPP](https://github.com/jomof/yaml-cpp).
   
This is not an official Google product. Also, it is a work in progress and subject to change.
   
## Linux and Mac
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

## Windows
Get started with CDep on Windows.

     > git clone https://github.com/jomof/cdep-redist.git  
     > cd my-project
     > ..\cdep-redist\cdep wrapper
     
After this, the instructions are the same as Linux and Mac.
    
## Boost [![Build Status](https://travis-ci.org/jomof/boost.svg?branch=master)](https://github.com/jomof/boost)
A CDep packaging of Boost (header only).

## SQLite [![Build Status](https://travis-ci.org/jomof/sqlite.svg?branch=master)](https://travis-ci.org/jomof/sqlite) [![CDep Status](https://cdep-io.github.io/com.github.jomof/sqlite/latest/latest.svg)](https://github.com/jomof/sqlite/releases/latest)

A CDep packaging of SQLite

## yaml-cpp [![Build Status](https://travis-ci.org/jomof/yaml-cpp.svg?branch=master)](https://github.com/jomof/yaml-cpp) [![CDep Status](https://cdep-io.github.io/com.github.jomof/yaml-cpp/latest/latest.svg)](https://github.com/jomof/yaml-cpp/releases/latest)
A CDep packaging of yaml-cpp

## Hello Boost [![Build Status](https://travis-ci.org/jomof/hello-boost.svg?branch=master)](https://github.com/jomof/hello-boost)
Helper tools to get CDep onto your system.

## CMakeify [![Build Status](https://travis-ci.org/jomof/cmakeify.svg?branch=master)](https://github.com/jomof/cmakeify)
Tools for building and deploying CDep packages for Android.

## Bootstrap [![Build Status](https://travis-ci.org/jomof/bootstrap.svg?branch=master)](https://github.com/jomof/bootstrap)
Helper tools to get CDep onto your system.

