package eb

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")


fun main(){
    logger.info("Hello from main")

    val pipeline = PipeLine()

    pipeline.addLast("1 Add", PipkHandler())
    pipeline.addLast("2 Add", PipkHandler())
    pipeline.addLast("3 Add", PipkHandler())
    println(pipeline)
    pipeline.addFirst("Now 1", PipkHandler())
    println(pipeline)
    pipeline.addFirst("Now 1",PipkHandler())

    pipeline.addLast("6 Add", PipkHandler())
    println(pipeline)

}


class PipeLine{

    val head: PipkHandlerContext
    val tail: PipkHandlerContext

    init{
        head = PipkHandlerContext("HEAD")
        tail = PipkHandlerContext("TAIL")
        head.after = tail
        tail.inFront = head
    }

    fun addLast( name: String, handler : PipkHandler  ){
        val newCtx = PipkHandlerContext(name)
        val previous = tail.inFront
        newCtx.inFront = previous
        newCtx.after = tail
        previous.after = newCtx
        tail.inFront = newCtx
        handler.ctx= newCtx
    }

    fun addFirst( name: String, handler : PipkHandler  ){
        val newCtx = PipkHandlerContext(name)
        val previous = head.after
        newCtx.inFront = head
        newCtx.after = previous
        previous.inFront = newCtx
        head.after = newCtx
        handler.ctx= newCtx
    }



    override fun toString(): String {
        var context = head.after;
        var message : String = ""
        while (context !== tail) {
            message += "[ Ctx: ${context.id} ]"
            context = context.after
        }
        return message
    }

}

class PipkHandler{
    var ctx: PipkHandlerContext? = null

}

class PipkHandlerContext(val id: String) {

    @Volatile var inFront: PipkHandlerContext = this
    @Volatile var after: PipkHandlerContext = this

}

