package br.com.meiadois.decole.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class Mask {
    companion object {
        const val CNPJ_MASK = "##.###.###/####-##"
        const val TEL_MASK = "(##) 9####-####"
        const val CPF_MASK = "###.###.###-##"
        const val CEP_MASK = "#####-###"
        private var countryCode = ""

        fun getTelMaskWithCountryCode(code: String): String{
            countryCode = "+$code"
            return  "$countryCode $TEL_MASK"
        }

        private fun replaceChars(fullValue: String): String {
            return fullValue.replace(" 9", "").replace(countryCode, "")
                .replace("(", "").replace(")", "")
                .replace("/", "").replace("-", "")
                .replace("*", "").replace(".", "")
                .replace(" ", "")
        }

        fun mask(mask: String, textInput: EditText, textChanged: (() -> Unit)? = null): TextWatcher {

            return object : TextWatcher {
                var isUpdating: Boolean = false
                var oldString: String = textInput.text.toString()
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val str = replaceChars(s.toString())
                    var valueWithMask = ""

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
                            valueWithMask += m
                            continue
                        }
                        try {
                            valueWithMask += str[i]
                        } catch (e: Exception) {
                            break
                        }
                        i++
                    }

                    isUpdating = true
                    textInput.setText(valueWithMask)
                    textInput.setSelection(valueWithMask.length)
                    textChanged?.invoke()
                }

                override fun afterTextChanged(editable: Editable) {
                }
            }
        }
    }
}