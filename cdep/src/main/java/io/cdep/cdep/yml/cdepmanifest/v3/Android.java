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
package io.cdep.cdep.yml.cdepmanifest.v3;

import io.cdep.annotations.Nullable;
import io.cdep.cdep.yml.cdepmanifest.HardNameDependency;

@SuppressWarnings("unused")
public class Android {
  @Nullable
  final public HardNameDependency dependencies[];
  @Nullable
  final public AndroidArchive archives[];

  Android() {
    this.dependencies = new HardNameDependency[0];
    this.archives = new AndroidArchive[0];
  }

  public Android(@Nullable HardNameDependency dependencies[], @Nullable AndroidArchive archives[]) {
    this.dependencies = dependencies;
    this.archives = archives;
  }
}
