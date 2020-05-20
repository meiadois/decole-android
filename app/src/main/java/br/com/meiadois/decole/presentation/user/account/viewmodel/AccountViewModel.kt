package br.com.meiadois.decole.presentation.user.account.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.account.binding.UserAccountData
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyAccountData
import br.com.meiadois.decole.util.extension.toSegmentModelList
import java.lang.Exception

class AccountViewModel(
    private val segmentRepository: SegmentRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var companyData: MutableLiveData<CompanyAccountData> = MutableLiveData()
    var userData: MutableLiveData<UserAccountData> = MutableLiveData()
    var segments: MutableLiveData<List<Segment>> = MutableLiveData()

    init {
        getUserCompany()
        getSegments()
        getUser()
    }

    private fun getSegments() {
        Coroutines.main {
            try {
                segments.value = segmentRepository.getAllSegments().toSegmentModelList()
            } catch (ex: Exception) {
                Log.i("AccountViewModel.init", ex.message!!)
            }
        }
    }

    private fun getUser() {
        try {
            userRepository.getUser().observeForever { user ->
                userData.value =
                    if (user != null) UserAccountData(user.name, user.email) else UserAccountData()
            }
        } catch (ex: Exception) {
            Log.i("AccountViewModel.init", ex.message!!)
        }
    }

    private fun getUserCompany() {
        Coroutines.main {
            try {
                companyData.value = userRepository.getUserCompany().toCompanyAccountData()
            } catch (ex: Exception) {
                Log.i("AccountViewModel.init", ex.message!!)
            }
        }
    }

    fun onSaveButtonClick(view: View) {
    }

    fun onSearchVisibilityChange(isChecked: Boolean) {
        companyData.value!!.visible = isChecked
    }
}