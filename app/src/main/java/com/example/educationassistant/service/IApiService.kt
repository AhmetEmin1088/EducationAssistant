package com.example.educationassistant.service

import com.example.educationassistant.model.RSS
import io.reactivex.Observable
import retrofit2.http.GET

interface IApiService {

    @get:GET("egitim_articles.rss")
    val fetchRSS : Observable<RSS?>?

}