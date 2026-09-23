package micro.repl.ma7moud3ly.model

/** A chunk of REPL output. [clear] asks the terminal to reset before showing [data]. */
data class BoardOutput(val data: String, val clear: Boolean)