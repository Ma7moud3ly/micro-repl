package micro.repl.ma7moud3ly.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import micro.repl.ma7moud3ly.managers.ScriptsManager
import micro.repl.ma7moud3ly.ui.ScriptsScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.utils.EditorMode
import micro.repl.ma7moud3ly.utils.MicroScript
import micro.repl.ma7moud3ly.utils.ScriptsUiEvents

class ScriptsFragment : BaseFragment(), ScriptsUiEvents {
    companion object {
        private const val TAG = "ScriptsActivity"
    }

    private lateinit var scriptsManager: ScriptsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        scriptsManager = ScriptsManager(requireContext())

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = isDarkMode) {
                    ScriptsScreen(
                        scripts = scriptsManager.scripts,
                        uiEvents = this@ScriptsFragment
                    )
                }
            }
        }
    }

    override fun onRun(script: MicroScript) {
    }

    override fun onOpen(script: MicroScript) {
        Log.i(TAG, "openScript - $script")
        viewModel.scriptPath.value = script.path
        viewModel.editorMode = EditorMode.LOCAL
        val action = ScriptsFragmentDirections.actionScriptsFragmentToEditorFragment()
        navigate(action)
    }

    override fun onDelete(script: MicroScript) {
        scriptsManager.deleteScript(script)
    }

    override fun onRename(script: MicroScript) {
        scriptsManager.renameScript(script)
    }

    override fun onUp() {
        findNavController().popBackStack()
    }
}

