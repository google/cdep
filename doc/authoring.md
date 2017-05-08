# CDep Package Author's Guide
This guide will show how to author your own CDep package and host it on github. When you're done you'll have a package that any CDep user can reference in their project.

Prequisites: CMake installed, Github account, read [Anatomy of a CDep Package](https://github.com/google/cdep/blob/master/doc/anatomy.md)

In this tutorial we'll author an Open SSL package and host it on Github. This tutorial runs on Ubuntu but it should work with simple modifications on MacOS and Windows.

## Step 1 -- Get the Open SSL Source Code
This tutorial uses the source code CMake project found on [LaunchPad](https://launchpad.net/openssl-cmake/1.0.1e/1.0.1e-1). I'd like to thank the author Brian Sidebotham for making this available.
```
mkdir openssl-package
cd openssl-package
wget https://launchpad.net/openssl-cmake/1.0.1e/1.0.1e-1/+download/openssl-cmake-1.0.1e-src.tar.gz
tar xvzf openssl-cmake-1.0.1e-src.tar.gz
```

