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

- **Long-press** the Import FAB on the Extensions screen, or
- Import `ext-example/example.dex` via the file picker

## Authoring

1. Implement `TarnhelmExt` (`ITarnhelmExt`) with entry class name `TarnhelmExt`
2. Return an `ExtService` from `createExtensionService(ExtContext)`
3. Compile against `app/src/main/java/cn/ac/lz233/tarnhelm/extension/api/`
4. Package classes into a single `.dex` file
