package com.example.booksearch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksearch.data.repository.BookSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository,
) : ViewModel(){

    //DataStore
    fun saveSortMode(value : String) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.saveSortMode(value)
    } //파일 IO작업이기 떄문에 IO로

    //반드시 값을 반환하고 종료되는 withContext에서 실행하게함
    suspend fun getSortMode() = withContext(Dispatchers.IO){
        bookSearchRepository.getSortMode().first()
    } //전체 데이터 스트림을 구독할 필요가 없음, 단일을 가져오기 떄문에 first

}