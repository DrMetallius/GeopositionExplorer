package com.malcolmsoft.geopositionexplorer.util

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private fun <T> MutableLiveData<T>.setValueFromAnyThread(value: T?) {
	if (Looper.getMainLooper() == Looper.myLooper()) {
		this.value = value
	} else {
		postValue(value)
	}
}

class LiveDataPublishingProperty<T>(private var value: T, liveData: LiveData<T>) : ReadWriteProperty<Any, T> {
	private val mutableLiveData = liveData as MutableLiveData<T>

	init {
		mutableLiveData.setValueFromAnyThread(value)
	}

	override fun getValue(thisRef: Any, property: KProperty<*>): T = synchronized(thisRef) {
		value
	}

	override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = synchronized(thisRef) {
		this.value = value
		mutableLiveData.setValueFromAnyThread(value)
	}
}