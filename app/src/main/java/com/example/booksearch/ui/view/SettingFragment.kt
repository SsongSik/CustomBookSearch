package com.example.booksearch.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.booksearch.R
import com.example.booksearch.databinding.FragmentSettingBinding
import com.example.booksearch.ui.viewmodel.SettingViewModel
import com.example.booksearch.util.Sort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment(){
    private var _binding : FragmentSettingBinding?= null
    private val binding : FragmentSettingBinding
        get() = _binding!!

//    private lateinit var bookSearchViewModel : BookSearchViewModel
//    private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()
    private val settingViewModel by viewModels<SettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        saveSetting()
        loadSetting()
    }

    private fun loadSetting() {
        lifecycleScope.launch{
            val buttonId =
                when(settingViewModel.getSortMode()){
                    Sort.ACCURACY.value -> R.id.rb_accuracy
                    Sort.LATEST.value -> R.id.rb_latest
                    else -> return@launch
                }
            binding.rgSort.check(buttonId)
        }
    }

    private fun saveSetting() {
        binding.rgSort.setOnCheckedChangeListener { _, checkId ->
            val value =
                when(checkId){
                    R.id.rb_accuracy -> Sort.ACCURACY.value
                    R.id.rb_latest -> Sort.LATEST.value
                    else -> return@setOnCheckedChangeListener
                }
            settingViewModel.saveSortMode(value)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}