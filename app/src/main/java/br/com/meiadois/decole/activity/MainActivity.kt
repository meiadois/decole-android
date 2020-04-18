package br.com.meiadois.decole.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var cnpjEditText: TextInputEditText
    private lateinit var nextBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cnpjEditText = findViewById(R.id.cnpjTextField)
        nextBtn = findViewById(R.id.loginNextBtn)
        nextBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == nextBtn.id) {
            val intent = Intent(this, PasswordDefinitionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
