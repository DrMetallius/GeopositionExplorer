package com.malcolmsoft.geopositionexplorer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.malcolmsoft.geopositionexplorer.R
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val REQUEST_PERMISSION = 1

class MainActivity : AppCompatActivity() {
	private val job = SupervisorJob()
	private val scope = CoroutineScope(job + Dispatchers.Main)

	private var permissionContinuation: CancellableContinuation<Boolean>? = null

	private val model by lazy { ViewModelProviders.of(this@MainActivity)[MainViewModel::class.java] }

	private suspend fun requestPermission(permission: String): Boolean {
		permissionContinuation?.cancel()
		return suspendCancellableCoroutine { cont ->
			permissionContinuation = cont
			requestPermissions(arrayOf(permission), REQUEST_PERMISSION)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val model = ViewModelProviders.of(this@MainActivity)[MainViewModel::class.java]

		val loadingProgressBar: ContentLoadingProgressBar = findViewById(R.id.mainLoadingProgressBar)

		val imageList: RecyclerView = findViewById(R.id.mainPageImagesList)
		val adapter = PageListAdapter { (title, images) ->
			startActivity(Intent(this, PageActivity::class.java).apply {
				putExtra(PageActivity.EXTRA_TITLE, title)
				putExtra(PageActivity.EXTRA_IMAGES, ArrayList(images))
			})
		}
		imageList.adapter = adapter
		imageList.layoutManager = LinearLayoutManager(this)

		model.searchRunningLiveData.observe(this, Observer { running ->
			if (running) {
				loadingProgressBar.show()
			} else {
				loadingProgressBar.hide()
			}

			imageList.isVisible = !running
		})

		model.searchResultsLiveData.observe(this, Observer { list ->
			adapter.submitList(list)
		})

		loadImages(false)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		if (requestCode != REQUEST_PERMISSION) return

		val continuation = permissionContinuation!!
		if (grantResults.isEmpty()) {
			continuation.cancel()
		} else {
			continuation.resume(grantResults.first() == PackageManager.PERMISSION_GRANTED)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		if (isFinishing) job.cancel()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.menuMainRefresh -> {
			loadImages(true)
			true
		}
		else -> false
	}

	private fun loadImages(refresh: Boolean) {
		scope.launch {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				val granted = requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
				if (!granted) return@launch
			}

			model.getPageImages(refresh)
		}
	}
}
