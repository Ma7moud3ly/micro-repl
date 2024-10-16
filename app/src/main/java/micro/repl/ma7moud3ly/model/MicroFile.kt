package micro.repl.ma7moud3ly.model

data class MicroFile(
    val name: String = "",
    var path: String = "",
    private val type: Int = FILE,
    private val size: Int = 0,
) {
    val fullPath: String
        get() = if (path.isEmpty()) name
        else "$path/$name".replace("//", "/")

    val isFile: Boolean get() = type == FILE
    val canRun: Boolean get() = ext == ".py"

    private val ext: String
        get() {
            return if (isFile && name.contains(".")) name.substring(name.indexOf(".")).trim()
            else ""
        }

    companion object {
        const val DIRECTORY = 0x4000
        const val FILE = 0x8000
    }
}