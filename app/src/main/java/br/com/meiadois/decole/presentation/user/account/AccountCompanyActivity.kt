package br.com.meiadois.decole.presentation.user.account

import android.Manifest
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
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.databinding.ActivityUserCompanyBinding
import br.com.meiadois.decole.presentation.user.account.binding.CompanyData
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.binding.ImageData
import br.com.meiadois.decole.presentation.user.account.listener.AccountListener
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountCompanyViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.factory.AccountCompanyViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.Mask
import br.com.meiadois.decole.util.dialog.CustomDialog
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.shortSnackbar
import br.com.meiadois.decole.util.receiver.NetworkChangeReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_company.*
import kotlinx.android.synthetic.main.activity_user_company.btn_save
import kotlinx.android.synthetic.main.activity_user_company.container_button
import kotlinx.android.synthetic.main.activity_user_company.container_layout
import kotlinx.android.synthetic.main.activity_user_company.page_progress_bar
import kotlinx.android.synthetic.main.activity_user_company.progress_bar
import kotlinx.android.synthetic.main.activity_user_company.toolbar_back_button
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import kotlin.reflect.KClass

class AccountCompanyActivity : AppCompatActivity(), KodeinAware, AccountListener {
    override val kodein by kodein()
    private val factory: AccountCompanyViewModelFactory by instance()
    private lateinit var mViewModel: AccountCompanyViewModel
    private var mNetworkReceiver: BroadcastReceiver? = null

    private var resultForAfterPermissionsGranted: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProvider(this, factory).get(AccountCompanyViewModel::class.java)
        mViewModel.accountListener = this

