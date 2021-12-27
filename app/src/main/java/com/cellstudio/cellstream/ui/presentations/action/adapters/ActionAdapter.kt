package com.cellstudio.cellstream.ui.presentations.action.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.databinding.ItemActionBinding
import com.cellstudio.cellstream.ui.presentations.action.models.ActionModel

class ActionAdapter(var models: List<ActionModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var listener: Listener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.bind(models[position])
            }
        }
    }

    class ItemViewHolder(binding: ItemActionBinding, private val listener: Listener?) : RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.tvActionName
        fun bind(info: ActionModel) {
            title.text = info.title
            itemView.setOnClickListener { listener?.onClick(info) }
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }


    interface Listener {
        fun onClick(model: ActionModel)
    }
}