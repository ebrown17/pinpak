package pinpak

import org.slf4j.LoggerFactory
import pinpak.core.AbstractInterceptor
import pinpak.core.BaseContext
import pinpak.core.EjectionHandler
import pinpak.core.PinPak
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

private val logger = LoggerFactory.getLogger("Main")

@Suppress("MagicNumber")
fun main() {
  var warmUpComplete = false
  var totalRecieved = 0
  val tests = PinPak.create("TEST2") { config ->

    for (i in 0..100) {
      config.addInterceptorLast("$i", PassThroughIntegerInterceptor())
    }

    config.addEjectionHandler(EjectionHandler { _, _ ->
      totalRecieved++
    })
  }
  var intPool = mutableListOf<Int>()
  for (i in 1..50000) {
    intPool.add(i)
  }

  println(tests.transportName)

  for (i in intPool) {
    tests.injectData(i)
  }
  for (i in intPool) {
    tests.injectData(i)
  }

  println("warm up done")
  println("Average Run times for 50K messages to pass through a pipeline with 100 Interceptors 1000 times")
  for (x in 0..25) {
    var avgTimes = mutableListOf<Long>()
    for (j in 0..1000) {
      totalRecieved = 0
      var duration = measureTimeMillis {
        for (i in intPool) {
          tests.injectData(i)
        }
      }
     // println("$totalRecieved == 50000")
      //check(totalRecieved == 50000)
      avgTimes.add(duration)
      Thread.sleep(20)
    }
    println("Run $x  average is ${avgTimes.average()} ms")
  }
}

private class PassThroughIntegerInterceptor : AbstractInterceptor<Int>() {
  override fun readData(context: BaseContext, data: Int) {
    context.passOnData(data)
  }
}






