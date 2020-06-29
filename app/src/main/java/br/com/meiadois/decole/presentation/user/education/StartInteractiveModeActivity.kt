package br.com.meiadois.decole.presentation.user.education

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.databinding.ActivityStartInteractiveModeBinding
import br.com.meiadois.decole.presentation.user.education.viewmodel.StartInteractiveModeViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.StartInteractiveModeViewModelFactory
import br.com.meiadois.decole.service.FloatingViewService
import br.com.meiadois.decole.util.Coroutines
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.receiver.NetworkChangeReceiver
import kotlinx.android.synthetic.main.activity_start_interactive_mode.*
import kotlinx.android.synthetic.main.activity_start_interactive_mode.btn_next
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class StartInteractiveModeActivity : AppCompatActivity(), InteractiveModeListener, KodeinAware {
    override val kodein by kodein()
    private val factory: StartInteractiveModeViewModelFactory by instance()

    private lateinit var mViewModel: StartInteractiveModeViewModel
    private lateinit var mNetworkReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityStartInteractiveModeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_start_interactive_mode)
        mViewModel = ViewModelProvider(this, factory).get(StartInteractiveModeViewModel::class.java)
        binding.apply {
            viewModel = mViewModel
        }

        mNetworkReceiver = NetworkChangeReceiver(this) {
            Log.i("AAAAAAA", it.toString())
            mViewModel.getSteps()
        }

        mViewModel.interactiveListener = this

        mViewModel.lessonClicked.value = intent.getLongExtra("lessonId", 0L)
        initializeViewComponents()
    }

    override fun onStarted() {

    }

    override fun onSuccess() {}

    override fun onFailure(ex: Exception) {
        when (ex) {
            is NoInternetException ->
                layout_start_interactive.longSnackbar(layout_start_interactive.context.getString(R.string.no_internet_connection_error_message))
            is ClientException ->
                layout_start_interactive.longSnackbar(layout_start_interactive.context.getString(R.string.getting_data_failed_error_message))
            else ->
                layout_start_interactive.longSnackbar(layout_start_interactive.context.getString(R.string.error_when_executing_the_action))
        }
    }

    //ask_overlay_region
    @ExperimentalCoroutinesApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DRAW_OVER_APP_RESULT_CODE -> {
                if (isOverLaysAllowed(this)) {
                    if (isMIUI())
                        showPermissionRationaleForMIUI()
                    else
                        startFloatingView(this, mViewModel.steps.value!!)
                } else
                    Toast.makeText(
                        this,
                        getString(R.string.ask_again_for_overlay_permission),
                        Toast.LENGTH_LONG
                    ).show()
            }
            MIUI_ADDITIONAL_PERMISSION_RESULT_CODE -> {
                startFloatingView(this, mViewModel.steps.value!!)
            }
            else -> {
            }
        }
    }

    private fun askPermissionToOverLays() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, DRAW_OVER_APP_RESULT_CODE)
    }

    private fun isOverLaysAllowed(c: Context?): Boolean {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(c))
    }
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        try {
            this.unregisterReceiver(mNetworkReceiver)
        } catch (ex: Exception) {

        }
    }

    //region MIUI treatment
    private fun isMIUI(): Boolean = !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))

    @Suppress("SameParameterValue")
    private fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            input = BufferedReader(
                InputStreamReader(
                    Runtime.getRuntime().exec("getprop $propName").inputStream
                ), 1024
            )
            line = input.readLine()
            input.close()
        } catch (ex: Exception) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    private fun showPermissionRationaleForMIUI() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.display_popup_permission_rationale_title))
            .setMessage(getString(R.string.display_popup_permission_rationale_message))
            .setNeutralButton(getString(R.string.ok)) { _, _ ->
                openMIUIAdditionalPermissions()
            }
            .show()
    }

    private fun openMIUIAdditionalPermissions() {
        Intent("miui.intent.action.APP_PERM_EDITOR").also {
            it.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            it.putExtra("extra_pkgname", packageName)
            startActivityForResult(it, MIUI_ADDITIONAL_PERMISSION_RESULT_CODE)
        }
    }
    //endregion

    //region Local
    private fun startFloatingView(c: Context?, steps: List<Step>) {
        Intent(c, FloatingViewService::class.java).also {
            it.putExtra("lessonId", mViewModel.lessonClicked.value!!)
            it.putExtra("routeId", intent.getLongExtra("routeId", 0L))
            it.putExtra("steps", ArrayList(steps))
            startService(it)
            finishAffinity()
        }
    }

    private fun initializeViewComponents() {
        mViewModel.steps.observe(this, Observer { steps ->
            btn_next.isEnabled = true
            btn_next.setOnClickListener {
                if (isOverLaysAllowed(this@StartInteractiveModeActivity)) {
                    startFloatingView(this@StartInteractiveModeActivity, steps)
                    finish()
                } else {
                    askPermissionToOverLays()
                }
            }
        })
        mViewModel.getSteps()
    }
    // endregion

    companion object {
        private const val DRAW_OVER_APP_RESULT_CODE = 2084
        private const val MIUI_ADDITIONAL_PERMISSION_RESULT_CODE = 2085
    }
}
