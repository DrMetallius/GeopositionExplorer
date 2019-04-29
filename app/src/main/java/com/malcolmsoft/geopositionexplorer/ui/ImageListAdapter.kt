package com.malcolmsoft.geopositionexplorer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ImageListAdapter : ListAdapter<String, ImageItemHolder>(object : DiffUtil.ItemCallback<String>() {
	override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

	override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ImageItemHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))

	override fun onBindViewHolder(holder: ImageItemHolder, position: Int) {
		holder.title.text = getItem(position)
	}
}

class ImageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	val title: TextView = itemView.findViewById(android.R.id.text1)
}