# CDep Commandline
These are flags and commands that are available from the cdep command line tool.

## Main mode: no flags or commands
If you just run cdep with no flags or commands then it will read the cdep.yml for the current directory and generate
cmake and ndk-build glue code for connecting those packages to your build.
In this case, cdep will not download any package archives. Download is deferred until the build executes. The reason
for this division of labor is that there may be many archives in the package that aren't needed.
```
$ ./cdep
Downloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/cdep-manifest.yml
Generating .cdep/modules/cdep-dependencies-config.cmake
```

## Download command
In some scenarios you may not want to download package archives at build time. In this case, you can predownload all
archives using the download command.
```
$ ./cdep download
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/cdep-manifest.yml
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-cxx-platform-21.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-cxx-platform-12.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-gnustl-platform-21.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-gnustl-platform-12.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-stlport-platform-21.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-stlport-platform-12.zip
Generating .cdep/modules/cdep-dependencies-config.cmake

```

## Redownload command
The download command will not download packages that already exist locally. It shouldn't normally be needed but if you
need to redownload all packages then you can use the redownload command.
```
$ ./cdep redownload
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/cdep-manifest.yml
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-cxx-platform-21.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-cxx-platform-12.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-gnustl-platform-21.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-gnustl-platform-12.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-stlport-platform-21.zip
Redownloading https://github.com/jomof/sdl2/releases/download/2.0.5-rev19/sdl2-android-stlport-platform-12.zip
Generating .cdep/modules/cdep-dependencies-config.cmake
```

## Wrapper command
The wrapper command is used to install cdep into the current project directory.
```
$ mkdir my-project
$ cd my-project
$ ../cdep-redist/cdep wrapper
Installing cdep wrapper from /usr/local/google/home/jomof/projects/cdep-redist
Installing ./cdep.bat
Installing ./cdep
Installing ./bootstrap/wrapper/bootstrap.jar
Installing ./cdep.yml

```
The files installed by the wrapper command are very small and are meant to be checked in to source control.
The wrapper command must be run from a different directory than the one the has the original cdep. Otherwise, it is an
error.
```
$ ./cdep wrapper
FAILURE (5ffdb7c): Install source and destination are the same
1 errors, exiting
```

## Show command
The show command is used to show information about CDep status. The show command requires a sub-command.

### Show folders command
The show folders command will show the locations of folders that CDep uses to store packages and generated cmake and
ndk-build glue.
```
$ ./cdep show folders
Downloads: /usr/local/google/home/jomof/.cdep/downloads
Exploded: /usr/local/google/home/jomof/.cdep/exploded
Modules: /usr/local/google/home/jomof/projects/my-project/./.cdep/modules
```

### Show manifest command
The show manifest command will read and interpret the local cdep.yml file and the print it.
```
$ ./cdep show manifest
builders: [cmake]
dependencies:
- compile: com.github.jomof:boost:1.0.63-rev10
```
If there are errors in the file then information about the error will be shown.

## --version flag
You can use this flag to print the current verion of cdep command-line tool.
```
$ ./cdep --version
cdep 0.8.9
```


