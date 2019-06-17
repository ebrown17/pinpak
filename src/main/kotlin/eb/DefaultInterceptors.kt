package eb

import org.slf4j.LoggerFactory
import java.lang.Exception

interface BaseInterceptor {
    fun readData0(context: BaseContext,data: Any)
}

class PassThroughStringInterceptor: AbstractInterceptor<String>() {
    private val logger = LoggerFactory.getLogger(PassThroughStringInterceptor::class.java)
    override fun readData(context: BaseContext, data: String) {
        logger.info("Got $data passing to ${context.name}")
        context.passOnData(data)
    }
}

class PassThroughIntegerInterceptor: AbstractInterceptor<Int>() {
    private val logger = LoggerFactory.getLogger(PassThroughIntegerInterceptor::class.java)
    override fun readData(context: BaseContext, data: Int) {
        logger.info("Got $data passing to ${context.name}")
        context.passOnData(data)
    }
}

abstract class AbstractInterceptor<I> : BaseInterceptor {

    override fun readData0(context: BaseContext, data: Any) {
        try {
            @Suppress("UNCHECKED_CAST")
            val iData = data as I
            readData(context, iData)
        }
        catch(e : Exception){
            context.passOnData(data)
          //  context.passOnException(e)
        }
    }

    abstract fun readData(context: BaseContext, data: I)
}


