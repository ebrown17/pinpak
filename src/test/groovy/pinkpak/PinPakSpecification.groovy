package pinkpak

import org.jetbrains.annotations.NotNull
import pinpak.core.AbstractInterceptor
import pinpak.core.BaseContext
import pinpak.core.DeliveryHandler
import pinpak.core.EjectionHandler
import pinpak.core.PinPak
import spock.lang.Shared
import spock.lang.Specification

class PinPakSpecification extends Specification {

    @Shared
    PinPak.Companion PINPAK = new PinPak.Companion()

    def "Interceptors are added correctly with PinPakConfig"() {
        given:
        PinPak transport = PINPAK.create("PinPak", { config ->
            interceptors.each { name ->
                config.addInterceptorFirst(name, new PinPakStringTestInterceptor())
            }
        })
        when:
        for (int i in 1..total) {
            transport.injectData("test")
        }

        then:
        def allMsgs = 0
        interceptors.each { name ->
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${interceptors.size() * total}"
        assert allMsgs == interceptors.size() * total

        where:
        interceptors              | total
        ["1"]                     | 50
        ["1", "2"]                | 50
        ["1", "2", "3", "4", "5"] | 50
    }

    def "Interceptors are added, injected and replaced correctly with PinPakConfig"() {
        given:
        PinPak transport = PINPAK.create("PinPak", { config ->
            interceptors.each { name ->
                config.addInterceptorFirst(name, new PinPakStringTestInterceptor())
            }
        })
        when:
        for (int i in 1..injects) {
            transport.injectData("test")
        }

        replaces.each { replacer ->
            transport.replaceInterceptor(replacer.o, replacer.n, new PinPakStringTestInterceptor())
        }

        then:
        def allMsgs = 0
        interceptors.each { name ->
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | replaces                             | injects | total
        ["1"]                     | []                                   | 25      | 25
        ["1", "2"]                | [[o: "1", n: "1"]]                   | 25      | 25
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"]]                   | 25      | 100
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "5"]] | 25      | 75
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "1"]] | 25      | 100
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "6", n: "1"]] | 25      | 100
    }

    def "Interceptors are added, replaced and injected correctly with PinPakConfig"() {
        given:
        PinPak transport = PINPAK.create("PinPak", { config ->
            interceptors.each { name ->
                config.addInterceptorFirst(name, new PinPakStringTestInterceptor())
            }
        })
        when:
        replaces.each { replacer ->
            transport.replaceInterceptor(replacer.o, replacer.n, new PinPakStringTestInterceptor())
        }

        for (int i in 1..injects) {
            transport.injectData("test")
        }

        then:
        def allMsgs = 0
        interceptors.each { name ->
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | replaces                             | injects | total
        ["1"]                     | []                                   | 25      | 25
        ["1", "2"]                | [[o: "1", n: "1"]]                   | 25      | 50
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"]]                   | 25      | 125
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "5"]] | 25      | 125
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "1"]] | 25      | 125
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "6", n: "1"]] | 25      | 125
    }

    def "Interceptors are added, removed and injected correctly with PinPakConfig"() {
        given:
        PinPak transport = PINPAK.create("PinPak", { config ->
            interceptors.each { name ->
                config.addInterceptorFirst(name, new PinPakStringTestInterceptor())
            }
        })
        when:

        def remaining = []
        remaining.addAll(interceptors)

        removes.each { name ->
            transport.removeInterceptor(name)
        }

        for (int i in 1..injects) {
            transport.injectData("test")
        }

        removes.each { remaining.remove(it) }

        then:
        def allMsgs = 0

        for (String name : remaining) {
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | removes                   | injects | total
        ["1"]                     | []                        | 25      | 25
        ["1", "2"]                | ["1"]                     | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "5"]                | 25      | 75
        ["1", "2", "3", "4", "5"] | ["2", "3", "4"]           | 25      | 50
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4"]      | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4", "5"] | 25      | 0
    }

    def "Interceptors are added, injected and removed correctly with PinPakConfig"() {
        given:
        PinPak transport = PINPAK.create("PinPak", { config ->
            interceptors.each { name ->
                config.addInterceptorFirst(name, new PinPakStringTestInterceptor())
            }
        })
        when:

        def remaining = []

        remaining.addAll(interceptors)

        for (int i in 1..injects) {
            transport.injectData("test")
        }

        removes.each { name ->
            transport.removeInterceptor(name)
        }

        removes.each { remaining.remove(it) }

        then:
        def allMsgs = 0

        for (String name : remaining) {
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | removes                   | injects | total
        ["1"]                     | []                        | 25      | 25
        ["1", "2"]                | ["1"]                     | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "5"]                | 25      | 75
        ["1", "2", "3", "4", "5"] | ["2", "3", "4"]           | 25      | 50
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4"]      | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4", "5"] | 25      | 0
    }
    /////////////////////////////

    def "Interceptors are added correctly with default PinPak"() {
        given:
        PinPak transport = PINPAK.create("PinPak")

        interceptors.each { name ->
            transport.addInterceptorFirst(name, new PinPakStringTestInterceptor())
        }

        when:
        for (int i in 1..total) {
            transport.injectData("test")
        }

        then:
        def allMsgs = 0
        interceptors.each { name ->
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${interceptors.size() * total}"
        assert allMsgs == interceptors.size() * total

        where:
        interceptors              | total
        ["1"]                     | 50
        ["1", "2"]                | 50
        ["1", "2", "3", "4", "5"] | 50
    }

    def "Interceptors are added, injected and replaced correctly with default PinPak"() {
        given:
        PinPak transport = PINPAK.create("PinPak")

        interceptors.each { name ->
            transport.addInterceptorFirst(name, new PinPakStringTestInterceptor())
        }

        when:
        for (int i in 1..injects) {
            transport.injectData("test")
        }

        replaces.each { replacer ->
            transport.replaceInterceptor(replacer.o, replacer.n, new PinPakStringTestInterceptor())
        }

        then:
        def allMsgs = 0
        interceptors.each { name ->
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | replaces                             | injects | total
        ["1"]                     | []                                   | 25      | 25
        ["1", "2"]                | [[o: "1", n: "1"]]                   | 25      | 25
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"]]                   | 25      | 100
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "5"]] | 25      | 75
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "1"]] | 25      | 100
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "6", n: "1"]] | 25      | 100
    }

    def "Interceptors are added, replaced and injected correctly with default PinPak"() {
        given:
        PinPak transport = PINPAK.create("PinPak")

        interceptors.each { name ->
            transport.addInterceptorFirst(name, new PinPakStringTestInterceptor())
        }

        when:
        replaces.each { replacer ->
            transport.replaceInterceptor(replacer.o, replacer.n, new PinPakStringTestInterceptor())
        }

        for (int i in 1..injects) {
            transport.injectData("test")
        }

        then:
        def allMsgs = 0
        interceptors.each { name ->
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | replaces                             | injects | total
        ["1"]                     | []                                   | 25      | 25
        ["1", "2"]                | [[o: "1", n: "1"]]                   | 25      | 50
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"]]                   | 25      | 125
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "5"]] | 25      | 125
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "5", n: "1"]] | 25      | 125
        ["1", "2", "3", "4", "5"] | [[o: "1", n: "1"], [o: "6", n: "1"]] | 25      | 125
    }

    def "Interceptors are added, removed and injected correctly with default PinPak"() {
        given:
        PinPak transport = PINPAK.create("PinPak")

        interceptors.each { name ->
            transport.addInterceptorFirst(name, new PinPakStringTestInterceptor())
        }
        when:

        def remaining = []
        remaining.addAll(interceptors)

        removes.each { name ->
            transport.removeInterceptor(name)
        }

        for (int i in 1..injects) {
            transport.injectData("test")
        }

        removes.each { remaining.remove(it) }

        then:
        def allMsgs = 0

        for (String name : remaining) {
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | removes                   | injects | total
        ["1"]                     | []                        | 25      | 25
        ["1", "2"]                | ["1"]                     | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "5"]                | 25      | 75
        ["1", "2", "3", "4", "5"] | ["2", "3", "4"]           | 25      | 50
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4"]      | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4", "5"] | 25      | 0
    }

    def "Interceptors are added, injected and removed correctly with default PinPak"() {
        given:
        PinPak transport = PINPAK.create("PinPak")

        interceptors.each { name ->
            transport.addInterceptorFirst(name, new PinPakStringTestInterceptor())
        }

        when:

        def remaining = []

        remaining.addAll(interceptors)

        for (int i in 1..injects) {
            transport.injectData("test")
        }

        removes.each { name ->
            transport.removeInterceptor(name)
        }

        removes.each { remaining.remove(it) }

        then:
        def allMsgs = 0

        for (String name : remaining) {
            def added = transport.getInterceptor(name)
            allMsgs += added.receivedMessages
        }

        println "$allMsgs == ${total}"
        assert allMsgs == total

        where:
        interceptors              | removes                   | injects | total
        ["1"]                     | []                        | 25      | 25
        ["1", "2"]                | ["1"]                     | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "5"]                | 25      | 75
        ["1", "2", "3", "4", "5"] | ["2", "3", "4"]           | 25      | 50
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4"]      | 25      | 25
        ["1", "2", "3", "4", "5"] | ["1", "2", "3", "4", "5"] | 25      | 0
    }

    def "Delivery Handler gets all data using PinPakConfig"() {
        given:
        def totalEjected = 0
        PinPak transport = PINPAK.create("PinPak", { config ->
            interceptors.each { name ->
                config.addInterceptorFirst(name, new PinPakStringTestInterceptor())
            }

            config.addDeliveryHandler( new DeliveryHandler({ name, data ->
                totalEjected++
            }))
        })
        when:
        for (int i in 1..total) {
            transport.injectData("test")
        }
        then:
        println "$totalEjected == ${total}"
        assert totalEjected == total

        where:
        interceptors              | total
        ["1"]                     | 50
        ["1", "2"]                | 100
        ["1", "2", "3", "4", "5"] | 500
        ["1", "2", "3", "4", "5"] | 2000
        ["1", "2", "3", "4", "5"] | 5000
    }

    def "Ejection Handler gets all ejections using defaults"() {
        given:
        def totalEjected = 0
        PinPak transport = PINPAK.create("PinPak", { config ->

            config.addEjectionHandler( new EjectionHandler({ name,data ->
                totalEjected++
            }))
        })

        interceptors.each { name ->
            transport.addInterceptorFirst(name, new PinPakStringTestEjectorInterceptor())
        }

        when:
        for (int i in 1..total) {
            transport.injectData("test")
        }
        then:
        println "$totalEjected == ${total}"
        assert totalEjected == total

        where:
        interceptors              | total
        ["1"]                     | 50
        ["1", "2"]                | 100
        ["1", "2", "3", "4", "5"] | 500
        ["1", "2", "3", "4", "5"] | 2000
        ["1", "2", "3", "4", "5"] | 5000
    }

    ////////////////// Test Interceptor ///////////////////////
    class PinPakStringTestInterceptor extends AbstractInterceptor<String> {
        int receivedMessages = 0

        @Override
        void readData(@NotNull BaseContext context, String data) {
            receivedMessages++
            context.pumpData(data)
        }
    }

    class PinPakStringTestEjectorInterceptor extends AbstractInterceptor<String> {
        int receivedMessages = 0
        def error = new Exception("read data error")
        @Override
        void readData(@NotNull BaseContext context, String data) {
            receivedMessages++
            context.pumpException(error)
        }
    }
}
