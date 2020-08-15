package br.com.meiadois.decole.presentation.user.account

import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityUserPasswordBinding
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.listener.AccountListener
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountChangePasswordViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.factory.AccountChangePasswordViewModelFactory
import br.com.meiadois.decole.util.extension.shortSnackbar
import br.com.meiadois.decole.util.receiver.NetworkChangeReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_user_password.*
import kotlinx.android.synthetic.main.activity_user_password.btn_save
import kotlinx.android.synthetic.main.activity_user_password.container_button
import kotlinx.android.synthetic.main.activity_user_password.container_layout
import kotlinx.android.synthetic.main.activity_user_password.no_internet_layout
import kotlinx.android.synthetic.main.activity_user_password.progress_bar
import kotlinx.android.synthetic.main.activity_user_password.toolbar_back_button
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AccountChangePasswordActivity : AppCompatActivity(), KodeinAware, AccountListener {
    override val kodein by kodein()
    private val factoryAccount: AccountChangePasswordViewModelFactory by instance()
    private lateinit var mViewModel: AccountChangePasswordViewModel
    private var mNetworkReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProvider(this, factoryAccount)
            .get(AccountChangePasswordViewModel::class.java)
        mViewModel.accountListener = this

        val binding: ActivityUserPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_password)
        binding.apply {
            viewModel = mViewModel
            lifecycleOwner = this@AccountChangePasswordActivity
        }

        mNetworkReceiver = NetworkChangeReceiver(this) {
            if (!it) showNoInternetLayout()
            unregisterNetworkReceiver()
        }

        setRemoveErrorListener()
        toolbar_back_button.setOnClickListener { finish() }
    }

    // region local functions
    private fun showNoInternetLayout() {
        container_layout.visibility = View.GONE
        container_button.visibility = container_layout.visibility
        no_internet_layout.visibility = View.VISIBLE
    }

    private fun setRemoveErrorListener() {
        input_currentPassword.addTextChangedListener(
            mViewModel.onTextFieldChange(
                changePassword_currentPassword_inputLayout
            )
        )
        input_confirmNewPass.addTextChangedListener(
            mViewModel.onTextFieldChange(
                changePassword_confirmNewPass_inputLayout
            )
        )
        input_newPassword.addTextChangedListener(
            mViewModel.onTextFieldChange(
                changePassword_newPassword_inputLayout
            )
        )
    }

    private fun unregisterNetworkReceiver() {
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver)
            mNetworkReceiver = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkReceiver()
    }
    // endregion

    // region AccountListener interface functions
    override fun riseValidationError(field: FieldsEnum, errorMessage: String) {
        val textInputLayout: TextInputLayout? = when (field) {
            FieldsEnum.USER_CURRENTPASSWORD -> changePassword_currentPassword_inputLayout
            FieldsEnum.USER_CONFIRMPASSWORD -> changePassword_confirmNewPass_inputLayout
            FieldsEnum.USER_NEWPASSWORD -> changePassword_newPassword_inputLayout
            else -> null
        }
        textInputLayout?.error = errorMessage
    }

    override fun onActionError(errorMessage: String?) {
        setButtonSaveVisibility(true)
        password_root_layout.shortSnackbar(
            errorMessage ?: getString(R.string.error_when_executing_the_action)
        )
    }

    override fun onActionSuccess() {
        setButtonSaveVisibility(true)
        password_root_layout.shortSnackbar(getString(R.string.success_when_executing_the_action)) {
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

    private fun setButtonSaveVisibility(visible: Boolean) {
        btn_save.visibility = if (visible) View.VISIBLE else View.GONE
        progress_bar.visibility = if (visible) View.GONE else View.VISIBLE
    }
    // endregion
}