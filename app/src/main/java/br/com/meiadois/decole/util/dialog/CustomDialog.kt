package br.com.meiadois.decole.util.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import br.com.meiadois.decole.R
import kotlinx.android.synthetic.main.layout_custom_dialog.*
import java.lang.Exception

/*
    link for the custom dialog in stackoverfow: https://stackoverflow.com/a/62167303/10457149
*/

class CustomDialog(private val activity: Activity): Dialog(activity) {
    private var isPositiveOrNegativeButtonSet = false
    private var isNeutralButtonSet = false

    private var configurations: Array<(() -> Unit)> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_custom_dialog)
        setCancelable(false)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        configurations.forEach { it.invoke() }
    }

    fun create(title: CharSequence?, message: CharSequence?): CustomDialog {
        configurations += {
            dialog_content.text = message
            dialog_title.text = title
        }
        return this
    }

    fun setNeutralButton(text: CharSequence?, clickListener: (button: View) -> Unit): CustomDialog {
        if (isPositiveOrNegativeButtonSet)
            throw Exception("Can't set a neutral button with positive and/or negative buttons")
        isNeutralButtonSet = true
        configurations +=  { setButton(btn_neutral_button, text, clickListener) }
        return this
    }

    fun setPositiveButton(text: CharSequence?, clickListener: (button: View) -> Unit): CustomDialog {
        if (isNeutralButtonSet)
            throw Exception("Can't set a positive button with neutral button")
        isPositiveOrNegativeButtonSet = true
        configurations +=  { setButton(btn_positive_button, text, clickListener) }
        return this
    }

    fun setNegativeButton(text: CharSequence?, clickListener: (button: View) -> Unit): CustomDialog {
        if (isNeutralButtonSet)
            throw Exception("Can't set a negative button with neutral button")
        isPositiveOrNegativeButtonSet = true
        configurations +=  { setButton(btn_negative_button, text, clickListener) }
        return this
    }

    fun setIcon(resId: Int): CustomDialog {
        configurations +=  {
            val size = activity.resources.getDimensionPixelSize(R.dimen.custom_dialog_size)
            val icon = activity.getDrawable(resId)
            icon?.setBounds(0, 0, size, size)
            dialog_title.setCompoundDrawables(null, icon, null, null)
        }
        return this
    }

    private fun setButton(button: Button, text: CharSequence?, clickListener: (button: View) -> Unit) {
        button.setOnClickListener { clickListener.invoke(it); dismiss() }
        button.visibility = View.VISIBLE
        button.text = text
    }
}

