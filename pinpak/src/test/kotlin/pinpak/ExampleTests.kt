package template

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import java.util.concurrent.atomic.AtomicInteger

class MyFirstTestClass : FunSpec({

    test("my first test") {
        1 + 2 shouldBe 3
    }

})

class NestedTestExamples : DescribeSpec({

    describe("an outer test") {

        it("an inner test") {
            1 + 2 shouldBe 3
        }

        it("an inner test too!") {
            3 + 4 shouldBe 7
        }
    }

})

class DynamicTests : FunSpec({

    listOf(
        "sam",
        "pam",
        "tim",
    ).forEach {
        test("$it should be a three letter name") {
            it.shouldHaveLength(3)
        }
    }
})

class Callbacks : FunSpec({

    val logger = logger(this)

    beforeEach {
        logger.info("Hello from $it")
    }

    test("sam should be a three letter name") {
        "sam".shouldHaveLength(3)
    }

    afterEach {
        logger.info("Goodbye from $it")
    }
})

class MyTests : StringSpec({
    "strings.length should return size of string".config(enabled = false, invocations = 5) {
        "hello".length shouldBe 5
    }
})

class InstanceTestExample : WordSpec() {


    val counter = AtomicInteger(0)

    init {
        "a" should {
            println("a=" + counter.getAndIncrement())
            "b" {
                println("b=" + counter.getAndIncrement())
            }
            "c" {
                println("c=" + counter.getAndIncrement())
            }
        }
    }
}

class InstancePerTestExample : WordSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    val counter = AtomicInteger(0)

    init {
        "a" should {
            println("a=" + counter.getAndIncrement())
            "b" {
                println("b=" + counter.getAndIncrement())
            }
            "c" {
                println("c=" + counter.getAndIncrement())
            }
        }
    }
}
