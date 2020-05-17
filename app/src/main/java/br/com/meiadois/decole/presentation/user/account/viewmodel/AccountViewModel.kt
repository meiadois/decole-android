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
        try{
            getUserCompany()
            getSegments()
            getUser()
        }catch (ex: Exception){
            Log.i("AccountViewModel.init", ex.message!!)
        }
    }

    private fun getSegments() {
        Coroutines.main {
            segments = segmentRepository.getAllSegments().toSegmentModelList()
        }
    }

    private fun getUser(){
        userRepository.getUser().observeForever{
            userData = if (it != null) UserAccountData(it.name, it.email) else UserAccountData()
        }
    }

    private fun getUserCompany(){
        Coroutines.main {
            companyData = userRepository.getUserCompany().toCompanyAccountData()
        }
    }

    fun onSaveButtonClick(view: View){

    }

    fun onSearchVisibilityChange(isChecked: Boolean){
        companyData?.visible = isChecked
    }
}