package com.example.pexelsapp.ui.recyclerview

import android.animation.ObjectAnimator
import android.util.Log
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
private const val TAG="LOAD_STATE_ADAPTER"
class FooterLoadStateAdapter(private val retry: suspend () -> Unit) : LoadStateAdapter<LoadStateFooterViewHolder>(){
    override fun onBindViewHolder(holder: LoadStateFooterViewHolder, loadState: LoadState) {
        holder.bind(loadState)
        val animator = ObjectAnimator.ofInt(holder.progressBar, "progress", 0, 100)
        animator.duration = 2000
        animator.interpolator = LinearInterpolator()
        animator.start()
        Log.d(TAG, " footer holder is bind")
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateFooterViewHolder {
        return LoadStateFooterViewHolder.create(parent, retry)
    }
}