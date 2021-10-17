package pinpak

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import pinpak.core.*

class PipelineInterceptorAddLastTest  : StringSpec({
    val logger = logger(PipelineInterceptorAddLastTest::class.java)

    "Pipeline has expected amount and order of Interceptors using addLast" {
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

            stringPipeline.pipeline.getSize() shouldBe total

            var context = stringPipeline.pipeline.getFirst()

            var count = 0
            var nameMatches = ""
            while(context != stringPipeline.pipeline.getTail()){
                nameMatches += "${context.name} shouldBe ${expectedNames[count]}"
                context.name shouldBe expectedNames[count++]
                context = context.next
                if(context != stringPipeline.pipeline.getTail()){
                    nameMatches += " | "
                }
            }
            logger.info("pipeline size: {} shouldBe {} :: names: {}",stringPipeline.pipeline.getSize(),total, nameMatches)
        }
    }
})

class PipelineInterceptorAddFirstTest  : StringSpec({
    val logger = logger(PipelineInterceptorAddFirstTest::class.java)

    "Pipeline has expected amount and order of Interceptors using addFirst" {
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
                stringPipeline.pipeline.addFirst(name, PassThroughStringInterceptor())
            }

            stringPipeline.pipeline.getSize() shouldBe total

            var context = stringPipeline.pipeline.getFirst()

            var count = total - 1
            var nameMatches = ""
            while(context != stringPipeline.pipeline.getTail()){
                nameMatches += "${context.name} shouldBe ${expectedNames[count]}"
                context.name shouldBe expectedNames[count--]
                context = context.next
                if(context != stringPipeline.pipeline.getTail()){
                    nameMatches += " | "
                }
            }
            logger.info("pipeline size: {} shouldBe {} :: names: {}",stringPipeline.pipeline.getSize(),total, nameMatches)
        }
    }
})

class PipelineInterceptorAddAfterTest  : StringSpec({
    val logger = logger(PipelineInterceptorAddAfterTest::class.java)

    "Pipeline has expected amount and order of Interceptors using addAfter" {
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
            var prevName = ""
            interceptorNames.forEachIndexed { index, name ->
                if(index == 0){
                    if(stringPipeline.pipeline.addFirst(name, PassThroughStringInterceptor())){
                        prevName = name
                    }
                }
                else{
                    if(stringPipeline.pipeline.addAfter(prevName,name, PassThroughStringInterceptor())){
                        prevName = name
                    }
                }
            }

            stringPipeline.pipeline.getSize() shouldBe total

            var context = stringPipeline.pipeline.getFirst()

            var count =0
            var nameMatches = ""
            while(context != stringPipeline.pipeline.getTail()){
                nameMatches += "${context.name} shouldBe ${expectedNames[count]}"
                context.name shouldBe expectedNames[count++]
                context = context.next
                if(context != stringPipeline.pipeline.getTail()){
                    nameMatches += " | "
                }
            }
            logger.info("pipeline size: {} shouldBe {} :: names: {}",stringPipeline.pipeline.getSize(),total, nameMatches)
        }
    }
})

class PipelineInterceptorAddBeforeTest  : StringSpec({
    val logger = logger(PipelineInterceptorAddBeforeTest::class.java)

    "Pipeline has expected amount and order of Interceptors using addBefore" {
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
            var prevName = ""
            interceptorNames.forEachIndexed { index, name ->
                if(index == 0){
                    if(stringPipeline.pipeline.addFirst(name, PassThroughStringInterceptor())){
                        prevName = name
                    }
                }
                else{
                    if(stringPipeline.pipeline.addBefore(prevName,name, PassThroughStringInterceptor())){
                        prevName = name
                    }
                }
            }

            stringPipeline.pipeline.getSize() shouldBe total

            var context = stringPipeline.pipeline.getFirst()

            var count = total -1
            var nameMatches = ""
            while(context != stringPipeline.pipeline.getTail()){
                nameMatches += "${context.name} shouldBe ${expectedNames[count]}"
                context.name shouldBe expectedNames[count--]
                context = context.next
                if(context != stringPipeline.pipeline.getTail()){
                    nameMatches += " | "
                }
            }
            logger.info("pipeline size: {} shouldBe {} :: names: {}",stringPipeline.pipeline.getSize(),total, nameMatches)
        }
    }
})

