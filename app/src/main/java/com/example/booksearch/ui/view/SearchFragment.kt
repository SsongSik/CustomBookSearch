package com.example.booksearch.ui.view

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksearch.databinding.FragmentSearchBinding
import com.example.booksearch.ui.adapter.BookSearchGridPagingAdapter
import com.example.booksearch.ui.adapter.BookSearchLoadStateAdapter
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
        setupLoadState()

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
//            adapter = bookSearchAdapter
            //Load State 하고 연결
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

    private fun setupLoadState(){
        //load State 값을 받아옴
        bookSearchAdapter.addLoadStateListener { combinedLoadStates ->
            val loadState = combinedLoadStates.source //source 의 값을 받아옴

            //리스트가 비어있는지 판단을 함
            val isListEmpty = bookSearchAdapter.itemCount < 1
                    && loadState.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
            //prepend 시작지점에 만들어짐
            //append 종료지점
            //refresh 로딩 값을 갱신할 때  , 세 개의 속성이 있음

            //검색결과가 없으면 noResult 를 보여줌, 리사이클러뷰는 가려짐
            binding.tvEmptylist.isVisible = isListEmpty
            binding.rvSearchResult.isVisible = !isListEmpty

            //로딩중일떄는 프로그레스바를 보이게함
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            //에러가 발생했을 때, 토스트로 화면을 표시함
            //-> LoadStateAdapter 를 생성하면서 더이상 에러 상황이 필요 없어짐
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