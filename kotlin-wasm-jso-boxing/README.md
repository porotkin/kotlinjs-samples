# K/Wasm: external values are boxed when passed through `apply`, `also`, `let`, ...

[`main.kt`](src/wasmJsMain/kotlin/main.kt) times 1,000,000 calls of each:

```
./gradlew :kotlin-wasm-jso-boxing:wasmJsNodeDevelopmentRun
./gradlew :kotlin-wasm-jso-boxing:wasmJsNodeProductionRun
```

```
withoutScopeFunction: 20 ms
withoutScopeFunction: 18 ms
withoutScopeFunction: 18 ms
withApply: 363 ms
withApply: 440 ms
withApply: 287 ms
```

`withApply` should be as fast as `withoutScopeFunction`.
