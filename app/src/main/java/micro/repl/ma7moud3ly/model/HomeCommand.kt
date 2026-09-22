package micro.repl.ma7moud3ly.model

/**
 * Feedback the home screen shows after a board action.
 *
 * The ViewModel says what happened; the screen owns the wording, so string
 * resources stay out of it.
 */
sealed interface HomeCommand {
    data object DeviceReset : HomeCommand
    data object DeviceSoftReset : HomeCommand
    data object ExecutionTerminated : HomeCommand
}
