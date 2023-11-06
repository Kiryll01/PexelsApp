package com.example.pexelsapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.HeaderLoadStateItemBinding



class LoadStateHeaderViewHolder (
    private val binding : HeaderLoadStateItemBinding,
    private val retry : () ->Unit
) : RecyclerView.ViewHolder(binding.root){

    init {
        binding.retry.setOnClickListener{
            retry.invoke()
        }
    }

    fun bind(loadState : LoadState){
        if(loadState is LoadState.Error){
            binding.errorMsg.text=loadState.error.localizedMessage
        }
        binding.circleProgressBar.isVisible=loadState is LoadState.Loading
        binding.retry.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }
    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateHeaderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.header_load_state_item, parent, false)
            val binding = HeaderLoadStateItemBinding.bind(view)
            return LoadStateHeaderViewHolder(binding, retry)
        }
    }
}