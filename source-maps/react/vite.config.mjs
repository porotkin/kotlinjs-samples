import {defineConfig, loadEnv} from 'vite'
import {sentryVitePlugin} from "@sentry/vite-plugin";
import sourcemaps from 'rollup-plugin-sourcemaps'

export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd(), '')

    return {
        root: "kotlin",
        plugins: [
            sourcemaps(),
            sentryVitePlugin({
                org: env.VITE_SENTRY_ORG,
                project: env.VITE_SENTRY_PROJECT,
                authToken: env.VITE_SENTRY_AUTH_TOKEN,
            }),
        ],
    }
})
