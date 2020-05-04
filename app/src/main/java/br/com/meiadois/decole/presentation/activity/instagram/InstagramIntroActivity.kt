package br.com.meiadois.decole.presentation.activity.instagram

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.activity.AskForInteractiveModePermissionActivity
import kotlinx.android.synthetic.main.activity_instagram_intro.*

class InstagramIntroActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instagram_intro)
        have_account_btn.setOnClickListener(this)
        does_not_have_account_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == have_account_btn.id) {
            val intent = Intent(this, HasInstagramAccountActivity::class.java)
            startActivity(intent)
            finish()
        } else if (v.id == does_not_have_account_btn.id) {
            val intent = Intent(this, AskForInteractiveModePermissionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
