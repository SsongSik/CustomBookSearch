package com.example.booksearch.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.booksearch.databinding.ItemLoadStateBinding

//책 검색에서 현재 상황을 LoadStateAdapter 를 통해 파악함
//이를 BookSearchGridPagingAdapter 하고 연결
class BookSearchLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<BookSearchLoadStateViewHolder>(){

    override fun onBindViewHolder(holder: BookSearchLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BookSearchLoadStateViewHolder {
        return BookSearchLoadStateViewHolder(
            ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retry
        )
    }

}