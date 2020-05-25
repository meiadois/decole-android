package br.com.meiadois.decole.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class Mask {
    companion object {
        const val CNPJ_MASK = "##.###.###/####-##"
        const val TEL_MASK = "(##) #####-####"
        const val CPF_MASK = "###.###.###-##"
        const val CEP_MASK = "#####-###"

        private fun replaceChars(cpfFull: String): String {
            return cpfFull.replace(".", "").replace("-", "")
                .replace("(", "").replace(")", "")
                .replace("/", "").replace(" ", "")
                .replace("*", "")
        }

        fun mask(mask: String, textInput: EditText, textChanged: (() -> Unit)? = null): TextWatcher {

            return object : TextWatcher {
                val mtextChanged = textChanged
                var isUpdating: Boolean = false
                var oldString: String = textInput.text.toString()
                override fun beforeTextChanged( charSequence: CharSequence, i: Int, i1: Int, i2: Int ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val str = replaceChars(s.toString())
                    var cpfWithMask = ""

                    if (count == 0)
                        isUpdating = true

                    if (isUpdating) {
                        oldString = str
                        isUpdating = false
                        return
                    }

                    var i = 0
                    for (m: Char in mask.toCharArray()) {
                        if (m != '#' && str.length > oldString.length) {
                            cpfWithMask += m
                            continue
                        }
                        try {
                            cpfWithMask += str[i]
                        } catch (e: Exception) {
                            break
                        }
                        i++
                    }

                    isUpdating = true
                    textInput.setText(cpfWithMask)
                    textInput.setSelection(cpfWithMask.length)
                    this.mtextChanged?.invoke()
                }

                override fun afterTextChanged(editable: Editable) {
                }
            }
        }
    }
}