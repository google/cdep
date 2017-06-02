## Anatomy of cdep.yml file
The cdep.yml file is where you can specify the packages that your project depends on.

```
builders: [cmake]
downloadedPackagesFolder: ./downloaded-packages/
dependencies:
- compile: com.github.jomof:sqlite:3.16.2-rev51
```

### builders section
The *builders* section of cdep.yml names the build system that is used. This tells cdep what sort of glue code to 
generate for each package. Valid values are *cmake* and *ndk-build*.

Additionally, there are two special-case builders that can be used to generate example projects for each package. 
These are called *cmakeExamples* and *ndk-build-examples*.

### downloadedPackagesFolder section
The *downloadedPackagesFolder* field lets you specify where cdep will download and unzip package archive files.
By default, cdep will download and unzip package archive files to $HOME/.cdep/downloads and $HOME/.cdep/exploded
respectively.

If this path is relative then it is relative to the folder that contains cdep.yml.

### generatedModulesFolder section
The *generatedModulesFolder* field lets you specify where cdep will generate build system glue code and generated
projects. By default, cdep will use .cdep/modules and .cdep/examples for these folders.

If this path is relative then it is relative to the folder that contains cdep.yml.

### dependencies section
This is the main section of cdep.yml. It is where you specify the packages that you want to import into your project.
Typically, this section will contain several coordinates.

```
dependencies:
- compile: com.github.jomof:sqlite:3.16.2-rev51
- compile: com.github.jomof:lua:5.3.4
```

You may also specify a relative path to a local cdep-manifest.yml file.
```
dependencies:
- compile: /usr/local/my-packages/my-package/cdep-manifest.yml
```