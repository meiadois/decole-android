package br.com.meiadois.decole.presentation.user.account.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.account.binding.UserAccountData
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.extension.toCompanyAccountData
import br.com.meiadois.decole.util.extension.toSegmentModelList
import java.lang.Exception

class AccountViewModel(
    private val segmentRepository: SegmentRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var companyData: CompanyAccountData? = null
    var userData: UserAccountData? = null
    var segments: List<Segment>? = null

    init {
        getUserCompany()
        getSegments()
        getUser()
    }

    private fun getSegments() {
        Coroutines.main {
            try {
                segments = segmentRepository.getAllSegments().toSegmentModelList()
            } catch (ex: Exception) {
                Log.i("AccountViewModel.init", ex.message!!)
            }
        }
    }

    private fun getUser() {
        try {
            userRepository.getUser().observeForever { user ->
                userData =
                    if (user != null) UserAccountData(user.name, user.email) else UserAccountData()
            }
        } catch (ex: Exception) {
            Log.i("AccountViewModel.init", ex.message!!)
        }
    }

    private fun getUserCompany() {
        Coroutines.main {
            try {
                companyData = userRepository.getUserCompany().toCompanyAccountData()
            } catch (ex: Exception) {
                Log.i("AccountViewModel.init", ex.message!!)
            }
        }
    }

    fun onSaveButtonClick(view: View) {

    }

    fun onSearchVisibilityChange(isChecked: Boolean) {
        companyData?.visible = isChecked
    }
}