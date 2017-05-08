package io.cdep.cdep.yml;

import io.cdep.cdep.yml.cdepmanifest.AndroidABI;
import net.java.quickcheck.Generator;

import static net.java.quickcheck.generator.PrimitiveGenerators.integers;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

public class AndroidABIGenerator implements Generator<AndroidABI> {
  Generator<String> strings = strings();
  Generator<Integer> integers = integers();

  @Override
  public AndroidABI next() {
    switch (Math.abs(integers.next()) % 3) {
      case 0:
        return AndroidABI.ARMEABI;
      case 1:
        return AndroidABI.ARM64_V8A;
      default:
        return new AndroidABI(strings.next());
    }
  }
}
