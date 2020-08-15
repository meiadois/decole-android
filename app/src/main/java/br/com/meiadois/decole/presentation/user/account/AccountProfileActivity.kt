package br.com.meiadois.decole.presentation.user.account

import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityUserProfileBinding
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.listener.AccountListener
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountProfileViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.factory.AccountProfileViewModelFactory
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.shortSnackbar
import br.com.meiadois.decole.util.receiver.NetworkChangeReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.btn_save
import kotlinx.android.synthetic.main.activity_user_profile.container_button
import kotlinx.android.synthetic.main.activity_user_profile.container_layout
import kotlinx.android.synthetic.main.activity_user_profile.progress_bar
import kotlinx.android.synthetic.main.activity_user_profile.toolbar_back_button
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AccountProfileActivity : AppCompatActivity(), KodeinAware,
    AccountListener {
    override val kodein by kodein()
    private val factory: AccountProfileViewModelFactory by instance()
    private lateinit var mViewModel: AccountProfileViewModel
    private var mNetworkReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        mViewModel = ViewModelProvider(this, factory).get(AccountProfileViewModel::class.java)
        mViewModel.accountListener = this

        val binding: ActivityUserProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        binding.apply {
            viewModel = mViewModel
            lifecycleOwner = this@AccountProfileActivity
        }

        mNetworkReceiver = NetworkChangeReceiver(this) {
            if (!it) setContentVisibility(CONTENT_NO_INTERNET)
            init { unregisterNetworkReceiver() }
        }

        setRemoveErrorListener()
        setClickListeners()
    }

    // region Local Functions
    private fun init(onFinish: () -> Unit) {
        try {
            mViewModel.init()
            setContentVisibility(CONTENT_FORM)
            onFinish.invoke()
        } catch (ex: NoInternetException) {
            setContentVisibility(CONTENT_NO_INTERNET)
        } catch (ex: ClientException) {
            showGenericErrorMessage()
        } catch (ex: Exception) {
            Firebase.crashlytics.recordException(ex)
            showGenericErrorMessage()
        }
    }

    private fun setContentVisibility(contentMode: Int) {
        container_layout.visibility = if (contentMode == CONTENT_FORM) View.VISIBLE else View.GONE
        container_button.visibility = container_layout.visibility
        no_internet_layout.visibility = if (contentMode == CONTENT_NO_INTERNET) View.VISIBLE else View.GONE
    }

    private fun showGenericErrorMessage() {
        profile_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                init { unregisterNetworkReceiver() }
                snackbar.dismiss()
            }
        }
    }

    private fun setButtonSaveVisibility(visible: Boolean) {
        btn_save.visibility = if (visible) View.VISIBLE else View.GONE
        progress_bar.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun setRemoveErrorListener() {
        profile_name?.addTextChangedListener(mViewModel.onTextFieldChange(profile_name_inputLayout))
        profile_mail?.addTextChangedListener(mViewModel.onTextFieldChange(profile_mail_inputLayout))
    }

    private fun setClickListeners() {
        toolbar_back_button.setOnClickListener { finish() }
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

    // region Listener Functions
    override fun riseValidationError(field: FieldsEnum, errorMessage: String) {
        val textInputLayout: TextInputLayout? = when (field) {
            FieldsEnum.USER_NAME -> profile_name_inputLayout
            FieldsEnum.USER_EMAIL -> profile_mail_inputLayout
            else -> null
        }
        textInputLayout?.error = errorMessage
    }

    override fun onActionError(errorMessage: String?) {
        setButtonSaveVisibility(true)
        profile_root_layout.shortSnackbar(
            errorMessage ?: getString(R.string.error_when_executing_the_action)
        )
    }

    override fun onActionSuccess() {
        setButtonSaveVisibility(true)
        profile_root_layout.shortSnackbar(getString(R.string.success_when_executing_the_action)) {
            it.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    finish()
                }
            })
        }
    }

    override fun onActionStarted() = setButtonSaveVisibility(false)
    // endregion

    companion object {
        private const val CONTENT_NO_INTERNET = 1
        private const val CONTENT_NO_CONTENT = 2
        private const val CONTENT_FORM = 3
    }
}