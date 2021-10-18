package ejectionHandler

import pinpak.core.*


fun main(){
    val pipeline = PinPak.create("EjectionHandlerExample") { config ->

        config.addEjectionHandler(EjectionHandler { name, data,error ->
            println("$name ejected [$data] with $error")
        })

        config.addDeliveryHandler(DeliveryHandler { name, data ->
            println("$name delivered $data")
        })

        config.addInterceptorLast("zero", PassThroughStringInterceptor())
        config.addInterceptorLast("one", PassThroughIntegerInterceptor())
        config.addInterceptorLast("two", PassThroughIntegerInterceptor())

    }

    pipeline.injectData("Will cause Exception")

}
