package pinkpak

import pinpak.core.BaseContext
import pinpak.core.PassThroughStringInterceptor
import pinpak.core.Pipeline
import spock.lang.Shared
import spock.lang.Specification

class PipelineSpecification extends Specification {
    @Shared
    Pipeline pipeline = new Pipeline("Spock")

    def cleanup() {
        pipeline.removeAll()
    }

    def "Pipeline has expected amount of Interceptors using addLast"() {
        given:

        when:
        interceptorNames.each { name ->
            pipeline.addLast(name, new PassThroughStringInterceptor())
        }
        then:
        println("addLast ${pipeline.getSize()} == $total")
        println("addLast $pipeline == $expected")
        assert pipeline.getSize() == total

        BaseContext ctx = pipeline.getFirst()
        def count = 0
        while (ctx != pipeline.getTail()) {
            assert ctx.name == expected[count++]
            ctx = ctx.next
        }

        where:
        interceptorNames                                               | total | expected
        []                                                             | 0     | []
        ["1"]                                                          | 1     | ["1"]
        ["1", "2", "3", "4", "5", "6", "7"]                            | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2"]                                                     | 2     | ["1", "2"]
        ["1", "1"]                                                     | 1     | ["1"]
        ["1", "1", "1"]                                                | 1     | ["1"]
        ["1", "2", "3", "3", "4", "1", "5", "6", "7", "1"]             | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2", "1", "2"]                                           | 2     | ["1", "2"]
        ["Spock-HeadContext", "2"]                                     | 1     | ["2"]
        ["Spock-HeadContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-HeadContext", "2", "3", "Spock-TailContext"]           | 2     | ["2", "3"]
        ["1", "Spock-HeadContext", "2", "3", "Spock-TailContext", "1"] | 3     | ["1", "2", "3"]
        ["Spock-TailContext", "2"]                                     | 1     | ["2"]
        ["Spock-TailContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-TailContext", "2", "3", "Spock-HeadContext"]           | 2     | ["2", "3"]
        ["1", "Spock-TailContext", "2", "3", "Spock-HeadContext", "1"] | 3     | ["1", "2", "3"]
    }

    def "Pipeline has expected amount of Interceptors using addFirst"() {
        given:

        when:
        interceptorNames.each { name ->
            pipeline.addFirst(name, new PassThroughStringInterceptor())
        }
        then:
        println("addFirst ${pipeline.getSize()} == $total")
        println("addFirst $pipeline == ${expected.reverse()}")
        assert pipeline.getSize() == total

        BaseContext ctx = pipeline.getFirst()
        def count = total - 1
        while (ctx != pipeline.getTail()) {
            assert ctx.name == expected[count--]
            ctx = ctx.next
        }

        where:
        interceptorNames                                               | total | expected
        []                                                             | 0     | []
        ["1"]                                                          | 1     | ["1"]
        ["1", "2", "3", "4", "5", "6", "7"]                            | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2"]                                                     | 2     | ["1", "2"]
        ["1", "1"]                                                     | 1     | ["1"]
        ["1", "1", "1"]                                                | 1     | ["1"]
        ["1", "2", "3", "3", "4", "1", "5", "6", "7", "1"]             | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2", "1", "2"]                                           | 2     | ["1", "2"]
        ["Spock-HeadContext", "2"]                                     | 1     | ["2"]
        ["Spock-HeadContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-HeadContext", "2", "3", "Spock-TailContext"]           | 2     | ["2", "3"]
        ["1", "Spock-HeadContext", "2", "3", "Spock-TailContext", "1"] | 3     | ["1", "2", "3"]
        ["Spock-TailContext", "2"]                                     | 1     | ["2"]
        ["Spock-TailContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-TailContext", "2", "3", "Spock-HeadContext"]           | 2     | ["2", "3"]
        ["1", "Spock-TailContext", "2", "3", "Spock-HeadContext", "1"] | 3     | ["1", "2", "3"]

    }

