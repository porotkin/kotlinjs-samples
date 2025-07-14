import js.reflect.unsafeCast
import react.create
import react.dom.client.RootOptions
import react.dom.client.createRoot
import sentry.react.SentryInitOptions
import sentry.react.init
import sentry.react.reactErrorHandler
import sentry.react.replayIntegration
import web.dom.document
import web.html.HtmlTagName.div

fun main() {
    init(
        SentryInitOptions(
            dsn = SENTRY_DSN,
            sendDefaultPii = true,

            integrations = arrayOf(
                replayIntegration(),
            ),

            replaysSessionSampleRate = 0.1,
            replaysOnErrorSampleRate = 1.0,
        ),
    )

    val container = document.createElement(div)
    document.body.appendChild(container)

    val options = RootOptions(
        onUncaughtError = unsafeCast(reactErrorHandler()),
        onCaughtError = unsafeCast(reactErrorHandler()),
        onRecoverableError = unsafeCast(reactErrorHandler()),
    )
    createRoot(container, options)
        .render(View.create())
}
