package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import com.ma7moud3ly.microterminal.MainActivity
import com.ma7moud3ly.microterminal.MainViewModel
import com.ma7moud3ly.microterminal.managers.DeviceManager
import com.ma7moud3ly.microterminal.managers.FilesManager
import com.ma7moud3ly.microterminal.managers.TerminalManager
import com.ma7moud3ly.microterminal.utils.ConnectionStatus
import com.ma7moud3ly.microterminal.utils.ThemeMode
import kotlinx.coroutines.launch

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
            activity?.navHost?.navController?.navigate(action)
        } catch (e: java.lang.Exception) {
            requireActivity().recreate()
            e.printStackTrace()
        }
    }

    private val activity: MainActivity?
        get() = try {
            requireActivity() as? MainActivity
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    val deviceManager: DeviceManager? get() = activity?.deviceManager
    val terminalManager: TerminalManager? get() = activity?.terminalManager
    val filesManager: FilesManager? get() = activity?.filesManager

    val isDarkMode: Boolean get() = ThemeMode.isDark(requireActivity())

}