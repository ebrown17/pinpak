package eb

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")


fun main() {
    logger.info("Hello from main")

    val pipeline = PipeLine()

    pipeline.addLast("1 Add", Interceptor())
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
        if (!isDuplicate(name)) {
            addAfter(tail.previous, name, interceptor)
        } else {
            logger.error("Duplicate Interceptor name: $name")
        }
    }

    fun addFirst(name: String, interceptor: Interceptor) {
        if (!isDuplicate(name)) {
            addBefore(head.next, name, interceptor)
        } else {
            logger.error("Duplicate Interceptor name: $name")
        }
    }

    fun addAfter(afterName: String, newName: String, interceptor: Interceptor) {
        val context = getContext(afterName)
        if(context !=null){
            addAfter(context, newName, interceptor)
        }
        else{
            logger.error("Context to add after not found with name $afterName")
        }
    }

    fun addBefore(beforeName: String, newName: String, interceptor: Interceptor) {
        val context = getContext(beforeName)
        if(context !=null){
            addBefore(context, newName, interceptor)
        }
        else{
            logger.error("Context to add before not found with name $beforeName")
        }
    }

    private fun addAfter(current: InterceptorContext, newName: String, interceptor: Interceptor) {
        val newCtx = bindNewContext(newName, interceptor)
        val after = current.next
        newCtx.next = after
        newCtx.previous = current
        after.previous = newCtx
        current.next = newCtx
    }

    private fun addBefore(current: InterceptorContext, newName: String, interceptor: Interceptor) {
        if (isDuplicate(newName)) {
            throw Exception("Duplicate Interceptor name: $newName")
        }

        val newCtx = bindNewContext(newName, interceptor)
        val front = current.previous
        newCtx.previous = front
        newCtx.next = current
        front.next = newCtx
        current.previous = newCtx

    }

    fun remove(name: String) {
        val context = getContext(name)
        if (context != null) {
            if (context != tail && context != head) {
                val front = context.previous
                val back = context.next
                front.next = back
                back.previous = front

                context.next = context
                context.previous = context
            }
        } else {
            logger.error("Context to remove not found with name $name")

        }
    }

    private fun remove(context: InterceptorContext) {
        if (context != tail && context != head) {
            val front = context.previous
            val back = context.next
            front.next = back
            back.previous = front

            context.next = context
            context.previous = context
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
        if (replaced != newName && isDuplicate(newName)) {
            logger.error("Duplicate Interceptor name: $newName")
        }
        val context = getContext(replaced)
        if (context != null) {
            replace(context, newName, interceptor)
        } else {
            logger.error("Context to replace not found with name $replaced")
        }
    }

    private fun replace(replaced: InterceptorContext, newName: String, interceptor: Interceptor) {
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

            val newCtx = bindNewContext(newName, interceptor)

            newCtx.previous = front
            newCtx.next = back

            front.next = newCtx
            back.previous = newCtx

            context.next = context
            context.previous = context

        }
    }

    private fun getContext(name: String): InterceptorContext? {
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
        return null
        ///throw Exception("Context not found with name $name")
    }

    private fun isDuplicate(name: String): Boolean {
        var context = head
        while (context != tail) {
            if (context.id == name) {
                return true
            }
            context = context.next
        }
        return false
    }

    private fun bindNewContext(name: String, interceptor: Interceptor): InterceptorContext {
        val ctx = InterceptorContext(name)
        interceptor.ctx = ctx
        ctx.interceptor = interceptor
        return ctx
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

