package com.example.storyapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.model.story.ListStoryItem
import com.example.storyapp.databinding.ListStoryBinding

class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var listener: OnItemClickListener

    fun onClick(click: OnItemClickListener){
        this.listener = click
    }

    inner class ViewHolder(private val binding: ListStoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ListStoryItem,context: Context){
            Glide.with(context)
                .load(item.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = item.name
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data,holder.itemView.context)
            holder.itemView.setOnClickListener{listener.onClick(data.id)}
        }
    }

    interface OnItemClickListener{
        fun onClick(id: String?)
    }

}