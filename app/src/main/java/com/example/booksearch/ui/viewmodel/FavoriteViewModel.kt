package com.example.booksearch.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.booksearch.data.model.Book
import com.example.booksearch.data.repository.BookSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository
) : ViewModel() {

    //Paging
    val favoritePagingBooks : StateFlow<PagingData<Book>> =
        bookSearchRepository.getFavoritePagingBooks()
            .cachedIn(viewModelScope) //코루틴이 데이터스트림을 캐시하고 공유가능하게끔 만들어줌.
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())
    //ui 에서 감시해야하기 때문에 stateIn 을 써서 stateFlow 로 만듬

    //Room
    fun saveBook(book : Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.insertBooks(book)
    }

    fun deleteBook(book : Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.deleteBooks(book)
    }

    //    val favoriteBooks : LiveData<List<Book>> = bookSearchRepository.getFavoriteBooks()
//    val favoriteBooks : Flow<List<Book>> = bookSearchRepository.getFavoriteBooks()
    // StateFlow 로 변환해서 Flow 동작을 FavoriteFragment 라이프사이클하고 동기화
    val favoriteBooks : StateFlow<List<Book>> = bookSearchRepository.getFavoriteBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())
    //Flow 타입을 변경함

    //관심 목록에 있는 책들의 총 할인가격
    val sumSalesPrice : LiveData<Int> = bookSearchRepository.sumSalesPrice()

    //관심 목록에 있는 책들의 총 가격(할인가격하고는 다름)
    val sumPrice : LiveData<Int> = bookSearchRepository.sumPrice()
}