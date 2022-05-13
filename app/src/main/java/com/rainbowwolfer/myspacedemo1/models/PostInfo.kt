package com.rainbowwolfer.myspacedemo1.models

import android.graphics.Bitmap
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.getImages
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.updateImage
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication

class PostInfo(
	val id: String,
	var post: Post,
	val images: Array<Bitmap?> = arrayOfNulls(MySpaceApplication.IMAGES_COUNT_PER_POST)
) {
	override fun toString(): String {
		return "PostInfo(id: $id, images_count: ${images.filterNotNull().size}, post:$post)"
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
		
		@JvmStatic
		fun ArrayList<PostInfo>.updateImage(id: String, bitmap: Bitmap?, index: Int) {
			val found = this.findPostInfo(id) ?: return
			if (index !in found.images.indices) {
				return
			}
			found.images[index] = bitmap
		}
		
		@JvmStatic
		fun ArrayList<PostInfo>.getImage(id: String, index: Int): Bitmap? {
			val found = this.findPostInfo(id) ?: return null
			if (index !in found.images.indices) {
				return null
			}
			return found.images[index]
		}
		
		@JvmStatic
		fun ArrayList<PostInfo>.getImages(id: String): List<Bitmap> {
			val found = this.findPostInfo(id) ?: return emptyList()
			return found.images.filterNotNull()
		}
	}
}