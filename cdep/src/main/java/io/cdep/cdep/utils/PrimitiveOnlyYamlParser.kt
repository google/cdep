package io.cdep.cdep.utils

import io.cdep.cdep.yml.cdep.BuildSystem
import io.cdep.cdep.yml.cdep.CDepYml
import io.cdep.cdep.yml.cdep.SoftNameDependency
import org.yaml.snakeyaml.events.*

/**
 * Parse a cdep.yml event stream and output a CDepYml instance from it.
 */
fun parseCDep(events : Iterable<Event>) : CDepYml {
    var event = events.iterator().next()
    val builders = mutableListOf<BuildSystem>()
    val dependencies = mutableListOf<SoftNameDependency>()
    var downloadPackagesFolder : String? = null
    var generatedModulesFolder : String? = null
    while(true) {
        when(event) {
            is StreamStartEvent -> {}
            is DocumentStartEvent -> {}
            is StreamEndEvent,
            is DocumentEndEvent -> {
                val result = CDepYml()
                result.builders = builders.toTypedArray()
                result.dependencies = dependencies.toTypedArray()
                result.downloadedPackagesFolder = downloadPackagesFolder
                result.generatedModulesFolder = generatedModulesFolder
                return result
            }
            is MappingStartEvent -> {
                event = events.iterator().next()
                var mappingDone = false
                while(!mappingDone) {
                    when(event) {
                        is ScalarEvent -> {
                            when(event.value) {
                               "builders" -> {
                                   builders.addAll(parseListOfString(events).map { BuildSystem(it) })
                                   event = events.iterator().next()
                                }
                                "dependencies" -> {
                                    dependencies.addAll(parseListOfDependency(events))
                                    event = events.iterator().next()
                                }
                                "downloadedPackagesFolder" -> {
                                    downloadPackagesFolder = parseString(events)
                                    event = events.iterator().next()
                                }
                                "generatedModulesFolder" -> {
                                    generatedModulesFolder = parseString(events)
                                    event = events.iterator().next()
                                }
                                else -> throw Exception(event.toString())
                            }
                        }
                        is MappingEndEvent -> mappingDone = true
                        else -> throw Exception(event.toString())
                    }
                }
            }
            else -> throw Exception(event.toString())
        }
        event = events.iterator().next()
    }
    throw Exception("unreachable")
}

private fun parseString(events : Iterable<Event>) : String {
    val event = events.iterator().next()
    when(event) {
        is ScalarEvent -> return event.value
        else -> throw Exception(event.toString())
    }
}


private fun parseListOfString(events : Iterable<Event>) : List<String> {
    val result = mutableListOf<String>()
    var done = false
    var event = events.iterator().next()
    while(!done) {
        when(event) {
            is SequenceStartEvent -> {}
            is ScalarEvent -> result.add(event.value)
            is SequenceEndEvent -> done = true
            else -> throw Exception(event.toString())
        }
        if (!done) event = events.iterator().next()
    }
    return result
}

private fun parseListOfDependency(events : Iterable<Event>) : List<SoftNameDependency> {
    val result = mutableListOf<SoftNameDependency>()
    var event = events.iterator().next()
    while(true) {
        when(event) {
            is SequenceStartEvent -> {}
            is SequenceEndEvent ->
                return result
            is MappingStartEvent -> {
                event = events.iterator().next()
                when(event) {
                    is ScalarEvent -> when(event.value) {
                        "compile" -> {
                            event = events.iterator().next()
                            when(event) {
                                is ScalarEvent -> result.add(SoftNameDependency(event.value))
                                else -> throw Exception(event.toString())
                            }
                        }
                        else -> throw Exception(event.value.toString())
                    }
                    else -> throw Exception(event.toString())
                }
            }
            is MappingEndEvent -> {}
            is ScalarEvent -> return result
            else -> throw Exception(event.toString())
        }
        event = events.iterator().next()
    }
}