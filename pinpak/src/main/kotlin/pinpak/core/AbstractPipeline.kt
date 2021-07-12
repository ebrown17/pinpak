package pinpak.core

abstract class AbstractPipeline(pName: String) {

  internal val head: HeadContext = HeadContext("$pName-HeadContext", this)

  internal val tail: TailContext = TailContext("$pName-TailContext", this)

  private var ejectionHandler: EjectionHandler? = null
  private var deliveryHandler: DeliveryHandler? = null

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

  private fun bindNewContext(name: String, interceptor: BaseInterceptor): InterceptorContext {
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
    head.pumpData(data)
  }

  fun eject(name: String, data: Any) {
    ejectionHandler?.handleEjection(name, data)
  }

  fun deliver(name: String, data: Any) {
    deliveryHandler?.handleDelivery(name, data)
  }

  /**
   * Registers a handler for any exceptions that get ejected through the pipeline
   */
  fun registerEjectionHandler(handler: EjectionHandler) {
    ejectionHandler = handler
  }

  /**
   * Registers a handler for any items that pass all the way through the pipeline
   */
  fun registerDeliverHandler(handler: DeliveryHandler) {
    deliveryHandler = handler
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
