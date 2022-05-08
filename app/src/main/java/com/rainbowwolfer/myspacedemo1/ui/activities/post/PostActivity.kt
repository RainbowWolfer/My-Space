package com.rainbowwolfer.myspacedemo1.ui.activities.post

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.activity.viewBinding
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import com.github.dhaval2404.imagepicker.ImagePickerActivity
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityPostBinding
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetTagInputBinding
import com.rainbowwolfer.myspacedemo1.databinding.LayoutPostImageViewBinding
import com.rainbowwolfer.myspacedemo1.models.PostResult
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.regex.Pattern


class PostActivity : AppCompatActivity() {
	companion object {
		private const val MAX_LINES = 15
		private const val MAX_CHARACTERS = 300
		
	}
	
	private val binding: ActivityPostBinding by viewBinding()
	private val viewModel: PostActivityViewModel by viewModels()
	
	private val imageSelectIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		val uri = result.data?.data ?: return@registerForActivityResult
		val iStream: InputStream? = contentResolver.openInputStream(uri)
		val bytes: ByteArray = EasyFunctions.getBytes(iStream!!)
		val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
		viewModel.addImage(Pair(bitmap, bytes))
	}
	
	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = "New Post"
		
		binding.postEditTextContent.hint = resources.getString(
			arrayListOf(
				R.string.post_text1,
				R.string.post_text2,
				R.string.post_text3,
				R.string.post_text4,
				R.string.post_text5,
			).random()
		)
		
		binding.postAutoTextVisibility.setOnItemClickListener { _, _, position, _ ->
			viewModel.postVisibility.value = PostVisibility.values()[position]
		}
		binding.postAutoTextReply.setOnItemClickListener { _, _, position, _ ->
			viewModel.replyVisiblity.value = PostVisibility.values()[position]
		}
		
		binding.postImageAvatar.setImageBitmap(
			if (User.avatar.value == null) {
				BitmapFactory.decodeResource(resources, R.drawable.default_avatar)
			} else {
				User.avatar.value
			}
		)
		
		viewModel.content.observe(this) {
			binding.postTextCount.text = "${it.length}/$MAX_CHARACTERS"
			binding.postTextCount.setTextColor(
				resources.getColor(
					if (it.length > MAX_CHARACTERS) {
						R.color.red
					} else {
						R.color.normal_text
					}, theme
				)
			)
		}
		
		binding.postEditTextContent.addTextChangedListener(object : TextWatcher {
			private var text: String? = null
			private var beforeCursorPosition = 0
			
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
				text = s.toString()
				beforeCursorPosition = start
			}
			
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			
			override fun afterTextChanged(s: Editable?) {
				val editText = binding.postEditTextContent
				editText.removeTextChangedListener(this)
				if (editText.lineCount > MAX_LINES) {
					editText.setText(text)
					editText.setSelection(beforeCursorPosition)
				}
				if (s.toString().length > MAX_CHARACTERS) {
					editText.setText(text)
					editText.setSelection(beforeCursorPosition)
				}
				editText.addTextChangedListener(this)
				viewModel.content.value = s?.toString() ?: ""
			}
		})
		
		viewModel.tags.observe(this) {
			updateTags(it)
		}
		
		viewModel.images.observe(this) {
			updateImages(it)
		}
		
		arrayListOf(
			binding.post11,
			binding.post12,
			binding.post13,
			binding.post21,
			binding.post22,
			binding.post23,
			binding.post31,
			binding.post32,
			binding.post33,
		).forEach {
			it.postImageViewButtonAdd.setOnClickListener {
				val bundle = Bundle().apply {
					putSerializable("extra.image_provider", ImageProvider.GALLERY)
					putStringArray(
						"extra.mime_types", arrayOf(
							"image/png",
							"image/jpg",
							"image/jpeg"
						)
					)
					putBoolean("extra.crop", true)
					putInt("extra.max_width", 1080)
					putInt("extra.max_height", 1080)
					putLong("extra.image_max_size", 1024 * 1024)
					putString("extra.save_directory", null)
				}
				val intent = Intent(this, ImagePickerActivity::class.java)
				intent.putExtras(bundle)
				imageSelectIntentLauncher.launch(intent)
			}
			it.postImageViewButtonClose.setOnClickListener { _ ->
				AlertDialog.Builder(this).apply {
					setTitle("Confirm")
					setMessage("Are you sure to remove this image?")
					setNegativeButton("No", null)
					setPositiveButton("Yes") { _, _ ->
						if (it.postImageViewImage.tag is Int) {
							viewModel.removeImage(it.postImageViewImage.tag as Int)
						}
					}
					show()
				}
			}
		}
	}
	
	override fun onResume() {
		super.onResume()
		binding.postAutoTextVisibility.setAdapter(ArrayAdapter(this, R.layout.dropdown_text_item, resources.getStringArray(R.array.post_visiblities)))
		binding.postAutoTextReply.setAdapter(ArrayAdapter(this, R.layout.dropdown_text_item, resources.getStringArray(R.array.reply_visiblities)))
	}
	
	private fun showDialog() {
		BottomSheetDialog(this, R.style.CustomizedBottomDialogStyle).apply {
			setOnShowListener {
				Handler(Looper.getMainLooper()).post {
					val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetDialog_root) as? FrameLayout
					bottomSheet?.let {
						BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
					}
				}
			}
			
			show()
			
			val tagInputBinding = BottomSheetTagInputBinding.inflate(LayoutInflater.from(this@PostActivity))
			setContentView(tagInputBinding.root)
			
			tagInputBinding.bottomSheetDialogButtonCheck.isEnabled = false
			tagInputBinding.bottomSheetDialogButtonCheck.isClickable = false
			tagInputBinding.bottomSheetDialogInputTag.requestFocus()
			
			tagInputBinding.bottomSheetDialogButtonBack.setOnClickListener {
				this.dismiss()
				this.hide()
			}
			
			tagInputBinding.bottomSheetDialogEditTextTag.doAfterTextChanged {
				//&*()_=+{}/.<>|\[\]~-
				tagInputBinding.bottomSheetDialogInputTag.error = when {
					arrayListOf('!', '@', '#', '$', '%', '^', '&', '*', '&', '(', ')', '_', '=', '+', '{', '}', '/', '.', '<', '>', '|', '\\', '[', ']', '~', '-').any { c -> it.toString().contains(c) } -> "No Special Character Allowed"
					TextUtils.isEmpty(it.toString()) -> "Tag cannot be empty"
					it.toString().length > 20 -> "Max length is 20"
					viewModel.hasTag(it.toString()) -> "Already exists"
					else -> null
				}
				(tagInputBinding.bottomSheetDialogInputTag.error == null).also { b ->
					tagInputBinding.bottomSheetDialogButtonCheck.isEnabled = b
					tagInputBinding.bottomSheetDialogButtonCheck.isClickable = b
				}
			}
			tagInputBinding.bottomSheetDialogButtonCheck.setOnClickListener {
				if (tagInputBinding.bottomSheetDialogInputTag.error != null) {
					return@setOnClickListener
				}
				
			}
		}
	}
	
	private fun updateTags(tags: ArrayList<String>) {
		binding.postChipsTags.removeAllViews()
		
		val add = Chip(this)
		add.chipIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_add_24)
		add.iconStartPadding = resources.getDimensionPixelSize(R.dimen.chip_add_padding).toFloat()
		binding.postChipsTags.addView(add)
		add.setOnClickListener {
			println("Add")
		}
		
		for (tag in tags) {
			val chip = Chip(this)
			chip.closeIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_close_24)
			binding.postChipsTags.addView(chip)
			chip.setOnCloseIconClickListener {
				println("Close")
			}
		}
	}
	
	@SuppressLint("SetTextI18n")
	private fun updateImages(array: Array<Pair<Bitmap, ByteArray>?>) {
		if (array.size < 9) {
			throw Exception("how could this be possible?")
		}
		
		binding.postTextImageCount.text = "${array.count { it != null }}/${array.size}"
		
		binding.postRow1.visibility = View.VISIBLE
		binding.postRow2.visibility = if (array.copyOfRange(2, 5).any { it != null }) {
			View.VISIBLE
		} else {
			View.GONE
		}
		binding.postRow3.visibility = if (array.copyOfRange(5, 8).any { it != null }) {
			View.VISIBLE
		} else {
			View.GONE
		}
		
		binding.post11.update(array, 0)
		binding.post12.update(array, 1)
		binding.post13.update(array, 2)
		binding.post21.update(array, 3)
		binding.post22.update(array, 4)
		binding.post23.update(array, 5)
		binding.post31.update(array, 6)
		binding.post32.update(array, 7)
		binding.post33.update(array, 8)
	}
	
	private fun LayoutPostImageViewBinding.update(array: Array<Pair<Bitmap, ByteArray>?>, index: Int) {
		if (index !in array.indices) {
			System.err.println("Index is out of bound")
			return
		}
		if (index == 0) {
			this.root.visibility = View.VISIBLE
		} else {
			this.root.visibility = if (array[index - 1] != null) View.VISIBLE else View.INVISIBLE
		}
		if (array[index] == null) {
			this.postImageViewButtonAdd.visibility = View.VISIBLE
			this.postImageViewLayoutImageRoot.visibility = View.GONE
			this.postImageViewImage.setImageBitmap(null)
			this.postImageViewImage.tag = null
		} else {
			this.postImageViewButtonAdd.visibility = View.GONE
			this.postImageViewLayoutImageRoot.visibility = View.VISIBLE
			this.postImageViewImage.setImageBitmap(array[index]!!.first)
			this.postImageViewImage.tag = index
		}
	}
	
	
	private fun complete(send: Boolean) {
		if (User.current == null) {
			//error
			return
		}
		CoroutineScope(Dispatchers.Main).launch {
			val dialog = LoadingDialog(this@PostActivity).apply {
				showDialog("Posting...")
			}
			withContext(Dispatchers.IO) {
				try {
					val post = PostResult(
						User.current!!.id,
						viewModel.getContent(),
						viewModel.getByteArrays(),
						viewModel.postVisibility.value!!,
						viewModel.replyVisiblity.value!!,
						arrayListOf("furry", "yiff")
					)
//					post.content.toRequestBody()
//					val body = bytes.toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, bytes.size)
//					val data = MultipartBody.Part.createFormData("file", "file", body)
					
					val multipartTypedOutput = arrayListOf<MultipartBody.Part>()
					
					for (index in post.images.indices) {
						if (post.images[index] == null) {
							continue
						}
						val image = post.images[index]!!.toRequestBody("multipart/form-data".toMediaType())
						multipartTypedOutput.add(MultipartBody.Part.createFormData("post_images", "$index", image))
					}
					
					val contentBody = post.content.toRequestBody("text/plain".toMediaType())
					val publisherIDBody = post.publisherID.toRequestBody("text/plain".toMediaType())
					val postVisibility = post.visibility.ordinal.toString().toRequestBody("text/plain".toMediaType())
					val replyVisibility = post.replyLimit.ordinal.toString().toRequestBody("text/plain".toMediaType())
					val tags = post.tags.joinToString("&#10;").toRequestBody("text/plain".toMediaType())
					
					RetrofitInstance.api_postMultipart.post(
						publisherIDBody, contentBody, postVisibility, replyVisibility, tags, multipartTypedOutput
					)
					
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
			}
		}
		//show success message and a back button
//		finish()
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.post_menu, menu)
		return true
	}
	
	
	override fun onNavigateUp(): Boolean {
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.post_draft -> {
//				complete(false)
				showDialog()
				true
			}
			R.id.post_send -> {
				complete(true)
				true
			}
			android.R.id.home -> {
				onBackPressed()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
		
	}
}