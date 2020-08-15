package br.com.meiadois.decole.presentation.user.account.validation

import android.text.TextUtils
import android.util.Patterns

// region Validators
abstract class Validator<T>(private val value: T) {
    var isValid: Boolean = true
        protected set
    var error: String = ""
        protected set
    private var validations: List<BaseRule<T>> = listOf()
    private var errorCallback: ((Validator<T>) -> Unit)? = null

    open fun addValidation(rule: BaseRule<T>): Validator<T> {
        validations = validations.plus(rule)
        return this
    }

    open fun addErrorCallback(callback: (Validator<T>) -> Unit): Validator<T> {
        errorCallback = callback
        return this
    }

    open fun validate(): Boolean {
        for (validation in validations) {
            if (!validation.validate(value)) {
                error = validation.errorMessage
                isValid = false
                break
            }
        }

        if (!isValid)
            errorCallback?.invoke(this)

        return isValid
    }
}

class StringValidator(value: String?) : Validator<String?>(value)

class IntegerValidator(value: Int?) : Validator<Int?>(value)
// endregion


// region rules
interface BaseRule<T> {
    val errorMessage: String
    fun validate(value: T?): Boolean
}

// string validations
class NotNullOrEmptyRule(override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = !value.isNullOrBlank()
}

class MinLengthRule(private val minLength: Int, override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = (value ?: "").length >= minLength
}

class MaxLengthRule(private val maxLength: Int, override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = (value ?: "").length <= maxLength
}

class BetweenLengthRule(private val minLength: Int, private val maxLength: Int, override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = (value ?: "").length in minLength..maxLength
}

class ExactLengthRule(private val length: Int, override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = value != null && value.length == length
}

class IsDigitsOnlyRule(override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = value != null && TextUtils.isDigitsOnly(value)
}

class EqualsTo(private val argument: String?, override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = value == argument
}

class ValidEmailRule(override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = (value ?: "").let {
        Patterns.EMAIL_ADDRESS.matcher(it.trim()).matches()
    }
}

class ValidCepRule(override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean {
        val cep = value?.trim()?.replace("-", "")
        return NotNullOrEmptyRule(String()).validate(cep) &&
                ExactLengthRule(8, String()).validate(cep) &&
                IsDigitsOnlyRule(String()).validate(cep)
    }
}

class ValidCnpjRule(override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean = isValid(
        value?.trim()
            ?.replace(".", "")
            ?.replace("/", "")
            ?.replace("-", "") ?: ""
    )

    private fun isValid(value: String): Boolean {
        return validateCNPJLength(value) && validateCNPJRepeatedNumbers(value)
                && validateCNPJVerificationDigit(true, value)
                && validateCNPJVerificationDigit(false, value)
    }

    private fun validateCNPJLength(cnpj: String) = cnpj.length == 14

    private fun validateCNPJRepeatedNumbers(cnpj: String): Boolean {
        return (0..9)
            .map { it.toString().repeat(14) }
            .map { cnpj == it }
            .all { !it }
    }

    private fun validateCNPJVerificationDigit(firstDigit: Boolean, cnpj: String): Boolean {
        val startPos = when (firstDigit) {
            true -> 11
            else -> 12
        }
        val weightOffset = when (firstDigit) {
            true -> 0
            false -> 1
        }
        val sum = (startPos downTo 0).fold(0) { acc, pos ->
            val weight = 2 + ((11 + weightOffset - pos) % 8)
            val num = cnpj[pos].toString().toInt()
            val sum = acc + (num * weight)
            sum
        }
        val expectedDigit = when (val result = sum % 11) {
            0, 1 -> 0
            else -> 11 - result
        }

        val actualDigit = cnpj[startPos + 1].toString().toInt()

        return expectedDigit == actualDigit
    }
}

class ValidTelephoneRule(override val errorMessage: String) : BaseRule<String?> {
    override fun validate(value: String?): Boolean {
        var tel = value?.trim()
        if (!NotNullOrEmptyRule(String()).validate(tel)) return false
        tel = tel!!
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "")
            .replace("-", "")
            .replace("+", "")
        return ExactLengthRule(13, String()).validate(tel) &&
                IsDigitsOnlyRule(String()).validate(tel)
    }
}

// numeric validations
class MaxRule(private val max: Int, override val errorMessage: String) : BaseRule<Int?> {
    override fun validate(value: Int?): Boolean = value != null && value <= max
}

class MinRule(private val min: Int, override val errorMessage: String) : BaseRule<Int?> {
    override fun validate(value: Int?): Boolean = value != null && value >= min
}
// endregion