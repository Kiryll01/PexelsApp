package com.example.pexelsapp.ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.ui.recyclerview.BookmarksListAdapter
import com.example.pexelsapp.PexelsApplication
import com.example.pexelsapp.databinding.FragmentBookmarksBinding
import com.example.pexelsapp.ui.ViewModels.BookmarksViewModel
import com.example.pexelsapp.ui.ViewModels.BookmarksViewModelFactory
import kotlinx.coroutines.launch

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    companion object{
        private var isFirstLaunch : Boolean = true
    }

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
            bookmarksExploreLayout.root.visibility=View.VISIBLE
            binding.bookmarksRecyclerView.visibility=View.GONE
            bookmarksExploreLayout.explore.setOnClickListener{
                val action=
                   BookmarksFragmentDirections.actionNavigationBookmarksToNavigationHome()
                findNavController().navigate(action)
            }
            val adapter = BookmarksListAdapter {
                val action =
                    BookmarksFragmentDirections.actionBookmarksFragmentToDetailsFragment(
                        it
                    )
                view.findNavController().navigate(action)
            }
            val layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
            lifecycleScope.launch {
                viewModel.likedPhotos().collect {
                        if (it.isNotEmpty()) {
                           binding.bookmarksRecyclerView.visibility=View.VISIBLE
                           bookmarksExploreLayout.root.visibility=View.GONE
                            adapter.submitList(it)
                        }
                }
            }
            bookmarksRecyclerView.adapter=adapter
            bookmarksRecyclerView.layoutManager=layoutManager


        }
    }
}