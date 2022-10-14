package com.example.booksearch.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.booksearch.data.model.Book
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingSource as PagingSource

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

    //관심목록에 담기를 클릭했을 때 이미 있는지 판단하는 쿼리
    @Query("SELECT * FROM books WHERE isbn = :isBn")
    fun favoriteFalse(isBn : String) : Book

    //관심목록에 있는 책들의 총 세일 가격
    @Query("SELECT SUM(sale_price) FROM books")
    fun sumSalesPrice() : LiveData<Int>

    //관심목록에 있는 총 가격 (할인 가격하고 다름)
    @Query("SELECT SUM(price) FROM books")
    fun sumPrice() : LiveData<Int>

    //Paging
    //PagingSource 로 반환받는 Paging
    @Query("SELECT * FROM books")
    fun getFavoritePagingBooks() : PagingSource<Int, Book>
}