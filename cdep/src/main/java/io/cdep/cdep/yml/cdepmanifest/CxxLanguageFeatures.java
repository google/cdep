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
package io.cdep.cdep.yml.cdepmanifest;

@SuppressWarnings("unused")
public enum CxxLanguageFeatures {
  cxx_std_98(0, "Compiler mode is aware of C++ 98."),
  cxx_std_11(11, "Compiler mode is aware of C++ 11."),
  cxx_std_14(14, "Compiler mode is aware of C++ 14."),
  cxx_std_17(17, "Compiler mode is aware of C++ 17."),
  cxx_aggregate_default_initializers(11, "Aggregate default initializers, as defined in N3605."),
  cxx_alias_templates(11, "Template aliases, as defined in N2258."),
  cxx_alignas(11, "Alignment control alignas, as defined in N2341."),
  cxx_alignof(11, "Alignment control alignof, as defined in N2341."),
  cxx_attributes(11, "Generic attributes, as defined in N2761."),
  cxx_attribute_deprecated(14, "[[deprecated]] attribute, as defined in N3760."),
  cxx_auto_type(11, "Automatic type deduction, as defined in N1984."),
  cxx_binary_literals(14, "Binary literals, as defined in N3472."),
  cxx_constexpr(11, "Constant expressions, as defined in N2235."),
  cxx_contextual_conversions(14, "Contextual conversions, as defined in N3323."),
  cxx_decltype_incomplete_return_types(11, "Decltype on incomplete return types, as defined in N3276."),
  cxx_decltype(11, "Decltype, as defined in N2343."),
  cxx_decltype_auto(14, "decltype(auto) semantics, as defined in N3638."),
  cxx_default_function_template_args(11, "Default template arguments for function templates, as defined in DR226"),
  cxx_defaulted_functions(11, "Defaulted functions, as defined in N2346."),
  cxx_defaulted_move_initializers(11, "Defaulted move initializers, as defined in N3053."),
  cxx_delegating_constructors(11, "Delegating constructors, as defined in N1986."),
  cxx_deleted_functions(11, "Deleted functions, as defined in N2346."),
  cxx_digit_separators(14, "Digit separators, as defined in N3781."),
  cxx_enum_forward_declarations(11, "Enum forward declarations, as defined in N2764."),
  cxx_explicit_conversions(11, "Explicit conversion operators, as defined in N2437."),
  cxx_extended_friend_declarations(11, "Extended friend declarations, as defined in N1791."),
  cxx_extern_templates(11, "Extern templates, as defined in N1987."),
  cxx_final(11, "Override control final keyword, as defined in N2928, N3206 and N3272."),
  cxx_func_identifier(11, "Predefined __func__ identifier, as defined in N2340."),
  cxx_generalized_initializers(11, "Initializer lists, as defined in N2672."),
  cxx_generic_lambdas(14, "Generic lambdas, as defined in N3649."),
  cxx_inheriting_constructors(11, "Inheriting constructors, as defined in N2540."),
  cxx_inline_namespaces(11, "Inline namespaces, as defined in N2535."),
  cxx_lambdas(11, "Lambda functions, as defined in N2927."),
  cxx_lambda_init_captures(14, "Initialized lambda captures, as defined in N3648."),
  cxx_local_type_template_args(11, "Local and unnamed types as template arguments, as defined in N2657."),
  cxx_long_long_type(11, "long long type, as defined in N1811."),
  cxx_noexcept(11, "Exception specifications, as defined in N3050."),
  cxx_nonstatic_member_init(11, "Non-static data member initialization, as defined in N2756."),
  cxx_nullptr(11, "Null pointer, as defined in N2431."),
  cxx_override(11, "Override control override keyword, as defined in N2928, N3206 and N3272."),
  cxx_range_for(11, "Range-based for, as defined in N2930."),
  cxx_raw_string_literals(11, "Raw constant literals, as defined in N2442."),
  cxx_reference_qualified_functions(11, "Reference qualified functions, as defined in N2439."),
  cxx_relaxed_constexpr(14, "Relaxed constexpr, as defined in N3652."),
  cxx_return_type_deduction(11, "Return type deduction on normal functions, as defined in N3386."),
  cxx_right_angle_brackets(11, "Right angle bracket parsing, as defined in N1757."),
  cxx_rvalue_references(11, "R-constant references, as defined in N2118."),
  cxx_sizeof_member(11, "Size of non-static data members, as defined in N2253."),
  cxx_static_assert(11, "Static assert, as defined in N1720."),
  cxx_strong_enums(11, "Strongly typed enums, as defined in N2347."),
  cxx_thread_local(11, "Thread-local variables, as defined in N2659."),
  cxx_trailing_return_types(11, "Automatic function return type, as defined in N2541."),
  cxx_unicode_literals(11, "Unicode constant literals, as defined in N2442."),
  cxx_uniform_initialization(11, "Uniform initialization, as defined in N2640."),
  cxx_unrestricted_unions(11, "Unrestricted unions, as defined in N2544."),
  cxx_user_literals(11, "User-defined literals, as defined in N2765."),
  cxx_variable_templates(14, "Variable templates, as defined in N3651."),
  cxx_variadic_macros(11, "Variadic macros, as defined in N1653."),
  cxx_variadic_templates(11, "Variadic templates, as defined in N2242."),
  cxx_template_template_parameters(11, "Template template parameters, as defined in ISO/IEC 14882:1998.");
  public final int standard;

  CxxLanguageFeatures(int standard, String description) {
    this.standard = standard;
  }
}
