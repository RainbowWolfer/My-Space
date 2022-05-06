package com.rainbowwolfer.myspacedemo1.services.api

import com.rainbowwolfer.myspacedemo1.services.api.interceptors.MultiPartIntercepector
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
	
	private val client = OkHttpClient.Builder().apply {
		addInterceptor(MultiPartIntercepector())
	}.build()
	
	private val retrofit_post_image by lazy {
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
	
	val api_postImage: MyApi by lazy {
		retrofit_post_image.create(MyApi::class.java)
	}
	
	val api: MyApi by lazy {
		retrofit_get.create(MyApi::class.java)
	}
}