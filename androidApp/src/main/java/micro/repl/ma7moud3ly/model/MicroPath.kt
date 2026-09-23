package micro.repl.ma7moud3ly.model

/**
 * A location on the board's filesystem.
 */
@JvmInline
value class MicroPath(val value: String = ROOT) {

    val isRoot: Boolean get() = value.isEmpty() || value == ROOT

    /** The directory above this one; the root is its own parent. */
    val parent: MicroPath
        get() = if (isRoot) this
        else MicroPath(value.trimEnd('/').substringBeforeLast('/', ROOT).ifEmpty { ROOT })

    /** This path with [name] appended, collapsing the separator. */
    fun child(name: String): MicroPath = MicroPath("${value.trimEnd('/')}/$name")

    override fun toString(): String = value

    companion object {
        const val ROOT = "/"
    }
}


fun MicroFile.asPath() = MicroPath(fullPath)
