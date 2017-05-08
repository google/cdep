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
package io.cdep.cdep.yml;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;
import com.google.common.truth.Truth;
import io.cdep.annotations.Nullable;
import io.cdep.cdep.Coordinate;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYml;
import io.cdep.cdep.yml.cdepmanifest.CDepManifestYmlReadonlyVisitor;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertAbout;

/**
 * A Truth subject for dealing with CDepManifestYm
 */
public class CDepManifestYmlSubject extends Subject<CDepManifestYmlSubject, CDepManifestYml> {
  private static final SubjectFactory<CDepManifestYmlSubject, CDepManifestYml> EMPLOYEE_SUBJECT_FACTORY =
      new SubjectFactory<CDepManifestYmlSubject, CDepManifestYml>() {
        @Override
        public CDepManifestYmlSubject getSubject(FailureStrategy failureStrategy, @Nullable CDepManifestYml target) {
          return new CDepManifestYmlSubject(failureStrategy, target);
        }
      };

  public CDepManifestYmlSubject(FailureStrategy failureStrategy, CDepManifestYml actual) {
    super(failureStrategy, actual);
  }

  public static CDepManifestYmlSubject assertThat(@Nullable CDepManifestYml employee) {
    return assertAbout(EMPLOYEE_SUBJECT_FACTORY).that(employee);
  }

  public void hasCoordinate(Coordinate coordinate) {
    if (!actual().coordinate.equals(coordinate)) {
      fail("Coordinate was not the same", actual().coordinate, coordinate);
    }
  }

  public void hasArchiveNamed(String archive) {
    final Set<String> archives = new HashSet<>();

    // Gather archive names
    (new GatherArchivesYmlReadonlyVisitor(archives)).visitCDepManifestYml(null, actual());

    Truth.assertThat(archives).contains(archive);
  }

  public static class GatherArchivesYmlReadonlyVisitor extends CDepManifestYmlReadonlyVisitor {

    private final Set<String> archives;

    public GatherArchivesYmlReadonlyVisitor(Set<String> archives) {
      this.archives = archives;
    }

    @Override
    public void visitString(String name, String node) {
      if (name == null) {
        return;
      }
      if (name.equals("file")) {
        archives.add(node);
      }
    }
  }
}
