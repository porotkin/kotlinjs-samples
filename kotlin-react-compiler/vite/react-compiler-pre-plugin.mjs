import fs from 'node:fs';
import { createRequire } from 'node:module';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

function createProjectRequire() {
  const candidates = [
    path.resolve(process.cwd(), 'build/js/package.json'),
    path.resolve(process.cwd(), '../build/js/package.json'),
    path.resolve(process.cwd(), '../../package.json'),
    path.resolve(process.cwd(), 'package.json'),
  ];
  const packageJson = candidates.find((candidate) => fs.existsSync(candidate));
  return createRequire(packageJson ?? import.meta.url);
}

export const projectRequire = createProjectRequire();
const babel = projectRequire('@babel/core');
const reactCompilerModule = projectRequire('babel-plugin-react-compiler');
const reactCompiler = reactCompilerModule.default ?? reactCompilerModule;
const { parseSync, Visitor } = await import(pathToFileURL(projectRequire.resolve('rolldown/utils')).href);
const { withFilter } = await import(pathToFileURL(projectRequire.resolve('rolldown/filter')).href);

const STATE_HOOKS = new Set(['useState', 'useTransition']);
const CALLBACK_HOOKS = new Set(['useMemo', 'useCallback', 'useEffect']);
const SYNTHETIC_HELPER = /^(?:use[A-Z0-9]\w*|[A-Z]\w*\$lambda)\$/;

const jsxFunction = {
  kind: 'function',
  positionalParams: ['read', 'freeze', 'read'],
  restParam: null,
  calleeEffect: 'read',
  returnType: { kind: 'type', name: 'Any' },
  returnValueKind: 'frozen',
};

export function moduleTypeProvider(moduleName) {
  return moduleName === 'react/jsx-runtime'
    ? { kind: 'object', properties: { jsx: jsxFunction, jsxs: jsxFunction } }
    : null;
}

export function normalizeKotlinMjs(code, filename = 'module.mjs') {
  const { program } = parseSync(filename, code, { lang: 'js' });
  const imports = new Map();
  const functions = new Map();
  for (const statement of program.body) {
    if (statement.type === 'ImportDeclaration') {
      for (const specifier of statement.specifiers) {
        if (specifier.type === 'ImportSpecifier') {
          imports.set(specifier.local.name, {
            source: statement.source.value,
            name: specifier.imported.name ?? specifier.imported.value,
          });
        }
      }
    } else if (statement.type === 'FunctionDeclaration' && statement.id) {
      functions.set(statement.id.name, statement);
    }
  }

  const importOf = (node) => (node?.type === 'Identifier' ? imports.get(node.name) : undefined);
  const isReactHook = (node, names) => {
    const binding = importOf(node);
    return binding?.source === 'react' && names.has(binding.name);
  };
  const isUnitReturn = (statement) =>
    statement?.type === 'ReturnStatement' &&
    statement.argument?.type === 'CallExpression' &&
    importOf(statement.argument.callee)?.name.startsWith('Unit_getInstance');
  const renames = new Map(
    [...functions.keys()]
      .filter((name) => SYNTHETIC_HELPER.test(name) && !functions.has(`_${name}`))
      .map((name) => [name, `_${name}`]),
  );

  const structural = [];
  const leaf = [];
  const liftedCallbacks = [];

  const dropTrailingUnitReturn = (fn) => {
    const statements = fn.body?.type === 'BlockStatement' ? fn.body.body : [];
    const last = statements.at(-1);
    if (isUnitReturn(last)) leaf.push({ start: last.start, end: last.end, text: '' });
  };

  new Visitor({
    VariableDeclaration(node) {
      if (node.kind === 'var') leaf.push({ start: node.start, end: node.start + 3, text: 'let' });
    },
    Identifier(node) {
      const rename = renames.get(node.name);
      if (rename) leaf.push({ start: node.start, end: node.end, text: rename });
    },
    CallExpression(node) {
      if (!isReactHook(node.callee, CALLBACK_HOOKS)) return;
      const callback = node.arguments[0];
      const lifted = callback?.type === 'Identifier' ? functions.get(callback.name) : undefined;
      const isEffect = importOf(node.callee).name === 'useEffect';
      if (lifted && !lifted.generator && !lifted.async) {
        if (isEffect) dropTrailingUnitReturn(lifted);
        liftedCallbacks.push({ reference: callback, lifted });
      } else if (isEffect && callback?.type === 'ArrowFunctionExpression') {
        dropTrailingUnitReturn(callback);
      }
    },
    BlockStatement(node) {
      destructureStateHooks(node.body, isReactHook, structural);
    },
  }).visit(program);

  for (const { reference, lifted } of liftedCallbacks) {
    const params = lifted.params.map((param) => applyEdits(code, leaf, param.start, param.end));
    const body = applyEdits(code, leaf, lifted.body.start, lifted.body.end);
    structural.push({ start: reference.start, end: reference.end, text: `(${params.join(', ')}) => ${body}` });
  }

  return applyEdits(code, [...structural, ...leaf]);
}

