package eb

abstract class BaseContext(val name: String, val pipeline: Pipeline ){
    var next : BaseContext = this
    var previous : BaseContext = this
    abstract fun  passOnData(data: Any)
    abstract  fun passOnException(error: Throwable)

}

class HeadContext(name: String,pipeline: Pipeline): BaseContext(name,pipeline){
    override fun passOnData(data: Any) {
        next.passOnData(data)
    }

    override fun passOnException(error: Throwable) {
        next.passOnException(error)
    }
}

class TailContext(name: String,pipeline: Pipeline): BaseContext(name,pipeline){
    override fun passOnData(data: Any){pipeline.eject(data)}

    override fun passOnException(error: Throwable) {
        pipeline.eject(error)
    }

}

class InterceptorContext(name: String,pipeline: Pipeline, private val interceptor: BaseInterceptor): BaseContext(name,pipeline) {
    override fun passOnData(data: Any){
        interceptor.readData0(next ,data)
    }

    override fun passOnException(error: Throwable) {
        next.passOnException(error)
    }
}
