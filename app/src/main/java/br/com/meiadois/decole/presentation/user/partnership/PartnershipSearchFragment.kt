package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
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
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_search_partner.*
import kotlinx.android.synthetic.main.fragment_search_partner.swipe_refresh
import kotlinx.android.synthetic.main.fragment_search_partner.toolbar_back_button
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
        setProgressVisibility(true)

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
            Coroutines.main {
                try {
                    mViewModel.sendLike(mViewModel.companyId!!, mViewModel.company.value!!.id)
                    mViewModel.removeCompany(mViewModel.company.value!!.id)
                } catch (ex: NoInternetException) {
                    view.longSnackbar(getString(R.string.no_internet_connection_error_message)) { snackbar ->
                        snackbar.setAction(getString(R.string.ok)) {
                            snackbar.dismiss()
                        }
                    }
                } catch (ex: Exception) {
                    Firebase.crashlytics.recordException(ex)
                    showGenericErrorMessage()
                }
                if (!mViewModel.getUpdateCompany())
                    setContentVisibility(CONTENT_NO_COMPANIES)
            }
        }

        btn_md_close.setOnClickListener {
            mViewModel.removeCompany(mViewModel.company.value!!.id)
            if (!mViewModel.getUpdateCompany())
                setContentVisibility(CONTENT_NO_COMPANIES)
        }

        setCompaniesAdapter()
        setDataCardView()
        init()
    }

    private fun init(fromSwipeRefresh: Boolean = false) {
        Coroutines.main {
            try {
                mViewModel.init()
            } catch (ex: Exception) {
                Firebase.crashlytics.recordException(ex)
                showGenericErrorMessage()
            }
            if (fromSwipeRefresh) swipe_refresh?.isRefreshing = false
            else setProgressVisibility(false)
        }
    }

    private fun setCompaniesAdapter() {
        mViewModel.companies.observe(viewLifecycleOwner, Observer {
            if (mViewModel.companies.value!!.isNotEmpty()) {
                mViewModel.company.value = mViewModel.companies.value?.get(0)
                setContentVisibility(CONTENT_HAS_COMPANIES)
            } else {
                setContentVisibility(CONTENT_NO_COMPANIES)
            }
            setProgressVisibility(false)
        })
    }

    private fun setDataCardView() {
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
        progress_bar_search?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun showGenericErrorMessage() {
        swipe_refresh.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.ok)) {
                snackbar.dismiss()
            }
        }
    }

    private fun setContentVisibility(contentMode: Int) {
        linear_button_container.visibility =
            if (contentMode == CONTENT_HAS_COMPANIES) View.VISIBLE else View.GONE
        cardview_company_profile.visibility =
            if (contentMode == CONTENT_HAS_COMPANIES) View.VISIBLE else View.GONE
        fragment_container_noCompanies.visibility =
            if (contentMode == CONTENT_NO_COMPANIES) View.VISIBLE else View.GONE
    }

    companion object {
        private const val CONTENT_HAS_COMPANIES = 0
        private const val CONTENT_NO_COMPANIES = 1
    }
}