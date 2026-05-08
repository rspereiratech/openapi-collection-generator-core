# OpenAPI Collection Generator — Core

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net/)
[![OpenAPI 3.0](https://img.shields.io/badge/OpenAPI-3.0-6BA539.svg)](https://spec.openapis.org/oas/v3.0.3)
[![Swagger Parser](https://img.shields.io/badge/Swagger-Parser-85EA2D.svg)](https://github.com/swagger-api/swagger-parser)

Core library for generating API client collections (Postman, Insomnia) from an OpenAPI 3.x specification.

This module is the engine. It is meant to be embedded in higher-level tools (Maven plugin, CLI, Gradle plugin) that wire it up and expose a user-facing entry point.

## Features

- Reads OpenAPI 3.x specs (YAML/JSON) via `swagger-parser`
- Generates collections in multiple formats from a single spec:
  - Postman v2.1
  - Insomnia v4
- Generates per-format environment files (variables, servers, security placeholders)
- Pluggable example generation for primitive, array, object, composed, and nullable schemas
- Pluggable security injection: API key (header/query/cookie), Basic, Bearer, OAuth2
- Extension processors for common `x-*` vendor extensions (`x-internal`, `x-beta`, `x-summary`, `x-deprecated-since`)
- Deterministic ID generation for stable diffs across regenerations

## Requirements

- Java 17+
- Maven 3.8+

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.rspereiratech</groupId>
    <artifactId>openapi-collection-generator-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

The single entry point is `CollectionGenerationOrchestrator`. It runs the full pipeline: validate → parse → generate → write.

```java
GenerationRequest request = new GenerationRequest(
        new File("openapi.yaml"),
        new File("build/collections"),
        List.of(CollectionFormat.POSTMAN, CollectionFormat.INSOMNIA),
        "My API"
);

CollectionGenerationOrchestrator orchestrator = /* wire components */;
List<GenerationResult> results = orchestrator.generate(request);
```

Each `GenerationResult` contains the generated collection file and any additional files (e.g. environment files).

### File name pattern

`GenerationRequest` accepts a `fileNamePattern` with `{name}` and `{format}` placeholders. When multiple formats are requested the pattern **must** include `{format}`, otherwise outputs would overwrite each other.

## Architecture

The pipeline is composed of small, replaceable components — every stage is an interface so you can override behavior without forking:

| Stage          | Contract                          | Default implementation             |
|----------------|-----------------------------------|------------------------------------|
| Load           | `SpecLoader`                      | `FileSpecLoader`                   |
| Parse          | `OpenApiParser`                   | `SwaggerOpenApiParser`             |
| Generate       | `CollectionGenerator` (per format)| via `CollectionGeneratorFactory`   |
| Serialize      | `CollectionSerializer`            | `JacksonCollectionSerializer`      |
| Write          | `CollectionWriter`                | `FileCollectionWriter`             |
| Env. files     | `EnvironmentWriter`               | `FileEnvironmentWriter`            |

Supporting subsystems:

- **Examples** — `SchemaExampleGenerator` chain (primitive, array, object, composed, nullable)
- **Security** — `SecurityInjectorFactory` selects an injector per scheme; defaults cover API key, Basic, Bearer, OAuth2
- **Extensions** — `ExtensionProcessorChain` runs `ExtensionProcessor`s for vendor `x-*` keys
- **Schema resolution** — `SchemaRefResolver` and `DiscriminatorResolver` for `$ref` and polymorphism
- **IDs** — `IdGenerator` with deterministic and UUID-based implementations

## Building

```bash
mvn clean install
```

## Documentation

In-depth docs live under [`docs/`](docs/README.md):

- [architecture.md](docs/architecture.md) — pipeline, components, error handling
- [configuration.md](docs/configuration.md) — `GenerationRequest`, `GenerationConfig`, file naming
- [extensibility.md](docs/extensibility.md) — adding formats, injectors, processors
- [security.md](docs/security.md) — security scheme handling
- [extensions.md](docs/extensions.md) — `x-*` vendor extensions
- [examples.md](docs/examples.md) — schema example generation

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for setup, conventions, and the pull-request workflow.

## Security

Found a vulnerability? Don't open a public issue — see [SECURITY.md](SECURITY.md) for the disclosure process.

For internals on how the library handles authentication and secrets, see [docs/security.md](docs/security.md).

## License

Released under the [MIT License](LICENSE).