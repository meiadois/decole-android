package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.text.InputType
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
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.fragment_partnership_search_filter.*
import kotlinx.android.synthetic.main.fragment_partnership_search_filter.progress_bar
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
            handleBackNavigation()
        }

        btn_apply_filter.setOnClickListener {
            Coroutines.main {
                mViewModel.segmentFilter.value = mViewModel.segmentClicked
                val segment = mViewModel.segments.value?.firstOrNull {
                    it.name == mViewModel.segmentFilter.value
                }
                try {
                    setProgressBarVisibility(true)
                    if (segment == null) mViewModel.getAllCompanies() else mViewModel.getCompaniesBySegment(
                        segment.id!!
                    )
                    handleBackNavigation()
                } catch (ex: NoInternetException) {
                    view.longSnackbar(getString(R.string.no_internet_connection_error_message)) { snackbar ->
                        snackbar.setAction(getString(R.string.ok)) {
                            snackbar.dismiss()
                        }
                        setProgressBarVisibility(false)
                    }
                } catch (ex: Exception) {
                    showGenericErrorMessage()
                }
            }
        }
        setAdapterToSegmentDropdown()
    }


    private fun setAdapterToSegmentDropdown() {
        mViewModel.segments.observe(viewLifecycleOwner, Observer { it ->
            it?.let { segments ->
                var segmentsString = segments.map { it.name }.toTypedArray()
                segmentsString =
                    segmentsString.plusElement(context?.getString(R.string.all_segments)!!)
                filled_exposed_dropdown.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.layout_exposed_dropdown_search_item,
                        segmentsString
                    )
                )
                filled_exposed_dropdown.inputType = InputType.TYPE_NULL
                filled_exposed_dropdown.setText(
                    mViewModel.segmentFilter.value ?: context?.getString(R.string.all_segments),
                    false
                )
                filled_exposed_dropdown.setOnItemClickListener { _, _, position, _ ->
                    mViewModel.segmentClicked = segmentsString[position]
                }

            }
        })
    }

    private fun handleBackNavigation() {
        val nextFragment = PartnershipSearchFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, nextFragment)
            .commit()
    }

    private fun showGenericErrorMessage() {
        container_button.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.ok)) {
                snackbar.dismiss()
            }
        }
    }

    private fun setProgressBarVisibility(visible: Boolean) {
        progress_bar?.visibility = if (visible) View.VISIBLE else View.GONE
        btn_apply_filter?.visibility = if (!visible) View.VISIBLE else View.GONE
    }

}