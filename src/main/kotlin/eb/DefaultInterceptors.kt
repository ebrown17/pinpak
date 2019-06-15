package eb

import org.slf4j.LoggerFactory

interface BaseInterceptor {
    fun readData(context: BaseContext,data: String)/*{
        context.passOnData(data)
    }*/
}

class DefaultInterceptor: BaseInterceptor {
    private val logger = LoggerFactory.getLogger(DefaultInterceptor::class.java)
    override fun readData(context: BaseContext, data: String) {
        logger.info("Got $data passing to ${context.name}")
        context.passOnData(data)
    }

}