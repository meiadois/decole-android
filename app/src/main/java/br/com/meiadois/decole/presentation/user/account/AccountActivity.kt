package br.com.meiadois.decole.presentation. user.account

import android.Manifest.permission.*
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
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
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File

class AccountActivity : AppCompatActivity(), KodeinAware, AccountListener {
    override val kodein by kodein()
    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()
    private lateinit var accountViewModel: AccountViewModel

    private var permissionsToRequest: ArrayList<String> = ArrayList()
    private val permissionsRejected: ArrayList<String> = ArrayList()
    private val permissions: ArrayList<String> = ArrayList()

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
        setToolBarButtonsListeners()
        setRemoveErrorListener()
        setImageInputs()
        setInputMasks()

        /*
            TODO:
                limpar os metodos de pick de imagem
         */
        askPermissions()
    }

    // region Local Functions
    private fun setToolBarButtonsListeners(){
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
        account_root_layout.shortSnackbar(errorMessage ?: getString(R.string.error_when_executing_the_action))
    }

    override fun onActionSuccess() {
        account_root_layout.shortSnackbar(getString(R.string.success_when_executing_the_action))
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

    private fun setImageInputs(){
        input_company_logo.inputType = android.text.InputType.TYPE_NULL
        input_company_logo.setOnClickListener {
            startActivityForResult(
                getPickImageChooserIntent(
                    getString(R.string.account_select_a, getString(R.string.account_company_logo_hint))
                ),
                IMAGE_LOGO_RESULT)
        }

        input_company_banner.inputType = android.text.InputType.TYPE_NULL
        input_company_banner.setOnClickListener {
            startActivityForResult(
                getPickImageChooserIntent(
                    getString(R.string.account_select_a, getString(R.string.account_company_promotional_photo_hint))
                ),
                IMAGE_BANNER_RESULT)
        }
    }
    // endregion

    // region Permissions
    private fun askPermissions() {
        permissions.add(CAMERA)
        permissions.add(WRITE_EXTERNAL_STORAGE)
        permissions.add(READ_EXTERNAL_STORAGE)
        permissionsToRequest = findUnAskedPermissions(permissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (permissionsToRequest.count() > 0)
                requestPermissions(
                    permissionsToRequest.toArray( arrayOfNulls<String>(permissionsToRequest.count())),
                    ALL_PERMISSIONS_RESULT
                )
    }

    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()
        for (perm in wanted)
            if (!hasPermission(perm))
                result.add(perm)
        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        return true
    }
    // endregion

    // region Image picking
    private fun getPickImageChooserIntent(intentTile: String): Intent? {
        val outputFileUri: Uri? = getCaptureImageOutputUri()
        val allIntents: MutableList<Intent> = ArrayList()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            allIntents.add(intent)
        }
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }
        var mainIntent = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component?.className.toString() == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)
        val chooserIntent = Intent.createChooser(mainIntent, intentTile)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())
        return chooserIntent
    }

    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage: File? = getExternalFilesDir("")
        if (getImage != null)
            outputFileUri = Uri.fromFile(File(getImage.path, "profile.png"))
        return outputFileUri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == IMAGE_LOGO_RESULT)
                    accountViewModel.companyData.value!!.thumbnail = getImageFromFilePath(data) ?: ""
                else if (requestCode == IMAGE_BANNER_RESULT)
                    accountViewModel.companyData.value!!.banner = getImageFromFilePath(data) ?: ""
            }
        }catch (ex: Exception){
            Log.i("imageEx, ", ex.message!!)
        }
    }

    private fun getImageFromFilePath(data: Intent?): String? {
        val isCamera = data == null || data.data == null
        return if (isCamera) getCaptureImageOutputUri()!!.path else data!!.data?.let { getPathFromURI(it)}
    }

    private fun getPathFromURI(contentUri: Uri): String? {
        val cursor: Cursor? = contentResolver.query(
            contentUri, arrayOf(MediaStore.Audio.Media.DATA), null, null, null)
        val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA) ?: 0
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex)
        cursor?.close()
        return path
    }
    // endregion

    companion object{
        private const val ALL_PERMISSIONS_RESULT = 107
        private const val IMAGE_BANNER_RESULT = 201
        private const val IMAGE_LOGO_RESULT = 200
    }
}