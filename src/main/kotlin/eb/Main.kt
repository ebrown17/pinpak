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
    pipeline.addFirst("Now 2", PipkHandler())
    println(pipeline)
    pipeline.addFirst("Now 1",PipkHandler())

    pipeline.addLast("6 Add", PipkHandler())
    println(pipeline)
    pipeline.addBefore("6 Add","4 Add", PipkHandler())
    println(pipeline)

    pipeline.addBefore("Now 1","Newest 1", PipkHandler())
    println(pipeline)

    pipeline.addAfter("4 Add","5 Add", PipkHandler())
    println(pipeline)
    pipeline.addAfter("6 Add","7 Add", PipkHandler())
    println(pipeline)
    pipeline.addAfter("Newest 1","2 Add", PipkHandler())
    println(pipeline)

    println("before")
    pipeline.addBefore("Newest 1121","2 Add", PipkHandler())
    println("after")
    println(pipeline)
}


class PipeLine{

    private val head: PipkHandlerContext = PipkHandlerContext("HEAD")
    private val tail: PipkHandlerContext = PipkHandlerContext("TAIL")

    init{
        head.after = tail
        tail.inFront = head
    }

    fun addLast( name: String, handler : PipkHandler  ){
        addAfter(tail.inFront,name,handler)
    }

    fun addAfter(afterName: String, newName: String, handler: PipkHandler){
        try{
            addAfter(getContext(afterName),newName,handler)
        }
        catch (e : Exception){
            logger.warn("{}",e.message)
        }
    }

    private fun addAfter(addAfterMe: PipkHandlerContext, newName: String, handler: PipkHandler){
        val newCtx = PipkHandlerContext(newName)
        val after = addAfterMe.after
        newCtx.after =  after
        newCtx.inFront = addAfterMe
        after.inFront = newCtx
        addAfterMe.after = newCtx
        handler.ctx= newCtx
    }

    fun addFirst( name: String, handler : PipkHandler  ){
        addBefore(head.after,name,handler)
    }

    fun addBefore(beforeName: String, newName: String, handler: PipkHandler){
        try{
            addBefore(getContext(beforeName),newName,handler)
        }
        catch (e : Exception){
            logger.warn("{}",e.message)
        }
    }

    private fun addBefore(addBeforeMe: PipkHandlerContext, newName: String, handler: PipkHandler){
        val newCtx = PipkHandlerContext(newName)
        val front = addBeforeMe.inFront
        newCtx.inFront = front
        newCtx.after = addBeforeMe
        front.after = newCtx
        addBeforeMe.inFront = newCtx
        handler.ctx= newCtx
    }

    fun getContext(name: String): PipkHandlerContext {
        var context = head.after
        while (context !== tail) {
            if(context.id == name){
                break
            }
            context = context.after
        }
        if(context != tail){
            return context
        }
        throw Exception("Context not found with name $name")

    }

     // fun checkDuplicate()


    override fun toString(): String {
        var context = head.after
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

