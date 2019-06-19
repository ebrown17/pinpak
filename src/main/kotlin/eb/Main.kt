package eb

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")


fun test2() {
    logger.info("Hello from test2")

    val pipeline = Pipeline("test2")
    println(pipeline.printAll())
    pipeline.addFirst("1", PassThroughStringInterceptor())
    pipeline.addLast("2", PassThroughStringInterceptor())
    pipeline.addLast("3", PassThroughStringInterceptor())

    pipeline.inject("DATA5")
    pipeline.inject(5)
    pipeline.addLast("0", PassThroughIntegerInterceptor())
    pipeline.inject(6)
    pipeline.remove("0")
    pipeline.inject(7)
    pipeline.inject("9")
}

fun main() {
    test2()
    maint()
}

fun maint() {
    logger.info("Hello from main")

    val pipeline = Pipeline("maint")
    println(pipeline.printAll())
    pipeline.addFirst("1 Add", PassThroughStringInterceptor())
    println(pipeline.printAll())
    //  pipeline.addLast("1 Add", PassThroughStringInterceptor())
    pipeline.addLast("2 Add", PassThroughStringInterceptor())
    pipeline.addLast("3 Add", PassThroughStringInterceptor())
    println(pipeline)

    pipeline.inject("DATA2")
    println(pipeline.printAll())

    Thread.sleep(1000)

    pipeline.addFirst("Now 2", PassThroughStringInterceptor())
    println(pipeline)
    pipeline.addFirst("Now 1", PassThroughStringInterceptor())

    pipeline.addLast("6 Add", PassThroughStringInterceptor())
    println(pipeline)
    pipeline.addBefore("6 Add", "4 Add", PassThroughStringInterceptor())
    println(pipeline)

    pipeline.addBefore("Now 1", "Newest 1", PassThroughStringInterceptor())
    println(pipeline)

    pipeline.addAfter("4 Add", "5 Add", PassThroughStringInterceptor())
    println(pipeline)
    pipeline.addAfter("6 Add", "7 Add", PassThroughStringInterceptor())
    println(pipeline)
    pipeline.addAfter("Newest 1", "2 Add", PassThroughStringInterceptor())
    println(pipeline)

    println("before")
    pipeline.addBefore("Newest 1121", "2 Add", PassThroughStringInterceptor())
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

    pipeline.replace("3 Add", "NEW ADD 3", PassThroughIntegerInterceptor())
    println(pipeline)
    pipeline.inject(10)

    pipeline.removeAll()
    println(pipeline)

    pipeline.removeLast()
    println(pipeline)

    pipeline.inject("DATA5")

}






