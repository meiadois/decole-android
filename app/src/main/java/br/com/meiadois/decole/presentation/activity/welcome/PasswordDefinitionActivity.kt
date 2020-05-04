package br.com.meiadois.decole.presentation.activity.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import kotlinx.android.synthetic.main.activity_register.*

class PasswordDefinitionActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_next.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == btn_next.id) {
            val intent = Intent(this, WelcomeInfoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
