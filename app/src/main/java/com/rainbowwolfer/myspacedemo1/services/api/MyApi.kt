package com.rainbowwolfer.myspacedemo1.services.api

import com.rainbowwolfer.myspacedemo1.models.*
import com.rainbowwolfer.myspacedemo1.models.api.*
import com.rainbowwolfer.myspacedemo1.models.enums.PostsLimit
import com.rainbowwolfer.myspacedemo1.models.flag.FlagMessage
import com.rainbowwolfer.myspacedemo1.models.records.RepostRecord
import com.rainbowwolfer.myspacedemo1.models.records.ScoreRecord
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
		@Query("id") id: String,
		@Query("self_id") selfID: String = "",
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
	
	@GET("post/id")
	suspend fun getPostByID(
		@Query("post_id") postID: String,
		@Query("email") email: String,
		@Query("password") password: String,
	): Response<Post>
	
	@GET("post")
	suspend fun getPosts(
		@Query("email") email: String,
		@Query("password") password: String,
		@Query("offset") offset: Int,
		@Query("posts_type") postsLimit: PostsLimit,
		@Query("seed") seed: Int,
		@Query("limit") limit: Int = 5,
	): Response<List<Post>>
	
	@GET("post/images")
	suspend fun getPostImage(
		@Query("id") id: String,
		@Query("index") index: Int,
	): ResponseBody
	
	@Headers("Content-Type: application/json")
	@POST("post/comment")
	suspend fun postComment(
		@Body comment: NewComment
	): Response<Comment>
	
	@GET("post/comments")
	suspend fun getPostComments(
		@Query("post_id") postID: String,
		@Query("offset") offset: Int,
		@Query("email") email: String = "",
		@Query("password") password: String = "",
		@Query("limit") limit: Int = 15,
	): Response<List<Comment>>
	
	@POST("post/vote")
	suspend fun postVote(
		@Body vote: NewPostVote
	): ResponseBody
	
	@POST("post/comment/vote")
	suspend fun commentVote(
		@Body vote: NewCommentVote
	): ResponseBody
	
	@POST("post/repost")
	suspend fun repost(
		@Body repost: NewRepost
	): ResponseBody
	
	@GET("post/repostRecords")
	suspend fun getRepostRecords(
		@Query("post_id") postID: String,
		@Query("offset") offset: Int,
	): Response<List<RepostRecord>>
	
	@GET("post/scoreRecords")
	suspend fun getScoreRecords(
		@Query("post_id") postID: String,
		@Query("offset") offset: Int,
	): Response<List<ScoreRecord>>
	
	@GET("collections")
	suspend fun getCollections(
		@Query("email") email: String,
		@Query("password") password: String,
		@Query("offset") offset: Int,
		@Query("limit") limit: Int = 15,
	): Response<List<UserCollection>>
	
	@POST("collections/add")
	suspend fun addCollection(
		@Body body: NewCollection,
	): ResponseBody
	
	@POST("collections/remove")
	suspend fun deleteCollection(
		@Body body: RemoveCollection,
	): ResponseBody
	
	@POST("user/follow")
	suspend fun postUserFollow(
		@Body body: NewUserFollow,
	): ResponseBody
	
	@GET("post/user")
	suspend fun getUserByUserID(
		@Query("email") email: String,
		@Query("password") password: String,
		@Query("target_id") targetID: String,
		@Query("offset") offset: Int,
		@Query("limit") limit: Int = 5,
	): Response<List<Post>>
	
	@GET("user/postsAndFollowersCount")
	suspend fun getPostsAndFollowersCount(
		@Query("user_id") user_id: String,
	): Response<List<Int>>
	
	@GET("user/getFollowers")
	suspend fun getUserFollowers(
		@Query("user_id") user_id: String,
		@Query("email") email: String,
		@Query("password") password: String,
	): Response<List<User>>
	
	@POST("post/delete")
	suspend fun postDelete(
		@Body body: DeletePost,
	): ResponseBody
	
	@GET("message/contacts")
	suspend fun getMessageContacts(
		@Query("email") email: String,
		@Query("password") password: String,
	): Response<List<MessageContact>>
	
	@GET("message/get")
	suspend fun getMessages(
		@Query("email") email: String,
		@Query("password") password: String,
		@Query("contact_id") contactID: String,
		@Query("offset") offset: Int,
		@Query("limit") limit: Int = 15,
	): Response<List<Message>>
	
	@POST("message/flagReceived")
	suspend fun flagReceived(
		@Body body: FlagMessage,
	): ResponseBody
	
	@GET("post/search")
	suspend fun getPostsBySearch(
		@Query("email") email: String,
		@Query("password") password: String,
		@Query("search") search: String,
		@Query("offset") offset: Int,
		@Query("limit") limit: Int = 5,
	): Response<List<Post>>
	
	@GET("myspace/version")
	suspend fun getAppVersion(): Response<GetAppVersion>
	
	@POST("user/sendResetPasswordEmail")
	suspend fun sendResetPasswordEmail(
		@Body body: SendResetPasswordEmail,
	): ResponseBody
	
}