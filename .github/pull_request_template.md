## Summary

<!-- What changes and why. Keep it brief; details go in the sections below. -->

## Type of change

<!-- Mark with an `x` everything that applies. -->

- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Breaking change (fix or feature that would change existing behaviour)
- [ ] Documentation only
- [ ] Refactor / internal cleanup
- [ ] Build / CI / tooling

## Related issues

<!-- Link any issues this PR closes or relates to. Use `Closes #123` to auto-close. -->

Closes #

## User-visible impact on the generated Insomnia export

<!--
Does this change the JSON output that ends up in Insomnia (fields, naming,
URLs, headers, body shape, deprecation markers, folder layout, …)?
If yes, describe the before/after.
-->

## Downstream modules

<!--
Does this change require follow-up work in `-core` or `-maven-plugin`?
If yes, link the corresponding issues or PRs.
-->

- [ ] No follow-up needed
- [ ] Follow-up required in `-core` — link:
- [ ] Follow-up required in `-maven-plugin` — link:

## How was this tested?

<!--
Describe the tests you added or ran. Include sample OpenAPI inputs when
relevant. Avoid network/file fixtures outside `src/test/resources/`.
-->

## Checklist

- [ ] `mvn clean verify` passes locally.
- [ ] New code is covered by unit tests (happy path + at least one fallback branch).
- [ ] Public APIs and behaviour changes are documented in `docs/`.
- [ ] Commit messages are descriptive and free of unrelated noise.
- [ ] My change belongs in this module (see the scope section in [CONTRIBUTING.md](../CONTRIBUTING.md)).
- [ ] I have read and follow the [Code of Conduct](../CODE_OF_CONDUCT.md).
