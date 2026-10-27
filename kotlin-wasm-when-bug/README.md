# Kotlin/Wasm `when` branch dropped in production

A `when` without a subject loses a branch that compares a different expression against a constant already used
by an earlier `x == <const>` branch. This happens only in the production Wasm executable, on Kotlin 2.4.0, 2.4.21
and 2.5.0-Beta2.

```kotlin
when {
    x == 0 -> "x is 0"
    x == 1 -> "x is 1"
    y == 0 -> "y is 0" // never taken in production
    else -> "else"
}
```

```
./gradlew :kotlin-wasm-when-bug:wasmJsNodeDevelopmentRun   # y is 0
./gradlew :kotlin-wasm-when-bug:wasmJsNodeProductionRun    # else
```

The branch is taken again when:

- the constant is new (`y == 5`), or
- there is only one `x == <const>` branch.

The `.wasm` is already wrong before Binaryen runs (`build/compileSync/wasmJs/main/productionExecutable/kotlin/`).

Workaround: use `when (x)` with a subject and move the other condition into `else`.
