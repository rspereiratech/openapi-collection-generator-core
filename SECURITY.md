# Security Policy

## Reporting a vulnerability

If you believe you've found a security vulnerability in `openapi-collection-generator-core`, **please do not open a public issue**. Public disclosure before a fix is available puts users at risk.

Instead:

- Use [GitHub's private security advisories](https://github.com/rspereiratech/openapi-collection-generator-core/security/advisories/new) for this repository, or
- Email **rspereiratech@gmail.com** with the subject line `SECURITY: openapi-collection-generator-core`.

Please include, at minimum:

- a description of the vulnerability and its impact;
- a minimal reproduction (an OpenAPI spec, code snippet, or test case);
- the affected version(s);
- any mitigations or workarounds you're aware of.

## What to expect

| Stage | Target time |
|-------|-------------|
| Acknowledge receipt | within 3 business days |
| Initial assessment (severity, scope) | within 7 business days |
| Fix or detailed update | within 30 days for high/critical issues |
| Coordinated public disclosure | after a fix is released |

We'll credit you in the advisory unless you ask to remain anonymous.

## Scope

In scope:

- Code in this repository (`src/main/...`).
- Default behaviour of the shipped components (parsers, injectors, writers).
- Vulnerabilities introduced by direct dependencies declared in `pom.xml`.

Out of scope (please report upstream):

- Issues in third-party libraries (`swagger-parser`, `jackson-databind`) that are not exploitable through this library's documented API.
- Issues in consumer tools (Maven plugin, CLI, Gradle plugin) — report those in their own repositories.
- Vulnerabilities in user-supplied OpenAPI specs themselves.

## Supported versions

Until a 1.0.0 release, only the latest `master` is supported with security fixes. Once 1.0.0 is released, the latest minor of the current major and the previous major's last minor will receive fixes.

## Security best practices for users

When generating collections from untrusted OpenAPI specs:

- Run generation in a sandbox or CI runner with no production credentials in scope.
- Review generated environment files before importing them into Postman / Insomnia — the spec controls variable names and defaults.
- The library does not execute any code from the spec, but downstream tools that import the generated collection might. Treat untrusted specs the same way you'd treat any untrusted input.

## Library security model (architecture)

For details on how the library handles authentication schemes, secrets, and environment variables internally, see [docs/security.md](docs/security.md). That document is descriptive; this one is policy.
