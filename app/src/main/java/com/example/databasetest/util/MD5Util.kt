package com.example.databasetest.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object MD5Util {
    /**
     * 1.java原生用法
     *
     * @param dataStr
     * @return
     */
    fun encrypt(dataStr: String): String {
        try {
            val m = MessageDigest.getInstance("MD5")
            m.update(dataStr.toByteArray(StandardCharsets.UTF_8))
            val s = m.digest()
            val result = StringBuilder()
            for (b in s) {
                result.append(Integer.toHexString(0x000000FF and b.toInt() or -0x100).substring(6))
            }
            return result.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}