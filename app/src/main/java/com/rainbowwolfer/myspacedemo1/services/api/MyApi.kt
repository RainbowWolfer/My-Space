package com.rainbowwolfer.myspacedemo1.services.api

import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostResult
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.api.NewUsername
import com.rainbowwolfer.myspacedemo1.models.api.SignUpInfo
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.models.enums.PostsLimit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MyApi {
	companion object {
		const val BASE_URL = "http://www.cqtest.top:4500"
	}
	
	@GET("user")
	suspend fun getUser(
		@Query("id") id: String
	): Response<User>
	
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
	
	@Multipart
	@POST("post")
	suspend fun post(
		@Part("publisher_id") id: RequestBody,
		@Part("content") content: RequestBody,
		@Part("post_visibility") postVisibility: RequestBody,
		@Part("reply_visibility") replyVisibility: RequestBody,
		@Part("tags") tags: RequestBody,
		@Part images: ArrayList<MultipartBody.Part>,
	): ResponseBody
	
	@GET("post")
	suspend fun getPosts(
		@Query("email") email: String,
		@Query("password") password: String,
		@Query("offset") offset: Int,
		@Query("posts_type") postsLimit: PostsLimit,
		@Query("seed") seed: Int,
		@Query("limit") limit: Int = 10,
	): Response<List<Post>>
	
	@GET("post/images")
	suspend fun getPostImage(
		@Query("id") id: String,
		@Query("index") index: Int,
	): ResponseBody
}