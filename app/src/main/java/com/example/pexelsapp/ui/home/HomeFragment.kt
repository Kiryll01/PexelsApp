package com.example.pexelsapp.ui.home

import android.location.GnssAntennaInfo.Listener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.Adapters.ImageListAdapter
import com.example.pexelsapp.R
import com.example.pexelsapp.garbage.SearchKeyWordsAdapter
import com.example.pexelsapp.databinding.FragmentHomeBinding
import com.google.android.material.radiobutton.MaterialRadioButton

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

    //TODO : reformat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ImageListAdapter()
        var previousCheckedId = -1
        binding.apply {
            val radioGroup = binding.collectionsScrollView.radioGroup

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                if (previousCheckedId != -1) {
                    val previousButton = binding.root.findViewById<MaterialRadioButton>(previousCheckedId)
                    previousButton.setTextColor(resources.getColor(R.color.black))
                    previousButton.setBackgroundResource(R.drawable.grey_round_rectangle)
                }
                val currentButton = binding.root.findViewById<MaterialRadioButton>(checkedId)
                currentButton.setTextColor(resources.getColor(R.color.white))
                currentButton.setBackgroundResource(R.drawable.red_round_rectangle)
                previousCheckedId = checkedId
            }
            imagesRecyclerView.adapter = adapter
            imagesRecyclerView.layoutManager =
                StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
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