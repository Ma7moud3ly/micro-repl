package micro.repl.ma7moud3ly.managers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.text.Html
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.compose.runtime.mutableStateListOf
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.utils.MicroScript
import java.io.*


class ScriptsManager(private val context: Context) {
    companion object {
        private const val TAG = "ScriptsManager"
    }

    val scripts = mutableStateListOf<MicroScript>()

    init {
        updateScriptsList()
    }


    fun scriptDirectory(): File? {
        if (context.getExternalFilesDir("scripts")?.exists() == false) {
            val outFile = context.getExternalFilesDir("scripts")
            outFile?.mkdirs()
        }
        return context.getExternalFilesDir("scripts")
    }

    private fun updateScriptsList() {
        val list = mutableListOf<MicroScript>()
        scriptDirectory()?.let { it ->
            it.listFiles()?.forEach { file ->
                val name = file.name
                val path = file.absolutePath
                val script = MicroScript(name = name, path = path)
                list.add(script)
            }
        }
        scripts.clear()
        scripts.addAll(list)
    }


    /**
     * Dialogs
     */

    fun showDoYouWantDialog(
        msg: String,
        isDark: Boolean,
        onYes: (() -> Unit)? = null,
        onNo: (() -> Unit)? = null
    ) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle(R.string.editor_wait)
        alert.setIcon(R.drawable.ic_baseline_error_outline_24)

        val message = if (isDark) "<font style='bold' color='#FFFFFF'>$msg</font>"
        else "<font style='bold' color='#000000'>$msg</font>"
        alert.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
        alert.setPositiveButton(
            "Yes"
        ) { _: DialogInterface?, _: Int -> onYes?.invoke() }
        alert.setNegativeButton(
            "No"
        ) { _: DialogInterface?, _: Int -> onNo?.invoke() }

        alert.show()
    }

    fun showScriptNameDialog(
        msg: String,
        placeholder: String = "",
        positiveButton: String? = null,
        negativeButton: String? = null,
        onOk: (name: String) -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(placeholder)

        input.textAlignment = View.TEXT_ALIGNMENT_CENTER
        builder.setView(input)
        builder.setMessage(msg)

        positiveButton?.let {
            builder.setNegativeButton(it) { _: DialogInterface?, _: Int ->
                val name = input.text.toString().trim()
                onOk.invoke(name)
            }
        }
        negativeButton?.let {
            builder.setPositiveButton(it) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                onCancel?.invoke()
            }
        }
        builder.show()
    }

    /**
     * Script Actions
     */


    fun deleteScript(script: MicroScript) {
        val msg = context.getString(R.string.editor_msg_delete, script.name)
        showDoYouWantDialog(
            msg = msg,
            isDark = false,
            onYes = {
                val b = delete(script)
                if (b) updateScriptsList()
            }
        )
    }

    fun renameScript(script: MicroScript) {
        val msg = context.getString(R.string.editor_msg_rename, script.name)
        showScriptNameDialog(
            msg = msg,
            placeholder = script.name,
            positiveButton = "Ok",
            onOk = { newName ->
                val b = rename(script, newName)
                if (b) updateScriptsList()
            }
        )
    }

    private fun delete(script: MicroScript): Boolean {
        val file = script.file
        return if (!file.exists()) false
        else try {
            return file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun rename(script: MicroScript, newName: String): Boolean {
        val newFile = File(script.parentFile, newName)
        val oldFile = script.file
        if (!oldFile.exists()) return false
        return try {
            oldFile.renameTo(newFile)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * File Methods
     */

    fun read(path: String): String {
        val file = File(path)
        return if (!file.exists()) "" else try {
            val dis = DataInputStream(FileInputStream(file))
            val byt = ByteArray(dis.available())
            dis.readFully(byt)
            dis.close()
            String(byt, 0, byt.size)
        } catch (e: IOException) {
            e.printStackTrace()
            e.stackTrace.toString()
        }
    }

    fun read(file: File): String {
        return if (!file.exists()) "" else try {
            val dis = DataInputStream(FileInputStream(file))
            val byt = ByteArray(dis.available())
            dis.readFully(byt)
            dis.close()
            String(byt, 0, byt.size)
        } catch (e: IOException) {
            e.printStackTrace()
            e.stackTrace.toString()
        }
    }

    fun write(path: String, data: String): Boolean {
        val file = File(path)
        return try {
            if (!file.exists()) file.createNewFile()
            val out = FileOutputStream(file)
            val writer = OutputStreamWriter(out)
            writer.append(data)
            writer.flush()
            writer.close()
            out.close()
            true
        } catch (e: Exception) {
            file.delete()
            e.printStackTrace()
            false
        }
    }

    private fun read(context: Context, uri: Uri): String {
        val stringBuilder = StringBuilder()
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            stringBuilder.toString().trim { it <= ' ' }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun write(context: Context, uri: Uri, data: String): Boolean {
        return try {
            val len = read(context, uri).length
            val diff = len - data.length
            val outputStream = context.contentResolver.openOutputStream(uri)
            val writer = OutputStreamWriter(outputStream)
            writer.write(data)
            if (diff > 0) for (i in 0 until diff) writer.write(" ")
            writer.flush()
            writer.close()
            outputStream!!.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

