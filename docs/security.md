# Security

This page documents how the library translates OpenAPI security schemes into request-level changes (headers, query params) and environment variables that downstream tools (Postman, Insomnia) can fill in at runtime.

## Components

| Component | Role |
|-----------|------|
| `SecurityApplier` | Top-level entry point. Produces a `SecurityInjection` for a single operation or for the whole spec. |
| `SecuritySchemeResolver` | Looks up the schemes referenced by an operation in `components.securitySchemes` and asks the factory for an injector. |
| `SecurityInjectorFactory` | Returns the right `SecurityInjector` for a given `SecurityScheme`. |
| `SecurityInjector` | Implements one auth flavour. Produces headers, query params, and environment variables. |

## Flow

For each operation:

1. `SecurityApplier#apply(op, openApi)` is called.
2. The applier delegates to `SecuritySchemeResolver#resolve`.
3. The resolver reads `op.getSecurity()` (or the spec-level `security` if the operation does not override it), looks each scheme up in `components.securitySchemes`, and asks the `SecurityInjectorFactory` to pick an injector.
4. The injector emits a `SecurityInjection`:
   ```
   record SecurityInjection(
       List<HttpHeader>           headers,
       List<HttpQueryParam>       queryParams,
       List<EnvironmentVariable>  variables) { }
   ```
5. The generator merges those into the request being built.

For the global scope, `DefaultSecurityApplier#applyGlobal(openApi)` walks every operation, collects all `EnvironmentVariable`s, and deduplicates them by name (preserving first-seen order). This is what populates the per-server environment files.

## Built-in schemes

| OpenAPI scheme | Injector | Effect |
|----------------|----------|--------|
| `http`, `bearer` | `BearerSecurityInjector` | Adds `Authorization: Bearer {{token}}` header; declares a token env var. |
| `http`, `basic` | `BasicAuthSecurityInjector` | Adds Basic auth; declares username/password env vars. |
| `apiKey` in `header` | `ApiKeyHeaderSecurityInjector` | Adds the configured header; declares its env var. |
| `apiKey` in `query` | `ApiKeyQuerySecurityInjector` | Adds the configured query param; declares its env var. |
| `apiKey` in `cookie` | `ApiKeyCookieSecurityInjector` | Adds a `Cookie` header; declares its env var. |
| `oauth2` | `OAuth2SecurityInjector` | Declares OAuth env vars (token URL, client id/secret, scopes). |
| anything else | `NoOpSecurityInjector` | Returns an empty injection. Always last in the chain. |

## Resolution order

`DefaultSecurityInjectorFactory` evaluates injectors in the order they were registered and returns the first whose `supports(scheme)` is `true`. The default order is:

```
Bearer → Basic → ApiKeyHeader → ApiKeyQuery → ApiKeyCookie → OAuth2 → NoOp
```

Two practical consequences:

- More specific injectors should appear before more permissive ones.
- `NoOpSecurityInjector` must always be last so unsupported schemes don't crash generation.

## Environment variables

`EnvironmentVariable(name, defaultValue)` is the unit of secret-handling. Generators write these as placeholders into the request (e.g. `Authorization: Bearer {{API_TOKEN}}`) and into the per-server environment files, so users fill in the value once per environment instead of editing every request.

Deduplication happens in `DefaultSecurityApplier#applyGlobal` via a `LinkedHashMap` keyed by name — first occurrence wins, insertion order is preserved.

## Adding a new scheme

See [extensibility.md](extensibility.md#adding-a-security-injector).
