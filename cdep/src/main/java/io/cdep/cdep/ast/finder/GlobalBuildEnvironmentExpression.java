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
package io.cdep.cdep.ast.finder;

import io.cdep.annotations.NotNull;

public class GlobalBuildEnvironmentExpression extends Expression {
  @NotNull final public ParameterExpression cdepExplodedRoot;
  @NotNull final public ParameterExpression buildSystemNoneRuntime; // 'system' in ndk-build, 'none' in CMake
  @NotNull final public ParameterExpression buildSystemTargetSystem; // Like CMAKE_SYSTEM_NAME
  @NotNull final public ParameterExpression buildSystemTargetPlatform; // Like 12 or 21 for Android
  @NotNull final public ParameterExpression cdepDeterminedAndroidAbi;
  @NotNull final public ParameterExpression cdepDeterminedAndroidRuntime;
  @NotNull final public ParameterExpression cmakeOsxSysroot;
  @NotNull final public ParameterExpression cmakeOsxArchitectures;
  // This is the standard 11, 14, 17 for the c++ compiler. Not specific to a particular
  // underlying build system.
  @NotNull final public ParameterExpression buildSystemCxxCompilerStandard;

  public GlobalBuildEnvironmentExpression() {
    // UPPER_CASE names are build system (CMake or ndk-build) toolchain variables.
    // lower_case names are CDep temporaries.
    this.cdepExplodedRoot = new ParameterExpression("cdep_exploded_root");
    this.buildSystemNoneRuntime = new ParameterExpression("build_system_none_runtime");
    this.buildSystemTargetSystem = new ParameterExpression("build_system_target_system");
    this.buildSystemTargetPlatform = new ParameterExpression("build_system_target_platform");
    this.cdepDeterminedAndroidAbi = new ParameterExpression("cdep_determined_android_abi");
    this.cdepDeterminedAndroidRuntime = new ParameterExpression("cdep_determined_android_runtime");
    this.cmakeOsxSysroot = new ParameterExpression("CMAKE_OSX_SYSROOT");
    this.cmakeOsxArchitectures = new ParameterExpression("CMAKE_OSX_ARCHITECTURES");
    this.buildSystemCxxCompilerStandard = new ParameterExpression("build_system_cxx_compiler_standard");
  }
}
