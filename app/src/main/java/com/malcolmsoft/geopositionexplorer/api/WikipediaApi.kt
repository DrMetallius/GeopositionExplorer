package com.malcolmsoft.geopositionexplorer.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val client by lazy(::OkHttpClient)

private val BASE_URL = HttpUrl.parse("https://en.wikipedia.org/w/api.php")!!

private const val SEARCH_RADIUS = 5_000 // In meters
private const val SEARCH_LIMIT = 50

private suspend fun Request.callAndSuspend(): Response = suspendCancellableCoroutine { cont ->
	client.newCall(this).enqueue(object : Callback {
		override fun onFailure(call: Call, e: IOException) {
			cont.resumeWithException(e)
		}

		override fun onResponse(call: Call, response: Response) {
			cont.resume(response)
		}
	})
}

suspend fun runSearch(latitude: Double, longitude: Double): List<PageImages> {
	val url = BASE_URL.newBuilder()
		.addQueryParameter("action", "query")
		.addQueryParameter("prop", "images")
		.addQueryParameter("imlimit", "max")
		.addQueryParameter("generator", "geosearch")
		.addQueryParameter("ggscoord", String.format(Locale.ENGLISH, "%.7f|%.7f", latitude, longitude))
		.addQueryParameter("ggsradius", SEARCH_RADIUS.toString())
		.addQueryParameter("ggslimit", SEARCH_LIMIT.toString())
		.addQueryParameter("format", "json")
		.build()

	var nextContinueBlock: JsonObject? = null

	val results = mutableListOf<PageImages>()
	do {
		val urlWithContinueParameters = nextContinueBlock?.let {
			val builder = url.newBuilder()
			for ((key, value) in it.entrySet()) {
				builder.addQueryParameter(key, value.asString)
			}
			builder.build()
		} ?: url

		val request = Request.Builder()
			.url(urlWithContinueParameters)
			.get()
			.build()

		val response = request.callAndSuspend()
		val body = response.body() ?: throw EmptyResponseBodyException()

		val responseObject = Gson().fromJson(body.string(), JsonObject::class.java)

		val pageCollection = responseObject["query"].asJsonObject["pages"].asJsonObject
		for ((_, page) in pageCollection.entrySet()) {
			val imageArray = page.asJsonObject["images"]?.asJsonArray ?: continue

			val title = page.asJsonObject["title"].asString
			val images = imageArray.map { it.asJsonObject["title"].asString }
			results.add(PageImages(title, images))
		}

		nextContinueBlock = responseObject["continue"]?.asJsonObject
	} while (nextContinueBlock != null)

	return results
}

class EmptyResponseBodyException : Exception()