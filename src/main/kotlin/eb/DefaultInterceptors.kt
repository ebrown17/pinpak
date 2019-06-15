package eb

import org.slf4j.LoggerFactory

open class BaseInterceptor {
    var ctx: InterceptorContext? = null
    private val logger = LoggerFactory.getLogger(BaseInterceptor::class.java)
    open fun readData(context: BaseContext,data: String){
        logger.info("[interceptor {}] got msg {}; passing to {}",ctx?.name,data,context.name)
        context.passOnData(data)
    }

}