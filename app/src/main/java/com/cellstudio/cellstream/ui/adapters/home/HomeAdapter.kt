package com.cellstudio.cellstream.ui.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.databinding.ItemLayoutHomeBinding
import com.cellstudio.cellstream.ui.utilities.ImageUtils

class HomeAdapter(private var models: List<VideoResponse>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener: Listener?= null
    private var loadingData = false

    fun setModels(models: List<VideoResponse>) {
        this.models = models
        notifyDataSetChanged()
    }

    fun getLoading(): Boolean {
        return loadingData
    }

    fun setLoading(loading: Boolean) {
        if (!loadingData && loading) notifyItemInserted(models.size)
        else if (loadingData && !loading) notifyItemRemoved(models.size)

        this.loadingData = loading
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLayoutHomeBinding.inflate(LayoutInflater.from(parent.context))
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

    interface Listener {
        fun onItemClicked(model: VideoResponse)
    }

    class ItemViewHolder(binding: ItemLayoutHomeBinding, private val listener: Listener?) : RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.tvHomeTitle
        private val image: ImageView = binding.ivHomeImage
        fun bind(info: VideoResponse) {
            title.text = info.title
            itemView.setOnClickListener { listener?.onItemClicked(info) }

            ImageUtils.setImageUri(image, info.image)
        }
    }
}