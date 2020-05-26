package br.com.meiadois.decole.presentation. user.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.databinding.ActivityAccountBinding
import br.com.meiadois.decole.presentation.auth.LoginActivity
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.Mask
import br.com.meiadois.decole.util.extension.shortSnackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AccountActivity : AppCompatActivity(), KodeinAware, AccountListener {
    override val kodein by kodein()
    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)
        accountViewModel.accountListener = this
        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account)
        binding.apply {
            viewModel = accountViewModel
            lifecycleOwner = this@AccountActivity
        }
        setAdapterToSegmentDropdown()
        setRemoveErrorListener()
        setClickListeners()
        setInputMasks()
    }

    // region Local Functions
    private fun setClickListeners(){
        toolbar_back_button.setOnClickListener { finish() }
        toolbar_exit_button.setOnClickListener {
            Coroutines.main {
                accountViewModel.onLogOutButtonClick()
                Intent(this, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        }
        go_to_change_password.setOnClickListener {
            Intent(this, ChangePasswordActivity::class.java).also { startActivity(it) }
        }
    }

    private fun setRemoveErrorListener(){
        input_company_name.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_name_input))
        input_company_cep.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_cep_input))
        input_company_cnpj.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_cnpj_input))
        input_company_telephone.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_telephone_input))
        input_company_mail.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_mail_input))
        input_company_description.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_description_input))
        input_company_city.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_city_input))
        input_company_neighborhood.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_neighborhood_input))
        filled_exposed_dropdown.addTextChangedListener(accountViewModel.onTextFieldChange(account_company_segment_input))
        input_me_name.addTextChangedListener(accountViewModel.onTextFieldChange(account_me_name_input))
        input_me_mail.addTextChangedListener(accountViewModel.onTextFieldChange(account_me_mail_input))
    }

    override fun riseValidationError(field: FieldsEnum, errorMessage: String) {
        val textInputLayout: TextInputLayout? = when(field){
            FieldsEnum.COMPANY_NAME -> account_company_name_input
            FieldsEnum.COMPANY_CEP -> account_company_cep_input
            FieldsEnum.COMPANY_CNPJ -> account_company_cnpj_input
            FieldsEnum.COMPANY_CELLPHONE -> account_company_telephone_input
            FieldsEnum.COMPANY_EMAIL -> account_company_mail_input
            FieldsEnum.COMPANY_DESCRIPTION -> account_company_description_input
            FieldsEnum.COMPANY_CITY -> account_company_city_input
            FieldsEnum.COMPANY_NEIGHBORHOOD -> account_company_neighborhood_input
            FieldsEnum.COMPANY_SEGMENT -> account_company_segment_input
            FieldsEnum.USER_NAME -> account_me_name_input
            FieldsEnum.USER_EMAIL -> account_me_mail_input
            else -> null
        }
        textInputLayout?.error = errorMessage
    }

    override fun onActionError(errorMessage: String?) {
        setButtonSaveVisibility(true)
        account_root_layout.shortSnackbar(errorMessage ?: getString(R.string.error_when_executing_the_action))
    }

    override fun onActionSuccess() {
        setButtonSaveVisibility(true)
        account_root_layout.shortSnackbar(getString(R.string.success_when_executing_the_action)){
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

    private fun setInputMasks(){
        input_company_telephone.addTextChangedListener(Mask.mask(Mask.TEL_MASK, input_company_telephone))
        input_company_cnpj.addTextChangedListener(Mask.mask(Mask.CNPJ_MASK, input_company_cnpj))
        input_company_cep.addTextChangedListener(Mask.mask(Mask.CEP_MASK, input_company_cep) {
            accountViewModel.onCepFieldChange(input_company_cep.text.toString())
        })
    }

    private fun setAdapterToSegmentDropdown(){
        accountViewModel.segments.observe(this, Observer { it ->
            it?.let {segments ->
                filled_exposed_dropdown.setAdapter(
                    ArrayAdapter(this, R.layout.layout_exposed_dropdown_item, segments.map { it.name }.toTypedArray()))
                filled_exposed_dropdown.inputType = android.text.InputType.TYPE_NULL
                if (accountViewModel.companyData.value == null)
                    accountViewModel.companyData.observe(this, Observer {
                        updateSegmentDropdown(segments, accountViewModel.companyData.value?.segmentName ?: "")
                    })
                else
                    updateSegmentDropdown(segments, accountViewModel.companyData.value?.segmentName ?: "")
                filled_exposed_dropdown.setOnItemClickListener { _, _, position, _ ->
                    accountViewModel.companyData.value?.segmentName = segments[position].name
                    accountViewModel.companyData.value?.segmentId = segments[position].id ?: 0
                }
            }
        })
    }

    private fun updateSegmentDropdown(segments: List<Segment>, segmentName: String){
        val mSegment = segments.firstOrNull { it.name == segmentName }
        filled_exposed_dropdown.setText(mSegment?.name, false)
        accountViewModel.companyData.value?.segmentId = mSegment?.id ?: -1
    }
    // endregion
}