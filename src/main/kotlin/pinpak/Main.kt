package pinpak

import org.slf4j.LoggerFactory
import pinpak.core.*

private val logger = LoggerFactory.getLogger("Main")

fun main() {
    val tests = PinPak.create("TEST2") { config ->

        for(i in 0..100){
            config.addInterceptorLast("$i", PassThroughStringInterceptorChecker("$i"))
        }
        config.addEjectionHandler(EjectionHandler { name, data -> println("$name ejected $data") })
    }

    println(tests.transportName)

    for(i in 100 downTo 0){
        tests.injectData("$i")
    }

}







