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

class CDepYml {
    @JvmField
    var builders = arrayOfNulls<BuildSystem>(0)
    @JvmField
    var dependencies: Array<SoftNameDependency?>? = arrayOfNulls(0)
    @JvmField
    var downloadedPackagesFolder: String? = null
    @JvmField
    var generatedModulesFolder: String? = null

    private fun toYaml(indent: Int): String {
        val prefix = " ".repeat(indent * 2)
        val sb = StringBuilder()
        sb.append("${prefix}builders: [")
        sb.append(builders.joinToString())
        sb.append("]\r\n")

        if (dependencies != null && dependencies!!.isNotEmpty()) {
            sb.append("${prefix}dependencies:\r\n", prefix)
            for (dependency in dependencies!!) {
                sb.append("- ")
                sb.append(dependency!!.toYaml(indent + 1))
            }
        }

        if (downloadedPackagesFolder != null && downloadedPackagesFolder!!.isNotEmpty()) {
            sb.append("${prefix}downloadedPackagesFolder: $downloadedPackagesFolder\r\n")
        }

        if (generatedModulesFolder != null && generatedModulesFolder!!.isNotEmpty()) {
            sb.append("${prefix}generatedModulesFolder: $generatedModulesFolder\r\n")
        }
        return sb.toString()
    }

    override fun toString(): String {
        return toYaml(0)
    }
}
