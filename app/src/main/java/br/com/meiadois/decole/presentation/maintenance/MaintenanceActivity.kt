package br.com.meiadois.decole.presentation.maintenance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.meiadois.decole.R
import kotlinx.android.synthetic.main.activity_maintenance.*

class MaintenanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)

        ok_button.setOnClickListener {
            finish()
        }
    }
}