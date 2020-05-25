package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Company
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

    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init(){
        Coroutines.main {
            toggleLoading(true)
            try{
                val company : Company = viewModel.getUserCompany()
                showUserCompany(company)
            }catch (ex: ClientException){
                if (ex.code == 404) showInviteToRegister()
            }catch (ex: Exception){
                Log.i("Fragment_top_exception", ex.message!!)
            }finally {
                toggleLoading(false)
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        if (loading){
            top_progress_bar?.visibility = View.VISIBLE
        }else {
            top_progress_bar?.visibility = View.GONE
        }
    }

    private fun showUserCompany(company: Company){
        container_company_layout_no_account.visibility = View.INVISIBLE
        container_company_layout_account.visibility = View.VISIBLE
        text_partner_name.text = company.name
        text_partner_segment.text = company.segment?.name
        Glide.with(container_company_layout_account).load(company.thumbnail).apply(RequestOptions.circleCropTransform()).into(image_partner)
    }

    private fun showInviteToRegister(){
        container_company_layout_account.visibility = View.INVISIBLE
        container_company_layout_no_account.visibility = View.VISIBLE
    }
}