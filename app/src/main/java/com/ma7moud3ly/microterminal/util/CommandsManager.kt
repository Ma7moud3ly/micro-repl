package com.ma7moud3ly.microterminal.util

object CommandsManager {

    const val endStatement = "EXEC DONE"
    private const val end = "print('\n','EXEC','DONE')"
    private fun container(s: String) = "print('\${',$s,'}\$')"
    fun listDir(path: String): String {
        return "import os;print('\${',os.listdir('$path'),'}\$;');$end"
    }

    fun iListDir(path: String): String {
        return "import os;print('\${',list(os.ilistdir('$path')),'}\$');$end"
    }

    fun readFile(path: String): String {
        return "f = open('$path');x = f.read();f.close();print('\${',x,'}\$')"
    }

    fun writeFile(path: String, content: String): String {
        return "f = open('$path','w');x = f.write('$content');f.close();print('\${',x,'}\$')"
    }

    fun removeFile(file: MicroFile): String {
        return "import os;os.remove('${file.name}');print('\${',list(os.ilistdir('${file.path}')),'}\$');$end"
    }

    fun removeDirectory(file: MicroFile): String {
        return "import os;os.rmdir('${file.name}');print('\${',list(os.ilistdir('${file.path}')),'}\$');$end"
    }

    fun makeDirectory(file: MicroFile): String {
        return "import os;os.mkdir('${file.name}');print('\${',list(os.ilistdir('${file.path}')),'}\$');$end"
    }

    fun makeFile(file: MicroFile): String {
        return "import os;f = open('${file.name}','w');f.write('');f.close();" +
                "print('\${',list(os.ilistdir('${file.path}')),'}\$');$end"
    }

    fun rename(src: String, dst: String): String {
        return "import os;os.rename('$src','$dst');print('\${',{'status':1},'}\$')"
    }

    private const val s1 = "\${"
    private const val s2 = "}\$"
    fun extractResult(data: String, default: String = ""): String {
        if (data.isEmpty()) return default
        val hasResponse = data.count { it == '$' } >= 4
        val i1 = data.lastIndexOf(s1)
        val i2 = data.lastIndexOf(s2)
        return if (hasResponse && i1 != -1 && i2 != -1 && i1 < i2)
            data.substring(i1 + s1.length, i2).trim()
        else default
    }
}