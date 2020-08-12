package br.com.meiadois.decole.presentation.user.account

import android.Manifest.permission.*
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.databinding.ActivityAccountBinding
import br.com.meiadois.decole.presentation.auth.LoginActivity
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.factory.AccountViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.Mask
import br.com.meiadois.decole.util.dialog.CustomDialog
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.shortSnackbar
import br.com.meiadois.decole.util.receiver.NetworkChangeReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import kotlin.reflect.KClass

class AccountActivity : AppCompatActivity(), KodeinAware, AccountListener {
    override val kodein by kodein()
    private val factory: AccountViewModelFactory by instance()
    private lateinit var accountViewModel: AccountViewModel
    private var mNetworkReceiver: BroadcastReceiver? = null

    private var resultForAfterPermissionsGranted: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)
        accountViewModel.accountListener = this
        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account)
        binding.apply {
            viewModel = accountViewModel
            lifecycleOwner = this@AccountActivity
        }

        mNetworkReceiver = NetworkChangeReceiver(this) {
            if (!it) setContentVisibility(CONTENT_NO_INTERNET)
            init { unregisterNetworkReceiver() }
        }

        setAdapterToSegmentDropdown()
        setRemoveErrorListener()
        setClickListeners()
        setImageInputs()
        setInputMasks()

        input_company_description.imeOptions = EditorInfo.IME_ACTION_DONE
        input_company_description.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
    }

    // region Permissions
    private fun initPickingFromGallery(resultCode: Int, showDialog: Boolean = true) {
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (showDialog && shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE))
                showRequestPermissionRationale(resultCode)
            else
                requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), READ_WRITE_PERMISSIONS_RESULT)
            resultForAfterPermissionsGranted = resultCode
        } else
            pickImageFromGallery(resultCode)
    }

    private fun showRequestPermissionRationale(resultCode: Int){
        val message = getString(R.string.storage_permission_rationale_message)
        val title = getString(R.string.storage_permission_rationale_title)

        CustomDialog(this)
            .create(title, message)
            .setNeutralButton(getString(R.string.ok)) { initPickingFromGallery(resultCode, false) }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_WRITE_PERMISSIONS_RESULT -> {
                if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }))
                    if (resultForAfterPermissionsGranted != -1)
                        pickImageFromGallery(resultForAfterPermissionsGranted)
            }
            else -> {
            }
        }
    }
    // endregion

    // region Image Picking
    private fun pickImageFromGallery(resultCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = DEFAULT_IMAGE_TYPE
        startActivityForResult(intent, resultCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    when (requestCode) {
                        IMAGE_BANNER_RESULT -> {
                            accountViewModel.companyData.value!!.banner.path =
                                getImageFromFilePath(data) ?: String()
                            if (accountViewModel.companyData.value!!.banner.path.isNotEmpty()) {
                                accountViewModel.companyData.value!!.banner.type =
                                    contentResolver.getType(data!!.data!!) ?: DEFAULT_IMAGE_TYPE
                                accountViewModel.companyData.value!!.banner.name =
                                    accountViewModel.getFileName(
                                        accountViewModel.companyData.value!!.banner.path
                                    )
                                accountViewModel.companyData.value!!.banner.updated = true
                            }
                        }
                        IMAGE_LOGO_RESULT -> {
                            accountViewModel.companyData.value!!.thumbnail.path =
                                getImageFromFilePath(data) ?: String()
                            if (accountViewModel.companyData.value!!.thumbnail.path.isNotEmpty()) {
                                accountViewModel.companyData.value!!.thumbnail.type =
                                    contentResolver.getType(data!!.data!!) ?: DEFAULT_IMAGE_TYPE
                                accountViewModel.companyData.value!!.thumbnail.name =
                                    accountViewModel.getFileName(
                                        accountViewModel.companyData.value!!.thumbnail.path
                                    )
                                accountViewModel.companyData.value!!.thumbnail.updated = true
                            }
                        }
                        else -> {
                        }
                    }
                }
                else -> {
                }
            }
        } catch (ex: Exception) {
            Log.i("ImagePickEx", ex.message ?: "No message")
            Firebase.crashlytics.recordException(ex)
        }
    }

    private fun getImageFromFilePath(data: Intent?): String? =
        if (data != null && data.data != null) getPathFromURI(data.data!!) else String()

    private fun getPathFromURI(contentUri: Uri): String? {
        val cursor = contentResolver.query(
            contentUri, arrayOf(MediaStore.Audio.Media.DATA), null, null, null
        )
        val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA) ?: 0
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex)
        cursor?.close()
        return path
    }
    // endregion

    // region Local Functions
    private fun init(onFinish: () -> Unit) {
        setContentVisibility(CONTENT_NO_CONTENT)
        setPageProgressBarVisibility(true)
        Coroutines.main {
            try {
                accountViewModel.init()
                setContentVisibility(CONTENT_FORM)
                onFinish.invoke()
            } catch (ex: NoInternetException) {
                setContentVisibility(CONTENT_NO_INTERNET)
            } catch (ex: ClientException) {
                showGenericErrorMessage()
            } catch (ex: Exception) {
                Firebase.crashlytics.recordException(ex)
                showGenericErrorMessage()
            } finally {
                setPageProgressBarVisibility(false)
            }
        }
    }

    private fun setContentVisibility(contentMode: Int) {
        container_layout.visibility = if (contentMode == CONTENT_FORM) View.VISIBLE else View.GONE
        container_button.visibility = container_layout.visibility
        account_no_internet_layout.visibility = if (contentMode == CONTENT_NO_INTERNET) View.VISIBLE else View.GONE
    }

    private fun setPageProgressBarVisibility(visible: Boolean) {
        page_progress_bar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun showGenericErrorMessage() {
        account_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                init { unregisterNetworkReceiver() }
                snackbar.dismiss()
            }
        }
    }

    private fun setImageInputs() {
        thumbnail_fake_input.bringToFront()
        thumbnail_fake_input.setOnClickListener {
            initPickingFromGallery(IMAGE_LOGO_RESULT)
        }
        banner_fake_input.bringToFront()
        banner_fake_input.setOnClickListener {
            initPickingFromGallery(IMAGE_BANNER_RESULT)
        }
    }

    private fun setClickListeners() {
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

    @Suppress("UNCHECKED_CAST")
    private fun <T: View> getFirstChildOfTypeList(types: Array<KClass<out T>>, children: Sequence<View>): T? {
        for (child in children) {
            for (type in types)
                if (type.isInstance(child))
                    return child as T
            if (child is ViewGroup)
                return getFirstChildOfTypeList(types, child.children)
        }
        return null
    }

    private fun setRemoveErrorListener() {
        val forms: Array<ViewGroup> = arrayOf(
            account_me_form,
            account_company_form,
            company_banner_frame,
            company_thumbnail_frame
        )
        val acceptedTypes: Array<KClass<out EditText>> = arrayOf(
            TextInputEditText::class,
            AutoCompleteTextView::class
        )
        for (form in forms)
            for (child in form.children)
                if (child is TextInputLayout) {
                    val editText = getFirstChildOfTypeList(acceptedTypes, child.children)
                    editText?.addTextChangedListener(accountViewModel.onTextFieldChange(child))
                }
    }

    override fun riseValidationError(field: FieldsEnum, errorMessage: String) {
        val textInputLayout: TextInputLayout? = when (field) {
            FieldsEnum.COMPANY_NAME -> account_company_name_input
            FieldsEnum.COMPANY_CEP -> account_company_cep_input
            FieldsEnum.COMPANY_CNPJ -> account_company_cnpj_input
            FieldsEnum.COMPANY_CELLPHONE -> account_company_telephone_input
            FieldsEnum.COMPANY_EMAIL -> account_company_mail_input
            FieldsEnum.COMPANY_DESCRIPTION -> account_company_description_input
            FieldsEnum.COMPANY_CITY -> account_company_city_input
            FieldsEnum.COMPANY_NEIGHBORHOOD -> account_company_neighborhood_input
            FieldsEnum.COMPANY_SEGMENT -> account_company_segment_input
            FieldsEnum.COMPANY_THUMBNAIL -> account_company_logo_input
            FieldsEnum.COMPANY_BANNER -> account_company_banner_input
            FieldsEnum.USER_NAME -> account_me_name_input
            FieldsEnum.USER_EMAIL -> account_me_mail_input
            else -> null
        }
        textInputLayout?.error = errorMessage
    }

    override fun onActionError(errorMessage: String?) {
        setButtonSaveVisibility(true)
        account_root_layout.shortSnackbar(
            errorMessage ?: getString(R.string.error_when_executing_the_action)
        )
    }

    override fun onActionSuccess() {
        setButtonSaveVisibility(true)
        account_root_layout.shortSnackbar(getString(R.string.success_when_executing_the_action)) {
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

    private fun setInputMasks() {
        input_company_telephone.addTextChangedListener(
            Mask.mask(Mask.getTelMaskWithCountryCode("55"), input_company_telephone)
        )
        input_company_cnpj.addTextChangedListener(Mask.mask(Mask.CNPJ_MASK, input_company_cnpj))
        input_company_cep.addTextChangedListener(Mask.mask(Mask.CEP_MASK, input_company_cep) {
            accountViewModel.onCepFieldChange(input_company_cep.text.toString())
        })
    }

    private fun setAdapterToSegmentDropdown() {
        accountViewModel.segments.observe(this, Observer { it ->
            it?.let { segments ->
                filled_exposed_dropdown.setAdapter(
                    ArrayAdapter(
                        this,
                        R.layout.layout_exposed_dropdown_item,
                        segments.map { it.name }.toTypedArray()
                    )
                )
                filled_exposed_dropdown.inputType = InputType.TYPE_NULL
                if (accountViewModel.companyData.value == null)
                    accountViewModel.companyData.observe(this, Observer {
                        updateSegmentDropdown(
                            segments,
                            accountViewModel.companyData.value?.segmentName ?: ""
                        )
                    })
                else
                    updateSegmentDropdown(
                        segments,
                        accountViewModel.companyData.value?.segmentName ?: ""
                    )
                filled_exposed_dropdown.setOnItemClickListener { _, _, position, _ ->
                    accountViewModel.companyData.value?.segmentName = segments[position].name
                    accountViewModel.companyData.value?.segmentId = segments[position].id ?: 0
                }
            }
        })
    }

    private fun updateSegmentDropdown(segments: List<Segment>, segmentName: String) {
        val mSegment = segments.firstOrNull { it.name == segmentName }
        filled_exposed_dropdown.setText(mSegment?.name, false)
        accountViewModel.companyData.value?.segmentId = mSegment?.id ?: -1
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

    companion object {
        private const val READ_WRITE_PERMISSIONS_RESULT = 107
        private const val IMAGE_BANNER_RESULT = 201
        private const val IMAGE_LOGO_RESULT = 200

        private const val DEFAULT_IMAGE_TYPE = "image/*"

        private const val CONTENT_NO_INTERNET = 1
        private const val CONTENT_NO_CONTENT = 2
        private const val CONTENT_FORM = 3
    }
}