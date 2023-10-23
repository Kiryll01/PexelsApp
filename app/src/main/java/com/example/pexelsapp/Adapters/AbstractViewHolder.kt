package com.example.pexelsapp.Adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class AbstractViewHolder<T,VB: ViewBinding>(protected val binding : VB ) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: T)
}