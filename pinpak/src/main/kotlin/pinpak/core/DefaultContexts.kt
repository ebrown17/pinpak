package pinpak.core

abstract class BaseContext internal constructor(val name: String, val pipeline: AbstractPipeline) {
    var next: BaseContext = this
    var previous: BaseContext = this
    abstract fun pumpData(data: Any)
    abstract fun pumpException(data: Any, error: Throwable)
}

@Suppress("TooGenericExceptionCaught")
internal class HeadContext internal constructor(name: String, pipeline: AbstractPipeline) :
    BaseContext(name, pipeline) {
    override fun pumpData(data: Any) {
        try {
            next.pumpData(data)
        } catch (e: Exception) {
            pumpException(data, e)
        }
    }

    override fun pumpException(data: Any, error: Throwable) {
        next.pumpException(data, error)
    }
}

@Suppress("TooGenericExceptionCaught")
internal class TailContext internal constructor(name: String, pipeline: AbstractPipeline) :
    BaseContext(name, pipeline) {

    override fun pumpData(data: Any) {
        try {
            pipeline.deliver(name, data)
        } catch (e: Exception) {
            pumpException(data, e)
        }
    }

    override fun pumpException(data: Any, error: Throwable) {
        pipeline.eject(name, data, error)
    }
}

@Suppress("TooGenericExceptionCaught")
class InterceptorContext(
    name: String,
    pipeline: AbstractPipeline,
    val interceptor: BaseInterceptor
) :
    BaseContext(name, pipeline) {

    override fun pumpData(data: Any) {
        try {
            interceptor.readData0(next, data)
        } catch (e: Exception) {
            pumpException(data, e)
        }
    }

    override fun pumpException(data: Any, error: Throwable) {
        next.pumpException(data, error)
    }
}
