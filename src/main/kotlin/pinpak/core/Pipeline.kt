package pinpak.core

import org.slf4j.LoggerFactory

class Pipeline(pipelineName: String) : AbstractPipeline(pipelineName) {

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
      remove(context)
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
