package br.com.meiadois.decole.util.extension

import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun View.longSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.shortSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.longSnackbar(message: String, block: (Snackbar) -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also(block).show()
}

fun View.shortSnackbar(message: String, block: (Snackbar) -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).also(block).show()
}

fun View.shortToast(message: CharSequence) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}
