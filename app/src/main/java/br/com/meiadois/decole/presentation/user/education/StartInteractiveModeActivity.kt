package br.com.meiadois.decole.presentation.user.education

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.databinding.ActivityStartInteractiveModeBinding
import br.com.meiadois.decole.presentation.user.education.viewmodel.StartInteractiveModeViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.StartInteractiveModeViewModelFactory
import br.com.meiadois.decole.service.FloatingViewService
import br.com.meiadois.decole.util.Coroutines
import kotlinx.android.synthetic.main.activity_start_interactive_mode.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class StartInteractiveModeActivity : AppCompatActivity(), KodeinAware {

    private val drawOverAppPermissionCode = 2084

    override val kodein by kodein()
    private val factory: StartInteractiveModeViewModelFactory by instance()

    private lateinit var mViewModel: StartInteractiveModeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityStartInteractiveModeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_start_interactive_mode)

        mViewModel = ViewModelProvider(this, factory).get(StartInteractiveModeViewModel::class.java)

        binding.apply {
            viewModel = mViewModel
        }

        mViewModel.lessonClicked.value = intent.getLongExtra("lessonId", 0L)

        initializeViewComponents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == drawOverAppPermissionCode) {
            if (isOverLaysAllowed(this)) {
                startFloatingView(this, mViewModel.steps.getCompleted().value!!)
            } else {
                Toast.makeText(
                    this,
                    "Para seguir adiante, você precisa clicar no botão e realizar a permissão.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startFloatingView(c: Context?, steps: List<Step>) {

        Intent(c, FloatingViewService::class.java).also {
            it.putExtra("lessonId", mViewModel.lessonClicked.value!!)
            it.putExtra("routeId", intent.getLongExtra("routeId", 0L))
            it.putExtra("steps", ArrayList(steps))
            startService(it)
            finishAffinity()
        }
    }

    private fun initializeViewComponents() = Coroutines.main {
        mViewModel.steps.await().observe(this, Observer { steps ->
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
    }

    private fun askPermissionToOverLays() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, drawOverAppPermissionCode)
    }

    private fun isOverLaysAllowed(c: Context?): Boolean {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(c))
    }
}
