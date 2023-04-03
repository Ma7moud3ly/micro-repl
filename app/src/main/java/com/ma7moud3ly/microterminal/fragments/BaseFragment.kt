package com.ma7moud3ly.microterminal.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.util.ConnectionStatus
import com.ma7moud3ly.microterminal.util.EditorManager
import kotlinx.coroutines.launch

open class BaseFragment : BottomSheetDialogFragment() {

    var onBackPressed: (() -> Unit)? = null
    var onConnectionChanges: ((connected: Boolean) -> Unit)? = null
    val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }

        dialog.setOnKeyListener { _: DialogInterface, keyCode: Int, keyEvent: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                onBackPressed?.invoke()
                return@setOnKeyListener onBackPressed != null
            }
            return@setOnKeyListener false
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: BottomSheetDialog) {
        val parentLayout =
            bottomSheet.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        parentLayout?.let { it ->
            val behaviour = BottomSheetBehavior.from(it)
            val layoutParams = it.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            it.layoutParams = layoutParams
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            behaviour.isDraggable = false
        }
    }

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


    //setup editor theme
    override fun getTheme(): Int {
        return if (EditorManager.isDark(requireActivity())) R.style.AppTheme
        else R.style.AppTheme_Dark
    }
}