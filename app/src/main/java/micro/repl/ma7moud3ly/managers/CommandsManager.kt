/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

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

    const val SILENT_MODE = "\u0001" //CTRL + A = Start of heading (SOH)
    const val REPL_MODE = "\u0002" //CTRL + B = Start of text (STX)
    const val TERMINATE = "\u0003" //CTRL + C = End of text (ETX)
    const val RESET = "\u0004" //CTRL + D = End of transmission (EOT)


    //////////////////////////

    /**
     * Extra commands to detect the end of code execution
     * this logic might be changed :3
     */
    private const val SILENT_EXECUTION_START = "OK"
    private const val SILENT_EXECUTION_END = "\u0004" //


    private fun responseStatement(path: String) = "print(list(os.ilistdir('$path')))"

    fun iListDir(path: String): String {
        return "import os;" + responseStatement(path)
    }


    fun readFile(path: String): String {
        return """
        with open('$path', encoding='utf-8') as f:
            lines = f.readlines()
            content = []
            for line in lines:
                if line.endswith("\r\n"):
                    line=line[:-2]+'\n'
                    content.append(line)
                else:
                    content.append(line)        
            content = "".join(content)        
            print(content)
    """.trimIndent()
    }

    fun writeFile(path: String, content: String): String {
        return "content = r'''$content'''\r\nf = open('$path','w',encoding='utf-8');" +
                "a = f.write(content);f.close();print(a)"
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


    /**
     * we use this to detect the end of execution in sync writing mode
     * this logic might be changed :3
     */

    fun isSilentExecutionDone(data: String): Boolean {
        return data.contains(SILENT_EXECUTION_END)
    }

    /**
     * we use this to extract the result of silent execution
     * ex:
     *      raw REPL; CTRL-B to exit
     *      >OK RESULT
     *      
     *
     *      We need to extract RESULT
     */
    fun trimSilentResult(data: String): String {
        val i1 = data.lastIndexOf(SILENT_EXECUTION_START)
        val i2 = data.indexOf(SILENT_EXECUTION_END)
        return if (i1 > -1 && i2 > -1 && i2 > i1) data.substring(
            i1 + SILENT_EXECUTION_START.length,
            i2
        ).trim()
        else data
    }

}