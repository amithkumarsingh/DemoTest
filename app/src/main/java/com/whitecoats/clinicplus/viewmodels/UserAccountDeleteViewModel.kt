package com.whitecoats.clinicplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitecoats.clinicplus.models.AccountDeleteRequest
import com.whitecoats.clinicplus.repositories.UserAccountDeleteRepository
import com.whitecoats.clinicplus.utils.Resource
import com.whitecoats.clinicplus.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserAccountDeleteViewModel : ViewModel() {

    sealed class AccountDeleteEvent {
        class Success(val resultedText: String) : AccountDeleteEvent()
        class Failure(val errorText: String) : AccountDeleteEvent()
    }

    private val _accountDeleteResponseLiveData = MutableLiveData<AccountDeleteEvent>()
    val accountDeleteResponseLiveData: LiveData<AccountDeleteEvent> = _accountDeleteResponseLiveData
    private val repository: UserAccountDeleteRepository by lazy {
        UserAccountDeleteRepository()
    }

    fun requestForUserAccountDelete(accountDeleteRequest: AccountDeleteRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            var response = repository.requestForAccountDelete(accountDeleteRequest)
            when (response) {
                is Resource.Success -> {
                    _accountDeleteResponseLiveData.postValue(AccountDeleteEvent.Success(response.data!!.msg))
                }
                is Resource.Error -> {
                    _accountDeleteResponseLiveData.postValue(AccountDeleteEvent.Failure(response.message!!))
                }
            }
        }
    }
}