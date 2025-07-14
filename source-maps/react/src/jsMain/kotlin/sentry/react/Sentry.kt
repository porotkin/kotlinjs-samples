@file:JsModule("@sentry/react")

package sentry.react

import react.dom.client.ErrorHandler

external fun init(options: SentryInitOptions)

external fun reactErrorHandler(): ErrorHandler<*>

external fun replayIntegration(): Any
