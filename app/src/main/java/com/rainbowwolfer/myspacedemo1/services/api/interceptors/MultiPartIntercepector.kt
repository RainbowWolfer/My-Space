package com.rainbowwolfer.myspacedemo1.services.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class MultiPartIntercepector : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
			.newBuilder()
			.addHeader("Content-Type", "multipart/form-data")
			.addHeader("X-Platform", "Android")
			.build()
		return chain.proceed(request)
	}
}