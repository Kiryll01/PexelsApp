package com.example.pexelsapp.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.Dtos.PexelsSize
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.ImageItemBinding

private const val TAG="IMAGE_LIST_ADAPTER"
class ImageListAdapter : ListAdapter<PexelsPhotoDto,ImageListAdapter.ImageViewHolder>(DiffCallback){

    companion object DiffCallback : DiffUtil.ItemCallback<PexelsPhotoDto>(){
        override fun areItemsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem==newItem
        }

    }
    class ImageViewHolder(private val binding : ImageItemBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(photo : PexelsPhotoDto){
            binding.apply {
                val imgUri = photo.src[PexelsSize.MEDIUM.sizeName]
                    ?.let{it.toUri().buildUpon().scheme("https").build()}
                image.load(imgUri) {
                    placeholder(R.drawable.loading_img)
                    error(R.drawable.icon_download_error)
                }
                Log.d("TAG", "image is bind to the item")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }
}