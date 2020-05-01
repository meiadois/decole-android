package br.com.meiadois.decole.activity.instagram

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import br.com.meiadois.decole.activity.AskForInteractiveModePermissionActivity
import kotlinx.android.synthetic.main.activity_has_instagram_account.*

class HasInstagramAccountActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_has_instagram_account)
        next_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == next_btn.id) {
            val intent = Intent(this, AskForInteractiveModePermissionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
