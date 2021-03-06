package com.rainbowwolfer.myspacedemo1.services.api

import com.rainbowwolfer.myspacedemo1.services.api.interceptors.MultiPartInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
	
	private val client = OkHttpClient.Builder().apply {
		addInterceptor(MultiPartInterceptor())
	}.build()
	
	private val retrofit_post_multipart by lazy {
		Retrofit.Builder()
			.baseUrl(MyApi.BASE_URL)
			.client(client)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
	
	private val retrofit_get by lazy {
		Retrofit.Builder()
			.baseUrl(MyApi.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
	
	val api_postMultipart: MyApi by lazy {
		retrofit_post_multipart.create(MyApi::class.java)
	}
	
	val api: MyApi by lazy {
		retrofit_get.create(MyApi::class.java)
	}
}