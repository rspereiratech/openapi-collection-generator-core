# Contributing

Thanks for your interest in `openapi-collection-generator-core`. This document describes how to set up the project, the conventions used, and what to expect from a pull request.

## Getting started

### Prerequisites

- Java 17+ (any LTS distribution; e.g. Temurin from [Adoptium](https://adoptium.net/))
- Maven 3.8+
- Git

### Clone and build

```bash
git clone git@github.com:rspereiratech/openapi-collection-generator-core.git
cd openapi-collection-generator-core
mvn clean install
```

Tests run as part of `install`. To run them in isolation:

```bash
mvn test
```

## How to contribute

### Reporting a bug

Open an [issue](https://github.com/rspereiratech/openapi-collection-generator-core/issues/new) with:

- a minimal OpenAPI spec that reproduces the problem (or a public link to one);
- the exact command / API call you ran;
- the expected vs actual output;
- versions: Java, the parent POM (`openapi-collection-generator-parent`), and any consumer (Maven plugin / CLI) version.

### Proposing a change

For anything beyond a small fix, open an issue first describing the change and the motivation. This avoids you spending time on something that isn't a fit, or duplicating in-flight work.

Good change candidates:

- New `CollectionFormat` (e.g. Bruno, HTTP file) — see [docs/extensibility.md](docs/extensibility.md#adding-a-new-collection-format).
- New `SecurityInjector` for an auth flavour we don't yet cover.
- New `ExtensionProcessor` for a widely-used `x-*` vendor extension.
- Fixes/improvements to schema example generation.

### Pull request workflow

1. Fork the repo and create a branch from `master` named after the change (e.g. `feat/bruno-format`, `fix/composed-schema-recursion`).
2. Make your changes in small, reviewable commits.
3. Add or update tests. We use **JUnit 5** (`org.junit.jupiter.api.Assertions`) + **Mockito**. New behaviour without tests will not be merged. Do not introduce AssertJ.
4. Run `mvn clean install` locally — both compile and tests must be green.
5. Update relevant docs under [`docs/`](docs/README.md) and the top-level [`README.md`](README.md) if your change is user-visible.
6. Open a PR. Link the issue it resolves. Describe what changed, why, and any trade-offs you considered.

### Code style

- Public types and methods carry **Javadoc**. Document the `why`/contract, not the obvious `what`.
- Prefer small interfaces over large classes. The library is built around replaceable single-purpose components — keep new code in that style.
- Don't add `null` checks at internal call sites; only validate at public entry points.
- No `@SuppressWarnings` without a comment explaining the reason.
- No introducing new dependencies without discussing in the issue first.

### Commit messages

- One logical change per commit.
- Imperative mood: `Add Bruno collection generator`, not `Added` or `Adds`.
- First line ≤ 72 chars; wrap the body at ~80.
- Reference the issue with `Refs #NN` or `Closes #NN` when applicable.

## Project layout

| Path | Contents |
|------|----------|
| `src/main/java/.../core/` | Top-level orchestrator, request/result records |
| `.../core/loader/` | Spec validation |
| `.../core/parser/` | OpenAPI parsing |
| `.../core/generator/` | `CollectionGenerator` contract |
| `.../core/factory/` | `CollectionGeneratorFactory` |
| `.../core/serializer/` | JSON serialisation |
| `.../core/writer/` | File writers (collection + environment) |
| `.../core/example/` | Schema example generation chain |
| `.../core/security/` | Security applier, resolver, factory, injectors |
| `.../core/extension/` | Vendor extension processors |
| `.../core/schema/` | `$ref` and discriminator resolution |
| `.../core/server/` | Server → environment generation |
| `.../core/id/` | Deterministic & UUID ID generators |
| `docs/` | Architecture and how-to docs |

## Releasing

Releases are managed from the parent POM `openapi-collection-generator-parent`. Maintainers tag a release by bumping the version, updating the changelog, and pushing the tag.

## License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).
