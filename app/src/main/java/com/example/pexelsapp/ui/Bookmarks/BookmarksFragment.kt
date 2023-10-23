package com.example.pexelsapp.ui.Bookmarks

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.pexelsapp.Adapters.BookmarksListAdapter
import com.example.pexelsapp.Adapters.ImageListAdapter
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FragmentBookmarksBinding
import com.example.pexelsapp.databinding.FragmentHomeBinding

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentBookmarksBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = BookmarksListAdapter{
            val action = BookmarksFragmentDirections.actionBookmarksFragmentToDetailsFragment(it)
                view.findNavController().navigate(action)
        }

    }
}