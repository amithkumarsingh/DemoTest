package com.whitecoats.clinicplus

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val isRecording: ObservableBoolean = ObservableBoolean(false);
    private val repository: MainActivityRepository = MainActivityRepository()
//    private val listOfSpl = setOf<String>("pediatrics","dermatology","neurology")


    init {

    }


    fun startAudioRecording(simboDevID: String,simboAuthKey : String,simboWss1_key: String) {
            repository.startAudioRecording(simboDevID,simboAuthKey,simboWss1_key);
            isRecording.set(true)

    }

    fun stopAudioRecording() {
        repository.stopAudioRecording()
        isRecording.set(false)
    }

    public fun getCurrentText(): MutableLiveData<String> {
        return repository.mutableString
    }

    public fun getPrescription(): MutableLiveData<String> {
        return repository.medMutable
    }

    public fun getSymtoms(): MutableLiveData<String> {
        return repository.symptomsMutable
    }

    public fun getDiagnosis(): MutableLiveData<String> {
        return repository.diagnosisMutable
    }

    public fun getInvestigation(): MutableLiveData<String> {
        return repository.investigationMutable
    }

    public fun getVitals(): MutableLiveData<String> {
        return repository.vitalsMutable
    }

    public fun getNotes(): MutableLiveData<String> {
        return repository.miscNotesMutable
    }

    public fun getLogs(): MutableLiveData<String> {
        return repository.logsMutable
    }

    public fun getAudioLevel(): MutableLiveData<Int> {
        return repository.audioMutable
    }


}