package com.example.booksearch.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksearch.R
import com.example.booksearch.databinding.FragmentFavoriteBinding
import com.example.booksearch.ui.adapter.BookSearchAdapter
import com.example.booksearch.ui.viewmodel.BookSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(){

    private var _binding : FragmentFavoriteBinding?= null
    private val binding : FragmentFavoriteBinding
        get() = _binding!!

    private lateinit var bookSearchViewModel : BookSearchViewModel
    private lateinit var bookSearchAdapter : BookSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        setUpRecyclerView()
        setupTouchHelper(view)

        //LiveData
//        bookSearchViewModel.favoriteBooks.observe(viewLifecycleOwner){
//            //관심목록에 아무것도 없을 때
//            if(it.isEmpty()){
//                binding.favoriteFalseBooks.visibility = View.VISIBLE
//                binding.rvFavoriteBooks.visibility = View.INVISIBLE
//            }else {
//                binding.favoriteFalseBooks.visibility = View.INVISIBLE
//                binding.rvFavoriteBooks.visibility = View.VISIBLE
//                bookSearchAdapter.submitList(it)
//            }
//        }

        //Flow
        //flow 는 코루틴안에서 콜렉트를 사용해서 데이터를 구독해야함
//        lifecycleScope.launch {
//            bookSearchViewModel.favoriteBooks.collectLatest {
//            if(it.isEmpty()){
//                binding.favoriteFalseBooks.visibility = View.VISIBLE
//                binding.rvFavoriteBooks.visibility = View.INVISIBLE
//            }else {
//                binding.favoriteFalseBooks.visibility = View.INVISIBLE
//                binding.rvFavoriteBooks.visibility = View.VISIBLE
//                bookSearchAdapter.submitList(it)
//            }
//            }
//        }

        //StateFlow
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                bookSearchViewModel.favoriteBooks.collectLatest {
                    if(it.isEmpty()){
                        binding.favoriteFalseBooks.visibility = View.VISIBLE
                        binding.rvFavoriteBooks.visibility = View.INVISIBLE
                    }else {
                        binding.favoriteFalseBooks.visibility = View.INVISIBLE
                        binding.rvFavoriteBooks.visibility = View.VISIBLE
                        bookSearchAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView(){
        bookSearchAdapter = BookSearchAdapter()
        binding.rvFavoriteBooks.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = bookSearchAdapter
        }
        bookSearchAdapter.setOnItemClickListener {
            val action  = FavoriteFragmentDirections.actionFragmentFavoriteToFragmentBook(it)
            findNavController().navigate(action)
        }
    }

    //왼쪽에서 오른쪽으로 스와이프
    private fun setupTouchHelper(view : View){
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT //드래그는 사용하지 않기떄문에 0으로
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //위치 획득
                val position = viewHolder.bindingAdapterPosition
                //위치의 book 을 찾음
                val book = bookSearchAdapter.currentList[position]
                bookSearchViewModel.deleteBook(book)
                Snackbar.make(view, "Book has delete", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        bookSearchViewModel.saveBook(book)
                    }
                }.show()
            }
        }
        //리사이클러뷰와 연결
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteBooks)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}