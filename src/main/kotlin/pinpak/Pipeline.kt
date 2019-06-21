package pinpak

import org.slf4j.LoggerFactory

class Pipeline(private val pipelineName: String) {

    private val logger = LoggerFactory.getLogger(Pipeline::class.java)

    private val head: HeadContext = HeadContext("HeadContext", this)
    private val tail: TailContext = TailContext("TailContext", this)

    init {
        head.next = tail
        tail.previous = head
    }

    fun addLast(name: String, interceptor: BaseInterceptor) {
        if (!isDuplicate(name)) {
            addBefore(tail, name, interceptor)
        } else {
            logger.error("Duplicate BaseInterceptor name: $name")
        }
    }

    fun addFirst(name: String, interceptor: BaseInterceptor) {
        if (!isDuplicate(name)) {
            addAfter(head, name, interceptor)
        } else {
            logger.error("Duplicate BaseInterceptor name: $name")
        }
    }

    fun addAfter(afterName: String, newName: String, interceptor: BaseInterceptor) {
        val context = getContext(afterName)
        if (context != null) {
            addAfter(context, newName, interceptor)
        } else {
            logger.error("Context to add after not found with name $afterName")
        }
    }

    fun addBefore(beforeName: String, newName: String, interceptor: BaseInterceptor) {
        val context = getContext(beforeName)
        if (context != null) {
            addBefore(context, newName, interceptor)
        } else {
            logger.error("Context to add before not found with name $beforeName")
        }
    }

    private fun addAfter(current: BaseContext, newName: String, interceptor: BaseInterceptor) {
        val newCtx = bindNewContext(newName, interceptor)
        val after = current.next
        newCtx.next = after
        newCtx.previous = current
        after.previous = newCtx
        current.next = newCtx
    }

    private fun addBefore(current: BaseContext, newName: String, interceptor: BaseInterceptor) {
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

    private fun remove(context: BaseContext) {
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
            remove(head.next.name)
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

    fun replace(replaced: String, newName: String, interceptor: BaseInterceptor) {
        if (replaced != newName && isDuplicate(newName)) {
            logger.error("Duplicate BaseInterceptor name: $newName")
        }
        val context = getContext(replaced)
        if (context != null) {
            replace(context, newName, interceptor)
        } else {
            logger.error("Context to replace not found with name $replaced")
        }
    }

    private fun replace(replaced: BaseContext, newName: String, interceptor: BaseInterceptor) {
        var context = head.next
        while (context != tail) {
            if (context.name == replaced.name) {
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

    private fun getContext(name: String): BaseContext? {
        var context = head.next
        while (context != tail) {
            if (context.name == name) {
                break
            }
            context = context.next
        }
        if (context != tail) {
            return context
        }
        return null
    }

    private fun isDuplicate(name: String): Boolean {
        var context: BaseContext = head
        while (context != tail) {
            if (context.name == name) {
                return true
            }
            context = context.next
        }
        return false
    }

    private fun bindNewContext(name: String, interceptor: BaseInterceptor): InterceptorContext {
        val ctx = InterceptorContext(name, this, interceptor)
        interceptor.name = name
        return ctx
    }

    fun printAll(): String {
        var context: BaseContext = head
        var message = "Context:["
        while (context !== tail) {
            message += "[ Ctx: ${context.name} | Prev: ${context.previous.name} Next:${context.next.name}  ]"
            context = context.next
        }
        message += "[ Ctx: ${context.name} |  Prev: ${context.previous.name} Next:${context.next.name}  ]"
        return "$message]"
    }

    override fun toString(): String {
        var context = head.next
        var message = "Context:["
        while (context !== tail) {
            message += "[ Ctx: ${context.name} ]"
            context = context.next
        }
        return "$message]"
    }

    fun inject(data: Any) {
        logger.info("injecting $data at $pipelineName's ${head.name}")
        head.passOnData(data)
    }

    fun eject(context: BaseContext, data: Any) {
        logger.info("$pipelineName's ${context.name} ejected $data")
    }

    fun getContext(): BaseContext {
        return head
    }

}
