package br.com.meiadois.decole.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.activity.user.HomeActivity
import br.com.meiadois.decole.presentation.activity.welcome.PasswordDefinitionActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_next.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == btn_next.id) {
            val intent = Intent(this, PasswordDefinitionActivity::class.java)
//            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
