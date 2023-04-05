package com.ma7moud3ly.microterminal.managers

object CommandsManager {

    const val END_OUTPUT = "EXEC DONE"
    private const val END_STATEMENT = "print('EXEC','DONE')"
    private const val RESULT_BEGIN = "@{"
    private const val RESULT_END = "}@"

    private fun responseStatement(path: String) =
        "print('$RESULT_BEGIN',list(os.ilistdir('$path')),'$RESULT_END');$END_STATEMENT"

    fun listDir(path: String): String {
        return "print('$RESULT_BEGIN',list(os.listdir('$path')),'$RESULT_END');$END_STATEMENT"
    }

    fun iListDir(path: String): String {
        return "import os;" + responseStatement(path)
    }

    fun readFile(path: String): String {
        return "f = open('$path',encoding='utf-8');content = f.read();f.close();" +
                "print('$RESULT_BEGIN',content,'$RESULT_END');$END_STATEMENT"

    }

    fun writeFile(path: String, content: String): String {
        return "content = '''$content''';f = open('$path','w',encoding='utf-8');" +
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
        return if (hasResponse && i1 != -1 && i2 != -1 && i1 < i2)
            data.substring(i1 + RESULT_BEGIN.length, i2).trim()
        else default
    }
}