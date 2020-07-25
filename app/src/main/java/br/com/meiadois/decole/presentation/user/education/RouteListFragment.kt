package br.com.meiadois.decole.presentation.user.education

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.education.binding.RouteItem
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteListViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.RouteListViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.toRouteItemList
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_partnership_home_bottom.*
import kotlinx.android.synthetic.main.fragment_route_list.*
import kotlinx.android.synthetic.main.fragment_route_list.progress_bar
import kotlinx.android.synthetic.main.fragment_route_list.swipe_refresh
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RouteListFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factoryFragment: RouteListViewModelFactory by instance<RouteListViewModelFactory>()

    private lateinit var mFragmentViewModel: RouteListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_route_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressBarVisibility(true)
        init()
        mFragmentViewModel =
            ViewModelProvider(this, factoryFragment).get(RouteListViewModel::class.java)
        bindUi()
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun bindUi() = Coroutines.main {
        swipe_refresh?.setOnRefreshListener {
            init(true)
        }
        mFragmentViewModel.routes.await().observe(this, Observer {
            initRecyclerView(it.toRouteItemList())
        })
    }

    private fun init(fromSwipeRefresh: Boolean = false) {
        Coroutines.main {
            try {
                mFragmentViewModel.onListRefresh()
                setContentVisibility(CONTENT_NONE)
            } catch (ex: NoInternetException) {
                setContentVisibility(CONTENT_NO_INTERNET)
            } catch (ex: Exception) {
                Firebase.crashlytics.recordException(ex)
                showGenericErrorMessage()
            } finally {
                if (fromSwipeRefresh) hideSwipe()
                else setProgressBarVisibility(false)
            }
        }
    }

    private fun initRecyclerView(routeItem: List<RouteItem>) {

        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(routeItem)

            setOnItemClickListener { item, view ->
                Coroutines.main {
                    try {
                        if (item is RouteItem) {
                            if (!item.route.locked) {
                                setContentVisibility(CONTENT_LOADING)
                                mFragmentViewModel.onItemClick(item.route, view)
                            } else {
                                view.longSnackbar(getString(R.string.route_access_denied))
                            }
                        }
                    } catch (ex: NoInternetException) {
                        view.longSnackbar(getString(R.string.no_internet_connection_error_message)) { snackbar ->
                            snackbar.setAction(getString(R.string.ok)) {
                                snackbar.dismiss()
                            }
                        }
                        setContentVisibility(CONTENT_NONE)
                    } catch (ex: Exception) {
                        Firebase.crashlytics.recordException(ex)
                        showGenericErrorMessage()
                    }
                }
            }
        }

        route_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    //content
    private fun setContentVisibility(contentMode: Int) {
        route_recycler_view?.visibility =
            if (contentMode == CONTENT_NONE) View.VISIBLE else View.GONE
        layout_no_internet_routes?.visibility =
            if (contentMode == CONTENT_NO_INTERNET) View.VISIBLE else View.GONE
        progress_bar?.visibility = if (contentMode == CONTENT_LOADING) View.VISIBLE else View.GONE

    }

    private fun showGenericErrorMessage(showProgressBar: Boolean = false) {
        fragment_bottom_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                init()
                setProgressBarVisibility(showProgressBar)
                snackbar.dismiss()
            }
        }
    }

    private fun setProgressBarVisibility(visible: Boolean) {
        progress_bar?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun hideSwipe() {
        swipe_refresh?.isRefreshing = false
    }
    //end region

    companion object {
        private const val CONTENT_NONE = 0
        private const val CONTENT_LOADING = 1
        private const val CONTENT_NO_INTERNET = 2
    }
}
