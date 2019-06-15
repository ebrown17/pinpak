package eb

import org.slf4j.LoggerFactory

interface BaseInterceptor {
    fun readData(context: BaseContext,data: String)
}

class PassThroughInterceptor: BaseInterceptor {
    private val logger = LoggerFactory.getLogger(PassThroughInterceptor::class.java)
    override fun readData(context: BaseContext, data: String) {
        logger.info("Got $data passing to ${context.name}")
        context.passOnData(data)
    }

}