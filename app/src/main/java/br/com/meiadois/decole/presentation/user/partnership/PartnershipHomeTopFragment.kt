package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeTopViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeTopViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_partnership_home_top.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PartnershipHomeTopFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipHomeTopViewModelFactory by instance<PartnershipHomeTopViewModelFactory>()
    private lateinit var viewModel: PartnershipHomeTopViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_partnership_home_top, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(PartnershipHomeTopViewModel::class.java)
        setContentVisibility(CONTENT_NONE)
        setProgressBarVisibility(true)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        Coroutines.main {
            try {
                viewModel.getUserCompany().observe(
                    this, Observer {
                        it?.let {
                            setContentVisibility(CONTENT_WITH_ACCOUNT)
                            showUserCompany(it)
                        }
                    }
                )
            } catch (ex: ClientException) {
                if (ex.code == 404) setContentVisibility(CONTENT_NO_ACCOUNT)
            } catch (ex: Exception) {
                Log.i("Fragment_top_exception", ex.message ?: "")
            } finally {
                setProgressBarVisibility(false)
            }
        }
    }

    private fun setProgressBarVisibility(visible: Boolean) {
        top_progress_bar?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setContentVisibility(contentMode: Int) {
        container_company_layout_account?.visibility = if (contentMode == CONTENT_WITH_ACCOUNT) View.VISIBLE else View.GONE
        container_company_layout_no_account?.visibility = if (contentMode == CONTENT_NO_ACCOUNT) View.VISIBLE else View.GONE
    }

    private fun showUserCompany(company: MyCompany) {
        company.let {
            text_partner_name.text = it.company.name
            text_partner_segment.text = it.segment?.name
            Glide.with(container_company_layout_account).load(it.company.thumbnail)
                .apply(RequestOptions.circleCropTransform()).into(image_partner)
        }
    }

    companion object {
        private const val CONTENT_NONE = 0
        private const val CONTENT_NO_ACCOUNT = 1
        private const val CONTENT_WITH_ACCOUNT = 2
    }
}