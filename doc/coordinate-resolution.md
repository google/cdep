## CDep coordinate resolution
A CDep coordinate uniquely identifies a package. It also contains the information needed to reconstruct a URL to the package's manifest. Here's an example:

```
com.github.jomof:sqlite:3.16.2-rev51
```

There are multiple coordinate forms that are used to solve different problems.

### Simple form
The simplest coordinate form looks like this:
```
com.github.jomof:sqlite:3.16.2-rev51
```
During resolution, CDep decomposes this into pieces.
```
domain = github.com
user = jomof
artifact = sqlite
version = 3.16.2-rev51
```
CDep then recomposes these pieces into a URL:
```
https://github.com/jomof/sqlite/releases/download/3.16.2-rev53/cdep-manifest.yml
```
The purpose of the simple form of coordinate is just to be able to host the source and releases for a single CDep package.

### Compound form
Sometimes you want a family of packages under a single umbrella repo. Coordinates of this form look like this:
```
com.github.jomof:firebase/admob:2.1.3-rev22
com.github.jomof:firebase/analytics:2.1.3-rev22
```
These are decomposed in a manner similar to the simple form. The single difference is that the artifact family name is extracted:
```
domain = github.com
user = jomof
family = firebase
artifact = admob
version = 2.1.3-rev22
```
Artifact family name is then used to recompose the URL by adding a suffix to the CDep manifest file name.
```
https://github.com/jomof/firebase/releases/download/2.1.3-rev23/cdep-manifest-admob.yml
```

### Multipackage form
Sometimes you want to use a single Github repo to hold multiple unrelated packages. In this case, you use the multi package form:
```
com.github.jomof.cdep:boost:1.0.63
```
The difference is that the group name has four segments rather than three. The final segment is the name of Github repo for the given user. CDep transforms this into a manifest URL similar to this one:
```
https://github.com/jomof/cdep/releases/download/boost@1.0.63/cdep-manifest.yml
```
The URL form actually needs to '@' to be escaped. So the true URL is like this:
```
https://github.com/jomof/cdep/releases/download/boost%401.0.63/cdep-manifest.yml
```
However, when you create the new Github release to hold this package you should create it with the @ symbol (like boost@1.0.63).

Multipackage form also supports compound packages. These have a coordinate like this:
```
com.github.jomof.cdep:firebase/admob:2.1.3-rev22
```




