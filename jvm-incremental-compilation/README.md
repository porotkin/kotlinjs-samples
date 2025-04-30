# Incremental Kotlin JVM error

### Error output

`../jvm-incremental-compilation/server/src/main/kotlin/com/example/Main.kt:6:18 Unresolved reference 'Provider'`

### How to get

1. Run `jvm-incremental-compilation/server/src/main/kotlin/com/example/Main.kt` // compiles fine
2. Make a change in that `main()` function
3. Re-run that `Main.kt` // `compileKotlin` fails with an error from above

Note: there's a tricky part with the same filename of `Provider.kt` files, as well as only a typealias (which is
inlined) in the common package
