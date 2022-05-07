package com.cellstudio.cellstream.ui.adapters.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.databinding.ItemLayoutEpisodeBinding

class EpisodeAdapter(private var models:List<EpisodeResponse>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var listener: Listener?= null

    interface Listener {
        fun onClick(model: EpisodeResponse)
    }

    fun refreshData(models: List<EpisodeResponse>) {
        this.models = models
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLayoutEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.bind(models[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    class ItemViewHolder(binding: ItemLayoutEpisodeBinding, private val listener: Listener?) : RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.mbEpisode
        fun bind(info: EpisodeResponse) {
            title.text = info.title
            itemView.setOnClickListener { listener?.onClick(info) }
        }
    }
}