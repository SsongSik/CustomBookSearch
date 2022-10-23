package com.example.booksearch.ui.view

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksearch.databinding.FragmentSearchBinding
import com.example.booksearch.ui.adapter.BookSearchGridPagingAdapter
import com.example.booksearch.ui.viewmodel.BookSearchViewModel
import com.example.booksearch.util.Constant.SEARCH_BOOKS_TIME_DELAY
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

class SearchFragment : Fragment(){
    private var _binding : FragmentSearchBinding?= null
    private val binding : FragmentSearchBinding
        get() = _binding!!

    private lateinit var bookSearchViewModel: BookSearchViewModel
//    private lateinit var bookSearchAdapter: BookSearchGridAdapter
    private lateinit var bookSearchAdapter : BookSearchGridPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel
        setUpRecyclerView()
        searchBooks()

//        bookSearchViewModel.resultSearch.observe(viewLifecycleOwner){ response ->
//            val books = response.documents
//            bookSearchAdapter.submitList(books)
//        }
        viewLifecycleOwner.lifecycleScope.launch {
            bookSearchViewModel.searchPagingResult.collectLatest {
                bookSearchAdapter.submitData(it)
            }
        }

    }
    private fun setUpRecyclerView(){
        bookSearchAdapter = BookSearchGridPagingAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            //그리드 레이아웃으로 변경
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
//            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = bookSearchAdapter
        }
        bookSearchAdapter.setOnItemClickListener {
            val action  = SearchFragmentDirections.actionFragmentSearchToFragmentBook(it)
            findNavController().navigate(action)
        }
    }

    private fun searchBooks(){
        var startTime = System.currentTimeMillis()
        var endTime : Long

        binding.etSearch.text =
            Editable.Factory.getInstance().newEditable(bookSearchViewModel.query)

        binding.etSearch.addTextChangedListener{ text: Editable? ->
            endTime = System.currentTimeMillis()
            if(endTime - startTime >= SEARCH_BOOKS_TIME_DELAY){
                text?.let {
                    val query = it.toString().trim()
                    if(query.isNotEmpty()){
//                        bookSearchViewModel.searchBooks(query)
                        //Paging 으로 변경, 무한스크롤 가능
                        bookSearchViewModel.searchBooksPaging(query)
                    }
                }
            }
            startTime = endTime
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}