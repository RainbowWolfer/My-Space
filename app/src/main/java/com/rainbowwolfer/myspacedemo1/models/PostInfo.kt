package com.rainbowwolfer.myspacedemo1.models

import android.graphics.Bitmap

class PostInfo(
	val id: String,
	var post: Post,
) {
	var originPost: Post? = null
	val images: Array<BitmapLoader>
	
	init {
		val list = arrayListOf<BitmapLoader>()
		for (index in 0 until post.imagesCount) {
			list.add(BitmapLoader(post.id, index))
		}
		images = list.toTypedArray()
	}
	
	override fun toString(): String {
		return "PostInfo(id: $id, images_count: ${images.size}, post:$post)"
	}
	
	companion object {
		@JvmStatic
		fun ArrayList<PostInfo>.findPostInfo(id: String): PostInfo? {
			return this.find { it.id == id }
		}
		
		@JvmStatic
		fun ArrayList<PostInfo>.addPost(post: Post) {
			val found = this.findPostInfo(post.id)
			if (found != null) {
				found.post = post
			} else {
				this.add(PostInfo(post.id, post))
			}
		}

//		@JvmStatic
//		fun ArrayList<PostInfo>.updateImage(id: String, bitmap: Bitmap?, index: Int) {
//			val found = this.findPostInfo(id) ?: return
//			if (index !in found.images.indices) {
//				return
//			}
//			found.images[index]?. = bitmap
//		}
		
		@JvmStatic
		fun ArrayList<PostInfo>.getImage(id: String, index: Int): BitmapLoader? {
			val found = this.findPostInfo(id) ?: return null
			if (index !in found.images.indices) {
				return null
			}
			return found.images[index]
		}
		
		@JvmStatic
		fun ArrayList<PostInfo>.getImages(id: String): List<Bitmap> {
			val found = this.findPostInfo(id) ?: return emptyList()
			return found.images.mapNotNull {
				it.bitmap.value
			}
		}
	}
}