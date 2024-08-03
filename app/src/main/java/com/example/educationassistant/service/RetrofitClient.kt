package com.example.educationassistant.service

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitClient {

    var instance : Retrofit? = null
        get() {
            if (field == null) field = Retrofit.Builder()
                .baseUrl("https://www.trthaber.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return field
        }


}