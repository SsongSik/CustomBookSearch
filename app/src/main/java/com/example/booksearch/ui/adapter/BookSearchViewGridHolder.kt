package com.example.booksearch.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.booksearch.data.model.Book
import com.example.booksearch.databinding.ItemBookPreviewBinding
import com.example.booksearch.databinding.ItemBookSearchBinding
import java.text.DecimalFormat

// 검색화면에서 사용하는 Grid RV Holder
class BookSearchViewGridHolder(
    private val binding : ItemBookSearchBinding
) : RecyclerView.ViewHolder(binding.root){

    val dec = DecimalFormat("#,###")

    fun bind(book : Book){
        val author = book.authors.toString().removeSurrounding("[", "]")
        val publisher = book.publisher
        val date = if(book.datetime.isNotEmpty()) book.datetime.substring(0, 10) else ""
//        val price = dec.format(book.price.toLong())

        itemView.apply{
            binding.ivArticleImage.load(book.thumbnail)
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = "$author | $publisher"
            binding.tvDatetime.text = date
//            binding.tvPrice.text = "$price 원"
        }
    }
}