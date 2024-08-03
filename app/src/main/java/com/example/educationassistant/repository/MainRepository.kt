package com.example.educationassistant.repository

import androidx.lifecycle.MutableLiveData
import com.example.educationassistant.model.RSS
import com.example.educationassistant.service.IApiService
import com.example.educationassistant.service.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainRepository {

    private val rssMutableLiveData = MutableLiveData<RSS>()
    private val error = MutableLiveData<String>()
    private val compositeDisposable = CompositeDisposable()
    private val apiService = RetrofitClient.instance!!.create(IApiService::class.java)

    fun getRssMutableLiveData(): MutableLiveData<RSS> {

        compositeDisposable.add(
            apiService.fetchRSS!!
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ rss ->
                    rssMutableLiveData.value = rss!!
                }) { throwable: Throwable -> error.value = throwable.message }
        )

        return rssMutableLiveData

    }

    fun getError() : MutableLiveData<String> {
        return error
    }

    fun clear() {
        compositeDisposable.clear()
    }

}