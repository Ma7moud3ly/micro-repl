/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import micro.repl.ma7moud3ly.model.MicroFile

/**
 * Manages the construction of commands sent to the MicroPython REPL and the decoding
 * of REPL responses to extract data.
 *
 * This object provides methods for generating commands for various file and directory
 * operations, as well as for controlling the REPL mode and interpreting its output.
 *
 * **REPL Control Commands:**
 * - `SILENT_MODE`: Enters silent mode (CTRL+A).
 * - `REPL_MODE`: Enters REPL mode (CTRL+B).
 * - `TERMINATE`: Terminates the current command (CTRL+C).
 * - `RESET`: Resets the MicroPython board (CTRL+D).
 *
 * **File and Directory Operations:**
 * - `iListDir()`: Lists the contents of a directory.
 * - `readFile()`: Reads the content of a file.
 * - `writeFile()`: Writes content to a file.
 * - `removeFile()`: Deletes a file.
 * - `removeDirectory()`: Deletes a directory.
 * - `makeDirectory()`: Creates a new directory.
 * - `makeFile()`: Creates a new file.
 * - `rename()`: Renames a file or directory.
 *
 * **Silent Execution Handling:**
 * - `isSilentExecutionDone()`: Checks if a silent execution has completed.
 * - `trimSilentResult()`: Extracts the result from a silent execution's output.
 */
object CommandsManager {

    /**
     * MicroPython REPL control commands.
     *
     * These commands are used to control the behavior of the MicroPython REPL.
     *
     * **References:**
     * - [MicroPython REPL documentation](https://docs.micropython.org/en/latest/esp8266/tutorial/repl.html)
     * - [Unicode Character Chart](https://www.techonthenet.com/unicode/chart.php)
     * - [ASCII Table](https://www.physics.udel.edu/~watson/scen103/ascii.html)
     */
    const val SILENT_MODE = "\u0001" //CTRL + A = Start of heading (SOH)
    const val REPL_MODE = "\u0002" //CTRL + B = Start of text (STX)
    const val TERMINATE = "\u0003" //CTRL + C = End of text (ETX)
    const val RESET = "\u0004" //CTRL + D = End of transmission (EOT)


    //////////////////////////

    /**
     * Extra commands to detect the end of code execution in silent mode.
     * This logic might be changed in the future.
     */
    private const val SILENT_EXECUTION_START = ">OK"
    private const val SILENT_EXECUTION_END = "\u0004" //

    /**
     * Generates a MicroPython command to list the contents of a directory.
     *
     * @param path The path of the directory to list.
     * @return The MicroPython command string.
     */
    private fun responseStatement(path: String) = "print(list(os.ilistdir('$path')))"

    /**
     * Generates a MicroPython command to list the contents of a directory.
     *
     * @param path The path of the directory to list.
     * @return The MicroPython command string.
     */
    fun iListDir(path: String): String {
        return "import os;" + responseStatement(path)
    }

    /**
     * Generates a MicroPython command to read the content of a file.
     *
     * @param path The path of the file to read.
     * @return The MicroPython command string.
     */
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

    /**
     * Generates a MicroPython command to write content to a file.
     *
     * @param path The path of the file to write to.
     * @param content The content to write to the file.
     * @return The MicroPython command string.
     */
    fun writeFile(path: String, content: String): String {
        return "content = r'''$content'''\r\nf = open('$path','w',encoding='utf-8');" +
                "a = f.write(content);f.close();print(a)"
    }

    /**
     * Generates a MicroPython command to remove a file.
     *
     * @param file The `MicroFile` representing the file to remove.
     * @return The MicroPython command string.
     */
    fun removeFile(file: MicroFile): String {
        return "import os;os.remove('${file.fullPath}');" +
                responseStatement(file.path)
    }

    /**
     * Generates a MicroPython command to remove a directory.
     *
     * @param file The `MicroFile` representing the directory to remove.
     * @return The MicroPython command string.
     */
    fun removeDirectory(file: MicroFile): String {
        return "import os;os.rmdir('${file.fullPath}');" +
                responseStatement(file.path)
    }

    /**
     * Generates a MicroPython command to create a new directory.
     *
     * @param file The `MicroFile` representing the directory to create.
     * @return The MicroPython command string.
     */
    fun makeDirectory(file: MicroFile): String {
        return "import os;os.mkdir('${file.fullPath}');" +
                responseStatement(file.path)
    }

    /**
     * Generates a MicroPython command to create a new file.
     *
     * @param file The `MicroFile` representing the file to create.
     * @return The MicroPython command string.
     */
    fun makeFile(file: MicroFile): String {
        return "import os;f = open('${file.fullPath}','w');f.write('');f.close();" +
                responseStatement(file.path)
    }

    /**
     * Generates a MicroPython command to rename a file or directory.
     *
     * @param src The `MicroFile` representing the source file or directory.
     * @param dst The `MicroFile` representing the destination file or directory.
     * @return The MicroPython command string.
     */
    fun rename(src: MicroFile, dst: MicroFile): String {
        return "import os;os.rename('${src.fullPath}','${dst.fullPath}');" + responseStatement(src.path)
    }


    /**
     * Checks if a silent execution has completed by searching for the
     * `SILENT_EXECUTION_END` marker in the received data.
     *
     * This method is used to detect the end of a MicroPython command
     * executed in silent mode.
     *
     * @param data The data received from the MicroPython REPL.
     * @return `true` if the silent execution has completed, `false` otherwise.
     */
    fun isSilentExecutionDone(data: String): Boolean {
        return data.contains(SILENT_EXECUTION_END)
    }

    /**
     * we use this to extract the result of silent execution
     * ex:
     *      raw REPL; CTRL-B to exit
     *      >OK
     *      RESULT
     *      RESULT
     *      RESULT
     *      
     *
     *      We need to extract RESULT
     */
    fun trimSilentResult(data: String): String {
        val i1 = data.indexOf(SILENT_EXECUTION_START)
        val i2 = data.indexOf(SILENT_EXECUTION_END)
        return if (i1 > -1 && i2 > -1 && i2 > i1) {
            data.substring(i1 + SILENT_EXECUTION_START.length, i2).trim()
        } else data
    }

}