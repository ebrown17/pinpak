package eb


class Transport private constructor(val config: TransportConfig) {

    private val pipeline: Pipeline = config.pipeline

    fun injectData(data: Any) {
        pipeline.inject(data)
    }

    fun addInterceptorFirst(name: String, interceptor: BaseInterceptor) {
        pipeline.addFirst(name, interceptor)
    }

    companion object {
        fun create(name: String): Transport {
            val defaultConfig = TransportConfig(name)
            return Transport(defaultConfig)
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

}

fun main() {
    val transport = Transport.create("TEST1") { config : TransportConfig ->
        config.defaultValue1 = 5
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


    transport.addInterceptorFirst("1", PassThroughStringInterceptor())
    transport.addInterceptorFirst("1", PassThroughStringInterceptor())
    transport.addInterceptorFirst("2", PassThroughStringInterceptor())

    transport.injectData("DATAT ATAT ATATA")
}