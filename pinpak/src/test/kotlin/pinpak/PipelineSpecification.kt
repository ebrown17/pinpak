package pinpak

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import pinpak.core.*

class PipelineInterceptorTest  : StringSpec({
    val logger = logger(PipelineInterceptorTest::class.java)

    "Pipeline has expected amount of Interceptors using addLast" {
        forAll(
            row(0, arrayOf(), arrayOf()),
            row(1, arrayOf("1"), arrayOf("1")),
            row(2, arrayOf("1","2"), arrayOf("1","2")),
            row(1, arrayOf("1","1"), arrayOf("1")),
            row(1, arrayOf("1","1","1"), arrayOf("1")),
            row(2, arrayOf("1","1","2","2"), arrayOf("1","2")),
            row(2, arrayOf("1","PipelineInterceptorTest-HeadContext","2"), arrayOf("1","2")),
            row(2, arrayOf("1","PipelineInterceptorTest-TailContext","2"), arrayOf("1","2")),
            row(2, arrayOf("1","PipelineInterceptorTest-HeadContext","PipelineInterceptorTest-TailContext","2"), arrayOf("1","2")),
            row(7, arrayOf("1","2","3","4","5","6","7"), arrayOf("1","2","3","4","5","6","7")),
        ) {total, interceptorNames, expectedNames ->

            val stringPipeline = PinPak.create("PipelineInterceptorTest")

            interceptorNames.forEach { name ->
               stringPipeline.pipeline.addLast(name, PassThroughStringInterceptor())
            }

            logger.info("pipeline size: ${stringPipeline.pipeline.getSize()} shouldBe $total")
            stringPipeline.pipeline.getSize() shouldBe total

            var context = stringPipeline.pipeline.getFirst()

            var count = 0
            while(context != stringPipeline.pipeline.getTail()){
                logger.info("context name: ${context.name} shouldBe ${expectedNames[count]}")
                context.name shouldBe expectedNames[count++]
                context = context.next
            }
        }
    }
})
