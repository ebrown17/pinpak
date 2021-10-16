package pinpak

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import pinpak.core.PinPak

class PipelineInterceptorTest  : StringSpec({
    val logger = logger(PipelineInterceptorTest::class.java)

    "Pipeline has expected amount of Interceptors using addLast" {
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

            logger.info("shouldBe $totalMessages * ${interceptorNames.size}")
            received shouldBe totalMessages * interceptorNames.size
        }
    }
})
