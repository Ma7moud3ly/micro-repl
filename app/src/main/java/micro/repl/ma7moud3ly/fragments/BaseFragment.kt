package micro.repl.ma7moud3ly.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.MainActivity
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.utils.ConnectionStatus
import micro.repl.ma7moud3ly.utils.ThemeMode

open class BaseFragment : Fragment() {

    var onConnectionChanges: ((connected: Boolean) -> Unit)? = null
    val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeConnectionStatus()
    }

    fun onUiReady(callback: () -> Unit) {
        view?.post { callback.invoke() }
    }

    private fun observeConnectionStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.status.collect { status ->
                    when (status) {
                        is ConnectionStatus.OnConnected -> onConnectionChanges?.invoke(true)
                        else -> onConnectionChanges?.invoke(false)
                    }
                }
            }
        }
    }

    fun navigate(action: NavDirections) {
        try {
            //findNavController().navigate(action)
            mainActivity?.navHost?.navController?.navigate(action)
        } catch (e: java.lang.Exception) {
            requireActivity().recreate()
            e.printStackTrace()
        }
    }

    private val mainActivity: MainActivity? get() = activity as? MainActivity

    val boardManager: BoardManager? get() = mainActivity?.boardManager
    val terminalManager: TerminalManager? get() = mainActivity?.terminalManager
    val filesManager: FilesManager? get() = mainActivity?.filesManager

    val isDarkMode: Boolean get() = ThemeMode.isDark(requireActivity())

}