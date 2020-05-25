package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivitySearchPartnerBinding
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_search_partner.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PartnershipSearchActivity: AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipCompanyProfileViewModelFactory by instance<PartnershipCompanyProfileViewModelFactory>()
    private lateinit var mViewModel: PartnershipCompanyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)

        mViewModel = ViewModelProvider(this, factory).get(PartnershipCompanyProfileViewModel::class.java)

        val binding: ActivitySearchPartnerBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_search_partner )

        binding.apply {
            viewModel = mViewModel
        }

        setAdapterToSegmentDropdown()
        setContentCardCompanyView()

        btn_back.setOnClickListener{
            finish()
        }

        btn_md_checked.setOnClickListener{
            val companyId = intent.getIntExtra("company_id", 0)
            try {
                 mViewModel.sendLike(companyId,mViewModel.company.value!!.id)
            }catch (ex: Exception){
                Log.i("sendLikes.ex", ex.message!!)
            }
            mViewModel.getUpdateCompany()
        }

        btn_md_close.setOnClickListener{
            mViewModel.getUpdateCompany()
        }
    }

    private fun setAdapterToSegmentDropdown(){
        mViewModel.segments.observe(this, Observer { it ->
            it?.let {segments ->
                var segmentsString = segments.map { it.name }.toTypedArray()
                segmentsString = segmentsString.plusElement("Todos os Segmentos")
                filled_exposed_dropdown.setAdapter(
                    ArrayAdapter(
                        this, R.layout.layout_exposed_dropdown_search_item, segmentsString)
                )
                filled_exposed_dropdown.inputType = InputType.TYPE_NULL
                filled_exposed_dropdown.setText("Todos os Segmentos", false)
                filled_exposed_dropdown.setOnItemClickListener { parent, view, position, id ->
                    val segment = segments.firstOrNull{
                        it.name == segmentsString[position]
                    }
                    if(segment == null){
                        try{
                            mViewModel.getAllCompanies()
                        }catch(ex: Exception){
                            Log.i("getAllComp.ex", ex.message!!)
                        }
                    }else{
                        try{
                            mViewModel.getCompaniesBySegment(segment.id!!)
                        }catch(ex: Exception){
                            Log.i("getCompBySeg.ex",ex.message!!)
                        }
                    }
                }
                
            }
        })
        mViewModel.companies.observe(this, Observer {
            mViewModel.company.value = mViewModel.companies.value?.get(0)
        })
    }

    private fun setContentCardCompanyView(){
        mViewModel.company.observe(this, Observer {
            it?.let{
                text_profile_name.text = it.name
                text_profile_description.text = it.description
                text_profile_segment.text = it.segment?.name
                Glide.with(cardview_company_profile).load(it.banner).into(image_profile_banner)
            }
        })
    }
}