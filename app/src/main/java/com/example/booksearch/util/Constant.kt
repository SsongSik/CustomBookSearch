package com.example.booksearch.util

import com.example.booksearch.BuildConfig

object Constant {
    const val BASE_URL = "https://dapi.kakao.com/"
    //API KEY 암호
    //Secrets Gradle Plugin 사용
    const val API_KEY = BuildConfig.bookApiKey
    const val SEARCH_BOOKS_TIME_DELAY = 100L
    const val DATASTORE_NAME = "preferences_datastore"
    const val PAGING_SIZE = 15
}