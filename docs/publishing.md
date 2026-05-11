# Publishing

This module publishes:

- **SNAPSHOT artifacts** to the Sonatype Central Portal on every push to
  `master`, via [`snapshot.yml`](../.github/workflows/snapshot.yml).
- **Release artifacts** (signed, tagged versions) to Maven Central when a
  `v*` tag is pushed, via [`release.yml`](../.github/workflows/release.yml).

## Coordinates

- **Group ID** — `com.github.rspereiratech`
- **Artifact ID** — `openapi-collection-generator-core`
- **Version** — `1.0.0-SNAPSHOT` (current)

## SNAPSHOT publishing

### Where SNAPSHOTs live

- **Repository URL** —
  <https://central.sonatype.com/repository/maven-snapshots/>
- Public read access (no auth needed to download).

### Triggers

- Every push to `master`.
- Manual run via `workflow_dispatch`.

The workflow refuses to deploy if the project version does not end with
`-SNAPSHOT`, to avoid accidentally pushing a release through this pipeline.

### Consuming SNAPSHOTs

This module's `pom.xml` declares only the dependency coordinates; the
`-SNAPSHOT` resolution is handled by [`ci-settings.xml`](../ci-settings.xml),
which adds the Sonatype snapshot repository to Maven's resolution path.

For **local builds**, either:

- Invoke Maven with the project's settings file:
  ```bash
  mvn --settings ci-settings.xml clean verify
  ```
- Or copy the `profile` / `server` blocks from `ci-settings.xml` into
  your `~/.m2/settings.xml` so resolution works without the `--settings`
  flag.

## Release publishing

Releases are signed with GPG and published through the Sonatype
`central-publishing-maven-plugin` configured in the parent's `release`
profile. The release flow:

1. Bump `<version>` in `pom.xml` from `1.0.0-SNAPSHOT` to e.g. `1.0.0`.
2. Commit and push to `master`.
3. Tag and push: `git tag v1.0.0 && git push --tags`.
4. The [`release.yml`](../.github/workflows/release.yml) workflow runs:
   - Imports the GPG private key from secrets.
   - Runs `mvn -Prelease deploy`, which attaches sources + javadoc jars,
     GPG-signs all artefacts, and uploads through the Central Publishing
     plugin. With `autoPublish=true` the deployment is promoted to the
     public release repository without manual intervention.
5. Bump back to the next `-SNAPSHOT` (e.g. `1.1.0-SNAPSHOT`) in a follow-up
   commit.

## Required secrets

Configure these in **Settings → Secrets and variables → Actions** on this
repository.

| Secret | Used by | Where to get it |
|---|---|---|
| `CENTRAL_USERNAME` | snapshot + release | Central Portal → View Account → Generate User Token → username |
| `CENTRAL_PASSWORD` | snapshot + release | Same place — password portion |
| `GPG_PRIVATE_KEY` | release | `gpg --armor --export-secret-keys <key-id>` |
| `GPG_PASSPHRASE` | release | Passphrase used when generating the GPG key |

## Notes and limitations

- **Parent POM resolution.** Maven resolves the `<parent>` element before
  it reads any `<repositories>` block in the consuming `pom.xml`. The
  parent therefore must be resolvable from `settings.xml` repositories
  (which `ci-settings.xml` provides) or from the local repository.
- **Snapshot reads are public.** No authentication is required to download
  SNAPSHOTs from the Central Portal snapshot URL.
- **Snapshot writes are tokenised.** Uploading requires the
  `CENTRAL_USERNAME` / `CENTRAL_PASSWORD` user-token credentials.
- **Cross-repo coordination.** A change in this repository that depends on
  unreleased changes in the parent POM must wait for the parent repo to
  publish a SNAPSHOT before this one's CI passes. Merge the parent change
  first, wait for its `snapshot.yml` to succeed, then rebase this PR.
  Downstream modules (`postman`, `insomnia`, `maven-plugin`) consume
  `core` SNAPSHOTs in the same way.
- **Releases are irreversible.** A version published to Maven Central
  cannot be removed or overwritten. Verify the release locally
  (`mvn -Prelease verify`) before tagging.
