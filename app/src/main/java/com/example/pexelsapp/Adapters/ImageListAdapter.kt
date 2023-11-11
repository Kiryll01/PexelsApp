package com.example.pexelsapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.PexelsSize
import com.example.pexelsapp.R
import com.example.pexelsapp.Web.loadImageWithCallback
import com.example.pexelsapp.databinding.ImageItemBinding
import com.example.pexelsapp.ui.home.ImageLoadingListener


private const val TAG="IMAGE_LIST_ADAPTER"
class ImageListAdapter (private val onImageClickAction : (PexelsPhotoDto)->Unit,
    private val imageLoadingListener: ImageLoadingListener)
    : PagingDataAdapter<PexelsPhotoDto,ImageListAdapter.ImageViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<PexelsPhotoDto>() {
        override fun areItemsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PexelsPhotoDto, newItem: PexelsPhotoDto): Boolean {
            return oldItem == newItem
        }

    }

    class ImageViewHolder(binding: ImageItemBinding,private val onImageLoadedAction: () -> Unit) : AbstractViewHolder<PexelsPhotoDto, ImageItemBinding>(binding) {
        override fun bind(photo: PexelsPhotoDto) {
            binding.apply {
                photo.src[PexelsSize.LARGE.sizeName]?.let {
                    loadImageWithCallback(imgUri = it,image=binding.image,
                        onResourceFailCallback = {
                        onImageLoadedAction
                    })
                }
            }
        }
        fun setPlaceholder(){
            binding.image.setImageResource(R.drawable.image_placeholder)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val viewHolder = ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onImageLoadedAction = { imageLoadingListener.onImageLoaded() }
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            val item = getItem(position)
            if(item != null) onImageClickAction(item)
            else viewHolder.setPlaceholder()
        }
    return viewHolder
}

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item=  getItem(position)
        item?.let { holder.bind(it) }
    }
}