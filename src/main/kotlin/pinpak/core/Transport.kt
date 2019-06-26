package pinpak.core

class Transport private constructor(val config: TransportConfig) {

    val pipeline: Pipeline = config.pipeline
    val transportName = config.name
    private var ejectionHandler: EjectionHandler? = null


    init {
        if (config.handleEjections) {
            ejectionHandler = config.ejectionHandler
            pipeline.registerEjectionHandler(ejectionHandler!!)
        }
    }

    fun injectData(data: Any) {
        pipeline.inject(data)
    }

    fun addInterceptorFirst(name: String, interceptor: BaseInterceptor) {
        pipeline.addFirst(name, interceptor)
    }

    fun addInterceptorLast(name: String, interceptor: BaseInterceptor) {
        pipeline.addLast(name, interceptor)
    }

    fun replaceInterceptor(replaced: String, newName: String, interceptor: BaseInterceptor) {
        pipeline.replace(replaced, newName, interceptor)
    }

    private fun catchEjection(name: String, data: Any) {
        ejectionHandler?.handleEjection(name, data)
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


class EjectionHandler(private val eject: (name: String, Any) -> Unit) {
    fun handleEjection(name: String, data: Any) {
        eject(name, data)
    }
}

class TransportConfig(val name: String) {
    val pipeline: Pipeline = Pipeline(name)
    var handleEjections = false
            private set
    lateinit var ejectionHandler: EjectionHandler
        private set

    fun addInterceptorFirst(name: String, interceptor: BaseInterceptor) {
        pipeline.addFirst(name, interceptor)
    }

    fun addInterceptorLast(name: String, interceptor: BaseInterceptor) {
        pipeline.addLast(name, interceptor)
    }

    fun addEjectionHandler(handler: EjectionHandler) {
        handleEjections = true
        ejectionHandler = handler
    }
}
