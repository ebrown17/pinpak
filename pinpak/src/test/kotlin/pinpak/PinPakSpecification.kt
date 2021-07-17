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

class InterceptorTests  : StringSpec({
    "All interceptors added in PinPakConfig receive all messages" {
        forAll(
            row(50, arrayOf()),
            row(50, arrayOf("1")),
            row(50, arrayOf("1","2")),
            row(100, arrayOf("1","2","3")),
            row(500, arrayOf("1","2","3","4")),
            row(500, arrayOf("1","2","3","4","5","6","7")),
        ) { totalMessages, interceptorNames ->

            val integerPipeline = PinPak.create("InterceptorTest1") { config ->
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

            println("$received shouldBe $totalMessages * ${interceptorNames.size}")
            received shouldBe totalMessages * interceptorNames.size
        }
    }
})


