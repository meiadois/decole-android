package br.com.meiadois.decole.presentation.user.education

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityFinishedRouteBinding
import br.com.meiadois.decole.presentation.user.HomeActivity
import br.com.meiadois.decole.presentation.user.education.viewmodel.FinishedRouteViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.FinishedRouteViewModelFactory
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.activity_finished_route.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FinishedRouteActivity : AppCompatActivity(), KodeinAware, IMResultListener {

    override val kodein by kodein()
    private val factory: FinishedRouteViewModelFactory by instance()

    private lateinit var mViewModel: FinishedRouteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityFinishedRouteBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_finished_route)

        mViewModel = ViewModelProvider(this, factory).get(FinishedRouteViewModel::class.java)

        binding.apply {
            viewModel = mViewModel
        }

        mViewModel.listener = this

        val route = intent.getLongExtra("targetRouteParent", 0L)

        mViewModel.targetRouteParent.postValue(route)

        mViewModel.handleStart(intent.getLongExtra("lessonDone", 0L))

        btn_next.setOnClickListener {
            Intent(this, RouteDetailsActivity::class.java).also {
                it.putExtra("itemId", route)
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

    }

    override fun onStarted() {
        toggleLoading(true)
    }

    override fun onSuccess() {
        toggleLoading(false)
    }

    override fun onFailure() {
        root_content.longSnackbar(getString(R.string.complete_lesson_error))
    }

    private fun toggleLoading(loading: Boolean) {
        if (loading) {
            btn_next.visibility = View.GONE
            progress_bar.visibility = View.VISIBLE
        } else {
            btn_next.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
        }
    }
}
