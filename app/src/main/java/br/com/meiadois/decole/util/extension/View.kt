package br.com.meiadois.decole.util.extension

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.longSnackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok"){
            snackbar.dismiss()
        }
    }.show()
}