    def "Pipeline has expected amount of Interceptors using addAfter"() {
        given:

        when:
        def prev = null
        interceptorNames.each { name ->
            if (prev == null) {
                if (pipeline.addFirst(name, new PassThroughStringInterceptor())) {
                    prev = name
                }
            } else {
                if (pipeline.addAfter(prev, name, new PassThroughStringInterceptor())) {
                    prev = name
                }
            }
        }
        then:
        println("addAfter ${pipeline.getSize()} == $total")
        println("addAfter $pipeline == $expected")
        assert pipeline.getSize() == total

        BaseContext ctx = pipeline.getFirst()
        def count = 0
        while (ctx != pipeline.getTail()) {
            assert ctx.name == expected[count++]
            ctx = ctx.next
        }

        where:
        interceptorNames                                               | total | expected
        []                                                             | 0     | []
        ["1"]                                                          | 1     | ["1"]
        ["1", "2", "3", "4", "5", "6", "7"]                            | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2"]                                                     | 2     | ["1", "2"]
        ["1", "1"]                                                     | 1     | ["1"]
        ["1", "1", "1"]                                                | 1     | ["1"]
        ["1", "2", "3", "3", "4", "1", "5", "6", "7", "1"]             | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2", "1", "2"]                                           | 2     | ["1", "2"]
        ["Spock-HeadContext", "2"]                                     | 1     | ["2"]
        ["Spock-HeadContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-HeadContext", "2", "3", "Spock-TailContext"]           | 2     | ["2", "3"]
        ["1", "Spock-HeadContext", "2", "3", "Spock-TailContext", "1"] | 3     | ["1", "2", "3"]
        ["Spock-TailContext", "2"]                                     | 1     | ["2"]
        ["Spock-TailContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-TailContext", "2", "3", "Spock-HeadContext"]           | 2     | ["2", "3"]
        ["1", "Spock-TailContext", "2", "3", "Spock-HeadContext", "1"] | 3     | ["1", "2", "3"]

    }

    def "Pipeline has expected amount of Interceptors using addBefore"() {
        given:

        when:
        def prev = null
        interceptorNames.each { name ->
            if (prev == null) {
                if (pipeline.addFirst(name, new PassThroughStringInterceptor())) {
                    prev = name
                }
            } else {
                if (pipeline.addBefore(prev, name, new PassThroughStringInterceptor())) {
                    prev = name
                }
            }
        }
        then:
        println("addBefore ${pipeline.getSize()} == $total")
        println("addBefore $pipeline == ${expected.reverse()}")
        assert pipeline.getSize() == total

        BaseContext ctx = pipeline.getFirst()
        def count = total - 1
        while (ctx != pipeline.getTail()) {
            assert ctx.name == expected[count--]
            ctx = ctx.next
        }

        where:
        interceptorNames                                               | total | expected
        []                                                             | 0     | []
        ["1"]                                                          | 1     | ["1"]
        ["1", "2", "3", "4", "5", "6", "7"]                            | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2"]                                                     | 2     | ["1", "2"]
        ["1", "1"]                                                     | 1     | ["1"]
        ["1", "1", "1"]                                                | 1     | ["1"]
        ["1", "2", "3", "3", "4", "1", "5", "6", "7", "1"]             | 7     | ["1", "2", "3", "4", "5", "6", "7"]
        ["1", "2", "1", "2"]                                           | 2     | ["1", "2"]
        ["Spock-HeadContext", "2"]                                     | 1     | ["2"]
        ["Spock-HeadContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-HeadContext", "2", "3", "Spock-TailContext"]           | 2     | ["2", "3"]
        ["1", "Spock-HeadContext", "2", "3", "Spock-TailContext", "1"] | 3     | ["1", "2", "3"]
        ["Spock-TailContext", "2"]                                     | 1     | ["2"]
        ["Spock-TailContext", "2", "3"]                                | 2     | ["2", "3"]
        ["Spock-TailContext", "2", "3", "Spock-HeadContext"]           | 2     | ["2", "3"]
        ["1", "Spock-TailContext", "2", "3", "Spock-HeadContext", "1"] | 3     | ["1", "2", "3"]
    }

    def "Pipeline has expected amount of Interceptors using remove"() {
        given:
        interceptorNames.each { name ->
            pipeline.addLast(name, new PassThroughStringInterceptor())
        }
        when:
        toRemove.each { name ->
            pipeline.remove(name)
        }
        then:
        println("remove ${pipeline.getSize()} == $totalLeft")
        pipeline.getSize() == totalLeft

        where:
        interceptorNames           | toRemove                       | totalLeft
        []                         | []                             | 0
        []                         | ["1"]                          | 0
        ["1"]                      | ["1"]                          | 0
        ["1", "2"]                 | ["1"]                          | 1
        ["1", "2", "3", "4", "5"]  | ["5", "1"]                     | 3
        ["1", "2", "3", "4", "5"]  | ["5", "1", "5", "3", "2"]      | 1
        ["Spock-HeadContext", "2"] | ["Spock-HeadContext"]          | 1
        ["Spock-HeadContext", "2"] | ["Spock-TailContext"]          | 1
        ["Spock-HeadContext"]      | ["Spock-TailContext"]          | 0
        ["1", "2", "3", "4", "5"]  | ["5", "1", "5", "3", "2", "4"] | 0

    }

