package com.example.booksearch.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.booksearch.data.model.Book

@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OrmConverter::class) //사용하고 싶을 때 알아서 사용함
abstract class BookSearchDatabase : RoomDatabase(){

    abstract fun bookSearchDao() : BookSearchDao

    companion object{
        @Volatile
        private var INSTANCE : BookSearchDatabase?= null

        private fun buildDatabase(context : Context) : BookSearchDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                BookSearchDatabase::class.java,
                "favorite-books"
            ).build()

        fun getInstance(context : Context) : BookSearchDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

    }
}