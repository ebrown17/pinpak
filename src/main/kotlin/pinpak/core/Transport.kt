package pinpak.core

class Transport private constructor(val config: TransportConfig) {

    private val pipeline: Pipeline = config.pipeline
    val transportName = config.name
    private var ejectionHandler: EjectionHandler? = null


    init {
        if (config.handleEjections) {
            pipeline.registerEjectionHandler(this)
        }
    }

    fun injectData(data: Any) {
        pipeline.inject(data)
    }

    fun addInterceptorFirst(name: String, interceptor: BaseInterceptor) {
        pipeline.addFirst(name, interceptor)
    }

    fun replaceInterceptor(replaced: String, newName: String, interceptor: BaseInterceptor) {
        pipeline.replace(replaced, newName, interceptor)
    }

    internal fun catchEjection(data: Any) {
        ejectionHandler?.handleEjection(data)
    }

    fun addEjectionHandler(handler: EjectionHandler){
        if(ejectionHandler == null){
            ejectionHandler = handler
        }
    }

    companion object {
        /**replaceInterceptor
         * Creates a Transport with default values
         */
        fun create(name: String): Transport {
            return create(name) { }
        }

        fun create(name: String, config: (TransportConfig) -> Unit): Transport {
            val userConfig = TransportConfig(name)
            config(userConfig)
            return Transport(userConfig)
        }
    }
}



class EjectionHandler(private val eject: (Any) -> Unit) {
    fun handleEjection(data: Any){
        eject(data)
    }
}

class TransportConfig(val name: String) {
    val pipeline: Pipeline = Pipeline(name)
    var handleEjections = false

    fun addInterceptor(name: String, interceptor: BaseInterceptor) {
        pipeline.addLast(name, interceptor)
    }

}
