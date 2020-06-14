package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.FragmentPartnershipSearchFilterBinding
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModel
import kotlinx.android.synthetic.main.fragment_partnership_search_filter.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class PartnershipSearchFilterFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val mViewModel: PartnershipCompanyProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPartnershipSearchFilterBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_partnership_search_filter, container, false
        )
        binding.viewModel = mViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar_back_button.setOnClickListener {
            val nextFragment = PartnershipSearchFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, nextFragment)
                .commit()
        }

        setAdapterToSegmentDropdown()
    }



    private fun setAdapterToSegmentDropdown() {
        mViewModel.segments.observe(viewLifecycleOwner, Observer { it ->
            it?.let { segments ->
                var segmentsString = segments.map { it.name }.toTypedArray()
                segmentsString = segmentsString.plusElement("Todos os Segmentos")
                filled_exposed_dropdown.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.layout_exposed_dropdown_search_item,
                        segmentsString
                    )
                )
                filled_exposed_dropdown.inputType = InputType.TYPE_NULL
                filled_exposed_dropdown.setText(mViewModel.segment ?:"Todos os Segmentos", false)
                filled_exposed_dropdown.setOnItemClickListener { parent, view, position, id ->
                    mViewModel.segment = segmentsString[position]
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

}