class PipelineInterceptorRemoveTest  : StringSpec({
    val logger = logger(PipelineInterceptorRemoveTest::class.java)

    "Pipeline has expected amount and order of Interceptors after using remove" {
        forAll(
            row(0, arrayOf(), arrayOf()),
            row(0, arrayOf(), arrayOf("1")),
            row(0, arrayOf("1"), arrayOf("1")),
            row(1, arrayOf("1","2"), arrayOf("1")),
            row(3, arrayOf("1","2","3","4","5"), arrayOf("5","1")),
            row(1, arrayOf("1","2","3","4","5"), arrayOf("5","1","3","2","5")),
            row(1, arrayOf("1","PipelineInterceptorTest-HeadContext"), arrayOf("PipelineInterceptorTest-HeadContext")),
            row(3, arrayOf("1","PipelineInterceptorTest-TailContext","2","3"), arrayOf("PipelineInterceptorTest-TailContext")),

        ) {total, interceptorNames, removeNames ->

            val stringPipeline = PinPak.create("PipelineInterceptorTest")

            interceptorNames.forEach { name ->
                stringPipeline.pipeline.addLast(name, PassThroughStringInterceptor())
            }

            removeNames.forEach { name -> stringPipeline.removeInterceptor(name) }

            stringPipeline.pipeline.getSize() shouldBe total

            logger.info("pipeline size: {} shouldBe {} ",stringPipeline.pipeline.getSize(),total)
        }
    }
})

class PipelineInterceptorRemoveFirstTest  : StringSpec({
    val logger = logger(PipelineInterceptorRemoveFirstTest::class.java)

    "Pipeline has expected amount and order of Interceptors after using remove" {
        forAll(
            row(5, arrayOf(), arrayOf()),
            row(0, arrayOf("1"), arrayOf("1")),
            row(1, arrayOf("1","2"), arrayOf("2")),
            row(2, arrayOf("1","2","3","4","5"), arrayOf("3","4","5")),
            row(2, arrayOf("5","4","3","2","1"), arrayOf("3","2","1")),
            row(5, arrayOf("1","2","3","4","5"), arrayOf()),
            ) {removes, interceptorNames,  interceptorsRemaining ->

            val stringPipeline = PinPak.create("PipelineInterceptorTest")

            interceptorNames.forEach { name ->
                stringPipeline.pipeline.addLast(name, PassThroughStringInterceptor())
            }

            for( i in 1..removes){
                stringPipeline.pipeline.removeFirst()
            }

            val remaining = if (interceptorNames.size - removes <= 0)  0 else interceptorNames.size - removes
            remaining shouldBe interceptorsRemaining.size

            var context = stringPipeline.pipeline.getFirst()

            var count =0
            var nameMatches = ""
            while(context != stringPipeline.pipeline.getTail()){
                nameMatches += "${context.name} shouldBe ${interceptorsRemaining[count]}"
                context.name shouldBe interceptorsRemaining[count++]
                context = context.next
                if(context != stringPipeline.pipeline.getTail()){
                    nameMatches += " | "
                }
            }
            logger.info("pipeline size: {} shouldBe {} :: names: {}",stringPipeline.pipeline.getSize(),remaining, nameMatches)


        }
    }
})

