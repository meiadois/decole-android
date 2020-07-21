package br.com.meiadois.decole.presentation.user.education

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityFinishedRouteBinding
import br.com.meiadois.decole.presentation.user.education.viewmodel.FinishedRouteViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.FinishedRouteViewModelFactory
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.receiver.NetworkChangeReceiver
import kotlinx.android.synthetic.main.activity_finished_route.*
import kotlinx.android.synthetic.main.activity_finished_route.btn_next
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FinishedRouteActivity : AppCompatActivity(), KodeinAware, IMResultListener {

    override val kodein by kodein()
    private val factory: FinishedRouteViewModelFactory by instance()

    private lateinit var mViewModel: FinishedRouteViewModel
    private var mNetworkReceiver: BroadcastReceiver? = null

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

        btn_next.setOnClickListener {
            Intent(this, RouteDetailsActivity::class.java).also {
                it.putExtra("itemId", route)
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        mNetworkReceiver = NetworkChangeReceiver(this) {
            if (!it) setContentVisibility(CONTENT_NO_INTERNET)
            mViewModel.handleStart(intent.getLongExtra("lessonDone", 0L))
        }
    }

    override fun onStarted() {
        setContentVisibility(CONTENT_LOADING)
    }

    override fun onSuccess() {
        setContentVisibility(CONTENT_NORMAL)
        unregisterNetworkReceiver()
    }

    override fun onFailure(message: String?) {
        setContentVisibility(CONTENT_NORMAL)
        root_content.longSnackbar(message ?: getString(R.string.complete_lesson_error))
    }

    override fun onFailure(ex: Exception) {
        val message = when(ex) {
            is NoInternetException -> {
                setContentVisibility(CONTENT_NO_INTERNET)
                root_content.context.getString(R.string.no_internet_connection_error_message)
            }
            else -> {
                setContentVisibility(CONTENT_NORMAL)
                root_content.context.getString(R.string.error_when_executing_the_action)
            }
        }
        root_content.longSnackbar(message)
    }

    private fun setContentVisibility(contentMode: Int) {
        btn_next.visibility = if (contentMode == CONTENT_NORMAL) View.VISIBLE else View.GONE
        progress_bar.visibility = if (contentMode == CONTENT_LOADING) View.VISIBLE else View.GONE
        txt_no_internet_message.visibility = if (contentMode == CONTENT_NO_INTERNET) View.VISIBLE else View.GONE
    }

    private fun unregisterNetworkReceiver() {
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver)
            mNetworkReceiver = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkReceiver()
    }

    companion object {
        private const val CONTENT_NORMAL = 1
        private const val CONTENT_LOADING = 2
        private const val CONTENT_NO_INTERNET = 3
    }
}
