package com.example.booksearch.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.booksearch.data.model.Book
import com.example.booksearch.databinding.ItemBookPreviewBinding

class BookSearchViewHolder(
    private val binding : ItemBookPreviewBinding
) : RecyclerView.ViewHolder(binding.root){

    fun bind(book : Book){
        val author = book.authors.toString().removeSurrounding("[", "]")
        val publisher = book.publisher
        val date = if(book.datetime.isNotEmpty()) book.datetime.substring(0, 10) else ""

        itemView.apply{
            binding.ivArticleImage.load(book.thumbnail)
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = "$author | $publisher"
            binding.tvDatetime.text = date
        }
    }
}