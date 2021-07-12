package pinpak.core

import pinpak.logger
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
      if (context.next != context.pipeline.tail) {
        context.pumpData(data)
      } else {
        context.pumpException(e)
      }
    }
  }

  abstract fun readData(context: BaseContext, data: I)
}

class PassThroughStringInterceptor : AbstractInterceptor<String>() {
  private val logger = logger(this)
  override fun readData(context: BaseContext, data: String) {
    logger.info("[{} got $data passing to [{}]", name, context.name)
    context.pumpData(data)
  }
}

class PassThroughIntegerInterceptor : AbstractInterceptor<Int>() {
  private val logger = logger(this)
  override fun readData(context: BaseContext, data: Int) {
    logger.info("[{} got $data passing to [{}]", name, context.name)
    context.pumpData(data)
  }
}
