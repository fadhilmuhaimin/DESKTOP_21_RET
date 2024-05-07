package com.lksprovinsi.desktop_21.libraries

import android.os.AsyncTask

class AsyncWorker : AsyncTask<Unit, Unit, Unit>() {

    private var backgroundHandler: Handler? = null
    private var beforeHandler: Handler? = null
    private var afterHandler: Handler? = null

    fun background(handler: Handler){
        backgroundHandler = handler
    }

    fun before(handler: Handler){
        beforeHandler = handler
    }

    fun after(handler: Handler){
        afterHandler = handler
    }

    override fun doInBackground(vararg p0: Unit?) {
        backgroundHandler?.handle()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        beforeHandler?.handle()
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        afterHandler?.handle()
    }

    fun interface Handler{
        fun handle()
    }
}