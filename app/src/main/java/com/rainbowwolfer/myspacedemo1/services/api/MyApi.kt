package com.rainbowwolfer.myspacedemo1.services.api

import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.api.NewUsername
import com.rainbowwolfer.myspacedemo1.models.api.SignUpInfo
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MyApi {
	companion object {
		const val BASE_URL = "http://www.cqtest.top:4500"
	}
	
	@GET("login")
	suspend fun tryLogin(
		@Query("email") email: String,
		@Query("password") password: String,
	): Response<User>
	
	@Multipart
	@POST("upload/avatar")
	suspend fun updateAvatar(
		@Query("id") id: String,
		@Part body: MultipartBody.Part
	): ResponseBody
	
	@GET("user/avatar")
	suspend fun getAvatar(
		@Query("id") id: String,
	): ResponseBody
	
	@Headers("Content-Type: application/json")
	@POST("user/update/username")
	suspend fun changeUsername(
		@Body new: NewUsername
	): ResponseBody
	
	@Headers("Content-Type: application/json")
	@POST("validation/email/send")
	suspend fun sendValidationEmail(
		@Body info: SignUpInfo
	): ResponseBody
	
	@GET("user/checkExisting")
	suspend fun checkUsername(
		@Query("username") username: String
	): Response<Boolean>
	
	@GET("user/checkExisting")
	suspend fun checkEmail(
		@Query("email") email: String
	): Response<Boolean>
	
}