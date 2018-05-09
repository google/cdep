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
package io.cdep.cdep.yml.cdep

class SoftNameDependency(
        @JvmField val compile: String? = null) {

    fun toYaml(indent: Int): String {
        val firstPrefix = " ".repeat((indent - 1) * 2)
        val sb = StringBuilder()
        if (compile != null && compile.isNotEmpty()) {
            sb.append("${firstPrefix}compile: $compile\n")
        }
        return sb.toString()
    }

    override fun toString(): String {
        return toYaml(1)
    }
}
