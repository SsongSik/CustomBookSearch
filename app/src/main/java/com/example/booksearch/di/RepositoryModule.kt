package com.example.booksearch.di

import com.example.booksearch.data.repository.BookSearchRepository
import com.example.booksearch.data.repository.BookSearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    //BookSearchRepository 가 interface 이기 때문에
    //Binds 를 사용해서 Hilt 가 의존성 객체를 생성할 수 있도록 해줌
    //AppModule -> BookSearchRepository 주입 -> BookSearchViewModel 주입
    // -> 각 프래그먼트에 BookSearchViewModel 을 주입
    @Singleton
    @Binds
    abstract fun bindBookSearchRepository(
        bookSearchRepositoryImpl: BookSearchRepositoryImpl,
    ) : BookSearchRepository
}