class PipelineInterceptorRemoveLastTest  : StringSpec({
    val logger = logger(PipelineInterceptorRemoveLastTest::class.java)

    "Pipeline has expected amount and order of Interceptors after using remove" {
        forAll(
            row(5, arrayOf(), arrayOf()),
            row(0, arrayOf("1"), arrayOf("1")),
            row(1, arrayOf("1","2"), arrayOf("1")),
            row(2, arrayOf("1","2","3","4","5"), arrayOf("1","2","3")),
            row(3, arrayOf("5","4","3","2","1"), arrayOf("5","4")),
            row(5, arrayOf("1","2","3","4","5"), arrayOf()),
        ) {removes, interceptorNames,  interceptorsRemaining ->

            val stringPipeline = PinPak.create("PipelineInterceptorTest")

            interceptorNames.forEach { name ->
                stringPipeline.pipeline.addLast(name, PassThroughStringInterceptor())
            }

            for( i in 1..removes){
                stringPipeline.pipeline.removeLast()
            }

            val remaining = if (interceptorNames.size - removes <= 0)  0 else interceptorNames.size - removes
            remaining shouldBe interceptorsRemaining.size

            var context = stringPipeline.pipeline.getFirst()

            var count =0
            var nameMatches = ""
            while(context != stringPipeline.pipeline.getTail()){
                nameMatches += "${context.name} shouldBe ${interceptorsRemaining[count]}"
                context.name shouldBe interceptorsRemaining[count++]
                context = context.next
                if(context != stringPipeline.pipeline.getTail()){
                    nameMatches += " | "
                }
            }
            logger.info("pipeline size: {} shouldBe {} :: names: {}",stringPipeline.pipeline.getSize(),remaining, nameMatches)


        }
    }
})

data class Replacer(val old:String,val new:String)

class PipelineInterceptorReplaceTest  : StringSpec({
    val logger = logger(PipelineInterceptorReplaceTest::class.java)

    "Pipeline has expected amount and order of Interceptors after using replace" {
        forAll(
            row(arrayOf(), arrayOf(), arrayOf()),
            row(arrayOf("1"), arrayOf(Replacer("1","1")), arrayOf("1")),
            row(arrayOf("1","2","3","4","5"), arrayOf(Replacer("3","1")),arrayOf("1","2","3","4","5")),
            row(arrayOf("1","2","3","4","5"), arrayOf(Replacer("5","6")),arrayOf("1","2","3","4","6")),
            row(arrayOf("1","2","3","4","5"), arrayOf(Replacer("1","6"),Replacer("3","7")),arrayOf("6","2","7","4","5")),
            row(arrayOf("1","2","3","4","5"), arrayOf(Replacer("1","7"),Replacer("5","1"),Replacer("3","5")),arrayOf("7","2","5","4","1")),
            row(arrayOf("1","2","3","4","5"), arrayOf(Replacer("2","7"),Replacer("7","2"),Replacer("2","10")),arrayOf("1","10","3","4","5")),
        ) {interceptorNames, interceptorReplacers,  interceptorsRemaining ->

            val stringPipeline = PinPak.create("PipelineInterceptorTest")

            interceptorNames.forEach { name ->
                stringPipeline.pipeline.addLast(name, PassThroughStringInterceptor())
            }

            interceptorReplacers.forEach { replacer ->
                stringPipeline.replaceInterceptor(replacer.old,replacer.new,PassThroughStringInterceptor())
            }

            stringPipeline.pipeline.getSize() shouldBe interceptorsRemaining.size

            var context = stringPipeline.pipeline.getFirst()
            var count =0
            var nameMatches = ""
            while(context != stringPipeline.pipeline.getTail()){
                nameMatches += "${context.name} shouldBe ${interceptorsRemaining[count]}"
                context.name shouldBe interceptorsRemaining[count++]
                context = context.next
                if(context != stringPipeline.pipeline.getTail()){
                    nameMatches += " | "
                }
            }
            logger.info("pipeline size: {} shouldBe {} :: names: {}",stringPipeline.pipeline.getSize(),interceptorsRemaining.size, nameMatches)


        }
    }
})
