# K/Wasm: external values are boxed when passed through `apply`, `also`, `let`, ...

[`main.kt`](src/wasmJsMain/kotlin/main.kt) times 1,000,000 calls of each:

```
./gradlew :kotlin-wasm-jso-boxing:wasmJsNodeDevelopmentRun
./gradlew :kotlin-wasm-jso-boxing:wasmJsNodeProductionRun
```

```
withApply: 480 ms, withoutScopeFunction: 47 ms
withApply: 1112 ms, withoutScopeFunction: 42 ms
withApply: 486 ms, withoutScopeFunction: 85 ms
```

`withApply` should be as fast as `withoutScopeFunction`.
