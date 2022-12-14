package com.example.booksearch.ui.view

import android.graphics.*
import android.os.Bundle
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.example.booksearch.ui.adapter.BookSearchPagingAdapter
import com.example.booksearch.ui.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class FavoriteFragment : Fragment(){

    private var _binding : FragmentFavoriteBinding?= null
    private val binding : FragmentFavoriteBinding
        get() = _binding!!

//    private lateinit var bookSearchViewModel : BookSearchViewModel
//    private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()
    private val favoriteViewModel by viewModels<FavoriteViewModel>()
//    private lateinit var bookSearchAdapter : BookSearchAdapter
    private lateinit var bookSearchAdapter : BookSearchPagingAdapter

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

//        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        setUpRecyclerView()
        setupTouchHelper(view)

        //LiveData
//        bookSearchViewModel.favoriteBooks.observe(viewLifecycleOwner){
//            //??????????????? ???????????? ?????? ???
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
        //flow ??? ?????????????????? ???????????? ???????????? ???????????? ???????????????
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
//        viewLifecycleOwner.lifecycleScope.launch{
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                bookSearchViewModel.favoriteBooks.collectLatest {
//                    if(it.isEmpty()){
//                        binding.favoriteFalseBooks.visibility = View.VISIBLE
////                        binding.favoriteSumPrice.visibility = View.INVISIBLE
//                    }else {
//                        binding.favoriteFalseBooks.visibility = View.INVISIBLE
////                        binding.favoriteSumPrice.visibility = View.VISIBLE
//                    }
//                    bookSearchAdapter.submitList(it)
//                }
//            }
//        }
        //StateFLow ??? ?????? ??? ????????? PagingData ??? ??????
        //submitList -> submitData ??? ??????
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                favoriteViewModel.favoriteBooks.collectLatest {
                    if(it.isEmpty()){
                        binding.favoriteFalseBooks.visibility = View.VISIBLE
                    }
                    else{
                        binding.favoriteFalseBooks.visibility = View.INVISIBLE
                    }
                    //????????? ???????????? ???????????? submitData ??? ??????
                    favoriteViewModel.favoritePagingBooks.collectLatest { pagedData ->
                        bookSearchAdapter.submitData(pagedData)
                    }
                }
            }
        }
        val dec = DecimalFormat("#,###")
        //??? ?????? ??????
        favoriteViewModel.sumSalesPrice.observe(viewLifecycleOwner){
            if(it == null){ //??????????????? ???????????? ????????? ???
                binding.favoriteSumPrice.visibility = View.INVISIBLE
            }else {
                binding.favoriteSumPrice.text = "?????? ?????? : ${dec.format(it.toLong())} ???"
                binding.favoriteSumPrice.visibility = View.VISIBLE
            }
        }
        //??? ??????
        favoriteViewModel.sumPrice.observe(viewLifecycleOwner){
            if(it == null){
                binding.favoriteSumRealPrice.visibility = View.INVISIBLE
            }else{
                binding.favoriteSumRealPrice.text = "??? ?????? : ${dec.format(it.toLong())} ??? ->"
                binding.favoriteSumRealPrice.visibility = View.VISIBLE
            }
        }
    }

    //?????????????????? ???????????? ????????????????????? ??????
    private fun setUpRecyclerView(){
//        bookSearchAdapter = BookSearchAdapter()
        bookSearchAdapter = BookSearchPagingAdapter()
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

    //???????????? ??????????????? ????????????
    private fun setupTouchHelper(view : View){
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT //???????????? ???????????? ??????????????? 0??????
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //?????? ??????
                val position = viewHolder.bindingAdapterPosition
                //????????? book ??? ??????
//                val book = bookSearchAdapter.currentList[position]
//                bookSearchViewModel.deleteBook(book)
//                Snackbar.make(view, "?????????????????? ?????????????????????.", Snackbar.LENGTH_SHORT).apply {
//                    setAction("??????") {
//                        bookSearchViewModel.saveBook(book)
//                    }
//                }.show()
                //current -> peek , null ?????? ?????????
                val pagedBook = bookSearchAdapter.peek(position)
                pagedBook?.let{ book ->
                    favoriteViewModel.deleteBook(book)
                    Snackbar.make(view, "?????????????????? ?????????????????????.", Snackbar.LENGTH_SHORT).apply {
                        setAction("??????") {
                            favoriteViewModel.saveBook(book)
                        }
                    }.show()
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //???????????? ??? ??? ???????????? ??? ??? ?????????
                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val height = (itemView.bottom - itemView.top).toFloat()
                    val width = height / 4
                    val paint = Paint()
                    if (dX < 0) {
                        paint.color = Color.parseColor("#ff0000")
                        val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, paint)

                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_delete)
                        val iconDst = RectF(
                            itemView.right.toFloat() - 3 * width,
                            itemView.top.toFloat() + width,
                            itemView.right.toFloat() - width,
                            itemView.bottom.toFloat() - width
                        )
                        c.drawBitmap(icon, null, iconDst, null)

                        val text = "??????"
                        val textPaint = TextPaint()
                        textPaint.textSize = 50f
                        textPaint.color = Color.WHITE
                        val bounds = Rect()
                        textPaint.getTextBounds(text, 0, text.length, bounds)
                        val textHeight = bounds.height()
                        val textWidth = textPaint.measureText(text)
                        val textX = itemView.right - 3 * width - itemView.paddingRight - textWidth
                        val textY = itemView.top + height / 2f + textHeight / 2f

                        c.drawText(text, textX, textY, textPaint)
                    }
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        //????????????????????? ??????
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteBooks)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}