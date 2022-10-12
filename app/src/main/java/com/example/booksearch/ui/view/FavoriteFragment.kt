package com.example.booksearch.ui.view

import android.graphics.*
import android.os.Bundle
import android.text.TextPaint
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
import com.example.booksearch.ui.adapter.BookSearchPagingAdapter
import com.example.booksearch.ui.viewmodel.BookSearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class FavoriteFragment : Fragment(){

    private var _binding : FragmentFavoriteBinding?= null
    private val binding : FragmentFavoriteBinding
        get() = _binding!!

    private lateinit var bookSearchViewModel : BookSearchViewModel
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
        //StateFLow 로 비교 후 있으면 PagingData 로 변환
        //submitList -> submitData 로 변경
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                bookSearchViewModel.favoriteBooks.collectLatest {
                    if(it.isEmpty()){
                        binding.favoriteFalseBooks.visibility = View.VISIBLE
                    }
                    else{
                        binding.favoriteFalseBooks.visibility = View.INVISIBLE
                    }
                    bookSearchViewModel.favoritePagingBooks.collectLatest { pagedData ->
                        bookSearchAdapter.submitData(pagedData)
                    }
                }
            }
        }
        val dec = DecimalFormat("#,###")
        //총 가격
        bookSearchViewModel.sumPrice.observe(viewLifecycleOwner){
            if(it == null){
                binding.favoriteSumPrice.visibility = View.INVISIBLE
            }else {
                binding.favoriteSumPrice.text = "총(할인) 가격 : ${dec.format(it.toLong())} 원"
                binding.favoriteSumPrice.visibility = View.VISIBLE
            }
        }
    }

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
//                val book = bookSearchAdapter.currentList[position]
//                bookSearchViewModel.deleteBook(book)
//                Snackbar.make(view, "관심목록에서 삭제되었습니다.", Snackbar.LENGTH_SHORT).apply {
//                    setAction("취소") {
//                        bookSearchViewModel.saveBook(book)
//                    }
//                }.show()
                //current -> peek , null 처리 추가함
                val pagedBook = bookSearchAdapter.peek(position)
                pagedBook?.let{ book ->
                    bookSearchViewModel.deleteBook(book)
                    Snackbar.make(view, "관심목록에서 삭제되었습니다.", Snackbar.LENGTH_SHORT).apply {
                        setAction("취소") {
                            bookSearchViewModel.saveBook(book)
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
                //스와이프 할 때 나타나는 색 및 아이콘
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

                        val text = "삭제"
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