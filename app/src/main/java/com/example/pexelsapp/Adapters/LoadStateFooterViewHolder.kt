package com.example.pexelsapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FooterLoadStateItemBinding
import com.example.pexelsapp.databinding.HeaderLoadStateItemBinding

class LoadStateFooterViewHolder(
    binding : FooterLoadStateItemBinding
) : AbstractViewHolder<LoadState,FooterLoadStateItemBinding>(binding) {

    val progressBar = binding.linearProgressBar

    override fun bind(loadState: LoadState) {
        binding.apply {
            linearProgressBar.isVisible = loadState is LoadState.Loading
            retry.isVisible=loadState is LoadState.Error || loadState is LoadState.NotLoading
            textView.isVisible = loadState is LoadState.Error || loadState is LoadState.NotLoading
        }
    }
    companion object {
        fun create(parent: ViewGroup) : LoadStateFooterViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.footer_load_state_item, parent, false)
            val binding = FooterLoadStateItemBinding.bind(view)
            return LoadStateFooterViewHolder(binding)
        }
    }
}