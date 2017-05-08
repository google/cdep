package io.cdep.cdep.yml;

import io.cdep.cdep.Coordinate;
import io.cdep.cdep.yml.cdepmanifest.*;
import net.java.quickcheck.Generator;

import static net.java.quickcheck.generator.CombinedGenerators.arrays;
import static net.java.quickcheck.generator.PrimitiveGenerators.enumValues;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

public class CDepManifestYmlGenerator implements Generator<CDepManifestYml> {
  Generator<CDepManifestYmlVersion> versionGenerator = enumValues(CDepManifestYmlVersion.class);
  Generator<Coordinate> coordinateGenerator = new CoordinateGenerator();
  Generator<HardNameDependency[]> dependenciesGenerator = arrays(new HardnameGenerator(), HardNameDependency.class);
  Generator<String> exampleGenerator = strings();
  Generator<Archive> archiveGenerator = new ArchiveGenerator();
  Generator<AndroidArchive[]> androidArchiveGenerator = arrays(new AndroidArchiveGenerator(), AndroidArchive.class);
  Generator<LinuxArchive[]> linuxArchiveGenerator = arrays(new LinuxArchiveGenerator(), LinuxArchive.class);


  @Override
  public CDepManifestYml next() {

    return new CDepManifestYml(
        versionGenerator.next(),
        coordinateGenerator.next(),
        dependenciesGenerator.next(),
        new License(),
        new Interfaces(archiveGenerator.next()),
        new Android(dependenciesGenerator.next(), androidArchiveGenerator.next()),
        null,
        new Linux(linuxArchiveGenerator.next()),
        exampleGenerator.next());
  }
}
