package com.example.pexelsapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.databinding.BookmarksItemBinding


class BookmarksListAdapter(private val onImageClickAction : (PexelsPhotoDto)->Unit)
    :ListAdapter<PexelsPhotoDto,BookmarksListAdapter.BookmarksViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<PexelsPhotoDto>() {
        override fun areItemsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem == newItem
        }

    }

    class BookmarksViewHolder(binding: BookmarksItemBinding) :
        AbstractViewHolder<PexelsPhotoDto, BookmarksItemBinding>(binding) {
        override fun bind(photo: PexelsPhotoDto) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksViewHolder {
        val viewHolder = BookmarksViewHolder(
            BookmarksItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            onImageClickAction(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}