package com.example.pexelsapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.Adapters.FooterLoadStateAdapter
import com.example.pexelsapp.Adapters.ImageListAdapter
import com.example.pexelsapp.Adapters.HeaderLoadStateAdapter
import com.example.pexelsapp.PexelsApplication
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FragmentHomeBinding
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch

private const val TAG="HOME_FRAGMENT"
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>{
        HomeViewModelFactory((activity?.application as PexelsApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.collectionsScrollView.root.visibility=View.GONE

        setQueryListener()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setErrorLayout()

        setScrollView()

        val adapter = getAdapter(view)

        setCollections()

        binding.setRecyclerView(adapter)

        lifecycleScope.launch {
            viewModel.photosFlow.collect{
                adapter.submitData(it)
            }

        }
    }

    override fun onPause() {
        super.onPause()
        binding.collectionsScrollView.root.visibility=View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setQueryListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //            private var job : Job?=null

            override fun onQueryTextChange(newText: String?): Boolean {
                //                job?.cancel()
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) Toast.makeText(
                    requireContext(),
                    "discover something new!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.setQuery(query!!)
                //                viewModel.setQueryParam(query!!)
                //              job = viewModel.refreshPhotos(query?:" ")

                return true
            }
        })
    }
    private fun FragmentHomeBinding.setRecyclerView(adapter: ImageListAdapter) {
        imagesRecyclerView.adapter = adapter
        imagesRecyclerView.layoutManager =
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
    }

    private fun setCollections() {
        var previousCheckedId1 = -1
        val radioGroup = binding.collectionsScrollView.radioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (previousCheckedId1 != -1) {
                val previousButton = binding.root.findViewById<MaterialRadioButton>(previousCheckedId1)
                previousButton.setTextColor(resources.getColor(R.color.black))
                previousButton.setBackgroundResource(R.drawable.grey_round_rectangle)
            }
            val currentButton = binding.root.findViewById<MaterialRadioButton>(checkedId)
            currentButton.setTextColor(resources.getColor(R.color.white))
            currentButton.setBackgroundResource(R.drawable.red_round_rectangle)
            //                viewModel.refreshPhotos(currentButton.text.toString())
            viewModel.setQuery(currentButton.text.toString())
            previousCheckedId1 = checkedId
        }
    }

    private fun getAdapter(view: View): ImageListAdapter {
        val adapter = ImageListAdapter {
            val action = HomeFragmentDirections.actionNavigationHomeToDetailsFragment(it)
            view.findNavController().navigate(action)
        }
        adapter.withLoadStateHeaderAndFooter(
            header = HeaderLoadStateAdapter { viewModel.setQuery(viewModel.searchQuery.replayCache.last()) },
            footer = FooterLoadStateAdapter()
        )
        return adapter
    }

    private fun setScrollView() {
        binding.collectionsScrollView.apply {

            viewModel.collections.observe(viewLifecycleOwner) {
                Log.d(TAG, "$it")
                if (it.isNotEmpty() && it.size > 6) {
                    radioButton1.text = it[0]
                    radioButton2.text = it[1]
                    radioButton3.text = it[2]
                    radioButton4.text = it[3]
                    radioButton5.text = it[4]
                    radioButton6.text = it[5]
                    radioButton7.text = it[6]
                }
                root.visibility = View.VISIBLE
            }
        }
    }

    private fun setErrorLayout() {
        binding.networkErrorLayout.root.apply {
            viewModel.launchException.observe(viewLifecycleOwner) { info ->
                if (info.launchException) {
                    visibility = View.VISIBLE
                    binding.networkErrorLayout.tryAgain.setOnClickListener {
                        viewModel.refreshPhotos(info.searchWord)
                    }
                } else {
                    if (visibility == View.VISIBLE)
                        visibility = View.GONE
                }
            }
        }
    }
}