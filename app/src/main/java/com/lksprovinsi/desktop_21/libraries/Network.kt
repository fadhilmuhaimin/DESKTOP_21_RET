package com.lksprovinsi.desktop_21.libraries

import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Network(path: String, method: String) {

    private var conn: HttpURLConnection

    val connection: HttpURLConnection
        get() = conn

    val isSuccess: Boolean
        get() = conn.responseCode in 200 until 300

    var body: String = ""
        set(value) {
            field = value;
            conn.outputStream.write(value.toByteArray())
        }

    val responseBody: String
        get(){
            val sb = StringBuilder()
            InputStreamReader(conn.inputStream).useLines {
                it.forEach { line -> sb.append(line) }
            }
            return sb.toString()
        }

    init{
        conn = URL("$BASE$path").openConnection() as HttpURLConnection
        conn.requestMethod = method
    }

    fun withConnection(block: HttpURLConnection.() -> Unit){
        block(conn);
    }

    companion object{
        const val BASE = "http://10.0.2.2:5000"
    }
}