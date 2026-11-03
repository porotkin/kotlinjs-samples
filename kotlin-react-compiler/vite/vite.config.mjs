import { cwd } from 'node:process';
import { defineConfig, loadEnv } from 'vite';
import { kotlinReactCompilerVitePlugin } from './react-compiler-pre-plugin.mjs';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, cwd(), '');
  return {
    plugins: [kotlinReactCompilerVitePlugin({ compilationMode: 'annotation' })],
    build: {
      target: 'es2020',
      minify: mode === 'production' ? 'oxc' : false,
      rolldownOptions: {
        input: [env.ENTRY_PATH],
        output: {
          entryFileNames: '[name].js',
        },
      },
    },
  };
});
