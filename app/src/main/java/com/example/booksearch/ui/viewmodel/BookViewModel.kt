package com.example.booksearch.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class BookViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository
) : ViewModel(){

    //Room
    fun saveBook(book : Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.insertBooks(book)
    }

    //담아두기 버튼을 클릭했을 때 장바구니에 이미 있을 경우 경고표시하기 위한 메서드
    private val _book = MutableLiveData<Book>()
    val book : LiveData<Book>
        get() = _book
    fun favoriteFalse(book : Book) = viewModelScope.launch(Dispatchers.IO){
        val bookFalse = bookSearchRepository.favoriteFalse(book.isbn)
        _book.postValue(bookFalse)
    }
}