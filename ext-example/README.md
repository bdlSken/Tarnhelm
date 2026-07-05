# Example Tarnhelm extension

Sample DEX extension that rewrites `bilibili.com` links to `b23.tv`.

## Build

```bash
./bin/build-example-ext.sh
```

Output:
- `app/src/main/assets/extensions/example.dex` (bundled in the APK)
- `ext-example/example.dex` (standalone file for import)

## Install in app

- Tap **Install sample extension** on the Extensions screen when the list is empty, or
- Import `ext-example/example.dex` via the file picker

## Authoring

1. Implement a class named `TarnhelmExt` (any package) implementing `ITarnhelmExt`
2. Return an `ExtService` from `createExtensionService(ExtContext)`
3. Declare `regexes()` for domain-specific matching, or leave empty to run on every processed URL
4. Compile against `app/src/main/java/cn/ac/lz233/tarnhelm/extension/api/`
5. Package classes into a single `.dex` file
