package br.com.meiadois.decole.presentation.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import br.com.meiadois.decole.service.FloatingViewService
import kotlinx.android.synthetic.main.activity_ask_for_interactive_mode_permission.*

class AskForInteractiveModePermissionActivity : AppCompatActivity() {

    private val drawOverAppPermissionCode = 2084

    private fun askPermissionToOverLays() {
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, drawOverAppPermissionCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_for_interactive_mode_permission)
        initializeView()
    }

    private fun startFloatingView(c: Context?) {
        startService(Intent(c, FloatingViewService::class.java))
        finish()
    }

    private fun isOverLaysAllowed(c: Context?): Boolean {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
            c
        ))
    }

    /**
     * Set and initialize the view elements.
     */
    private fun initializeView() {
        btn_next.setOnClickListener {
            if (isOverLaysAllowed(this@AskForInteractiveModePermissionActivity)) {
                startFloatingView(this@AskForInteractiveModePermissionActivity)
                finish()
            } else {
                askPermissionToOverLays()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == drawOverAppPermissionCode) {
            if (isOverLaysAllowed(this)) {
                startFloatingView(this)
            } else { //Permission is not available
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
}
