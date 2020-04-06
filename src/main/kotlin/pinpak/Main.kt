package pinpak

import org.slf4j.LoggerFactory
import pinpak.core.*
import java.lang.Thread.sleep
import kotlin.system.measureTimeMillis

private val logger = LoggerFactory.getLogger("Main")

@Suppress("MagicNumber")
fun main() {

  val pipeline = PinPak.create("tester"){ config ->

    config.addDeliveryHandler(DeliveryHandler {name,data ->
      println("$name delivered $data")
    })

    config.addEjectionHandler(EjectionHandler { name, data ->
      println("$name ejected $data")
    })

    config.addInterceptorLast("one", PassThroughIntegerInterceptor())

  }

  pipeline.injectData("test 1")
  pipeline.injectData(1)

  sleep(5000)

}


private fun stress(){
  var totalRecieved = 0
  val tt = "TTTT"
  val tests = PinPak.create("stress") { config ->

    for (i in 0..100) {
      config.addInterceptorLast("$i", PassThroughIntegerInterceptor())
    }

    config.addEjectionHandler(EjectionHandler { _, _ ->
      totalRecieved++
    })
  }

  println("${tests.transportName} warming up")
  for (x in 0..25) {
    for (i in 1..50000) {
      tests.injectData(tt)
    }
  }
  println("${tests.transportName} warm up done")

  println("Average Run times for 1K messages to pass through a pipeline with 100 Interceptors 1000 times")
  for (x in 0..25) {
    var avgTimes = mutableListOf<Long>()
    for (j in 0..1000) {
      totalRecieved = 0
      val tts = "55"
      var duration = measureTimeMillis {
        for (i in 1..1000) {
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
    context.pumpData(data)
  }
}