    def "Pipeline has expected amount of Interceptors using removeFirst"() {
        given:
        interceptorNames.each { name ->
            pipeline.addLast(name, new PassThroughStringInterceptor())
        }
        when:
        for (int i in 1..removes) {
            pipeline.removeFirst()
        }

        then:
        int max = ((interceptorNames.size() - removes <= 0) ? 0 : interceptorNames.size() - removes)
        assert (max >= expectedLeft.size())
        println("removeFirst ${pipeline.toString()} == $expectedLeft")
        BaseContext ctx = pipeline.getFirst()
        def count = 0
        while (ctx != pipeline.getTail()) {
            assert ctx.name == expectedLeft[count++]
            ctx = ctx.next
        }

        where:
        interceptorNames          | removes | expectedLeft
        []                        | 5       | []
        ["1"]                     | 1       | []
        ["1", "2"]                | 1       | ["2"]
        ["1", "2", "3", "4", "5"] | 2       | ["3", "4", "5"]
        ["5", "4", "3", "1", "2"] | 2       | ["3", "1", "2"]
        ["1", "2", "3", "4", "5"] | 5       | []
    }

    def "Pipeline has expected amount of Interceptors using removeLast"() {
        given:
        interceptorNames.each { name ->
            pipeline.addLast(name, new PassThroughStringInterceptor())
        }
        when:
        for (int i in 1..removes) {
            pipeline.removeLast()
        }

        then:
        int max = ((interceptorNames.size() - removes <= 0) ? 0 : interceptorNames.size() - removes)
        assert (max >= expectedLeft.size())
        println("removeLast ${pipeline.toString()} == $expectedLeft")
        BaseContext ctx = pipeline.getFirst()
        def count = 0
        while (ctx != pipeline.getTail()) {
            assert ctx.name == expectedLeft[count++]
            ctx = ctx.next
        }

        where:
        interceptorNames          | removes | expectedLeft
        []                        | 5       | []
        ["1"]                     | 1       | []
        ["1", "2"]                | 1       | ["1"]
        ["1", "2", "3", "4", "5"] | 2       | ["1", "2", "3"]
        ["5", "4", "3", "1", "2"] | 2       | ["5", "4", "3"]
        ["1", "2", "3", "4", "5"] | 5       | []
    }

    def "Pipeline has expected amount of Interceptors using replace"() {
        given:
        interceptors.each { name ->
            pipeline.addLast(name, new PassThroughStringInterceptor())
        }
        when:
        replaces.each { replacer ->
            pipeline.replace(replacer.o, replacer.n, new PassThroughStringInterceptor())
        }
        then:
        println("replace ${pipeline} == $expectedLeft")
        BaseContext ctx = pipeline.getFirst()
        def count = 0
        while (ctx != pipeline.getTail()) {
            assert ctx.name == expectedLeft[count++]
            ctx = ctx.next
        }
        where:
        interceptors              | replaces                                                 | expectedLeft
        []                        | []                                                       | []
        ["1"]                     | [[o: "1", n: "1"]]                                       | ["1"]
        ["1", "2"]                | [[o: "1", n: "2"]]                                       | ["1", "2"]
        ["1", "2", "3", "4", "5"] | [[o: "3", n: "1"]]                                       | ["1", "2", "3", "4", "5"]
        ["1", "2", "3", "4", "5"] | [[o: "5", n: "6"], [o: "1", n: "7"]]                     | ["7", "2", "3", "4", "6"]
        ["1", "2", "3", "4", "5"] | [[o: "5", n: "0"], [o: "1", n: "5"], [o: "4", n: "1"]
                                     , [o: "2", n: "4"], [o: "1", n: "2"], [o: "0", n: "1"]] | ["5", "4", "3", "2", "1"]

    }

}

