/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package io.cdep.cdep.fullfill;

import io.cdep.cdep.utils.CDepManifestYmlUtils;
import io.cdep.cdep.utils.FileUtils;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class TestZipFilesRewritingVisitor {
  @Test
  public void testBasic() throws IOException {
    CDepManifestYml before = CDepManifestYmlUtils.convertStringToManifest(
        FileUtils.readAllText(new File("../third_party/stb/cdep/cdep-manifest-divide.yml")));

    CDepManifestYml afterSubstitution = new SubstituteStringsRewriter()
        .replace("${source}", new File("../third_party/stb").getAbsolutePath())
        .visitCDepManifestYml(before);

    File outputFolder = new File(".test-files/testZipFullfill").getAbsoluteFile();
    outputFolder.delete();

    File layout = new File(outputFolder, "layout");
    layout.delete();
    layout.mkdirs();

    File staging = new File(outputFolder, "staging");
    staging.delete();
    staging.mkdirs();

    ZipFilesRewriter zipper = new ZipFilesRewriter(layout, staging);
    CDepManifestYml afterZipping = zipper.visitCDepManifestYml(afterSubstitution);

    assertThat(layout.isDirectory()).isTrue();
    assertThat(afterZipping.interfaces.headers.file).isEqualTo("stb-divide-headers.zip");
    assertThat(afterZipping.interfaces.headers.include).isEqualTo("include");
  }
}