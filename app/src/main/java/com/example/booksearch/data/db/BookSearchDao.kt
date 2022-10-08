package com.example.booksearch.data.db

import androidx.room.*
import com.example.booksearch.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookSearchDao {

    //CRUD 구현
    //만일 데이터베이스에 같은 isbn 데이터가 있다면 대체하는 것으로 함
    //CUD 작업은 시간이 걸리는 작업이기 떄문에 코루틴으로 설정하였음
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book : Book)

    @Delete
    suspend fun deleteBook(book : Book)

    @Query("SELECT * FROM books")
//    fun getFavoriteBooks() : LiveData<List<Book>>
    fun getFavoriteBooks() : Flow<List<Book>>

    @Query("SELECT * FROM books WHERE isbn = :isBn")
    fun favoriteFalse(isBn : String) : Book
}