# Kotlin/JS & React Compiler Compatibility

This sample project pins and reproduces the compatibility discrepancies between **Kotlin/JS + Kotlin Wrappers (
`kotlin-react`) + Seskar** output and the **React Compiler** (`babel-plugin-react-compiler`)

Each case is isolated in its own file under [
`src/jsMain/kotlin/sample/reactcompiler/`](src/jsMain/kotlin/sample/reactcompiler/) so that per-file ES2015 compilation
emits a dedicated `.mjs` module that can be inspected or pasted directly into
the [React Compiler Playground](https://playground.react.dev/).

## Building the `.mjs` Output

```bash
./gradlew :kotlin-react-compiler:compileDevelopmentExecutableKotlinJs
```

Generated `.mjs` modules are written to:

```text
kotlin-react-compiler/build/compileSync/js/main/developmentExecutable/kotlin/kotlinjs-samples-kotlin-react-compiler/sample/reactcompiler/
```

---

## Summary Matrix: Where Each Discrepancy Is Solved

| Case                                                                                                  | Source File                                                                                                                         | Category                                                 | Solved By                                                                                                 |
|:------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------|:----------------------------------------------------------------------------------------------------------|
| **[01](#case-01-var-variable-declarations-buildhir-bailout-in-kotlin--25)**                           | [`Case01VarDeclarations.kt`](src/jsMain/kotlin/sample/reactcompiler/Case01VarDeclarations.kt)                                       | Hard Bailout (`BuildHIR`)                                | **Kotlin 2.5** (`KT-67574` emits `const`/`let`) or Vite pre-plugin (`var` -> `let`)                       |
| **[02](#case-02-fc-component-body-wrapped-in-non-inline-fc--fragmentcreate)**                         | [`Case02FragmentCreateWrapper.kt`](src/jsMain/kotlin/sample/reactcompiler/Case02FragmentCreateWrapper.kt)                           | Hard Error (Rules of Hooks / Frozen Props)               | **Kotlin Wrappers (`kotlin-react`)**: inline `FC` + `createElementOrNull` (`jsx(Fragment, builder)`)      |
| **[03](#case-03-usememo-and-usecallback-reversed-parameters--spilled-temporaries)**                   | [`Case03UseMemoUseCallbackSpilling.kt`](src/jsMain/kotlin/sample/reactcompiler/Case03UseMemoUseCallbackSpilling.kt)                 | Hard Error (`BuildHIR` / `DropManualMemoization`)        | **Kotlin Wrappers (`kotlin-react`)** (pass-through `callback()`) or Vite pre-plugin                       |
| **[04](#case-04-seskar-valuetransformer-rewriting-hook-dependency-arrays)**                           | [`Case04SeskarValueTransformerDeps.kt`](src/jsMain/kotlin/sample/reactcompiler/Case04SeskarValueTransformerDeps.kt)                 | Hard Error (Dependency Validation)                       | **Seskar (`ValueTransformer`)** or **Kotlin Wrappers** pass-through `useMemo`/`useCallback`               |
| **[05](#case-05-hoisted-lambdas-and-coroutine-slambdas-inside-use-hooks)**                            | [`Case05CustomHookHoistedLambdas.kt`](src/jsMain/kotlin/sample/reactcompiler/Case05CustomHookHoistedLambdas.kt)                     | Hard Error (Misclassified as Hook)                       | **Kotlin/JS Compiler** (avoid `use[A-Z]` prefix on hoisted helpers) or Vite pre-plugin (`_useFoo$lambda`) |
| **[06](#case-06-mutating-this-receiver-parameters-in-hoisted-childrenbuilder--props-lambdas)**        | [`Case06HoistedBuilderReceiverMutation.kt`](src/jsMain/kotlin/sample/reactcompiler/Case06HoistedBuilderReceiverMutation.kt)         | Hard Error (Frozen Props Mutation)                       | **React Compiler Config** (`compilationMode: "annotation"` with `"use memo"` only on `FC` / `use*`)       |
| **[07](#case-07-stateless-component-discovery-failure-in-infer-mode--missing-builtinprops-type)**     | [`Case07StatelessComponentDiscovery.kt`](src/jsMain/kotlin/sample/reactcompiler/Case07StatelessComponentDiscovery.kt)               | Skipped Component                                        | **Kotlin Wrappers (`kotlin-react`)**: emit `"use memo"` inside `FC`                                       |
| **[08](#case-08-imperative-childrenbuilder-thiscreate-addchild-setdefaultkey-vs-jsxelement-ast)**     | [`Case08ImperativeChildrenBuilderScopes.kt`](src/jsMain/kotlin/sample/reactcompiler/Case08ImperativeChildrenBuilderScopes.kt)       | Degraded Memoization Scopes                              | **Kotlin IR Plugin**: move root builder after hooks & lower `@ElementBuilder` to declarative `jsx()`      |
| **[09](#case-09-usestate--usetransition-destructuring-via-indexed-access)**                           | [`Case09UseStateDestructuring.kt`](src/jsMain/kotlin/sample/reactcompiler/Case09UseStateDestructuring.kt)                           | Degraded Type Inference (`SetState` / `StartTransition`) | **Vite Pre-Plugin** (`[state, setState] = useState()`)                                                    |
| **[10](#case-10-top-level-propertysingletonenum-getters-get_foo-foo_getinstance--useeffect-wrapper)** | [`Case10TopLevelGettersAndUseEffectWrapper.kt`](src/jsMain/kotlin/sample/reactcompiler/Case10TopLevelGettersAndUseEffectWrapper.kt) | Extra `_c(N)` Cache Slots                                | **Kotlin Wrappers / IR**                                                                                  |
| **[11](#case-11-kotlin-wrappers-mitigation-demo)**                                                    | [`Case11WrapperMitigationDemo.kt`](src/jsMain/kotlin/sample/reactcompiler/Case11WrapperMitigationDemo.kt)                           | Reference Mitigation                                     | Demonstrates `compilerFriendlyFC`, `useMemoInline`, and `useCallbackInline`                               |

---

## Detailed Discrepancy Breakdown

### Category A: Hard Compiler Bailouts & Validation Errors (`BuildHIR` & Validation Passes)

#### Case 01: `var` Variable Declarations (`BuildHIR` Bailout in Kotlin < 2.5)

- **Source:** [`Case01VarDeclarations.kt`](src/jsMain/kotlin/sample/reactcompiler/Case01VarDeclarations.kt) ->
  `Case01VarDeclarations.mjs`
- **Cause:** In Kotlin 2.4.x and earlier, the Kotlin/JS IR backend emits `var` for all local variables and compiler
  temporaries (`var tmp$ret$0 = ...`, `var normalizedPrefix = ...`), even when targeting ES2015 modules.
- **Generated `.mjs`:**
  ```js
  function useFilteredLabels(items, prefix) {
    var tmp$ret$0 = toString(trim(isCharSequence(prefix) ? prefix : THROW_CCE()));
    var normalizedPrefix = useState(tmp$ret$0)[0];
    var hasPrefix = charSequenceLength(normalizedPrefix) > 0;
    ...
  }
  ```
- **React Compiler Behavior:** `BuildHIR::lowerStatement` only supports `let` and `const` in `VariableDeclaration` and
  immediately bails out with:
  ```text
  Todo: (BuildHIR::lowerStatement) Handle var kinds in VariableDeclaration
  ```
- **Fix:** Resolved natively in **Kotlin 2.5** (`KT-67574`, which emits `const` and `let`), or mitigated on Kotlin 2.4.x
  by rewriting `var` declarations to `let` in the Vite pre-transform plugin.

---

#### Case 02: `FC` Component Body Wrapped in Non-Inline `FC` / `Fragment.create`

- **Source:** [
  `Case02FragmentCreateWrapper.kt`](src/jsMain/kotlin/sample/reactcompiler/Case02FragmentCreateWrapper.kt) ->
  `Case02FragmentCreateWrapper.mjs`
- **Cause & React Compiler Behavior:**
    1. **Case 02a — Upstream `kotlin-react` non-inline `FC` (`CounterCard`):**
       In upstream `kotlin-react`, `fun <P : Props> FC(block)` (`kotlin-react/react/FC.mjs`) is **not `inline`**. The
       actual React component arrow function `(props) => createElementOrNull(...)` is hidden inside
       `kotlin-react/react/FC.mjs`, while the user module emits a 2-parameter builder function
       `CounterCard$lambda($this$FC, props)` passed to `memo(FC(CounterCard$lambda))`:
       ```js
       function CounterCard$lambda($this$FC, props) {
         var _destruct__k2r9zo = useState(0);
         var count = _destruct__k2r9zo[0];
         var setCount = _destruct__k2r9zo[1];
         setDefaultKey($this$FC, '@rdk/40_4');
         ...
         addChild($this$FC, 'div', block);
         return Unit_getInstance();
       }
       ```
       When React Compiler compiles `CounterCard$lambda($this$FC, props)`:
        - It treats the first parameter `$this$FC` as frozen component `props` and fails validation when
          `setDefaultKey` / `addChild` mutates `$this$FC`:
          ```text
          Error: This value cannot be modified. Modifying component props or hook arguments is not allowed.
          ```
        - Even if parameter mutation were ignored, `PruneNonEscapingScopes` prunes all reactive scopes because
          `$this$FC` is a parameter rather than the return value (`return Unit_getInstance()`).

    2. **Case 02b — Inlining `FC` on top of `Fragment.create` for 1-arg `FC<Props>` (`InlineFragmentCounter`):**
       When `FC` and `createElementOrNull` are marked `inline` on top of `Fragment.create { block(props) }`,
       `ElementType.create` (`kotlin-react/react/create.mjs`) is still not `inline`, so the component body is compiled
       inside a nested arrow function passed to `create(Fragment, ...)`:
       ```js
       function InlineFragmentCounter$lambda(props) {
         var tmp = Fragment;
         var this_0 = create(tmp, ($this$create) => {
           var props_0 = props;
           var _destruct__k2r9zo = useState(0);
           ...
         });
         ...
       }
       ```
       Calling `useState` inside the nested arrow function `($this$create) => { ... }` fails with:
       ```text
       Error: Hooks must be called at the top level in the body of a function component or custom hook, and may not be called within function expressions. Cannot call hook within a function expression.
       ```

    3. **Case 02c — Inlining `FC` on top of `Fragment.create` for 0-arg `FC` (`ZeroArgCounter`):**
       Because a 0-arg `FC { ... }` lambda captures no outer parameters, Kotlin/JS hoists it into a separate top-level
       function `ZeroArgCounter$lambda$lambda($this$create)`:
       ```js
       function ZeroArgCounter$lambda() {
         var tmp = Fragment;
         var this_0 = create(tmp, ZeroArgCounter$lambda$lambda);
         ...
       }
       function ZeroArgCounter$lambda$lambda($this$create) {
         var _destruct__k2r9zo = useState(0);
         ...
       }
       ```
       `ZeroArgCounter$lambda()` has no hooks and no JSX and is skipped, while
       `ZeroArgCounter$lambda$lambda($this$create)` is misidentified as the component with `$this$create` treated as
       frozen `props`.
- **Fix:** Inline **both** `FC` and `createElementOrNull` in `kotlin-react` using `unsafeJso<FragmentProps>()` and
  `jsx(Fragment, builder)` (see [Case 11](#case-11-kotlin-wrappers-mitigation-demo)).

---

#### Case 03: `useMemo` and `useCallback` Reversed Parameters & Spilled Temporaries

- **Source:** [
  `Case03UseMemoUseCallbackSpilling.kt`](src/jsMain/kotlin/sample/reactcompiler/Case03UseMemoUseCallbackSpilling.kt) ->
  `Case03UseMemoUseCallbackSpilling.mjs`
- **Cause:** In `kotlin-react`, `inline fun useMemo(vararg dependencies: Any?, noinline callback: () -> T)` reverses the
  parameter order relative to React's `useMemo(callback, dependencies)`. To preserve Kotlin's left-to-right evaluation
  order (even in 0-arg `useMemo { ... }` where `emptyArray()` is inlined), the Kotlin IR inliner spills both arguments
  into local variables. Forwarding `vararg elements` in a helper hook lowers to `elements.slice()`.
- **Generated `.mjs`:**
  ```js
  var tmp0_0 = [items, query];
  var callback_0 = () => { ... };
  var filtered = useMemo(callback_0, tmp0_0);

  // and in vararg forwarding:
  var tmp0 = elements.slice();
  var callback = () => asList(elements);
  return useMemo(callback, tmp0);
  ```
- **React Compiler Behavior:** Fails in `BuildHIR` / `DropManualMemoization` with two hard errors:
  ```text
  Error: Expected the dependency list for useMemo to be an array literal.
  Error: Expected the first argument to be an inline function expression.
  ```
- **Fix:** Make `useMemo` and `useCallback` inline pass-throughs (`callback()` / `callback`) when React Compiler is
  active (see [Case 11](#case-11-kotlin-wrappers-mitigation-demo)), or inline `callback` and `tmp0` in the Vite
  pre-transform plugin.

---

#### Case 04: Seskar `ValueTransformer` Rewriting Hook Dependency Arrays

- **Source:** [
  `Case04SeskarValueTransformerDeps.kt`](src/jsMain/kotlin/sample/reactcompiler/Case04SeskarValueTransformerDeps.kt) ->
  `Case04SeskarValueTransformerDeps.mjs`
- **Cause:** When passing a Kotlin `value class` (`UserId`), `Duration`, or `Long` in `dependencies` to `useMemo`,
  `useCallback`, or `useEffect`, `seskar.compiler.react.value.backend.ValueTransformer` rewrites the dependency array
  elements in IR into function/constructor calls while the callback body still references the original parameters.
- **Generated `.mjs`:**
  ```js
  function useUserRequestUrl(userId, timeout, version) {
    var tmp0 = [_UserId___get_id__impl__c607dv(userId), toString(new (Duration())(timeout)), version];
    var callback = () => '/api/users/' + _UserId___get_id__impl__c607dv(userId) + ...;
    var endpoint = useMemo(callback, tmp0);
    ...
  }
  ```
- **React Compiler Behavior:** Fails in `BuildHIR` and dependency validation with:
  ```text
  Error: Expected the dependency list to be an array of simple expressions (e.g. `x`, `x.y.z`, `x?.y?.z`).
  Error: Found missing memoization dependencies.
  ```
- **Fix:** Disable `ValueTransformer` on `useMemo` / `useCallback` when React Compiler is enabled (or make `useMemo` /
  `useCallback` inline pass-throughs in `kotlin-react`).

---

#### Case 05: Hoisted Lambdas and Coroutine `slambda`s Inside `use*` Hooks

- **Source:** [
  `Case05CustomHookHoistedLambdas.kt`](src/jsMain/kotlin/sample/reactcompiler/Case05CustomHookHoistedLambdas.kt) ->
  `Case05CustomHookHoistedLambdas.mjs`
- **Cause:** Inside any custom hook `fun usePolledItems(...)`, Kotlin/JS names hoisted non-capturing lambdas and
  coroutine `slambda` factories by prefixing the enclosing function name: `usePolledItems$lambda$lambda`,
  `usePolledItems$slambda`, `usePolledItems$slambda$lambda`.
- **Generated `.mjs`:**
  ```js
  function usePolledItems(items, onTick) {
    ...
    useEffect([items, onTick], usePolledItems$slambda(items, setTickCount, onTick));
    ...
    return to(filtered, () => {
      setTickCount(usePolledItems$lambda$lambda);
      return Unit_getInstance();
    });
  }
  function usePolledItems$slambda$lambda(it) {
    return it + 1 | 0;
  }
  function usePolledItems$slambda($items, $setTickCount, $onTick) {
    return constructCallableReference(($this$useEffect, $completion) => orPromise($completion, function *($completion) {
      ...
      $setTickCount(usePolledItems$slambda$lambda);
      ...
    }), 1);
  }
  function usePolledItems$lambda$lambda(it) {
    return it + 1 | 0;
  }
  ```
- **React Compiler Behavior:** Because `usePolledItems$lambda$lambda` and `usePolledItems$slambda` match
  `/^use[A-Z0-9]/`, React Compiler classifies these synthetic helper functions **as React Hooks**:
    - Passing or calling `usePolledItems$lambda$lambda` inside an effect or callback fails with:
      ```text
      Error: Hooks must be called at the top level in the body of a function component or custom hook, and may not be called within function expressions. Cannot call hook within a function expression.
      ```
    - Calling `usePolledItems$slambda(...)` at the top level of `usePolledItems` causes React Compiler to treat the
      coroutine `slambda` factory as a Hook call, refusing to memoize it.
- **Fix:** Avoid `use[A-Z0-9]` prefixes for hoisted synthetic helper functions in `kotlinc-js`, or prefix them with `_`
  (`_usePolledItems$lambda`, `_usePolledItems$slambda`) in the Vite pre-transform plugin.

---

#### Case 06: Mutating `$this$...` Receiver Parameters in Hoisted `ChildrenBuilder` / `Props` Lambdas

- **Source:** [
  `Case06HoistedBuilderReceiverMutation.kt`](src/jsMain/kotlin/sample/reactcompiler/Case06HoistedBuilderReceiverMutation.kt) ->
  `Case06HoistedBuilderReceiverMutation.mjs`
- **Cause:** Non-capturing child `Props` builder blocks inside a component (such as
  `footer { button { type = ButtonType.button; +"Proceed" } }`) are hoisted to top-level functions named
  `ConfirmBanner$lambda$lambda$lambda$lambda($this$button)` that mutate properties on their first parameter.
- **Generated `.mjs`:**
  ```js
  function ConfirmBanner$lambda$lambda$lambda$lambda($this$button) {
    _init_properties_Case06HoistedBuilderReceiverMutation_kt__mozy0p();
    $this$button.type = 'button';
    addChildNode($this$button, 'Proceed');
    return Unit_getInstance();
  }
  ```
- **React Compiler Behavior:** If compiled by React Compiler (e.g., in `compilationMode: "all"` or if matched by
  component heuristics), React Compiler treats the first parameter `$this$button` as frozen component `props` and fails
  with:
  ```text
  Error: This value cannot be modified. Modifying component props or hook arguments is not allowed.
  ```
- **Fix:** Use `compilationMode: "annotation"` and emit `"use memo"` only on actual `FC` component functions and
  top-level `use*` hooks.

---

### Category B: Component Discovery & Degraded Memoization Quality

#### Case 07: Stateless Component Discovery Failure in `infer` Mode & Missing `BuiltInProps` Type

- **Source:** [
  `Case07StatelessComponentDiscovery.kt`](src/jsMain/kotlin/sample/reactcompiler/Case07StatelessComponentDiscovery.kt) ->
  `Case07StatelessComponentDiscovery.mjs`
- **Cause:** Kotlin/JS names component functions `StatusBadge$lambda` and emits `jsx()` / `addChild()` function calls
  rather than `JSXElement` / `JSXFragment` AST nodes.
- **React Compiler Behavior:**
    1. In default `infer` mode, React Compiler only compiles a PascalCase function if its body directly contains
       `JSXElement` / `JSXFragment` AST nodes or top-level hook calls (`use*`). Even when `Fragment.create` is inlined
       (`StatusBadge`), **every stateless component** has no hook calls and no JSX AST nodes, so React Compiler
       **silently skips all of them** unless `"use memo"` is present (`AnnotatedStatusBadge`).
    2. Even with `"use memo"`, a function without `JSXElement` AST nodes and without hook calls is typed by `InferTypes`
       as `AnnotatedStatusBadge(<unknown> props)` instead of `AnnotatedStatusBadge(props: TObject<BuiltInProps>)`,
       losing the guarantee that `props` is frozen/immutable.

---

#### Case 08: Imperative `ChildrenBuilder` (`$this$create`, `addChild`, `setDefaultKey`) vs. `JSXElement` AST

- **Source:** [
  `Case08ImperativeChildrenBuilderScopes.kt`](src/jsMain/kotlin/sample/reactcompiler/Case08ImperativeChildrenBuilderScopes.kt) ->
  `Case08ImperativeChildrenBuilderScopes.mjs`
- **Generated `.mjs`:**
  ```js
  function ExpandableSection$lambda(props) {
    'use memo';
    var builder = {};
    $l$block: {
      var props_0 = props;
      var _destruct__k2r9zo = useState(false);
      var expanded = _destruct__k2r9zo[0];
      var setExpanded = _destruct__k2r9zo[1];
      if (!props_0.visible) {
        break $l$block;
      }
      setDefaultKey(builder, '@rdk/43_4');
      ...
      addChild(builder, 'div', block);
    }
    var children = buildRootChildren(builder);
    return !(children == null) ? jsx(Fragment, builder) : null;
  }
  ```
- **React Compiler Behavior:**
    1. **Scope Flattening Across Hooks (`FlattenScopesWithHooksOrUseHIR`):** Even when all hooks (`useState(false)`) are
       called at the very top of the `FC` lambda, `var builder = {}` in `FC` is allocated before `block(builder, props)`
       (and thus before `useState(false)`) and mutated by `addChild(builder, ...)` after `useState(false)`. Because
       `builder`'s mutable range spans across a hook call, `FlattenScopesWithHooksOrUseHIR` destroys any reactive scope
       spanning that hook call, leaving the root `jsx(Fragment, builder)` unmemoized.
    2. **Early `return@FC` Lowered to `$l$block: { ... break $l$block; }`:** Because `FC` is an inline function wrapping
       `block(builder, props)`, `if (!props.visible) return@compilerFriendlyFC` (placed after all hooks) compiles to
       `break $l$block` rather than `return null`, keeping `builder` alive across the entire block.
    3. **Coarse/Merged Scopes & Nested Builder Closures:** Even when `builder = {}` is moved after all hooks,
       `addChild(builder, 'div', ($this$div) => { ... })` mutates `builder` and wraps child trees (`header` and
       `section`) inside nested closures. React Compiler does not create reactive scopes inside nested closures and
       merges all mutations to `builder` into a single coarse scope (whereas `JSXElement` AST nodes lower to `JSX`
       instructions with `Create = frozen` and `ImmutableCapture`, allowing independent per-element memoization).

---

#### Case 09: `useState` & `useTransition` Destructuring via Indexed Access (`_destruct[0]`, `_destruct.at(0)`)

- **Source:** [
  `Case09UseStateDestructuring.kt`](src/jsMain/kotlin/sample/reactcompiler/Case09UseStateDestructuring.kt) ->
  `Case09UseStateDestructuring.mjs`
- **Cause & React Compiler Behavior:**
    - `val (count, setCount) = useState(0)` lowers to `_destruct[0]` / `_destruct[1]`, and
      `val (isPending, startTransition) = useTransition()` lowers to `_destruct.at(0)` / `_destruct.at(1)`.
    - `InferTypes` only assigns the `SetState` and `StartTransition` built-in types when destructured via a JS
      `ArrayPattern` (`const [count, setCount] = useState(0)`). With `_destruct[1]` or `_destruct.at(1)`, `setCount` and
      `startTransition` lose their `SetState` / `StartTransition` built-in type guarantees.

---

#### Case 10: Top-Level Property/Singleton/Enum Getters (`get_Foo()`, `Foo_getInstance()`) & `useEffect` Wrapper

- **Source:** [
  `Case10TopLevelGettersAndUseEffectWrapper.kt`](src/jsMain/kotlin/sample/reactcompiler/Case10TopLevelGettersAndUseEffectWrapper.kt) ->
  `Case10TopLevelGettersAndUseEffectWrapper.mjs`
- **Cause & React Compiler Behavior:**
    1. Component references (`get_ChildBadge()`), `object` singletons (`DefaultConfig_getInstance()`), and `enum`
       entries (`BadgeTone_ACCENT_getInstance()`) are emitted as 0-arg function calls rather than module-level constant
       identifiers. React Compiler treats each 0-arg getter call as a potentially allocating function call and burns a
       `Symbol.for("react.memo_cache_sentinel")` slot in `_c(N)` for every getter call.
    2. `kotlin-react`'s `useEffect([props.label], slambda)` (`kotlin-react/react/useEffect.mjs`) reverses the argument
       order `(dependencies, effect)` and wraps the coroutine `slambda`, causing React Compiler to allocate memo cache
       slots for `[props.label]` and the `slambda` closure.

---

#### Case 11: Kotlin Wrappers Mitigation Demo

- **Source:** [
  `Case11WrapperMitigationDemo.kt`](src/jsMain/kotlin/sample/reactcompiler/Case11WrapperMitigationDemo.kt) ->
  `Case11WrapperMitigationDemo.mjs`
- Demonstrates how updating `FC`, `useMemo`, and `useCallback` in `kotlin-react` resolves **Cases 02, 03, 04, and 07**
  directly at the wrapper level:
    - Emits `'use memo';` at the top of the component body (`Case 07`).
    - Inlines root `FragmentProps` builder creation and `jsx(Fragment, builder)` directly into
      `CompilerFriendlyCounter$lambda(props)` so hooks are called at the top level of a 1-parameter `(props)` component
      function (`Case 02`).
    - Makes `useMemoInline` and `useCallbackInline` inline pass-throughs, eliminating reversed-parameter spilled
      temporaries (`Case 03`) and Seskar `ValueTransformer` dependency array rewrites (`Case 04`).
