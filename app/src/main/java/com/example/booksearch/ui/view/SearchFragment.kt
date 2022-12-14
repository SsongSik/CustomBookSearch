package com.example.booksearch.ui.view

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksearch.databinding.FragmentSearchBinding
import com.example.booksearch.ui.adapter.BookSearchGridPagingAdapter
import com.example.booksearch.ui.adapter.BookSearchLoadStateAdapter
import com.example.booksearch.ui.viewmodel.SearchViewModel
import com.example.booksearch.util.Constant.SEARCH_BOOKS_TIME_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(){
    private var _binding : FragmentSearchBinding?= null
    private val binding : FragmentSearchBinding
        get() = _binding!!

//    private lateinit var bookSearchViewModel: BookSearchViewModel
//    private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()

    private val searchViewModel by viewModels<SearchViewModel>()

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

//        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel
        setUpRecyclerView()
        searchBooks()
        setupLoadState()

//        bookSearchViewModel.resultSearch.observe(viewLifecycleOwner){ response ->
//            val books = response.documents
//            bookSearchAdapter.submitList(books)
//        }
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchPagingResult.collectLatest {
                bookSearchAdapter.submitData(it)
            }
        }

    }
    private fun setUpRecyclerView(){
        bookSearchAdapter = BookSearchGridPagingAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            //????????? ?????????????????? ??????
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
//            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
//            adapter = bookSearchAdapter
            //Load State ?????? ??????
            adapter = bookSearchAdapter.withLoadStateFooter(
                footer = BookSearchLoadStateAdapter(bookSearchAdapter::retry)
            )
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
            Editable.Factory.getInstance().newEditable(searchViewModel.query)

        binding.etSearch.addTextChangedListener{ text: Editable? ->
            endTime = System.currentTimeMillis()
            if(endTime - startTime >= SEARCH_BOOKS_TIME_DELAY){
                text?.let {
                    val query = it.toString().trim()
                    if(query.isNotEmpty()){
//                        bookSearchViewModel.searchBooks(query)
                        //Paging ?????? ??????, ??????????????? ??????
                        searchViewModel.searchBooksPaging(query)
                    }
                }
            }
            startTime = endTime
        }
    }

    private fun setupLoadState(){
        //load State ?????? ?????????
        bookSearchAdapter.addLoadStateListener { combinedLoadStates ->
            val loadState = combinedLoadStates.source //source ??? ?????? ?????????

            //???????????? ??????????????? ????????? ???
            val isListEmpty = bookSearchAdapter.itemCount < 1
                    && loadState.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
            //prepend ??????????????? ????????????
            //append ????????????
            //refresh ?????? ?????? ????????? ???  , ??? ?????? ????????? ??????

            //??????????????? ????????? noResult ??? ?????????, ????????????????????? ?????????
            binding.tvEmptylist.isVisible = isListEmpty
            binding.rvSearchResult.isVisible = !isListEmpty

            //?????????????????? ????????????????????? ????????????
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            //????????? ???????????? ???, ???????????? ????????? ?????????
            //-> LoadStateAdapter ??? ??????????????? ????????? ?????? ????????? ?????? ?????????
//            binding.btnRetry.isVisible = loadState.refresh is LoadState.Error
//                    || loadState.append is LoadState.Error
//                    || loadState.prepend is LoadState.Error
//            val errorState : LoadState.Error? = loadState.append as? LoadState.Error
//                ?: loadState.prepend as? LoadState.Error
//                ?: loadState.refresh as? LoadState.Error
//            errorState?.let{
//                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
//            }

        }
//        binding.btnRetry.setOnClickListener {
//            bookSearchAdapter.retry()
//        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}