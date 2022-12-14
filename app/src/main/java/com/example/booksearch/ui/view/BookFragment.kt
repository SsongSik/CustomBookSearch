package com.example.booksearch.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.booksearch.databinding.FragmentBookBinding
import com.example.booksearch.ui.viewmodel.BookViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookFragment : Fragment(){
    private var _binding :  FragmentBookBinding?= null
    private val binding : FragmentBookBinding
        get() = _binding!!

    private val args : BookFragmentArgs by navArgs<BookFragmentArgs>()
//    private lateinit var bookSearchViewModel : BookSearchViewModel
//    private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()
    private val bookViewModel by viewModels<BookViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        val book = args.book
        binding.webview.apply{
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(book.url)
        }

        binding.fabFavorite.setOnClickListener{
            //이미 관심목록에 있는지 판단하기 위해
            bookViewModel.favoriteFalse(book)

            bookViewModel.book.observe(viewLifecycleOwner){
                if(it == null){
                    //만일 조회했던 book 에서 null 이 나왔을 경우에만 저장
                    bookViewModel.saveBook(book)
                    Snackbar.make(view, "관심목록에 저장되었습니다.", Snackbar.LENGTH_SHORT).show()
                }
                else{
                    //아니면 경고메세지
                    Snackbar.make(view, "이미 관심목록에 존재합니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        binding.webview.onPause()
        super.onPause()
    }

    override fun onResume() {
        binding.webview.onResume()
        super.onResume()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}