package com.ma7moud3ly.microterminal.util

object CommandsManager {

    private fun container(s: String) = "print('\${',$s,'}\$')"

    fun listDir(path: String): String {
        return "import os;print('\${',os.listdir('$path'),'}\$')"
    }

    fun iListDir(path: String): String {
        return "import os;print('\${',list(os.ilistdir('$path')),'}\$')"
    }

    fun readFile(path: String): String {
        return "f = open('$path');x = f.read();f.close();print('\${',x,'}\$')"
    }

    fun writeFile(path: String, content: String): String {
        return "f = open('$path','w');x = f.write('$content');f.close();print('\${',x,'}\$')"
    }

    fun removeFile(path: String): String {
        return "import os;os.remove('$path');print('\${',{'status':1},'}\$')"
    }

    fun removeDirectory(path: String): String {
        return "import os;os.rmdir('$path');print('\${',{'status':1},'}\$')"
    }

    fun makeDirectory(path: String): String {
        return "import os;os.mkdir('$path');print('\${',{'status':1},'}\$')"
    }

    fun rename(src: String, dst: String): String {
        return "import os;os.rename('$src','$dst');print('\${',{'status':1},'}\$')"
    }

    private const val s1 = "\${"
    private const val s2 = "}\$"
    fun extractData(data: String): String {
        if (data.isEmpty()) return ""
        val hasResponse = data.count { it == '$' } >= 4
        val i1 = data.lastIndexOf(s1)
        val i2 = data.lastIndexOf(s2)
        return if (hasResponse && i1 != -1 && i2 != -1 && i1 < i2)
            data.substring(i1 + s1.length, i2).trim()
        else ""
    }
}