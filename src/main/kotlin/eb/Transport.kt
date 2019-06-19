package eb

class Transport private constructor(val transportName: String) {

    private val pipeline: Pipeline = Pipeline(transportName)

    fun injectData(data:Any){
        pipeline.inject(data)
    }

    fun addInterceptorFirst(name: String, interceptor: BaseInterceptor){
        pipeline.addFirst(name,interceptor)
    }

    companion object{

        fun create(name: String): Transport {
            return Transport(name)
        }

    }


}

fun main(){
   val transport = Transport.create("FIRST PIPELINE")

    transport.addInterceptorFirst("1", PassThroughStringInterceptor())
    transport.addInterceptorFirst("1", PassThroughStringInterceptor())
    transport.addInterceptorFirst("2", PassThroughStringInterceptor())

    transport.injectData("DATAT ATAT ATATA")
}