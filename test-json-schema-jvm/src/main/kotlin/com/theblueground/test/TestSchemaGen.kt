package com.theblueground.test

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.bluegroundltd.GenerateJsonSchema
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@GenerateJsonSchema
data class AClass(@JsonProperty(value = "foo") val aProperty: String, @JsonProperty(value = "bar") val bProperty: String)

@GenerateJsonSchema
data class Person(
    val name: String,
    val age: Int,
    val date: LocalDate,
    val time: LocalTime,
    val dateTime: LocalDateTime,
    val instant: Instant
)
