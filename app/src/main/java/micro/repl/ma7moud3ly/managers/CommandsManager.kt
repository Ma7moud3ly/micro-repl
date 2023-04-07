/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import com.google.gson.Gson
import micro.repl.ma7moud3ly.utils.MicroFile

/**
 * This class manages building commands to send to the python REPL
 * also decoding the responses of REPL to extract data.
 */
object CommandsManager {

    /**
     * MicroPython REPL commands
     * References ->
     * https://docs.micropython.org/en/latest/esp8266/tutorial/repl.html
     * https://www.techonthenet.com/unicode/chart.php
     * https://www.physics.udel.edu/~watson/scen103/ascii.html
     **/

    const val HIDDEN_MODE = "\u0001" //CTRL + A = Start of heading (SOH)
    const val REPL_MODE = "\u0002" //CTRL + B = Start of text (STX)
    const val TERMINATE = "\u0003" //CTRL + C = End of text (ETX)
    const val SOFT_RESET = "\u0004" //CTRL + D = End of transmission (EOT)


    //////////////////////////

    /**
     * Extra commands to detect the end of code execution
     * this logic might be changed :3
     */
    const val END_OUTPUT = "EXEC DONE"
    const val END_OUTPUT2 = "EXECDONE"
    const val END_OF_REPL_RESPONSE = "\n>>> "
    const val END_OF_REPL_RESPONSE2 = "\n>>> \r\n>>> "
    private const val END_STATEMENT = "print('EXEC','DONE')"
    private const val RESULT_BEGIN = "@{"
    private const val RESULT_END = "}@"


    private fun responseStatement(path: String) =
        "print('$RESULT_BEGIN',list(os.ilistdir('$path')),'$RESULT_END');$END_STATEMENT"

    fun listDir(path: String): String {
        return "print('$RESULT_BEGIN',list(os.listdir('$path')),'$RESULT_END');$END_STATEMENT"
    }

    fun execute(code: String, toJson: Boolean = false): String {
        val data = if (toJson) toJson(code) else code
        return "print();exec('''$data''')"
    }

    fun iListDir(path: String): String {
        return "import os;" + responseStatement(path)
    }

    fun readFile(path: String): String {
        return "import json;f = open('$path',encoding='utf-8');content = f.read();f.close();" +
                "print('$RESULT_BEGIN',json.dumps(content),'$RESULT_END');$END_STATEMENT"
    }

    fun writeFile(path: String, content: String, parseJson: Boolean = false): String {
        val data = if (parseJson) toJson(content) else content
        return "content = '''$data''';f = open('$path','w',encoding='utf-8');" +
                "f.write(content);f.close();$END_STATEMENT"
    }

    fun removeFile(file: MicroFile): String {
        return "import os;os.remove('${file.fullPath}');" +
                responseStatement(file.path)
    }

    fun removeDirectory(file: MicroFile): String {
        return "import os;os.rmdir('${file.fullPath}');" +
                responseStatement(file.path)
    }

    fun makeDirectory(file: MicroFile): String {
        return "import os;os.mkdir('${file.fullPath}');" +
                responseStatement(file.path)
    }

    fun makeFile(file: MicroFile): String {
        return "import os;f = open('${file.fullPath}','w');f.write('');f.close();" +
                responseStatement(file.path)
    }

    fun rename(src: MicroFile, dst: MicroFile): String {
        return "import os;os.rename('${src.fullPath}','${dst.fullPath}');" + responseStatement(src.path)
    }

    fun extractResult(data: String, default: String = ""): String {
        if (data.isEmpty()) return default
        val hasResponse = data.count { it == '@' } >= 4
        val i1 = data.lastIndexOf(RESULT_BEGIN)
        val i2 = data.lastIndexOf(RESULT_END)
        return if (hasResponse && i1 != -1 && i2 != -1 && i1 < i2) {
            val result = data.substring(i1 + RESULT_BEGIN.length, i2).trim()
            return stripQuotes(result)
        } else default
    }

    private fun stripQuotes(data: String): String {
        return if (data.length >= 3 && data.startsWith("\"") && data.endsWith("\""))
            data.substring(startIndex = 1, endIndex = data.length - 1)
        else if (data.trim() == "\"\"") return ""
        else data
    }

    private fun toJson(data: String): String {
        return stripQuotes(Gson().toJson(data))
    }
}