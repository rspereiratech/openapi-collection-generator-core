# Schema example generation

When the spec doesn't ship request bodies or response samples, the library synthesises example values from the schema. This page documents the strategy.

## Chain

`SchemaExampleGenerator` is a single-method contract:

```java
Object generate(Schema<?> schema, OpenAPI openApi);
```

The default chain dispatches by schema kind:

```
NullableSchemaExampleGenerator
        │
        ▼
ComposedSchemaExampleGenerator     (allOf / oneOf / anyOf)
        │
        ▼
ObjectSchemaExampleGenerator       (type: object)
        │
        ▼
ArraySchemaExampleGenerator        (type: array)
        │
        ▼
PrimitiveSchemaExampleGenerator    (string / integer / number / boolean)
```

`DelegatingSchemaExampleGenerator` is woven in to break the cycle: object/array generators recurse via the chain, but the chain depends on those generators. The delegate is set once construction is complete.

## Resolution order in `PrimitiveSchemaExampleGenerator`

Primitive examples are resolved in this order — the first match wins:

1. **`x-examples` vendor extension.** If present and non-empty, the first entry's `value` (or the entry itself, if not a map) is returned. Lets you ship rich, named examples in the spec without changing the runtime.
2. **`example` field** on the schema (single OpenAPI example).
3. **First `enum` value**, when the schema declares an enum.
4. **`default` value**, when the schema declares one.
5. **Type-based fallback** (see below).

Step 5 covers schemas without any explicit hint:

| `type` | Result |
|--------|--------|
| `string` (with `format`) | format-specific value (table below) |
| `string` (no `format`) | `"string"` |
| `integer` | `minimum` if set, else `0` |
| `number` | `minimum` if set, else `0.0` |
| `boolean` | `true` |
| anything else | delegates to the next generator in the chain |

Format table:

| `format` | Example value |
|----------|---------------|
| `date` | `2024-01-01` |
| `date-time` | `2024-01-01T00:00:00Z` |
| `uuid` | `00000000-0000-0000-0000-000000000000` |
| `email` | `user@example.com` |
| `uri` | `https://example.com` |
| `byte` | `dGVzdA==` |
| `binary` | `<binary>` |
| `password` | `********` |
| anything else | `"string"` |

## Composed schemas

`ComposedSchemaExampleGenerator` handles `allOf`, `oneOf`, and `anyOf`. For polymorphism it uses `DiscriminatorResolver` to pick a concrete subtype when a `discriminator` is present.

## Nullable schemas

`NullableSchemaExampleGenerator` wraps the rest of the chain. When `nullable: true` *and* no example/default is available, it returns `null` rather than a synthesised value.

## Customisation

Common patterns:

- **Honour spec-level `examples` everywhere** — wrap the chain with a generator that returns `schema.getExample()` first and only delegates when missing.
- **Domain defaults** — inject a generator that intercepts specific `format`s (e.g. `country-code` → `"PT"`).
- **Test fixtures** — replace the chain entirely in tests with one that returns canned values.

See [extensibility.md](extensibility.md#customising-example-generation) for wiring details.
