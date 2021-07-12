package deliveryHandler

import pinpak.core.DeliveryHandler
import pinpak.core.PassThroughIntegerInterceptor
import pinpak.core.PinPak


fun main(){
    val pipeline = PinPak.create("DeliverHandlerExample") { config ->

        config.addDeliveryHandler(DeliveryHandler { name, data ->
            println("$name delivered $data")
        })

        config.addInterceptorLast("one", PassThroughIntegerInterceptor())
    }

    pipeline.injectData(1)

}
