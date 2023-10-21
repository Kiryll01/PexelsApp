package com.example.pexelsapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.SearchRequestItemBinding

private const val TAG="SEARCH_KEYWORD_ADAPTER"
class SearchKeyWordsAdapter(private val collections : List<String>) : RecyclerView.Adapter<SearchKeyWordsAdapter.CollectionsViewHolder>(){
    class CollectionsViewHolder(private val binding : SearchRequestItemBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(collectionName : String){
            binding.collectionName.text=collectionName
            // Todo : set search
            binding.collectionName.apply { setOnClickListener{
               setBackgroundResource( R.drawable.red_round_rectangle)
            }
        }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        return CollectionsViewHolder(SearchRequestItemBinding.inflate( LayoutInflater.from(parent.context)))
    }

    override fun getItemCount()= collections.size

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        holder.bind(collections[position])
    }

}