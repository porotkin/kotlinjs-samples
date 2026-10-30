package structure.common.core

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("JvmHttp")

fun logGreeting(service: String) {
    logger.info(jvmGreeting(service))
}
