import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { compileKotlinMjs, normalizeKotlinMjs } from './react-compiler-pre-plugin.mjs';

const buildDir = fileURLToPath(new URL('../build/', import.meta.url));
const sampleDir = path.join(
  buildDir,
  'compileSync/js/main/developmentExecutable/kotlin/kotlinjs-samples-kotlin-react-compiler/sample/reactcompiler',
);
const bundle = path.join(buildDir, 'dist/js/production/kotlinjs-samples-kotlin-react-compiler.js');

const cases = {
  'Case01VarDeclarations.mjs': {
    functions: ['useFilteredLabels'],
    present: ['[normalizedPrefix] = useState('],
  },
  'Case02FragmentCreateWrapper.mjs': {
    functions: ['CounterCard$lambda', 'InlineFragmentCounter$lambda', 'ZeroArgCounter$lambda'],
  },
  'Case03UseMemoUseCallbackSpilling.mjs': {
    functions: ['useSearchResults', 'useMemoizedList'],
    present: ['useMemo(() =>'],
  },
  'Case04SeskarValueTransformerDeps.mjs': {
    functions: ['useUserRequestUrl'],
  },
  'Case05CustomHookHoistedLambdas.mjs': {
    functions: ['usePolledItems'],
  },
  'Case06HoistedBuilderReceiverMutation.mjs': {
    functions: ['ConfirmBanner$lambda'],
  },
  'Case07StatelessComponentDiscovery.mjs': {
    functions: ['StatusBadge$lambda', 'AnnotatedStatusBadge$lambda'],
  },
  'Case08ImperativeChildrenBuilderScopes.mjs': {
    functions: ['ExpandableSection$lambda'],
    minMemoBlocks: 6,
  },
  'Case09UseStateDestructuring.mjs': {
    functions: ['DestructuredStateDemo$lambda'],
    present: ['[count, setCount] = useState(0)', '[isPending, startTransition] = useTransition()'],
  },
  'Case10TopLevelGettersAndUseEffectWrapper.mjs': {
    functions: ['ChildBadge$lambda', 'EffectAndGettersDemo$lambda'],
    absent: [/useEffect\(\(\) => \{[^}]*return Unit_getInstance/],
  },
  'Case11WrapperMitigationDemo.mjs': {
    functions: ['CompilerFriendlyCounter$lambda'],
  },
};

for (const [file, expected] of Object.entries(cases)) {
  const raw = fs.readFileSync(path.join(sampleDir, file), 'utf8');

  const normalized = normalizeKotlinMjs(raw, file);
  assert.doesNotMatch(normalized, /^\s*var\s/m, `${file}: var declarations left`);
  for (const text of expected.present ?? []) assert.ok(normalized.includes(text), `${file}: missing ${text}`);
  for (const pattern of expected.absent ?? []) assert.doesNotMatch(normalized, pattern, `${file}: still has ${pattern}`);

  for (const compilationMode of ['annotation', 'infer']) {
    const { events } = compileKotlinMjs(raw, file, { compilationMode });
    const errors = events.filter((event) => event.kind === 'CompileError');
    assert.deepEqual(errors, [], `${file}: React Compiler errors in ${compilationMode} mode`);
    if (compilationMode !== 'annotation') continue;

    const compiled = events.filter((event) => event.kind === 'CompileSuccess' && event.fnName);
    assert.deepEqual(compiled.map((event) => event.fnName), expected.functions, `${file}: compiled functions`);
    for (const event of compiled) {
      assert.ok(
        event.memoBlocks >= (expected.minMemoBlocks ?? 1),
        `${file}: ${event.fnName} has ${event.memoBlocks} memo blocks`,
      );
    }
  }
  console.log(`✓ ${file}: ${expected.functions.join(', ')}`);
}

assert.ok(fs.existsSync(bundle), `missing Vite bundle ${bundle}`);
assert.ok(
  fs.readFileSync(bundle, 'utf8').includes('react.memo_cache_sentinel'),
  'Vite bundle was not compiled by React Compiler',
);
console.log(`✓ ${path.relative(buildDir, bundle)} compiled by React Compiler`);
