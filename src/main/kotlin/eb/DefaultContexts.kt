package eb

abstract class BaseContext(val name: String, val pipeline: Pipeline ){
    var next : BaseContext = this
    var previous : BaseContext = this
    abstract fun  passOnData(data: String)

}

class HeadContext(name: String,pipeline: Pipeline): BaseContext(name,pipeline){
    override fun passOnData(data: String) {
        next.passOnData(data)
    }

    fun inject(data: String){
        passOnData(data)
    }
}

class TailContext(name: String,pipeline: Pipeline): BaseContext(name,pipeline){
    override fun passOnData(data: String){pipeline.eject(data)}
}

class InterceptorContext(name: String,pipeline: Pipeline, private val interceptor: BaseInterceptor): BaseContext(name,pipeline) {
    override fun passOnData(data: String){
        interceptor.readData(next ,data)
    }
}