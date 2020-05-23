package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipSearchViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipSearchViewModelFactory
import kotlinx.android.synthetic.main.activity_search_partner.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PartnershipSearchActivity: AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipSearchViewModelFactory by instance<PartnershipSearchViewModelFactory>()
    private lateinit var viewModel: PartnershipSearchViewModel
    private lateinit var viewModelProfile: PartnershipCompanyProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_search_partner)

        viewModel = ViewModelProvider(this, factory).get(PartnershipSearchViewModel::class.java)
        setAdapterToSegmentDropdown()
        layoutInflater.inflate(R.layout.card_perfil_company, frame_company_container)
        //setContentCardCompanyView()
        /*filled_exposed_dropdown.setOnClickListener{
            Log.i("Selecionado:",filled_exposed_dropdown.)
        }*/

    }
    private fun observeDropdownItem(){
        input_exposed_dropdown.setOnClickListener{
            Log.i("Selecionado:",filled_exposed_dropdown.listSelection.toString())
        }
        filled_exposed_dropdown.text.toString()
    }

    private fun setAdapterToSegmentDropdown(){
        viewModel.segments.observe(this, Observer { it ->
            it?.let {segments ->
                var segmentsList = segments.map { it.name }.toTypedArray()
                segmentsList = segmentsList.plusElement("Todos os Segmentos")
                filled_exposed_dropdown.setAdapter(
                    ArrayAdapter<String>(
                        this, R.layout.layout_exposed_dropdown_search_item, segmentsList)
                )
                filled_exposed_dropdown.inputType = InputType.TYPE_NULL
                filled_exposed_dropdown.setText("Todos os Segmentos", false)

                //  Log.i("Selecionado:",filled_exposed_dropdown.text.toString())
            }
        })
    }

    private fun setContentCardCompanyView(){
        viewModel.companies.observe(this, Observer { it ->
            it?.let {companies ->
                var companiesList = companies.toTypedArray()
                layoutInflater.inflate(R.layout.card_perfil_company, frame_company_container)

            }
        })
    }
}