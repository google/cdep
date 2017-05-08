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
import io.cdep.annotations.Nullable;
import io.cdep.cdep.yml.cdepmanifest.CxxLanguageFeatures;

import java.io.File;
import java.lang.reflect.Method;

import static io.cdep.cdep.utils.ReflectionUtils.getMethod;

@SuppressWarnings("unused")
public class ExternalFunctionExpression extends Expression {

  // Given /a/b/c.txt returns c.txt
  final public static ExternalFunctionExpression FILE_GETNAME = new ExternalFunctionExpression(File.class, "getName");

  // Given /a/b/c.1.txt returns index of . in .txt
  // CMAKE constant(FIND <constant> <substring> <output variable> [REVERSE])
  final public static ExternalFunctionExpression STRING_LASTINDEXOF = new ExternalFunctionExpression(String.class,
      "lastIndexOf",
      String.class);

  // Given abcde, 2, 1 returns c
  final public static ExternalFunctionExpression STRING_SUBSTRING_BEGIN_END = new ExternalFunctionExpression(String.class,
      "substring",
      int.class,
      int.class);

  final public static ExternalFunctionExpression STRING_STARTSWITH = new ExternalFunctionExpression(String.class, "startsWith",
      String.class);

  final public static ExternalFunctionExpression FILE_JOIN_SEGMENTS = new ExternalFunctionExpression(ExternalFunctionExpression
      .class,

      "fileJoinSegments",
      File.class,
      String[].class);

  final public static ExternalFunctionExpression INTEGER_GTE = new ExternalFunctionExpression(ExternalFunctionExpression.class,
      "gte",
      int.class,
      int.class);

  final public static ExternalFunctionExpression NOT = new ExternalFunctionExpression(ExternalFunctionExpression.class,
      "not",
      boolean.class);

  final public static ExternalFunctionExpression OR = new ExternalFunctionExpression(ExternalFunctionExpression.class,
      "or",
      boolean.class,
      boolean.class);

  final public static ExternalFunctionExpression REQUIRE_MINIMUM_CXX_COMPILER_STANDARD = new ExternalFunctionExpression(
      ExternalFunctionExpression.class,
      "requireMinimumCxxCompilerStandard",
      String.class);

  final public static ExternalFunctionExpression STRING_EQUALS = new ExternalFunctionExpression(ExternalFunctionExpression.class,
      "eq",
      String.class,
      String.class);

  final public static ExternalFunctionExpression ARRAY_HAS_ONLY_ELEMENT = new ExternalFunctionExpression(
      ExternalFunctionExpression.class,
      "hasOnlyElement",
      String[].class,
      String.class);

  final public static ExternalFunctionExpression REQUIRES_COMPILER_FEATURES = new ExternalFunctionExpression(
      ExternalFunctionExpression.class,
      "requiresCompilerFeatures",
      CxxLanguageFeatures[].class);

  final public static ExternalFunctionExpression SUPPORTS_COMPILER_FEATURES = new ExternalFunctionExpression(
      ExternalFunctionExpression.class,
      "supportsCompilerFeatures");

  final public static ExternalFunctionExpression SET_CXX_COMPILER_STANDARD_FOR_ALL_TARGETS = new ExternalFunctionExpression(
      ExternalFunctionExpression.class,
      "setCxxCompilerStandardForAllTargets",
      int.class);

  final public Method method;

  private ExternalFunctionExpression(@NotNull Class clazz, @NotNull String functionName, @NotNull Class<?>... parameterTypes) {
    this.method = getMethod(clazz, functionName, parameterTypes);
  }

  static public File fileJoinSegments(File base, @NotNull String... segments) {
    for (String segment : segments) {
      base = new File(base, segment);
    }
    return base;
  }

  static public boolean gte(int left, int right) {
    return left >= right;
  }

  static public boolean lt(int left, int right) {
    return left < right;
  }

  static public boolean eq(@NotNull String left, String right) {
    return left.equals(right);
  }

  static public boolean not(boolean value) {
    return !value;
  }

  static public boolean or(boolean left, boolean right) {
    return left || right;
  }

  static public boolean defined(@Nullable Object object) {
    return object != null;
  }

  static public boolean hasOnlyElement(@NotNull String array[], @NotNull String value) {
    return array.length == 1 && value.equals(array[0]);
  }

  /**
   * Declares that the current module requires certain language features. It is up
   * to the build system to supply a compiler that can meet those features.
   */
  static public CxxLanguageFeatures[] requiresCompilerFeatures(CxxLanguageFeatures[] features) {
    return features;
  }

  /**
   * Returns true if this build system supports compiler features.
   */
  @SuppressWarnings("SameReturnValue")
  static public boolean supportsCompilerFeatures() {
    return true;
  }

  /**
   * Sets the global compiler standard for all targets. Prefer requiresCompilerFeatures
   * if available.
   */
  @SuppressWarnings("EmptyMethod")
  static public void setCxxCompilerStandardForAllTargets(int standard) {
  }

  /**
   * Require at least the given Cxx compiler standard.
   */
  @NotNull
  static public String requireMinimumCxxCompilerStandard(String level) {
    return "Compiler Standard Level " + level;
  }
}
