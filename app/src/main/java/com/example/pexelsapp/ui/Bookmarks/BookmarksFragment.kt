package com.example.pexelsapp.ui.Bookmarks

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.Adapters.BookmarksListAdapter
import com.example.pexelsapp.Adapters.ImageListAdapter
import com.example.pexelsapp.PexelsApplication
import com.example.pexelsapp.PhotosRepository
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FragmentBookmarksBinding
import com.example.pexelsapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BookmarksFragment() : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val viewModel : BookmarksViewModel by viewModels {
        BookmarksViewModelFactory((activity?.application as PexelsApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentBookmarksBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val adapter = BookmarksListAdapter {
                val action = BookmarksFragmentDirections.actionBookmarksFragmentToDetailsFragment(it)
                view.findNavController().navigate(action)
            }
            val layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
            lifecycleScope.launch {
                viewModel.likedPhotos().collect {
                    adapter.submitList(it)
                }
            }
            bookmarksRecyclerView.adapter=adapter
            bookmarksRecyclerView.layoutManager=layoutManager


        }
    }
}