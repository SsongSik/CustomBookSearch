package com.example.booksearch.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.booksearch.data.api.RetrofitInstance.api
import com.example.booksearch.data.model.Book
import com.example.booksearch.util.Constant.PAGING_SIZE
import retrofit2.HttpException
import java.io.IOException

//PagingSource
class BookSearchPagingSource(
    private val query : String,
    private val sort : String,
) : PagingSource<Int, Book>(){
//PagingSource 를 상속받음
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
    //페이지를 갱신해야 할 때
    //가장 최근에 접근한 페이지를 반환
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    //페이저가 데이터를 호출할 때 마다 불리는 함수
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        return try{
            // 키 값을 받아와서 pageNumber 에 저장
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            val response = api.searchBooks(query, sort, pageNumber, params.loadSize)
            val endOfPaginationReached = response.body()?.meta?.isEnd!!
            //값이 트루면 마지막 페이지 이기 때문에 nextKey 의 값을 null 로 반환

            val data = response.body()?.documents!!
            val prevKey = if(pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if(endOfPaginationReached) {
                null
            }else{
                pageNumber + (params.loadSize / PAGING_SIZE)
            }
            //이전 페이지와 다음 페이지를 같이 넘겨줌
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception : IOException){
            LoadResult.Error(exception)
        } catch (exception : HttpException){
            LoadResult.Error(exception)
        }
    }

    //시작 페이지를 1로 정해놓음
    companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}