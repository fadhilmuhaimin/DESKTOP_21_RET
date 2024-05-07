package com.lksprovinsi.desktop_21.libraries

import android.app.ProgressDialog
import android.content.Context

class Dialogs {
    companion object{
        fun loading(ctx: Context): ProgressDialog{
            val dialog = ProgressDialog(ctx)
            dialog.setCancelable(false)
            dialog.setMessage("Loading...")
            return dialog
        }
    }
}