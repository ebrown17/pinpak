package pinpak.core

import org.slf4j.LoggerFactory
import java.lang.Exception

interface BaseInterceptor {
  var name: String
  fun readData0(context: BaseContext, data: Any)
}

@Suppress("TooGenericExceptionCaught")
abstract class AbstractInterceptor<I> : BaseInterceptor {
  override lateinit var name: String
  override fun readData0(context: BaseContext, data: Any) {
    try {
      @Suppress("UNCHECKED_CAST")
      readData(context, data as I)
    } catch (e: Exception) {
      context.passOnData(data)
    }
  }

  abstract fun readData(context: BaseContext, data: I)
}

class PassThroughStringInterceptorChecker(private val value: String) :
  AbstractInterceptor<String>() {
  private val logger = LoggerFactory.getLogger(PassThroughStringInterceptor::class.java)
  override fun readData(context: BaseContext, data: String) {
    if (value == data) {
      logger.info("[$name] got $data, KEEPING!")
    } else {
      context.passOnData(data)
    }
  }
}

class PassThroughStringInterceptor : AbstractInterceptor<String>() {
  private val logger = LoggerFactory.getLogger(PassThroughStringInterceptor::class.java)
  override fun readData(context: BaseContext, data: String) {
    logger.info("[{} got $data passing to [{}]",name,context.name)
    context.passOnData(data)
  }
}

class PassThroughIntegerInterceptor : AbstractInterceptor<Int>() {
  private val logger = LoggerFactory.getLogger(PassThroughIntegerInterceptor::class.java)
  override fun readData(context: BaseContext, data: Int) {
    logger.info("[{} got $data passing to [{}]",name,context.name)
    context.passOnData(data)
  }
}
