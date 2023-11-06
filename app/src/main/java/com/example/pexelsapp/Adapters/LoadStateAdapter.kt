package com.example.pexelsapp.Adapters

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class LoadStateAdapter(
    private val retry : () -> Unit
) : LoadStateAdapter<LoadStateHeaderViewHolder>(){
    override fun onBindViewHolder(holder: LoadStateHeaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateHeaderViewHolder {
        return LoadStateHeaderViewHolder.create(parent, retry)
    }

}