package com.malcolmsoft.geopositionexplorer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.malcolmsoft.geopositionexplorer.BuildConfig

class PageActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		title = intent.getStringExtra(EXTRA_TITLE)

		val imageList = RecyclerView(this)
		setContentView(imageList)

		val adapter = ImageListAdapter()
		imageList.adapter = adapter
		imageList.layoutManager = LinearLayoutManager(this)

		adapter.submitList(intent.getStringArrayListExtra(EXTRA_IMAGES))
	}

	companion object {
		const val EXTRA_TITLE = "${BuildConfig.APPLICATION_ID}.extra.TITLE"
		const val EXTRA_IMAGES = "${BuildConfig.APPLICATION_ID}.extra.IMAGES"
	}
}