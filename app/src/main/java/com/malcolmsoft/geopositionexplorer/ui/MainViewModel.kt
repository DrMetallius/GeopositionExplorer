package com.malcolmsoft.geopositionexplorer.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.malcolmsoft.geopositionexplorer.api.PageImages
import com.malcolmsoft.geopositionexplorer.api.runSearch
import com.malcolmsoft.geopositionexplorer.util.LiveDataPublishingProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume

class MainViewModel(application: Application) : AndroidViewModel(application) {
	private val job = SupervisorJob()
	private val scope = CoroutineScope(job + Dispatchers.Main)

	private val locationManager: LocationManager = application.getSystemService(LocationManager::class.java)

	private var resultsRetrieved = false

	val searchResultsLiveData: LiveData<List<PageImages>> = MutableLiveData()
	private var searchResults: List<PageImages> by LiveDataPublishingProperty(emptyList(), searchResultsLiveData)

	val searchRunningLiveData: LiveData<Boolean> = MutableLiveData()
	private var searchRunning: Boolean by LiveDataPublishingProperty(false, searchRunningLiveData)

	@RequiresPermission(ACCESS_FINE_LOCATION)
	fun getPageImages(refresh: Boolean) {
		scope.launch {
			if (searchRunning || resultsRetrieved && !refresh) return@launch

			searchRunning = true
			try {
				val location = withTimeout(10_000) {
					suspendCancellableCoroutine<Location> { cont ->
						val listener = object : LocationListener {
							override fun onLocationChanged(location: Location) {
								cont.resume(location)
							}

							override fun onStatusChanged(provider: String, status: Int, extras: Bundle) = Unit

							override fun onProviderEnabled(provider: String) = Unit

							override fun onProviderDisabled(provider: String) = Unit
						}
						locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, Looper.getMainLooper())
					}
				}

				searchResults = withContext(Dispatchers.IO) {
					runSearch(location.latitude, location.longitude)
				}
				resultsRetrieved = true
			} catch (ex: Exception) {
				Log.e("MainViewModel", "Exception when getting page images", ex)
			} finally {
				searchRunning = false
			}
		}
	}

	override fun onCleared() {
		job.cancel()
	}
}