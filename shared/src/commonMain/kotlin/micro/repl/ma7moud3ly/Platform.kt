package micro.repl.ma7moud3ly

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform