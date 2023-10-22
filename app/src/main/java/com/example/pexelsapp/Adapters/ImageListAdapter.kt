package com.example.pexelsapp.Adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.Dtos.PexelsSize
import com.example.pexelsapp.R
import com.example.pexelsapp.Web.loadImage
import com.example.pexelsapp.databinding.ImageItemBinding
import com.example.pexelsapp.ui.home.HomeFragmentArgs
import com.example.pexelsapp.ui.home.HomeFragmentDirections

private const val TAG="IMAGE_LIST_ADAPTER"
class ImageListAdapter(private val onImageClickAction : (PexelsPhotoDto)->Unit) : ListAdapter<PexelsPhotoDto,ImageListAdapter.ImageViewHolder>(DiffCallback){

    companion object DiffCallback : DiffUtil.ItemCallback<PexelsPhotoDto>(){
        override fun areItemsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem==newItem
        }

    }
    class ImageViewHolder(private val binding : ImageItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: PexelsPhotoDto) {
            binding.apply {
                photo.src[PexelsSize.MEDIUM.sizeName]?.let {
                    loadImage(it, image)
                }
                Log.d("TAG", "image is bind to the item")
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val viewHolder =  ImageViewHolder(
            ImageItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        )
        viewHolder.itemView.setOnClickListener{
            val position=viewHolder.bindingAdapterPosition
            onImageClickAction(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }
}