package pinpak.core

import pinpak.logger

class PinPak private constructor(config: PinPakConfig) {

    val pipeline: Pipeline = config.pipeline
    val transportName = config.name
    private var ejectionHandler: EjectionHandler? = null
    private var deliveryHandler: DeliveryHandler? = null

    init {
        if (config.handleEjections) {
            ejectionHandler = config.ejectionHandler
            pipeline.registerEjectionHandler(ejectionHandler!!)
        }
        if (config.handleDeliveries) {
            deliveryHandler = config.deliveryHandler
            pipeline.registerDeliverHandler(deliveryHandler!!)
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

    fun removeInterceptor(name: String) {
        pipeline.remove(name)
    }

    private fun catchEjection(name: String, data: Any, error: Throwable) {
        ejectionHandler?.handleEjection(name, data, error)
    }

    private fun deliverData(name: String, data: Any) {
        deliveryHandler?.handleDelivery(name, data)
    }

    fun getInterceptor(name: String): BaseInterceptor? {
        val context: InterceptorContext? = pipeline.getContext(name) as InterceptorContext?
        return context?.interceptor
    }

    companion object {
    /*
     * Creates a Transport with default values
     */
        fun create(name: String): PinPak {
            return create(name) { }
        }

    /*
     * Creates a Transport with default values
     */
        fun create(name: String, config: (PinPakConfig) -> Unit): PinPak {
            val userConfig = PinPakConfig(name)
            config(userConfig)
            return PinPak(userConfig)
        }
    }
}

class PinPakConfig(val name: String) {
    val logger = logger(this)
    val pipeline: Pipeline = Pipeline(name)
    var handleEjections = false
        private set
    lateinit var ejectionHandler: EjectionHandler
        private set
    var handleDeliveries = false
    lateinit var deliveryHandler: DeliveryHandler
        private set

    fun addInterceptorFirst(name: String, interceptor: BaseInterceptor) {
        pipeline.addFirst(name, interceptor)
    }

    fun addInterceptorLast(name: String, interceptor: BaseInterceptor) {
        pipeline.addLast(name, interceptor)
    }

    fun addEjectionHandler(handler: EjectionHandler) {
        if (!handleEjections) {
            handleEjections = true
            ejectionHandler = handler
        } else {
            logger.error("EjectionHandler already part of pipeline, not adding {}", handler)
        }
    }

    fun addDeliveryHandler(handler: DeliveryHandler) {
        if (!handleDeliveries) {
            handleDeliveries = true
            deliveryHandler = handler
        } else {
            logger.error("DeliveryHandler already part of pipeline, not adding {}", handler)
        }
    }
}

/**
 * The Ejection Handler is used for catching exceptions or errors in the pipeline.
 *
 * The Ejection Handler will also catch items that reach the end of the pipeline that are not intercepted.
 */
open class EjectionHandler(private val eject: (name: String, data: Any, error: Throwable) -> Unit) {
    fun handleEjection(name: String, data: Any, error: Throwable) {
        eject(name, data, error)
    }
}

/**
 * The Delivery Handler is used for handling all data that passes through the pipeline that is not in error.
 *
 * Interceptors are not required to pass data through the pipeline, so this handler can't guarantee receiving all data.
 */
open class DeliveryHandler(private val deliver: (name: String, Any) -> Unit) {
    fun handleDelivery(name: String, data: Any) {
        deliver(name, data)
    }
}
