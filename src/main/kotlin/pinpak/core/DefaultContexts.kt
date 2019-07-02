package pinpak.core

abstract class BaseContext(val name: String, val pipeline: AbstractPipeline) {
  var next: BaseContext = this
  var previous: BaseContext = this
  abstract fun passOnData(data: Any)
  abstract fun passOnException(error: Throwable)
}

@Suppress("TooGenericExceptionCaught")
class HeadContext(name: String, pipeline: AbstractPipeline) : BaseContext(name, pipeline) {
  override fun passOnData(data: Any) {
    try {
      next.passOnData(data)
    } catch (e: Exception) {
      passOnException(e)
    }
  }

  override fun passOnException(error: Throwable) {
    next.passOnException(error)
  }
}

@Suppress("TooGenericExceptionCaught")
class TailContext(name: String, pipeline: AbstractPipeline) : BaseContext(name, pipeline) {

  override fun passOnData(data: Any) {
    try {
      pipeline.eject(name, data)
    } catch (e: Exception) {
      passOnException(e)
    }
  }

  override fun passOnException(error: Throwable) {
    pipeline.eject(name, error)
  }
}

@Suppress("TooGenericExceptionCaught")
class InterceptorContext(name: String, pipeline: AbstractPipeline, val interceptor: BaseInterceptor) :
  BaseContext(name, pipeline) {

  override fun passOnData(data: Any) {
    try {
      interceptor.readData0(next, data)
    } catch (e: Exception) {
      passOnException(e)
    }
  }

  override fun passOnException(error: Throwable) {
    next.passOnException(error)
  }
}
