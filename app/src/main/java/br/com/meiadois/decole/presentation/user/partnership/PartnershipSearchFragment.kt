package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.FragmentSearchPartnerBinding
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModel
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.shortSnackbar
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_search_partner.*
import kotlinx.android.synthetic.main.fragment_search_partner.progress_bar
import kotlinx.android.synthetic.main.fragment_search_partner.swipe_refresh
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class PartnershipSearchFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val mViewModel: PartnershipCompanyProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchPartnerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search_partner, container, false
        )
        binding.viewModel = mViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_refresh.setOnRefreshListener { init(true) }
        parentFragmentManager.popBackStack()

        toolbar_filter_button.setOnClickListener {
            val nextFragment = PartnershipSearchFilterFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
        }
        toolbar_back_button.setOnClickListener {
            activity?.finish()
        }
        btn_md_checked.setOnClickListener {
            try {
                mViewModel.sendLike(mViewModel.companyId!!, mViewModel.company.value!!.id)
                mViewModel.removeCompany(mViewModel.company.value!!.id)
            } catch (ex: Exception) {
                Log.i("sendLikes.ex", ex.message!!)
            }
            mViewModel.getUpdateCompany()
        }
        btn_md_close.setOnClickListener {
            when {
                mViewModel.companies.value!!.count() - 1 > mViewModel.state -> {
                    mViewModel.getUpdateCompany()
                }
                mViewModel.companies.value!!.count() == 1 -> {
                    layout_bottom_search.longSnackbar(getString(R.string.only_one_company_found))
                }
                mViewModel.companies.value!!.count() - 1 == mViewModel.state -> {
                    linear_button_container.visibility = View.INVISIBLE
                    layout_bottom_search.shortSnackbar(getString(R.string.end_of_list_companies)) {
                        it.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                init(true)
                            }
                        })
                    }
                }
            }
        }
        init()
    }

    private fun init(fromSwipeRefresh: Boolean = false) {
        Coroutines.main {
            try {
                setLoadingView()
                setCompaniesAdapter()
                setContentCardCompanyView()
            } catch (ex: Exception) {
                Log.i("SwipeRefresh.ex", ex.message!!)
            }
            if (fromSwipeRefresh) swipe_refresh?.isRefreshing = false
            else setProgressVisibility(false)
        }
        mViewModel.state = 0
    }

    private fun setCompaniesAdapter() {
        progress_bar.visibility
        mViewModel.companies.observe(viewLifecycleOwner, Observer {
            if (mViewModel.companies.value!!.isNotEmpty()) {
                mViewModel.company.value = mViewModel.companies.value?.get(0)
                linear_button_container.visibility = View.VISIBLE
                cardview_company_profile.visibility = View.VISIBLE
                fragment_container_noCompanies.visibility = View.GONE
            } else {
                linear_button_container.visibility = View.GONE
                cardview_company_profile.visibility = View.GONE
                fragment_container_noCompanies.visibility = View.VISIBLE
            }
            setProgressVisibility(false)
        })
    }

    private fun setLoadingView() {
        linear_button_container.visibility = View.GONE
        cardview_company_profile.visibility = View.GONE
        fragment_container_noCompanies.visibility = View.GONE
        setProgressVisibility(true)
    }

    private fun setContentCardCompanyView() {
        mViewModel.company.observe(viewLifecycleOwner, Observer {
            it?.let {
                text_profile_name.text = it.name
                text_profile_description.text = it.description
                text_profile_segment.text = it.segment?.name
                Glide.with(cardview_company_profile).load(it.banner).into(image_profile_banner)
            }
        })
    }

    private fun setProgressVisibility(visible: Boolean) {
        progress_bar.visibility = if (visible) View.VISIBLE else View.GONE
    }

}