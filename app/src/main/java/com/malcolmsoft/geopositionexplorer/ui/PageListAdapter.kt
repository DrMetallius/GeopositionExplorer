package com.malcolmsoft.geopositionexplorer.ui

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malcolmsoft.geopositionexplorer.R
import com.malcolmsoft.geopositionexplorer.api.PageImages

class PageListAdapter(private val onItemClickListener: (PageImages) -> Unit) : ListAdapter<PageImages, PageItemHolder>(
	object : DiffUtil.ItemCallback<PageImages>() {
		override fun areItemsTheSame(oldItem: PageImages, newItem: PageImages) = oldItem.title == newItem.title

		override fun areContentsTheSame(oldItem: PageImages, newItem: PageImages) = oldItem == newItem
	}
) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PageItemHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false))

	override fun onBindViewHolder(holder: PageItemHolder, position: Int) {
		val pageImages = getItem(position)

		holder.itemView.setOnClickListener {
			onItemClickListener(getItem(holder.adapterPosition))
		}

		holder.title.text = SpannableString(pageImages.title).apply {
			setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
		}

		val imageCount = pageImages.images.size
		holder.imageInformation.text = holder.itemView.resources.getQuantityString(R.plurals.main_image_count, imageCount, imageCount)
	}
}

class PageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	val title: TextView = itemView.findViewById(android.R.id.text1)
	val imageInformation: TextView = itemView.findViewById(android.R.id.text2)
}