package eb

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")


fun main() {
    logger.info("Hello from main")

    val pipeline = PipeLine()

    pipeline.addLast("1 Add", Interceptor())
    pipeline.addLast("2 Add", Interceptor())
    pipeline.addLast("3 Add", Interceptor())
    println(pipeline)
    pipeline.addFirst("Now 2", Interceptor())
    println(pipeline)
    pipeline.addFirst("Now 1", Interceptor())

    pipeline.addLast("6 Add", Interceptor())
    println(pipeline)
    pipeline.addBefore("6 Add", "4 Add", Interceptor())
    println(pipeline)

    pipeline.addBefore("Now 1", "Newest 1", Interceptor())
    println(pipeline)

    pipeline.addAfter("4 Add", "5 Add", Interceptor())
    println(pipeline)
    pipeline.addAfter("6 Add", "7 Add", Interceptor())
    println(pipeline)
    pipeline.addAfter("Newest 1", "2 Add", Interceptor())
    println(pipeline)

    println("before")
    pipeline.addBefore("Newest 1121", "2 Add", Interceptor())
    println("after")
    println(pipeline)

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

    pipeline.replace("3 Add", "NEW ADD 3", Interceptor())
    println(pipeline)

    pipeline.removeAll()
    println(pipeline)

    pipeline.removeLast()
    println(pipeline)

}


class PipeLine {

    private val head: InterceptorContext = InterceptorContext("HEAD")
    private val tail: InterceptorContext = InterceptorContext("TAIL")

    init {
        head.next = tail
        tail.previous = head
    }

    fun addLast(name: String, interceptor: Interceptor) {
        addAfter(tail.previous, name, interceptor)
    }

    fun addFirst(name: String, interceptor: Interceptor) {
        addBefore(head.next, name, interceptor)
    }

    fun addAfter(afterName: String, newName: String, interceptor: Interceptor) {
        try {
            addAfter(getContext(afterName), newName, interceptor)
        } catch (e: Exception) {
            logger.warn("{}", e.message)
        }
    }

    fun addBefore(beforeName: String, newName: String, interceptor: Interceptor) {
        try {
            addBefore(getContext(beforeName), newName, interceptor)
        } catch (e: Exception) {
            logger.warn("{}", e.message)
        }
    }

    private fun addAfter(current: InterceptorContext, newName: String, interceptor: Interceptor) {
        val newCtx =  bindNewContext(newName,interceptor)
        val after = current.next
        newCtx.next = after
        newCtx.previous = current
        after.previous = newCtx
        current.next = newCtx
    }

    private fun addBefore(current: InterceptorContext, newName: String, interceptor: Interceptor) {
        val newCtx = bindNewContext(newName,interceptor)
        val front = current.previous
        newCtx.previous = front
        newCtx.next = current
        front.next = newCtx
        current.previous = newCtx

    }

    fun remove(name: String) {
        try {
            val context = getContext(name)
            if (context != tail && context != head) {
                val front = context.previous
                val back = context.next
                front.next = back
                back.previous = front

                context.next = context
                context.previous = context
            }

        } catch (e: Exception) {
            logger.warn("{}", e.message)
        }
    }

    fun remove(context: InterceptorContext) {
        if (context != tail && context != head) {
            try {
                val front = context.previous
                val back = context.next
                front.next = back
                back.previous = front

                context.next = context
                context.previous = context

            } catch (e: Exception) {
                logger.warn("{}", e.message)
            }
        }
    }


    fun removeAll() {
        while (head.next !== tail) {
            remove(head.next.id)
        }
        head.next = tail
        tail.previous = head
    }

    fun removeFirst() {
        remove(head.next)
    }

    fun removeLast() {
        remove(tail.previous)
    }

    fun replace(replaced: String, newName: String, interceptor: Interceptor) {
        val context = getContext(replaced)
        replace(context, newName, interceptor)
    }

    fun replace(replaced: InterceptorContext, newName: String, interceptor: Interceptor) {
        var context = head.next
        while (context !== tail) {
            if (context.id == replaced.id) {
                break
            }
            context = context.next
        }
        if (replaced != tail && replaced != head) {
            val front = context.previous
            val back = context.next

            val newCtx = bindNewContext(newName,interceptor)

            newCtx.previous = front
            newCtx.next = back

            front.next = newCtx
            back.previous = newCtx

            context.next = context
            context.previous = context

        }
    }




    private fun getContext(name: String): InterceptorContext {
        var context = head.next
        while (context !== tail) {
            if (context.id == name) {
                break
            }
            context = context.next
        }
        if (context != tail) {
            return context
        }
        throw Exception("Context not found with name $name")
    }

    // fun checkDuplicate()

    private fun bindNewContext(name: String, interceptor: Interceptor): InterceptorContext {
        // check duplicates
        val ctx = InterceptorContext(name)
        interceptor.ctx = ctx
        ctx.interceptor=interceptor

        return  ctx
    }

    override fun toString(): String {
        var context = head.next
        var message = "Context:["
        while (context !== tail) {
            message += "[ Ctx: ${context.id} ]"
            context = context.next
        }
        return "$message]"
    }

}


class Interceptor {
    var ctx: InterceptorContext? = null

}

////Interceptor
class InterceptorContext(val id: String) {

    @Volatile
    var previous: InterceptorContext = this
    @Volatile
    var next: InterceptorContext = this

    var interceptor: Interceptor? = null

}

