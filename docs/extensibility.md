# Extensibility

Every pipeline stage and every supporting subsystem is defined by an interface. This page lists the common extension points and shows how to plug in custom behaviour.

## Adding a new collection format

1. Add the format to `model.CollectionFormat`:

   ```java
   public enum CollectionFormat {
       POSTMAN, INSOMNIA, BRUNO; // new
   }
   ```

2. Implement `generator.CollectionGenerator`:

   ```java
   public class BrunoCollectionGenerator implements CollectionGenerator {
       @Override
       public String generate(OpenAPI openApi, GenerationConfig config) { ... }

       @Override
       public List<AdditionalFile> generateAdditionalFiles(OpenAPI openApi, GenerationConfig config) {
           return List.of(); // or environment files
       }
   }
   ```

3. Register it in your `CollectionGeneratorFactory`:

   ```java
   public class MyFactory implements CollectionGeneratorFactory {
       @Override
       public CollectionGenerator create(CollectionFormat format) {
           return switch (format) {
               case POSTMAN  -> new PostmanCollectionGenerator(...);
               case INSOMNIA -> new InsomniaCollectionGenerator(...);
               case BRUNO    -> new BrunoCollectionGenerator(...);
           };
       }
   }
   ```

The orchestrator calls `factory.create(format)` once per format in the request — there's no other registration to do.

## Replacing the spec loader

Default: `FileSpecLoader` validates that the path is an existing regular file. To accept a URL instead:

```java
public class HttpSpecLoader implements SpecLoader {
    @Override
    public void validate(File f) throws SpecValidationException {
        // download to a temp file, or change the contract upstream
    }
}
```

Then pass `new HttpSpecLoader()` to the orchestrator. If you need a non-`File` source, fork the contract — `SpecLoader` and `OpenApiParser` are the only stages that touch `java.io.File`.

## Replacing the parser

`OpenApiParser#parse(File) -> OpenAPI` is the contract. The default uses `swagger-parser` with `setResolveFully(true)`. If you want to keep `$ref`s un-inlined or use a different parser, supply your own:

```java
public class LenientParser implements OpenApiParser {
    @Override
    public OpenAPI parse(File specFile) throws OpenApiParseException { ... }
}
```

## Adding a vendor extension processor

Vendor extensions on operations (`x-internal`, `x-beta`, etc.) are handled by `ExtensionProcessor`. To add a new one:

```java
public class XOwnerExtensionProcessor implements ExtensionProcessor {
    @Override
    public boolean supports(String key) {
        return "x-owner".equals(key);
    }

    @Override
    public ExtensionResult process(String key, Object value, ExtensionContext ctx) {
        return new ExtensionResult(
                /* nameOverride */ null,
                /* descriptionAppend */ "Owner: " + value);
    }
}
```

Register it in your `ExtensionProcessorChain`:

```java
new ExtensionProcessorChain(List.of(
        new XInternalExtensionProcessor(),
        new XBetaExtensionProcessor(),
        new XSummaryExtensionProcessor(),
        new XDeprecatedSinceExtensionProcessor(),
        new XOwnerExtensionProcessor()));
```

Notes:
- Order matters for **name overrides**: later processors win.
- **Description fragments** are concatenated in encounter order, separated by newlines.
- A processor that returns no override and no append is a no-op.

## Adding a security injector

`SecurityInjector` produces request mutations (`HttpHeader`, `HttpQueryParam`) and `EnvironmentVariable`s for a given OpenAPI security scheme. To support a new scheme (or override an existing one), implement:

```java
public class MutualTlsSecurityInjector implements SecurityInjector {
    @Override
    public boolean supports(SecurityScheme scheme) {
        return scheme != null && "mutualTLS".equalsIgnoreCase(scheme.getType().toString());
    }

    @Override
    public SecurityInjection inject(SecurityScheme scheme, String name) {
        return new SecurityInjection(
                /* headers */ List.of(),
                /* queryParams */ List.of(),
                /* envVars */ List.of(new EnvironmentVariable("MTLS_CLIENT_CERT", "")));
    }
}
```

Register it ahead of the fallback:

```java
new DefaultSecurityInjectorFactory(List.of(
        new BearerSecurityInjector(),
        new BasicAuthSecurityInjector(),
        new ApiKeyHeaderSecurityInjector(),
        new ApiKeyQuerySecurityInjector(),
        new ApiKeyCookieSecurityInjector(),
        new OAuth2SecurityInjector(),
        new MutualTlsSecurityInjector(),
        new NoOpSecurityInjector()));
```

`DefaultSecurityInjectorFactory` walks the list in order and returns the first injector whose `supports(scheme)` is `true`. Always keep `NoOpSecurityInjector` last — it's the safety net.

## Customising example generation

`SchemaExampleGenerator#generate(Schema<?>, OpenAPI) -> Object` is the contract. The default chain dispatches by schema kind (primitive, array, object, composed, nullable). Common reasons to customise:

- **Honour `examples`/`example` from the spec** — wrap the chain in a generator that returns `schema.getExample()` when present, else delegates.
- **Domain-specific defaults** — e.g. always return a valid ISO-3166 country code for `format: country`.

`DelegatingSchemaExampleGenerator` is provided to break circular wiring: object/array generators recurse via the chain, but the chain is constructed *after* its members. Set the delegate after assembly:

```java
DelegatingSchemaExampleGenerator chain = new DelegatingSchemaExampleGenerator();
SchemaExampleGenerator object = new ObjectSchemaExampleGenerator(chain);
SchemaExampleGenerator array  = new ArraySchemaExampleGenerator(chain);
// ... compose all generators into `root`
chain.setDelegate(root);
```

## Choosing an ID strategy

| Implementation | Behaviour | When to use |
|----------------|-----------|-------------|
| `DeterministicIdGenerator` | SHA-256(context) → `prefix_<16 hex>` | Default. Stable diffs across regenerations. |
| `UUIDGenerator` | Random UUIDs | When IDs must be globally unique across regenerations (rare). |

You can implement `IdGenerator` for any other strategy (e.g. monotonically increasing IDs from a database).

## Replacing writers

`CollectionWriter` and `EnvironmentWriter` write strings/`AdditionalFile`s to disk. Replace either to:

- write to an `OutputStream` instead of a `File`;
- post the result to a remote API (e.g. Postman API);
- skip writing during dry runs.

The orchestrator depends only on `write(String, GenerationConfig) -> File` and `writeAll(List<AdditionalFile>, GenerationConfig) -> List<File>`. The returned `File`s populate `GenerationResult`, so even an in-memory writer should return something representative.

## Replacing the serializer

`CollectionSerializer` converts a generator's in-memory model to the JSON string that gets written. Default: `JacksonCollectionSerializer`. Override if you need:

- pretty printing toggles;
- non-default Jackson modules;
- a different serialisation library.

This contract is consumed by `FileCollectionWriter` and `FileEnvironmentWriter`, not by the orchestrator directly.
