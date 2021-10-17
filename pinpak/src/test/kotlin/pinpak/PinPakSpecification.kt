package pinpak

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import pinpak.core.*

class PassThroughIntegerInterceptorTest : AbstractInterceptor<Int>() {
    private val logger = logger(this)
    var totalReceived = 0
    override fun readData(context: BaseContext, data: Int) {
        logger.trace("{} got $data passing to [{}]", name, context.name)
        totalReceived++
        context.pumpData(data)
    }
}

class InterceptorAddTest  : StringSpec({
    val logger = logger(InterceptorAddTest::class.java)

    "All interceptors added in PinPakConfig receive all messages" {
        forAll(
            row(50, arrayOf()),
            row(50, arrayOf("1")),
            row(50, arrayOf("1","2")),
            row(100, arrayOf("1","2","3")),
            row(500, arrayOf("1","2","3","4")),
            row(500, arrayOf("1","2","3","4","5","6","7")),
        ) { totalMessages, interceptorNames ->

            val integerPipeline = PinPak.create("InterceptorAddTest") { config ->
                interceptorNames.forEach { name ->
                    config.addInterceptorLast(name, PassThroughIntegerInterceptorTest())
                }
            }

            for( i in 1..totalMessages){
                integerPipeline.injectData(1)
            }

            var received = 0
            interceptorNames.forEach { name ->
                val interceptor =  integerPipeline.getInterceptor(name) as PassThroughIntegerInterceptorTest
                received += interceptor.totalReceived
            }

            logger.info("$received shouldBe $totalMessages * ${interceptorNames.size}")
            received shouldBe totalMessages * interceptorNames.size
        }
    }
})

class InterceptorReplaceTest  : StringSpec({
    val logger = logger(InterceptorReplaceTest::class.java)

    "Interceptors are added, injected and replaced correctly with PinPakConfig" {
        forAll(
            row(50, arrayOf(), arrayOf()),
            row(50,  arrayOf("1"), arrayOf(mapOf("old" to "1", "new" to "n1"))),
            row(50,  arrayOf("1", "2"), arrayOf(mapOf("old" to "1", "new" to "n1"), mapOf("old" to "2", "new" to "n2"))),
            row(
                500,
                arrayOf("1", "2", "3", "4"),
                arrayOf(mapOf("old" to "1", "new" to "n1"), mapOf("old" to "4", "new" to "n4"))
            ),
            row(
                500,
                arrayOf("1", "2", "3", "4", "5", "6", "7"),
                arrayOf(mapOf("old" to "6", "new" to "n6"), mapOf("old" to "7", "new" to "n7"))
            ),
        ) { totalMessages, interceptorNames, replacementNames ->

            val integerPipeline = PinPak.create("InterceptorReplaceTest") { config ->
                interceptorNames.forEach { name ->
                    config.addInterceptorLast(name, PassThroughIntegerInterceptorTest())
                }
            }

            for( i in 1..totalMessages){
                integerPipeline.injectData(1)
            }

            replacementNames.forEach { replacer ->
                val old = replacer["old"]!!
                val new = replacer["new"]!!
                integerPipeline.replaceInterceptor(old, new,  PassThroughIntegerInterceptorTest())
            }

            var received = 0
            interceptorNames.forEach { name ->
                integerPipeline.getInterceptor(name)?.let { it ->
                    val interceptor = it as PassThroughIntegerInterceptorTest
                    received += interceptor.totalReceived
                }
            }

            replacementNames.forEach { replacer ->
                val newName = replacer["new"]!!

                integerPipeline.getInterceptor(newName)?.let { it ->
                    val interceptor = it as PassThroughIntegerInterceptorTest
                    received += interceptor.totalReceived
                }
            }

            val answer = (interceptorNames.size - (replacementNames.size)) * totalMessages

            logger.info("$received shouldBe $answer")
            received shouldBe answer
        }
    }
})

