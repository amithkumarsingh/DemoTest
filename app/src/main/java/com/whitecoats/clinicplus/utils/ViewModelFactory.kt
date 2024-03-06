package com.whitecoats.clinicplus.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.repositories.SettingScreenRepository
import com.whitecoats.clinicplus.viewmodels.SettingsViewModel

class ViewModelFactory constructor(private val settingScreenRepository: SettingScreenRepository) :
    ViewModelProvider.Factory {
  /*  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel() as T
        } else {
            throw  IllegalArgumentException("ViewModel Not Found")
        }
    }*/

}