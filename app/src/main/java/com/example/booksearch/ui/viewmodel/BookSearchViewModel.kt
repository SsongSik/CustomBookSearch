package com.example.booksearch.ui.viewmodel

import androidx.lifecycle.*
import com.example.booksearch.data.model.Book
import com.example.booksearch.data.model.SearchResponse
import com.example.booksearch.data.repository.BookSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookSearchViewModel(
    private val bookSearchRepository: BookSearchRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(){

    private val _resultSearch = MutableLiveData<SearchResponse>()
    val resultSearch : LiveData<SearchResponse>
        get() = _resultSearch

    fun searchBooks(query : String) = viewModelScope.launch(Dispatchers.IO){
        val response = bookSearchRepository.searchBooks(query, "accuracy", 1, 15)

        if(response.isSuccessful){
            response.body()?.let{ body ->
                _resultSearch.postValue(body)
            }
        }
    }

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

    //담아두기 버튼을 클릭했을 때 장바구니에 이미 있을 경우 경고표시하기 위한 메서드
    private val _book = MutableLiveData<Book>()
    val book : LiveData<Book>
        get() = _book
    fun favoriteFalse(book : Book) = viewModelScope.launch(Dispatchers.IO){
        val bookFalse = bookSearchRepository.favoriteFalse(book.isbn)
        _book.postValue(bookFalse)
    }

    //SavedState
    var query = String()
        set(value){
            field = value //쿼리의 값이 변화하면 바로 반영
            savedStateHandle.set(SAVE_STATE_KEY, value)
        }

    init {
        query = savedStateHandle.get<String>(SAVE_STATE_KEY) ?: ""
    }

    companion object{
        private const val SAVE_STATE_KEY = "query"
    }
}
