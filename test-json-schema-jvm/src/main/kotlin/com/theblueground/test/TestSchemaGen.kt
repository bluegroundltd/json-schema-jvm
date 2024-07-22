package com.theblueground.test

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bluegroundltd.GenerateJsonSchema

@GenerateJsonSchema
data class AClass(@JsonProperty(value = "foo") val aProperty: String, @JsonProperty(value = "bar") val bProperty: String)

@GenerateJsonSchema
data class Person(
    val name: String,
    val age: Int
)
