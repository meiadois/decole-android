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
import br.com.meiadois.decole.databinding.ActivityAccountBinding
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModelFactory
import br.com.meiadois.decole.util.Mask
import kotlinx.android.synthetic.main.activity_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.lang.Exception

class AccountActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()
    private lateinit var accountViewModel: AccountViewModel

    private var permissionsToRequest: ArrayList<String> = ArrayList()
    private val permissionsRejected: ArrayList<String> = ArrayList()
    private val permissions: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)
        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account)
        binding.apply {
            viewModel = accountViewModel
            lifecycleOwner = this@AccountActivity
        }
        toolbar_back_button.setOnClickListener { finish() }
        setAdapterToSegmentDropdown()
        setImageInputs()
        setInputMasks()

        askPermissions()

        /* TODO:
            - implementar as validacoes dos campos
                *obrigatorio e valido (para email, cnpj, cep, etc)
            - verificar prblema no upload de foto e, quando resolver, limpar essa classe (mover
                o que for de direito para a viewmodel)
         */
    }

    // region Local Functions
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
                    ArrayAdapter<String>(
                        this, R.layout.layout_exposed_dropdown_item, segments.map { it.name }.toTypedArray())
                )
                filled_exposed_dropdown.inputType = android.text.InputType.TYPE_NULL
                filled_exposed_dropdown.setText(accountViewModel.companyData.value?.segmentName, false)
            }
        })
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