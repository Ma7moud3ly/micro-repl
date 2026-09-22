package micro.repl.ma7moud3ly.model

data class Message(
    val value: String = "",
    val type: Int = SUCCESS,
    var code: Int? = null
) {
    companion object {
        const val ERROR = 0
        const val SUCCESS = 1
    }

    val isError: Boolean get() = type == ERROR
}

val String.asErrorMessage get() = Message(this, Message.ERROR)
val String.asSuccessMessage get() = Message(this, Message.SUCCESS)