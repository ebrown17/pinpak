package pinpak.core

import org.slf4j.LoggerFactory

class Pipeline(private val pipelineName: String) : AbstractPipeline(pipelineName) {

  private val logger = LoggerFactory.getLogger(Pipeline::class.java)

  fun addLast(name: String, interceptor: BaseInterceptor): Boolean {
    return if (isDuplicate(name)) {
      logger.error("Duplicate BaseInterceptor name: $name")
      false
    } else {
      addBefore(tail, name, interceptor)
    }
  }

  fun addFirst(name: String, interceptor: BaseInterceptor): Boolean {
    return if (isDuplicate(name)) {
      logger.error("Duplicate BaseInterceptor name: $name")
      false
    } else {
      addAfter(head, name, interceptor)
    }
  }

  fun addAfter(after: String, newName: String, interceptor: BaseInterceptor): Boolean {
    return if (isDuplicate(newName)) {
      logger.error("Duplicate BaseInterceptor name: $newName")
      false
    } else {
      val context = getContext(after)
      if (context != null) {
        addAfter(context, newName, interceptor)
      } else {
        logger.error("Context to add after not found with name $after")
        false
      }
    }
  }

  fun addBefore(before: String, newName: String, interceptor: BaseInterceptor): Boolean {
    return if (isDuplicate(newName)) {
      logger.error("Duplicate BaseInterceptor name: $newName")
      false
    } else {
      val context = getContext(before)
      if (context != null) {
        addBefore(context, newName, interceptor)
        true
      } else {
        logger.error("Context to add after not found with name $before")
        false
      }
    }
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
    } else {
      val context = getContext(replaced)
      if (context != null) {
        replace(context, newName, interceptor)
      } else {
        logger.error("Context to replace not found with name $replaced")
      }
    }
  }
}

abstract class AbstractPipeline(private val pName: String) {

  internal val head: HeadContext = HeadContext("$pName-HeadContext", this)

  internal val tail: TailContext = TailContext("$pName-TailContext", this)

  private var ejectionHandler: EjectionHandler? = null

  init {
    head.next = tail
    tail.previous = head
  }

  protected fun addAfter(cur: BaseContext, newName: String, interceptor: BaseInterceptor): Boolean {
    val newCtx = bindNewContext(newName, interceptor)
    val after = cur.next
    newCtx.next = after
    newCtx.previous = cur
    after.previous = newCtx
    cur.next = newCtx
    return true
  }

  protected fun addBefore(
    cur: BaseContext,
    newName: String,
    interceptor: BaseInterceptor
  ): Boolean {
    val newCtx = bindNewContext(newName, interceptor)
    val front = cur.previous
    newCtx.previous = front
    newCtx.next = cur
    front.next = newCtx
    cur.previous = newCtx
    return true
  }

  protected fun remove(context: BaseContext) {
    if (context != tail && context != head) {
      val front = context.previous
      val back = context.next
      front.next = back
      back.previous = front

      context.next = context
      context.previous = context
    }
  }

  protected fun replace(replaced: BaseContext, newName: String, interceptor: BaseInterceptor) {
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

  protected fun isDuplicate(name: String): Boolean {
    var context: BaseContext = head
    while (context != tail) {
      if (context.name == name) {
        return true
      }
      context = context.next
    }
    return (tail.name == name || head.name == name)
  }

  protected fun bindNewContext(name: String, interceptor: BaseInterceptor): InterceptorContext {
    val ctx = InterceptorContext(name, this, interceptor)
    interceptor.name = name
    return ctx
  }

  override fun toString(): String {
    var context = head.next
    var message = "Pipeline:["
    while (context !== tail) {
      message += "(${context.name})"
      context = context.next
    }
    return "$message]"
  }

  fun isContextPresent(name: String): Boolean {
    return isDuplicate(name)
  }

  fun inject(data: Any) {
    head.passOnData(data)
  }

  fun eject(name: String, data: Any) {
    ejectionHandler?.handleEjection(name, data)
  }

  /**
   * Registers a handler for any items that pass all the way through the pipeline
   */
  fun registerEjectionHandler(handler: EjectionHandler) {
    ejectionHandler = handler
  }

  /**
   * returns the pipeline's head context
   */
  fun getHead(): BaseContext {
    return head
  }

  /**
   * returns the pipeline's tail context
   */
  fun getTail(): BaseContext {
    return tail
  }

  /**
   * returns the pipeline's first context
   */
  fun getFirst(): BaseContext {
    return head.next
  }

  /**
   * returns the pipeline's last context
   */
  fun getLast(): BaseContext {
    return tail.previous
  }

  fun getContext(name: String): BaseContext? {
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

  /**
   * returns count of pipelines's interceptors
   */
  fun getSize(): Int {
    var count = 0
    var context = head.next
    while (context !== tail) {
      count++
      context = context.next
    }
    return count
  }
}
