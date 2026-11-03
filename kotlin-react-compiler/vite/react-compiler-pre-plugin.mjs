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
const { withFilter } = await import(pathToFileURL(projectRequire.resolve('rolldown/filter')).href);

export function compileKotlinMjs(code, filename = 'module.mjs', { compilationMode = 'annotation' } = {}) {
  const events = [];
  const result = babel.transformSync(code, {
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
