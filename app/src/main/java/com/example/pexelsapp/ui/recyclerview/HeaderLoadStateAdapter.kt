package com.example.pexelsapp.ui.recyclerview

import android.util.Log
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

private const val TAG="LOAD_STATE_ADAPTER"
class HeaderLoadStateAdapter(
    private val retry : () -> Unit,
) : LoadStateAdapter<LoadStateHeaderViewHolder>(){
    override fun onBindViewHolder(holder: LoadStateHeaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
        Log.d(TAG, "header holder is bind")
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateHeaderViewHolder {
        return LoadStateHeaderViewHolder.create(parent, retry)
    }
}