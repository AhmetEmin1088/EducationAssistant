package com.example.educationassistant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.educationassistant.model.RSS
import com.example.educationassistant.repository.MainRepository

class MainViewModel : ViewModel() {

    private var mainRepository = MainRepository()

    val rssFeed : MutableLiveData<RSS>
        get() = mainRepository.getRssMutableLiveData()

    val error : LiveData<String>
        get() = mainRepository.getError()

    fun clear() {
        mainRepository.clear()
    }

}