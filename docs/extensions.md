# Vendor extensions

OpenAPI lets specs include implementation-specific metadata under `x-*` keys on operations. This library understands a small set of these out of the box and produces collection-level annotations from them.

The processing pipeline is:

```
Operation
   │  (extensions map)
   ▼
ExtensionProcessorChain
   │  for every (key, value) call every supports() processor
   ▼
ExtensionResult(nameOverride?, descriptionAppend?)
```

When the chain runs:

- Each `ExtensionProcessor` decides whether it handles a given key via `supports(key)`.
- Multiple processors can fire for the same operation; their results are combined by `ExtensionProcessorChain`:
  - **Name overrides:** the *last* processor to set one wins.
  - **Description fragments:** all are concatenated, separated by newlines, in encounter order.
- A processor that returns `ExtensionResult.noChange()` contributes nothing.

## Built-in processors

### `x-internal`

`XInternalExtensionProcessor` — only fires when the value is exactly `true`.

| Spec | Effect on operation |
|------|---------------------|
| `x-internal: true` | Name → `<original name> (Internal)`<br>Description appends: `🔒 Internal endpoint.` |
| `x-internal: false` / absent | No change |

### `x-beta`

`XBetaExtensionProcessor` — only fires when the value is exactly `true`.

| Spec | Effect |
|------|--------|
| `x-beta: true` | Name → `<original name> (Beta)`<br>Description appends: `🧪 Beta — may change without notice.` |
| `x-beta: false` / absent | No change |

### `x-summary`

`XSummaryExtensionProcessor` — uses the value as a full name override.

| Spec | Effect |
|------|--------|
| `x-summary: "List active users"` | Name → `List active users` |
| empty string / non-string / absent | No change |

Useful when the spec's `summary` field is too terse for end users but you can't change it without breaking other tooling.

### `x-deprecated-since`

`XDeprecatedSinceExtensionProcessor` — adds a versioned deprecation note.

| Spec | Effect |
|------|--------|
| `x-deprecated-since: "2.4.0"` | Description appends: `Deprecated since: **2.4.0**` |
| `null` / absent | No change |

This is independent of OpenAPI's `deprecated: true` flag (which is handled separately by `deprecated.DeprecationMarker`); use both together to mark an operation deprecated *and* state when.

## Order of operations

Within a single operation, name overrides from later processors in the chain win. The default registration order is:

```
XInternalExtensionProcessor → XBetaExtensionProcessor → XSummaryExtensionProcessor → XDeprecatedSinceExtensionProcessor
```

So a spec that has both `x-internal: true` *and* `x-summary: "..."` will end up with the `x-summary` value as the name, plus `🔒 Internal endpoint.` in the description.

## Adding your own

See [extensibility.md](extensibility.md#adding-a-vendor-extension-processor).
