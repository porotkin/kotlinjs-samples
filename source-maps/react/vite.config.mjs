import {defineConfig, loadEnv} from 'vite'
import {sentryVitePlugin} from "@sentry/vite-plugin";
import sourcemaps from 'rollup-plugin-sourcemaps'

export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd(), '')
    const sentryToken = env.VITE_SENTRY_AUTH_TOKEN

    return {
        root: "kotlin",
        plugins: [
            sourcemaps(),
            sentryVitePlugin({
                disable: !sentryToken,
                authToken: sentryToken,
                org: env.VITE_SENTRY_ORG,
                project: env.VITE_SENTRY_PROJECT,
            }),
        ],
    }
})