        val binding: ActivityUserCompanyBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_company)
        binding.apply {
            viewModel = mViewModel
            lifecycleOwner = this@AccountCompanyActivity
        }

        mNetworkReceiver = NetworkChangeReceiver(this) {
            if (!it) setContentVisibility(CONTENT_NO_INTERNET)
            init { unregisterNetworkReceiver() }
        }

        setAdapterToSegmentDropdown()
        setRemoveErrorListener()
        setClickListeners()
        setInputMasks()

        company_description.imeOptions = EditorInfo.IME_ACTION_DONE
        company_description.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
    }

    // region Permissions
    private fun initPickingFromGallery(resultCode: Int, showDialog: Boolean = true) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (showDialog && shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                showRequestPermissionRationale(resultCode)
            else
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_WRITE_PERMISSIONS_RESULT
                )
            resultForAfterPermissionsGranted = resultCode
        } else
            pickImageFromGallery(resultCode)
    }

    private fun showRequestPermissionRationale(resultCode: Int) {
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
                            updateImageData(mViewModel.companyData.value!!.banner, data)
                            Glide.with(company_banner_layout).load(data?.data ?: "")
                                .centerCrop().into(comany_banner)
                        }
                        IMAGE_LOGO_RESULT -> {
                            updateImageData(mViewModel.companyData.value!!.thumbnail, data)
                            Glide.with(logo_image_layout).load(data?.data ?: "")
                                .apply(RequestOptions.circleCropTransform()).into(company_logo)
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

    private fun updateImageData(image: ImageData, data: Intent?) {
        image.path = getImageFromFilePath(data) ?: String()
        if (image.path.isNotEmpty()) {
            image.type = contentResolver.getType(data!!.data!!) ?: DEFAULT_IMAGE_TYPE
            image.name = mViewModel.getFileName(mViewModel.companyData.value!!.banner.path)
            image.updated = true
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
                mViewModel.init()
                updateCompanyimages(mViewModel.companyData.value)
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

    private fun updateCompanyimages(company: CompanyData?) {
        company?.let {
            Glide.with(company_banner_layout).load(it.banner.path)
                .centerCrop().into(comany_banner)
            Glide.with(logo_image_layout).load(it.thumbnail.path)
                .apply(RequestOptions.circleCropTransform()).into(company_logo)
        }
    }

    private fun setContentVisibility(contentMode: Int) {
        container_layout.visibility = if (contentMode == CONTENT_FORM) View.VISIBLE else View.GONE
        container_button.visibility = container_layout.visibility
        no_internet_layout.visibility = if (contentMode == CONTENT_NO_INTERNET) View.VISIBLE else View.GONE
    }

    private fun setPageProgressBarVisibility(visible: Boolean) {
        page_progress_bar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun showGenericErrorMessage() {
        company_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                init { unregisterNetworkReceiver() }
                snackbar.dismiss()
            }
        }
    }

    private fun setImageInputs() {
        logo_image_layout.setOnClickListener {
            initPickingFromGallery(IMAGE_LOGO_RESULT)
        }

        change_banner_icon.setOnClickListener {
            initPickingFromGallery(IMAGE_BANNER_RESULT)
        }
    }

    private fun setClickListeners() {
        toolbar_back_button.setOnClickListener { finish() }
        setImageInputs()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : View> getFirstChildOfTypeList(types: Array<KClass<out T>>, children: Sequence<View>): T? {
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
        val acceptedTypes: Array<KClass<out EditText>> = arrayOf(
            AutoCompleteTextView::class,
            TextInputEditText::class
        )
        for (child in company_form.children)
            if (child is TextInputLayout) {
                val editText = getFirstChildOfTypeList(acceptedTypes, child.children)
                editText?.addTextChangedListener(mViewModel.onTextFieldChange(child))
            }
    }

    private fun setButtonSaveVisibility(visible: Boolean) {
        btn_save.visibility = if (visible) View.VISIBLE else View.GONE
        progress_bar.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun setInputMasks() {
        company_telephone.addTextChangedListener(Mask.mask(Mask.getTelMaskWithCountryCode("55"), company_telephone))
        company_cnpj.addTextChangedListener(Mask.mask(Mask.CNPJ_MASK, company_cnpj))
        company_cep.addTextChangedListener(Mask.mask(Mask.CEP_MASK, company_cep) {
            mViewModel.onCepFieldChange(company_cep.text.toString())
        })
    }

    private fun setAdapterToSegmentDropdown() {
        mViewModel.segments.observe(this, Observer { it ->
            it?.let { segments ->
                company_segment.setAdapter(
                    ArrayAdapter(
                        this,
                        R.layout.layout_exposed_dropdown_item,
                        segments.map { it.name }.toTypedArray()
                    )
                )
                company_segment.inputType = InputType.TYPE_NULL
                if (mViewModel.companyData.value == null)
                    mViewModel.companyData.observe(this, Observer {
                        updateSegmentDropdown(
                            segments,
                            mViewModel.companyData.value?.segmentName ?: ""
                        )
                    })
                else
                    updateSegmentDropdown(
                        segments,
                        mViewModel.companyData.value?.segmentName ?: ""
                    )
                company_segment.setOnItemClickListener { _, _, position, _ ->
                    mViewModel.companyData.value?.segmentName = segments[position].name
                    mViewModel.companyData.value?.segmentId = segments[position].id ?: 0
                }
            }
        })
    }

    private fun updateSegmentDropdown(segments: List<Segment>, segmentName: String) {
        val mSegment = segments.firstOrNull { it.name == segmentName }
        company_segment.setText(mSegment?.name, false)
        mViewModel.companyData.value?.segmentId = mSegment?.id ?: -1
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
            FieldsEnum.COMPANY_NAME -> company_name_inputLayout
            FieldsEnum.COMPANY_CEP -> company_cep_inputLayout
            FieldsEnum.COMPANY_CNPJ -> company_cnpj_inputLayout
            FieldsEnum.COMPANY_CELLPHONE -> company_telephone_inputLayout
            FieldsEnum.COMPANY_EMAIL -> company_mail_inputLayout
            FieldsEnum.COMPANY_DESCRIPTION -> company_description_inputLayout
            FieldsEnum.COMPANY_CITY -> company_city_inputLayout
            FieldsEnum.COMPANY_NEIGHBORHOOD -> company_neighborhood_inputLayout
            FieldsEnum.COMPANY_SEGMENT -> company_segment_inputLayout
            FieldsEnum.COMPANY_BANNER, FieldsEnum.COMPANY_THUMBNAIL -> {
                company_scroolable_view.fullScroll(ScrollView.FOCUS_UP)
                company_root_layout.longSnackbar(errorMessage)
                null
            }
            else -> null
        }
        textInputLayout?.error = errorMessage
    }

    override fun onActionError(errorMessage: String?) {
        setButtonSaveVisibility(true)
        company_root_layout.shortSnackbar(
            errorMessage ?: getString(R.string.error_when_executing_the_action)
        )
    }

    override fun onActionSuccess() {
        setButtonSaveVisibility(true)
        company_root_layout.shortSnackbar(getString(R.string.success_when_executing_the_action)) {
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
        private const val READ_WRITE_PERMISSIONS_RESULT = 107
        private const val IMAGE_BANNER_RESULT = 201
        private const val IMAGE_LOGO_RESULT = 200

        private const val DEFAULT_IMAGE_TYPE = "image/*"

        private const val CONTENT_NO_INTERNET = 1
        private const val CONTENT_NO_CONTENT = 2
        private const val CONTENT_FORM = 3
    }
}