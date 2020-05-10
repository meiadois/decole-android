package br.com.meiadois.decole.presentation.user.education

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityRouteDetailsBinding
import br.com.meiadois.decole.presentation.user.education.binding.LessonItem
import br.com.meiadois.decole.presentation.user.education.binding.RouteItem
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteDetailsViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteDetailsViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toLessonItemList
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_route_details.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class RouteDetailsActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: RouteDetailsViewModelFactory by instance()

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

        bindUi()
    }

    private fun bindUi() = Coroutines.main {
        toggleLoading(true)
        mViewModel.lessons.await().observe(this, Observer {
            it?.let {

                toggleLoading(false)
                scroll_container.post {
                    scroll_container.smoothScrollTo(0, Int.MAX_VALUE)
                }

                initRecyclerView(it.toLessonItemList())
            }
        })
    }

    private fun toggleLoading(boolean: Boolean) {
        if (boolean) {
            progress_bar.visibility = View.VISIBLE
            root_content.visibility = View.GONE
        } else {
            progress_bar.visibility = View.GONE
            root_content.visibility = View.VISIBLE
        }
    }

    private fun initRecyclerView(items: List<LessonItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)

            setOnItemClickListener { item, view ->
                if (item is RouteItem) {
                    mViewModel.onItemClick(item.route, view)
                }
            }
        }

        lesson_recycler_view.apply {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, true)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }
}
