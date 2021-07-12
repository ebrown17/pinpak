package manyInterceptors

import pinpak.core.DeliveryHandler
import pinpak.core.EjectionHandler
import pinpak.core.PassThroughIntegerInterceptor
import pinpak.core.PinPak

fun main(){
    val integerPipeline = PinPak.create("IntegerPipelineExample") { config ->

        config.addEjectionHandler(EjectionHandler { name, data, error ->
            println("$name ejected [$data] with $error")
        })

        config.addDeliveryHandler(DeliveryHandler { name, data ->
            println("$name delivered $data")
        })

        for (i in 1..100) {
            config.addInterceptorLast("$i", PassThroughIntegerInterceptor())
        }
    }

    integerPipeline.injectData(1)


}
