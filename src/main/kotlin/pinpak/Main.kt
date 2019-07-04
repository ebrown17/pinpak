package pinpak

import org.slf4j.LoggerFactory
import pinpak.core.AbstractInterceptor
import pinpak.core.BaseContext
import pinpak.core.EjectionHandler
import pinpak.core.PinPak
import kotlin.system.measureTimeMillis

private val logger = LoggerFactory.getLogger("Main")

@Suppress("MagicNumber")
fun main() {
  var totalRecieved = 0
  val tt = "TTTT"
  val tests = PinPak.create("TEST2") { config ->

    for (i in 0..100) {
      config.addInterceptorLast("$i", PassThroughStrInterceptor())
    }

    config.addEjectionHandler(EjectionHandler { _, _ ->
      totalRecieved++
    })
  }

  println(tests.transportName)

  for (i in 1..50000) {
    tests.injectData(tt)
  }
  for (i in 1..50000) {
    tests.injectData(tt)
  }

  println("warm up done")
  println("Average Run times for 50K messages to pass through a pipeline with 100 Interceptors 1000 times")
  for (x in 0..25) {
    var avgTimes = mutableListOf<Long>()
    for (j in 0..1000) {
      totalRecieved = 0
      val tts = 55
      var duration = measureTimeMillis {
        for (i in 1..50000) {
          tests.injectData(tts)
        }
      }
      // println("$totalRecieved == 50000")
      // check(totalRecieved == 50000)
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

private class PassThroughStrInterceptor : AbstractInterceptor<String>() {
  override fun readData(context: BaseContext, data: String) {
    context.passOnData(data)
  }
}