package com.example.booksearch.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import com.example.booksearch.data.api.RetrofitInstance.api
import com.example.booksearch.data.db.BookSearchDatabase
import com.example.booksearch.data.model.Book
import com.example.booksearch.data.model.SearchResponse
import com.example.booksearch.data.repository.BookSearchRepositoryImpl.PreferencesKeys.SORT_MODE
import com.example.booksearch.util.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException

class BookSearchRepositoryImpl(
    private val db : BookSearchDatabase,
    private val dataStore: DataStore<Preferences>
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

    override fun sumPrice(): LiveData<Int> {
        return db.bookSearchDao().sumPrice()
    }

    //DataStore
    private object PreferencesKeys {
        //저장, 불러올 키를 정의, String 사용
        val SORT_MODE = stringPreferencesKey("sort_mode")
    }

    //저장하는 작업을 코루틴 안에서
    override suspend fun saveSortMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[SORT_MODE] = mode
        }
    }

    override suspend fun getSortMode(): Flow<String> {
        return dataStore.data //data 메서드
                //실패 했을 대비에 예외처리
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
                //키를 전달 : 기본 값은 ACCURACY
            .map { prefs ->
                prefs[SORT_MODE] ?: Sort.ACCURACY.value
            }
    }
}