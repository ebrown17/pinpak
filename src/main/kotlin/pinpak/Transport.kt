package pinpak


class Transport private constructor(val config: TransportConfig) {

    private val pipeline: Pipeline = config.pipeline

    fun injectData(data: Any) {
        pipeline.inject(data)
    }

    fun addInterceptorFirst(name: String, interceptor: BaseInterceptor) {
        pipeline.addFirst(name, interceptor)
    }

    companion object {
        /**
         * Creates a Transport with default values
         */
        fun create(name: String): Transport {
            return create(name) { }
        }
        fun create(name: String,config: (TransportConfig) -> Unit): Transport {
            val userConfig= TransportConfig(name)
            config(userConfig)

            return Transport(userConfig)
        }
    }
}


class TransportConfig(val name: String) {
    val pipeline: Pipeline = Pipeline(name)
    var defaultValue1:Int = 1
    var defaultValue2:Int = 2

    fun addInterceptor(name: String, interceptor: BaseInterceptor) {
        pipeline.addLast(name, interceptor)
    }

}

fun main() {
    val transport = Transport.create("TEST1") { config : TransportConfig ->
        config.defaultValue1 = 5
        config.addInterceptor("1", PassThroughStringInterceptor())
        config.addInterceptor("2", PassThroughStringInterceptor())
        config.addInterceptor("3", PassThroughStringInterceptor())
    }

    println("TEST 1")
    println(transport.config.name)
    println(transport.config.defaultValue1)
    println(transport.config.defaultValue2)

    val transport2 = Transport.create("TEST2")
    println("TEST 2")
    println(transport2.config.name)
    println(transport2.config.defaultValue1)
    println(transport2.config.defaultValue2)


    transport2.addInterceptorFirst("1", PassThroughStringInterceptor())
    transport2.addInterceptorFirst("2", PassThroughStringInterceptor())
    transport2.addInterceptorFirst("3", PassThroughStringInterceptor())

    transport.injectData("DATAT 1")
    transport2.injectData("DATAT 2")


}
