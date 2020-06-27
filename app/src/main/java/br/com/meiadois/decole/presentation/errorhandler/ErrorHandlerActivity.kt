package br.com.meiadois.decole.presentation.errorhandler

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.HomeActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class ErrorHandlerActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_handler)
    }

    fun onClick(view: View) = goHome(view.context)

    override fun onBackPressed() {
        super.onBackPressed()
        goHome(applicationContext)
    }

    private fun goHome(context: Context) {
        Intent(context, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }
}