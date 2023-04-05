package micro.repl.ma7moud3ly.managers

import android.util.Log
import micro.repl.ma7moud3ly.utils.MicroFile
import org.json.JSONArray


class FilesManager(
    private val deviceManager: DeviceManager,
    private val onUpdateFiles: ((files: List<MicroFile>) -> Unit)? = null
) {

    companion object {
        private const val TAG = "FileManager"
    }

    var path = ""
    fun listDir() {
        val code = CommandsManager.iListDir(path)
        deviceManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "result $result")
            decodeFiles(result)
        })
    }

    fun remove(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.removeFile(file)
        else CommandsManager.removeDirectory(file)
        deviceManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "result $result")
            decodeFiles(result)
        })
    }

    fun new(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.makeFile(file)
        else CommandsManager.makeDirectory(file)
        deviceManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "result $result")
            decodeFiles(result)
        })
    }

    fun rename(src: MicroFile, dst: MicroFile) {
        val code = CommandsManager.rename(src, dst)
        deviceManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "result $result")
            decodeFiles(result)
        })
    }

    fun read(path: String, onRead: (content: String) -> Unit) {
        val code = CommandsManager.readFile2(path)
        deviceManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(
                response, default = ""
            ).replace("\\n", "\n")

            Log.i(TAG, "response $response")
            Log.i(TAG, "result $result")
            onRead.invoke(result)
        })
    }

    fun write(path: String, content: String, onSave: () -> Unit) {
        val code = CommandsManager.writeFile(path, content, parseJson = true)
        Log.i(TAG, "code $code")
        deviceManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "result $result")
            onSave.invoke()
        })
    }

    private fun decodeFiles(json: String) {
        val list = mutableListOf<MicroFile>()
        val jsonFormated = json.replace("(", "[").replace(")", "]")
        val items: JSONArray?
        try {
            items = JSONArray(jsonFormated)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        for (i in 0 until items.length()) {
            val item = items[i] as? JSONArray
            if (item?.length() == 4) {
                val name = (item[0] as? String) ?: ""
                val type = (item[1] as? Int) ?: 0x8000
                val size = (item[3] as? Int) ?: 0
                list.add(MicroFile(name = name, path = this.path, type = type, size = size))
            }
        }
        Log.i(TAG, list.toString())
        onUpdateFiles?.invoke(list)
    }
}
