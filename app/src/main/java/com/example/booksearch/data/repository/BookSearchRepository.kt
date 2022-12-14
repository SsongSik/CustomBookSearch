package com.example.booksearch.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.booksearch.data.model.Book
import com.example.booksearch.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface BookSearchRepository {

    suspend fun searchBooks(
        query : String,
        sort : String,
        page : Int,
        size : Int,
    ) : Response<SearchResponse>

    //Room
    suspend fun insertBooks(book : Book)

    suspend fun deleteBooks(book : Book)

//    fun getFavoriteBooks() : LiveData<List<Book>>
    fun getFavoriteBooks() : Flow<List<Book>>

    fun favoriteFalse(isbn : String) : Book

    fun sumSalesPrice() : LiveData<Int>

    fun sumPrice() : LiveData<Int>

    //DataStore
    suspend fun saveSortMode(mode : String)

    suspend fun getSortMode() : Flow<String>

    //Paging
    fun getFavoritePagingBooks() : Flow<PagingData<Book>>

    fun searchBooksPaging(query : String, sort : String) : Flow<PagingData<Book>>
}