function destructureStateHooks(statements, isReactHook, edits) {
  const declarator = (statement) =>
    statement?.type === 'VariableDeclaration' &&
    statement.declarations.length === 1 &&
    statement.declarations[0].id.type === 'Identifier'
      ? statement.declarations[0]
      : undefined;
  const isStateHookCall = (node) => node?.type === 'CallExpression' && isReactHook(node.callee, STATE_HOOKS);
  const replaceBinding = (statement, names) =>
    edits.push({
      start: statement.start,
      end: statement.declarations[0].id.end,
      text: `let [${names.join(', ')}]`,
    });

  for (let i = 0; i < statements.length; i++) {
    const head = declarator(statements[i]);
    if (!head?.init) continue;

    const single = indexedAccess(head.init);
    if (single?.index === 0 && isStateHookCall(single.target)) {
      replaceBinding(statements[i], [head.id.name]);
      edits.push({ start: single.target.end, end: head.init.end, text: '' });
      continue;
    }

    if (!isStateHookCall(head.init)) continue;
    const first = declarator(statements[i + 1]);
    const second = declarator(statements[i + 2]);
    const isComponent = (decl, index) =>
      decl?.init &&
      indexedAccess(decl.init)?.index === index &&
      indexedAccess(decl.init).target.type === 'Identifier' &&
      indexedAccess(decl.init).target.name === head.id.name;
    if (isComponent(first, 0) && isComponent(second, 1)) {
      replaceBinding(statements[i], [first.id.name, second.id.name]);
      edits.push({ start: statements[i + 1].start, end: statements[i + 1].end, text: '' });
      edits.push({ start: statements[i + 2].start, end: statements[i + 2].end, text: '' });
      i += 2;
    }
  }
}

function indexedAccess(node) {
  if (node?.type === 'MemberExpression' && node.computed && typeof node.property.value === 'number') {
    return { target: node.object, index: node.property.value };
  }
  if (
    node?.type === 'CallExpression' &&
    node.callee.type === 'MemberExpression' &&
    !node.callee.computed &&
    node.callee.property.name === 'at' &&
    node.arguments.length === 1 &&
    typeof node.arguments[0].value === 'number'
  ) {
    return { target: node.callee.object, index: node.arguments[0].value };
  }
  return undefined;
}

function applyEdits(code, edits, start = 0, end = code.length) {
  const inRange = edits
    .filter((edit) => edit.start >= start && edit.end <= end)
    .sort((a, b) => a.start - b.start || b.end - b.start - (a.end - a.start));
  let out = '';
  let cursor = start;
  for (const edit of inRange) {
    if (edit.start < cursor) continue;
    out += code.slice(cursor, edit.start) + edit.text;
    cursor = edit.end;
  }
  return out + code.slice(cursor, end);
}

export function compileKotlinMjs(code, filename = 'module.mjs', { compilationMode = 'annotation' } = {}) {
  const events = [];
  const result = babel.transformSync(normalizeKotlinMjs(code, filename), {
    filename,
    sourceType: 'module',
    configFile: false,
    babelrc: false,
    comments: false,
    plugins: [
      [
        reactCompiler,
        {
          compilationMode,
          panicThreshold: 'none',
          environment: { moduleTypeProvider },
          logger: { logEvent: (_filename, event) => events.push(event) },
        },
      ],
    ],
  });
  return { code: result.code, events };
}

export function kotlinReactCompilerVitePlugin({
  compilationMode = 'annotation',
  include = [/sample[\\/]reactcompiler[\\/].*\.mjs$/],
} = {}) {
  const filter = {
    id: { include, exclude: [/node_modules/] },
    code: { include: [/["']use memo["']/] },
  };
  return withFilter(
    {
      name: 'kotlin-react-compiler',
      enforce: 'pre',
      transform: {
        filter,
        handler(code, id) {
          return { code: compileKotlinMjs(code, id, { compilationMode }).code, map: null };
        },
      },
    },
    { transform: filter },
  );
}
