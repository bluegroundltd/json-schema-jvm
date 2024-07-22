# JSON Schema JVM

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

JSON Schema JVM is a Java/Kotlin library for generating JSON schemas from your data classes. 
This library simplifies the process of creating JSON schemas directly from your Java or Kotlin classes, 
making it easy to ensure your data structures conform to specified formats.

## Features

- Generate JSON schemas from Java/Kotlin classes

## Getting Started

### Prerequisites

- Java 17
- Kotlin 2.0

### Installation

Add the following dependencies to your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.theblueground.json.schema.jvm.gradle-plugin") version "1.0.0"
}

dependencies {
    implementation("io.github.bluegroundltd:generate-schema-annotation:1.0.0")
}
```

### Usage

Here is a simple example of how to use the library to generate a JSON schema from a Kotlin data class.

#### Example Kotlin Data Class

```kotlin
package com.example

@GenerateJsonSchema
data class Person(
    val name: String,
    val age: Int
)
```

#### Example generated JSON schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Person",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "name": {
      "type": "string"
    },
    "age": {
      "type": "integer"
    }
  },
  "required": [
    "age"
  ]
}
```

### Running the Example

Just execute the `jsonSchemaJvmGenerationTask` in `test-json-schema-jvm` project.

## Contributing

We welcome contributions to the project. Please follow these steps to contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a new Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
