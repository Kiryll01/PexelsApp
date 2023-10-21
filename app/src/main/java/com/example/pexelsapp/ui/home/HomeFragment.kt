package com.example.pexelsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.Adapters.ImageListAdapter
import com.example.pexelsapp.Adapters.SearchKeyWordsAdapter
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.coroutineContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter=ImageListAdapter()
        binding.apply {
            searchTitlesRecyclerView.adapter=SearchKeyWordsAdapter(viewModel.collections)
            val collectionsLayoutManager=LinearLayoutManager(requireContext())
            collectionsLayoutManager.orientation=RecyclerView.HORIZONTAL
            searchTitlesRecyclerView.layoutManager= collectionsLayoutManager
            imagesRecyclerView.adapter = adapter
            imagesRecyclerView.layoutManager=
               StaggeredGridLayoutManager( 2,
                   StaggeredGridLayoutManager.VERTICAL)
        }
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}