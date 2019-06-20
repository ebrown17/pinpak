package pinpak

abstract class BaseContext(val name: String, val pipeline: Pipeline) {
    var next: BaseContext = this
    var previous: BaseContext = this
    abstract fun passOnData(data: Any)
    abstract fun passOnException(error: Throwable)

}

class HeadContext(name: String, pipeline: Pipeline) : BaseContext(name, pipeline) {
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

class TailContext(name: String, pipeline: Pipeline) : BaseContext(name, pipeline) {

    override fun passOnData(data: Any) {
        try {
            pipeline.eject(data)
        } catch (e: Exception) {
            passOnException(e)
        }
    }

    override fun passOnException(error: Throwable) {
        pipeline.eject(error)
    }

}

class InterceptorContext(name: String, pipeline: Pipeline, private val interceptor: BaseInterceptor) :
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
