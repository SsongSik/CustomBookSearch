package com.example.booksearch.util

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

//Hilt
@HiltAndroidApp
class BookSearchApplication : Application() {

}