class InterceptorReplaceInjectTest  : StringSpec({
    val logger = logger(InterceptorReplaceInjectTest::class.java)

    "Interceptors are added, replaced and injected correctly with PinPakConfig" {
        forAll(
            row(50, arrayOf(), arrayOf()),
            row(50,  arrayOf("1"), arrayOf(mapOf("old" to "1", "new" to "n1"))),
            row(50,  arrayOf("1", "2"), arrayOf(mapOf("old" to "1", "new" to "n1"), mapOf("old" to "2", "new" to "n2"))),
            row(
                500,
                arrayOf("1", "2", "3", "4"),
                arrayOf(mapOf("old" to "1", "new" to "n1"), mapOf("old" to "4", "new" to "n4"))
            ),
            row(
                500,
                arrayOf("1", "2", "3", "4", "5", "6", "7"),
                arrayOf(mapOf("old" to "6", "new" to "n6"), mapOf("old" to "7", "new" to "n7"))
            ),
        ) { totalMessages, interceptorNames, replacementNames ->

            val integerPipeline = PinPak.create("InterceptorReplaceTest") { config ->
                interceptorNames.forEach { name ->
                    config.addInterceptorLast(name, PassThroughIntegerInterceptorTest())
                }
            }

            replacementNames.forEach { replacer ->
                val old = replacer["old"]!!
                val new = replacer["new"]!!
                integerPipeline.replaceInterceptor(old, new,  PassThroughIntegerInterceptorTest())
            }

            for( i in 1..totalMessages){
                integerPipeline.injectData(1)
            }


            var received = 0
            interceptorNames.forEach { name ->
                integerPipeline.getInterceptor(name)?.let { it ->
                    val interceptor = it as PassThroughIntegerInterceptorTest
                    received += interceptor.totalReceived
                }
            }

            replacementNames.forEach { replacer ->
                val newName = replacer["new"]!!

                integerPipeline.getInterceptor(newName)?.let { it ->
                    val interceptor = it as PassThroughIntegerInterceptorTest
                    received += interceptor.totalReceived
                }
            }

            val answer = (interceptorNames.size) * totalMessages

            logger.info("$received shouldBe $answer")
            received shouldBe answer
        }
    }
})

class InterceptorRemoveTest  : StringSpec({
    val logger = logger(InterceptorRemoveTest::class.java)

    "Interceptors are added, removed and injected correctly with PinPakConfig" {
        forAll(
            row(50, arrayOf(), arrayOf()),
            row(50,  arrayOf("1"), arrayOf("1")),
            row(50,  arrayOf("1", "2"), arrayOf("1")),
            row(500, arrayOf("1", "2", "3", "4"), arrayOf("2","4")),
            row(500, arrayOf("1", "2", "3", "4", "5", "6", "7"), arrayOf("2","5","7")),
        ) { totalMessages, interceptorNames, removeNames ->

            val integerPipeline = PinPak.create("InterceptorRemoveTest") { config ->
                interceptorNames.forEach { name ->
                    config.addInterceptorLast(name, PassThroughIntegerInterceptorTest())
                }
            }

            removeNames.forEach { removeName ->
                integerPipeline.removeInterceptor(removeName)
            }

            for( i in 1..totalMessages){
                integerPipeline.injectData(1)
            }


            var received = 0
            interceptorNames.forEach { name ->
                integerPipeline.getInterceptor(name)?.let { it ->
                    val interceptor = it as PassThroughIntegerInterceptorTest
                    received += interceptor.totalReceived
                }
            }

            val answer = (interceptorNames.size - removeNames.size) * totalMessages

            logger.info("$received shouldBe $answer")
            received shouldBe answer
        }
    }
})

class DeliveryHandlerTest  : StringSpec({
    val logger = logger(DeliveryHandlerTest::class.java)

    "Delivery Handler gets all data with PinPakConfig" {
        forAll(
            row(50, arrayOf()),
            row(50,  arrayOf("1")),
            row(50,  arrayOf("1", "2")),
            row(500, arrayOf("1", "2", "3", "4")),
            row(500, arrayOf("1", "2", "3", "4", "5", "6", "7")),
        ) { totalMessages, interceptorNames ->

            var totalDelivered = 0

            val integerPipeline = PinPak.create("DeliveryHandlerTest") { config ->
                interceptorNames.forEach { name ->
                    config.addInterceptorLast(name, PassThroughIntegerInterceptorTest())
                }

                config.addDeliveryHandler(DeliveryHandler { _, _ ->
                    totalDelivered++
                })
            }

            for( i in 1..totalMessages){
                integerPipeline.injectData(1)
            }

            logger.info("$totalDelivered shouldBe $totalMessages")
            totalDelivered shouldBe totalMessages
        }
    }
})

class EjectionHandlerTest  : StringSpec({
    val logger = logger(EjectionHandlerTest::class.java)

    "Ejection Handler gets all data with PinPakConfig" {
        forAll(
            row(50, arrayOf()),
            row(50,  arrayOf("1")),
            row(50,  arrayOf("1", "2")),
            row(500, arrayOf("1", "2", "3", "4")),
            row(500, arrayOf("1", "2", "3", "4", "5", "6", "7")),
        ) { totalMessages, interceptorNames ->

            var totalEjected = 0

            val integerPipeline = PinPak.create("EjectionHandlerTest") { config ->
                interceptorNames.forEach { name ->
                    config.addInterceptorLast(name, PassThroughIntegerInterceptorTest())
                }

                config.addEjectionHandler(EjectionHandler { _, _ , _ ->
                    totalEjected++
                })
            }

            for( i in 1..totalMessages){
                integerPipeline.injectData("1")
            }

            logger.info("$totalEjected shouldBe $totalMessages")
            totalEjected shouldBe totalMessages
        }
    }
})
