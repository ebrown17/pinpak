package eb

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")


fun main() {
    logger.info("Hello from main")

    val pipeline = Pipeline()
    println(pipeline.printAll())
    pipeline.addFirst("1 Add", BaseInterceptor())
    println(pipeline.printAll())
  //  pipeline.addLast("1 Add", BaseInterceptor())
   pipeline.addLast("2 Add", BaseInterceptor())
    pipeline.addLast("3 Add", BaseInterceptor())
   println(pipeline)

    pipeline.inject("DATA2")
    println(pipeline.printAll())

    Thread.sleep(1000)

    pipeline.addFirst("Now 2", BaseInterceptor())
    println(pipeline)
    pipeline.addFirst("Now 1", BaseInterceptor())

    pipeline.addLast("6 Add", BaseInterceptor())
    println(pipeline)
    pipeline.addBefore("6 Add", "4 Add", BaseInterceptor())
    println(pipeline)

    pipeline.addBefore("Now 1", "Newest 1", BaseInterceptor())
    println(pipeline)

    pipeline.addAfter("4 Add", "5 Add", BaseInterceptor())
    println(pipeline)
    pipeline.addAfter("6 Add", "7 Add", BaseInterceptor())
    println(pipeline)
    pipeline.addAfter("Newest 1", "2 Add", BaseInterceptor())
    println(pipeline)

    println("before")
    pipeline.addBefore("Newest 1121", "2 Add", BaseInterceptor())
    println("after")
    println(pipeline)

    pipeline.inject("DATA3")

    pipeline.remove("Newest 1")
    println(pipeline)

    pipeline.remove("7 Add")
    println(pipeline)

    pipeline.remove("6 Add")
    println(pipeline)

    pipeline.remove("Now 1")
    println(pipeline)

    pipeline.removeFirst()
    println(pipeline)

    pipeline.removeLast()
    println(pipeline)

    pipeline.replace("3 Add", "NEW ADD 3", BaseInterceptor())
    println(pipeline)
    pipeline.inject("DATA4")

    pipeline.removeAll()
    println(pipeline)

    pipeline.removeLast()
    println(pipeline)

    pipeline.inject("DATA5")

}






