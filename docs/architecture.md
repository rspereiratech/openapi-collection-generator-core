# Architecture

`openapi-collection-generator-core` turns an OpenAPI 3.x spec into one or more API client collections (Postman, Insomnia) plus the supporting environment files. It is built as a small set of single-purpose interfaces wired together by a single orchestrator, so any stage can be replaced without forking.

## Pipeline

The full lifecycle lives in `CollectionGenerationOrchestrator#generate`:

```
GenerationRequest
      │
      ▼
┌──────────────┐
│ SpecLoader   │  validate file exists / is regular
└──────┬───────┘
       ▼
┌──────────────┐
│ OpenApiParser│  parse YAML/JSON, fully resolve $refs
└──────┬───────┘
       ▼
   for each format in request:
       │
       ▼
┌─────────────────────────────┐
│ CollectionGeneratorFactory  │  pick generator for format
└──────┬──────────────────────┘
       ▼
┌─────────────────────────────┐
│ CollectionGenerator         │  build collection JSON +
│                             │  optional AdditionalFile list
└──────┬──────────────────────┘
       ▼
┌─────────────────────────────┐
│ CollectionWriter            │  write collection.json
└──────┬──────────────────────┘
       ▼
┌─────────────────────────────┐
│ EnvironmentWriter (if any   │  write *.environment.json
│ AdditionalFile produced)    │
└──────┬──────────────────────┘
       ▼
   GenerationResult(collectionFile, additionalFiles)
```

A `GenerationRequest` with two formats (e.g. `POSTMAN`, `INSOMNIA`) results in two iterations of the inner loop and two `GenerationResult` entries.

## Top-level types

| Type | Role |
|------|------|
| `GenerationRequest` | Immutable input record: spec file, output dir, formats, collection name, file name pattern. Validates invariants in its compact constructor (e.g. `{format}` placeholder when multiple formats are configured). |
| `GenerationConfig` | Per-format configuration derived from a `GenerationRequest` via `toGenerationConfig(format)`. Carries everything writers and generators need for a single format. |
| `GenerationResult` | Output record for a single format: the main collection file plus any additional files that were written (typically environment files). |
| `CollectionFormat` | Enum of supported targets: `POSTMAN`, `INSOMNIA`. `fromString` accepts case-insensitive strings. |

## Stage contracts

Every pipeline stage is an interface. Defaults are provided but each is replaceable.

| Stage | Contract | Default |
|-------|----------|---------|
| Load | `loader.SpecLoader` | `FileSpecLoader` — checks the file exists and is a regular file |
| Parse | `parser.OpenApiParser` | `SwaggerOpenApiParser` — uses `swagger-parser` with `setResolveFully(true)` |
| Generate | `generator.CollectionGenerator` (per format) | provided by `factory.CollectionGeneratorFactory` |
| Serialize | `serializer.CollectionSerializer` | `JacksonCollectionSerializer` |
| Write collection | `writer.CollectionWriter` | `FileCollectionWriter` |
| Write env files | `writer.EnvironmentWriter` | `FileEnvironmentWriter` |

The orchestrator depends only on the interfaces, never on concrete implementations.

## Subsystems used by generators

Concrete generators (one per `CollectionFormat`) typically draw on the following collaborators:

### Schema example generation (`example/`)

`SchemaExampleGenerator` is the contract. The shipped chain handles:

- `PrimitiveSchemaExampleGenerator` — strings, numbers, booleans, dates
- `ArraySchemaExampleGenerator` — arrays, recursing into the item schema
- `ObjectSchemaExampleGenerator` — objects, walking properties
- `ComposedSchemaExampleGenerator` — `allOf` / `oneOf` / `anyOf`
- `NullableSchemaExampleGenerator` — wraps a delegate, returns `null` when nullable

`DelegatingSchemaExampleGenerator` exists to break circular wiring: object/array generators recurse via the chain, but the chain itself depends on those generators. Construction wires the delegate into the leaf generators after the chain is built.

### Security (`security/`)

- `applier.SecurityApplier` — entry point that walks the spec and produces `SecurityInjection`s (per-operation and global).
- `resolver.SecuritySchemeResolver` — resolves the schemes referenced by an operation against `components.securitySchemes`.
- `factory.SecurityInjectorFactory` — picks an injector per scheme. `DefaultSecurityInjectorFactory` registers, in order: `Bearer`, `Basic`, `ApiKeyHeader`, `ApiKeyQuery`, `ApiKeyCookie`, `OAuth2`, then `NoOp` as a fallback.
- `injector.SecurityInjector` — produces `HttpHeader`, `HttpQueryParam` and `EnvironmentVariable` records that downstream generators apply to requests and environment files.

`DefaultSecurityApplier#applyGlobal` deduplicates `EnvironmentVariable`s across all operations using a `LinkedHashMap` keyed by name, preserving first-seen order.

### Vendor extensions (`extension/`)

`ExtensionProcessor` decides whether it handles a given `x-*` key and produces an `ExtensionResult` (optional name override + optional description fragment). `ExtensionProcessorChain` runs every processor against every extension on an operation; later name overrides win, description fragments are concatenated with newlines.

Built-in processors live under `extension/impl/`:

- `XInternalExtensionProcessor`
- `XBetaExtensionProcessor`
- `XSummaryExtensionProcessor`
- `XDeprecatedSinceExtensionProcessor`

See [extensions.md](extensions.md).

### Schema resolution (`schema/`)

- `ref.SchemaRefResolver` — follows `$ref` pointers when the parser does not fully inline them.
- `discriminator.DiscriminatorResolver` — picks a concrete subtype for polymorphic `oneOf`/`anyOf` schemas using the spec's `discriminator`.

### Servers and environments (`server/`)

`ServerEnvironmentGenerator#generate` returns one `ServerEnvironment` per `servers` entry in the spec. `DefaultServerEnvironmentGenerator`:

- uses `server.description` as the env name when available;
- falls back to `Production` / `Staging` / `Development` / `Environment N` based on position;
- creates a single `http://localhost:8080` "Local" environment when the spec defines no servers;
- sanitises env names to `[a-zA-Z0-9_-]` for use in file names like `<collection>.<env>.environment.json`.

### IDs (`id/`)

- `DeterministicIdGenerator` — SHA-256 hash of the context, truncated to 16 hex chars and prefixed; same input always yields the same ID, which keeps regenerated collections diff-stable.
- `UUIDGenerator` — random UUIDs for cases where determinism is not required.

### Other

- `link.LinkDescriptionEnricher` — augments operation descriptions with information from OpenAPI `links`.
- `callback.CallbackProcessor` — handles OpenAPI `callbacks`.
- `deprecated.DeprecationMarker` — marks deprecated operations consistently.

## Error handling

Each pipeline stage throws a checked, stage-specific exception:

- `SpecValidationException` (load)
- `OpenApiParseException` (parse)
- `SerializationException` (serialize)
- `WriterException` (write)

The orchestrator wraps each in a `CollectionGenerationException` with a descriptive message, so callers only need to catch one type for the whole pipeline while still getting the original cause via `getCause()`.

## Why so many small interfaces?

The library is consumed by multiple front-ends (planned: Maven plugin, CLI, Gradle plugin). Keeping each stage as an interface makes it possible to:

- swap the loader for a URL-based one without touching the generators;
- replace example generation with a version that reads `examples` from the spec rather than synthesising values;
- add a new collection format by implementing a single `CollectionGenerator` and registering it in a factory.

See [extensibility.md](extensibility.md) for the recipes.
