package com.example.booksearch.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import com.example.booksearch.data.model.Book
import com.example.booksearch.databinding.ItemBookPreviewBinding
import com.example.booksearch.databinding.ItemBookSearchBinding

//검색 화면 페이징 어댑터, GridLayout 으로 설정하여 따로 어댑터를 만들어서 활용했음
class BookSearchGridPagingAdapter : PagingDataAdapter<Book, BookSearchViewGridHolder>(BookDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchViewGridHolder {
        return BookSearchViewGridHolder(
            ItemBookSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BookSearchViewGridHolder, position: Int) {
        val pagedBook = getItem(position)
        pagedBook?.let { book ->
            holder.bind(book)
            holder.itemView.setOnClickListener {
                onItemClickListener?.let{ it(book) }
            }
        }
    }

    private var onItemClickListener : ((Book) -> Unit)? = null
    fun setOnItemClickListener(listener : (Book) -> Unit){
        onItemClickListener = listener
    }

    companion object{
        private val BookDiffCallback = object : DiffUtil.ItemCallback<Book>(){
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.isbn == newItem.isbn
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }

        }
    }


}