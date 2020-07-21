package br.com.meiadois.decole.presentation.user.education

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.databinding.ActivityRouteDetailsBinding
import br.com.meiadois.decole.presentation.user.HomeActivity
import br.com.meiadois.decole.presentation.user.education.binding.LessonItem
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteDetailsViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.RouteDetailsViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.toLessonItemList
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_route_details.*
import kotlinx.android.synthetic.main.activity_route_details.progress_bar
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class RouteDetailsActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: RouteDetailsViewModelFactory by instance<RouteDetailsViewModelFactory>()

    private lateinit var mViewModel: RouteDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRouteDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_route_details)

        mViewModel = ViewModelProvider(this, factory).get(RouteDetailsViewModel::class.java)

        binding.apply {
            viewModel = mViewModel
        }

        mViewModel.routeClicked.postValue(intent.getLongExtra("itemId", 0L))

        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_jump.setOnClickListener {
            jumpRoute(it)
            onBackPressed()
        }

        renderData()

    }

    private fun renderData() = Coroutines.main {
        mViewModel.routeDetails.await().observe(this, Observer {
            toggleLoading(true)
            it?.let {
                initHeader(it.route)
                if (it.lessons.isNotEmpty()) {
                    toggleLoading(false)
                    scroll_container.post {
                        scroll_container.fullScroll(View.FOCUS_DOWN)
                    }
                    initRecyclerView(it.lessons.toLessonItemList())
                }
            }
        })

    }

    override fun onBackPressed() {
        Intent(this, HomeActivity::class.java).also {
            it.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(it)
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun toggleLoading(boolean: Boolean) {
        if (boolean) {
            progress_bar.visibility = View.VISIBLE
            root_content.visibility = View.GONE
            btn_jump.isEnabled = false
        } else {
            progress_bar.visibility = View.GONE
            root_content.visibility = View.VISIBLE
            btn_jump.isEnabled = true
        }
    }

    private fun initHeader(route: Route) {
        text_title.text = route.title
        text_description.text = route.description
        text_progress.text = getString(R.string.route_details_progress_label).format(
            route.lessonsCompleted,
            route.lessonsAvailable
        )

        btn_jump.visibility =
            if (route.lessonsCompleted != route.lessonsAvailable) View.VISIBLE else View.INVISIBLE
        btn_jump.setOnClickListener {
            jumpRoute(it)
        }
    }

    private fun initRecyclerView(items: List<LessonItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)

            setOnItemClickListener { item, view ->

                if (item is LessonItem && !item.lesson.locked) {

                    mViewModel.onItemClick(item.lesson, view)
                }
                toggleLoading(false)
            }
        }

        lesson_recycler_view.apply {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, true)
            setHasFixedSize(true)
            adapter = mAdapter
        }

    }

    private fun showGenericErrorMessage() {
        root_content.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                toggleLoading(false)
                snackbar.dismiss()
            }
        }
    }

    private fun jumpRoute(view: View) {
        Coroutines.main {
            try {
                mViewModel.onJumpButtonClick()
            } catch (ex: NoInternetException) {
                toggleLoading(true)
                view.longSnackbar(getString(R.string.no_internet_connection_error_message)) { snackbar ->
                    snackbar.setAction(getString(R.string.ok)) {
                        toggleLoading(false)
                        snackbar.dismiss()
                    }
                }
            } catch (ex: Exception) {
                showGenericErrorMessage()
            } finally {
                toggleLoading(false)
            }
        }
    }

}
