package com.example.booksearch.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.booksearch.data.api.BookSearchApi
import com.example.booksearch.data.db.BookSearchDatabase
import com.example.booksearch.data.model.Book
import com.example.booksearch.data.model.SearchResponse
import com.example.booksearch.data.repository.BookSearchRepositoryImpl.PreferencesKeys.SORT_MODE
import com.example.booksearch.util.Constant.PAGING_SIZE
import com.example.booksearch.util.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSearchRepositoryImpl  @Inject constructor(
    private val db : BookSearchDatabase,
    private val dataStore: DataStore<Preferences>,
    private val api : BookSearchApi
) : BookSearchRepository{
    override suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): Response<SearchResponse> {
        return api.searchBooks(query, sort, page, size)
    }

    //Room
    override suspend fun insertBooks(book: Book) {
        db.bookSearchDao().insertBook(book)
    }

    override suspend fun deleteBooks(book: Book) {
        db.bookSearchDao().deleteBook(book)
    }

//    override fun getFavoriteBooks(): LiveData<List<Book>> {
//        return db.bookSearchDao().getFavoriteBooks()
//    }
    override fun getFavoriteBooks(): Flow<List<Book>> {
        return db.bookSearchDao().getFavoriteBooks()
    }

    override fun favoriteFalse(isbn: String): Book {
        return db.bookSearchDao().favoriteFalse(isbn)
    }

    override fun sumSalesPrice(): LiveData<Int> {
        return db.bookSearchDao().sumSalesPrice()
    }

    override fun sumPrice(): LiveData<Int> {
        return db.bookSearchDao().sumPrice()
    }

    //DataStore
    private object PreferencesKeys {
        //??????, ????????? ?????? ??????, String ??????
        val SORT_MODE = stringPreferencesKey("sort_mode")
    }

    //???????????? ????????? ????????? ?????????
    override suspend fun saveSortMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[SORT_MODE] = mode
        }
    }

    override suspend fun getSortMode(): Flow<String> {
        return dataStore.data //data ?????????
                //?????? ?????? ????????? ????????????
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
                //?????? ?????? : ?????? ?????? ACCURACY
            .map { prefs ->
                prefs[SORT_MODE] ?: Sort.ACCURACY.value
            }
    }

    //Paging
    override fun getFavoritePagingBooks(): Flow<PagingData<Book>> {
        val pagingSourceFactory = { db.bookSearchDao().getFavoritePagingBooks() }

        //Pager ??? ???????????? ???????????? Config ??? ???????????????
        //viewholder ??? ?????? ???????????? ??????
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    //?????? Paging
    override fun searchBooksPaging(query: String, sort: String): Flow<PagingData<Book>> {
        val pagingSourceFactory = { BookSearchPagingSource(api, query, sort)}

        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE, //??? ???????????? ????????? ???
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3 //?????? ?????????
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}