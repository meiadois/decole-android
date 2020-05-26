package br.com.meiadois.decole.presentation.user.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityChangePasswordBinding
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.viewmodel.ChangePasswordViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.ChangePasswordViewModelFactory
import br.com.meiadois.decole.util.extension.shortSnackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_change_password.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ChangePasswordActivity() : AppCompatActivity(), KodeinAware, AccountListener {
    override val kodein by kodein()
    private val factory: ChangePasswordViewModelFactory by instance<ChangePasswordViewModelFactory>()
    private lateinit var changePasswordViewModel: ChangePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        changePasswordViewModel = ViewModelProvider(this, factory).get(ChangePasswordViewModel::class.java)
        changePasswordViewModel.accountListener = this
        val binding: ActivityChangePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        binding.apply {
            viewModel = changePasswordViewModel
            lifecycleOwner = this@ChangePasswordActivity
        }
        setRemoveErrorListener()
        toolbar_back_button.setOnClickListener { finish() }
    }

    // region local functions
    private fun setRemoveErrorListener(){
        input_currentPassword.addTextChangedListener(changePasswordViewModel.onTextFieldChange(change_password_currentPassword_input))
        input_confirmPassword.addTextChangedListener(changePasswordViewModel.onTextFieldChange(change_password_confirmPassword_input))
        input_newPassword.addTextChangedListener(changePasswordViewModel.onTextFieldChange(change_password_newPassword_input))
    }
    // endregion

    // region AccountListener interface functions
    override fun riseValidationError(field: FieldsEnum, errorMessage: String) {
        val textInputLayout: TextInputLayout? = when(field){
            FieldsEnum.USER_CURRENTPASSWORD -> change_password_currentPassword_input
            FieldsEnum.USER_CONFIRMPASSWORD -> change_password_confirmPassword_input
            FieldsEnum.USER_NEWPASSWORD -> change_password_newPassword_input
            else -> null
        }
        textInputLayout?.error = errorMessage
    }

    override fun onActionError(errorMessage: String?) {
        setButtonSaveVisibility(true)
        change_password_root_layout.shortSnackbar(errorMessage ?: getString(R.string.error_when_executing_the_action))
    }

    override fun onActionSuccess() {
        setButtonSaveVisibility(true)
        change_password_root_layout.shortSnackbar(getString(R.string.success_when_executing_the_action)){
            it.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    finish()
                }
            })
        }
    }

    override fun onActionStarted() {
        setButtonSaveVisibility(false)
    }

    private fun setButtonSaveVisibility(visible: Boolean){
        btn_save.visibility = if (visible) View.VISIBLE else View.GONE
        progress_bar.visibility = if (visible) View.GONE else View.VISIBLE
    }
    // endregion
}