package com.theblueground.test

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bluegroundltd.GenerateJsonSchema
import kotlinx.serialization.Serializable

@Serializable
@GenerateJsonSchema
data class AClass(@JsonProperty(value = "foo") val aProperty: String, @JsonProperty(value = "bar") val bProperty: String)

@Serializable
@GenerateJsonSchema
data class BClass(@JsonProperty(value = "qux") val aProperty: String)
