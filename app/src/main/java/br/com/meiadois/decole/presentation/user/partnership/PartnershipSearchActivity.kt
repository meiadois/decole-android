package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivitySearchPartnerBinding
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModelFactory
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.shortSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_search_partner.*
import kotlinx.android.synthetic.main.activity_search_partner.progress_bar
import kotlinx.android.synthetic.main.fragment_partnership_home_bottom.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PartnershipSearchActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipCompanyProfileViewModelFactory by instance<PartnershipCompanyProfileViewModelFactory>()
    private lateinit var mViewModel: PartnershipCompanyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)

        mViewModel =
            ViewModelProvider(this, factory).get(PartnershipCompanyProfileViewModel::class.java)

        val binding: ActivitySearchPartnerBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_search_partner)

        binding.apply {
            viewModel = mViewModel
        }

        setLoadingView()
        setAdapterToSegmentDropdown()
        setCompaniesAdapter()
        setContentCardCompanyView()

        btn_back.setOnClickListener {
            finish()
        }
        btn_md_checked.setOnClickListener {
            val companyId = intent.getIntExtra(PARTNERSHIP_SEARCH_COMPANY_ID, 0)
            try {
                mViewModel.sendLike(companyId, mViewModel.company.value!!.id)
                mViewModel.removeCompany(mViewModel.company.value!!.id)
            } catch (ex: Exception) {
                Log.i("sendLikes.ex", ex.message!!)
            }
            mViewModel.getUpdateCompany()
        }
        btn_md_close.setOnClickListener {
            if(mViewModel.companies.value!!.count()-1 > mViewModel.state) {
                mViewModel.getUpdateCompany()
            }else if(mViewModel.companies.value!!.count() == 1){
                layout_bottom_search.longSnackbar("Infelizmente só essa companhia está disponível nesse segmento")
            }
            else if(mViewModel.companies.value!!.count()-1 == mViewModel.state) {
                btn_md_close.visibility = View.INVISIBLE
                btn_md_checked.visibility = View.INVISIBLE
                layout_bottom_search.shortSnackbar("Você chegou ao fim da lista.Retornamos para o início"){
                    it.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            mViewModel.getUpdateCompany()
                            btn_md_close.visibility = View.VISIBLE
                            btn_md_checked.visibility = View.VISIBLE
                        }
                    })
                }
            }
        }
    }

    private fun setAdapterToSegmentDropdown() {
        mViewModel.segments.observe(this, Observer { it ->
            it?.let { segments ->
                var segmentsString = segments.map { it.name }.toTypedArray()
                segmentsString = segmentsString.plusElement("Todos os Segmentos")
                filled_exposed_dropdown.setAdapter(
                    ArrayAdapter(
                        this, R.layout.layout_exposed_dropdown_search_item, segmentsString
                    )
                )
                filled_exposed_dropdown.inputType = InputType.TYPE_NULL
                filled_exposed_dropdown.setText("Todos os Segmentos", false)
                filled_exposed_dropdown.setOnItemClickListener { parent, view, position, id ->
                    val segment = segments.firstOrNull {
                        it.name == segmentsString[position]
                    }
                    if (segment == null) {
                        try {
                            mViewModel.getAllCompanies()
                        } catch (ex: Exception) {
                            Log.i("getAllComp.ex", ex.message!!)
                        }
                    } else {
                        try {
                            mViewModel.getCompaniesBySegment(segment.id!!)
                        } catch (ex: Exception) {
                            Log.i("getCompBySeg.ex", ex.message!!)
                        }
                    }
                }

            }
        })
    }

    private fun setCompaniesAdapter() {
        progress_bar.visibility
        mViewModel.companies.observe(this, Observer {
            if (mViewModel.companies.value!!.isNotEmpty()) {
                mViewModel.company.value = mViewModel.companies.value?.get(0)
                btn_md_checked.visibility = View.VISIBLE
                btn_md_close.visibility = View.VISIBLE
                cardview_company_profile.visibility = View.VISIBLE
                fragment_container_noCompanies.visibility = View.GONE
            } else {
                btn_md_checked.visibility = View.GONE
                btn_md_close.visibility = View.GONE
                cardview_company_profile.visibility = View.GONE
                fragment_container_noCompanies.visibility = View.VISIBLE
            }
            setProgressVisibility(false)
        })
    }

    private fun setLoadingView(){
        btn_md_checked.visibility = View.GONE
        btn_md_close.visibility = View.GONE
        cardview_company_profile.visibility = View.GONE
        fragment_container_noCompanies.visibility = View.GONE
        setProgressVisibility(true)
    }

    private fun setContentCardCompanyView() {
        mViewModel.company.observe(this, Observer {
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

    companion object {
        const val PARTNERSHIP_SEARCH_COMPANY_ID = "company_id"
    }
}