package br.com.meiadois.decole.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R

class PasswordDefinitionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_definition)
        nextButton = findViewById(R.id.passDefNextBtn)
        nextButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == nextButton.id) {
            val intent = Intent(this, StartInteractiveModeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
