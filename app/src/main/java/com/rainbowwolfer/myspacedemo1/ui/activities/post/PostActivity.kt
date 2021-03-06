package com.rainbowwolfer.myspacedemo1.ui.activities.post

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.viewbinding.library.activity.viewBinding
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePickerActivity
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityPostBinding
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetTagInputBinding
import com.rainbowwolfer.myspacedemo1.databinding.LayoutPostImageViewBinding
import com.rainbowwolfer.myspacedemo1.models.Draft
import com.rainbowwolfer.myspacedemo1.models.PostResult
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import com.rainbowwolfer.myspacedemo1.ui.views.SuccessBackDialog
import com.rainbowwolfer.myspacedemo1.util.DateTimeUtils.getDateTime
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.setAutoClearEditTextFocus
import com.rainbowwolfer.myspacedemo1.util.LocaleUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.DateTime
import java.io.FileNotFoundException
import java.io.InputStream


class PostActivity : AppCompatActivity() {
	companion object {
		const val ARGS_DRAFT = "draft"
		private const val MAX_LINES = 15
		private const val MAX_CHARACTERS = 300
	}
	
	private val binding: ActivityPostBinding by viewBinding()
	private val viewModel: PostActivityViewModel by viewModels()
	private val application = MySpaceApplication.instance
	
	private var draft: Draft? = null
	
	private val imageSelectIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		val uri = result.data?.data ?: return@registerForActivityResult
		val info = loadImageFromURI(uri)
		viewModel.addImage(info)
	}
	
	override fun attachBaseContext(newBase: Context?) {
		super.attachBaseContext(LocaleUtils.attachBaseContext(newBase))
	}
	
	private fun loadImageFromURI(uri: Uri): PostActivityViewModel.ImageInfo? {
		return try {
			val fileName = uri.pathSegments.last()
			val ext = fileName.substring(fileName.lastIndexOf("."))
			val iStream: InputStream? = contentResolver.openInputStream(uri)
			val bytes: ByteArray = EasyFunctions.getBytes(iStream!!)
			val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
			PostActivityViewModel.ImageInfo(uri, bitmap, bytes, ext)
		} catch (ex: FileNotFoundException) {
			ex.printStackTrace()
			null
		} catch (ex: NoSuchElementException) {
			ex.printStackTrace()
			null
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		draft = intent.getParcelableExtra(ARGS_DRAFT)
		if (draft != null) {
			binding.initializeWithDraft(draft!!)
		}
		
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = if (draft == null) getString(R.string.new_post) else getString(R.string.saved_draft)
		
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
			viewModel.replyVisibility.value = PostVisibility.values()[position]
		}
		
		application.currentAvatar.observe(this) {
			binding.postImageAvatar.setImageBitmap(it)
		}
		
		application.currentUser.observe(this) {
		
		}
		
		viewModel.content.observe(this) {
			@SuppressLint("SetTextI18n")
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
				binding.postEditTextContent.error = null
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
					setTitle(getString(R.string.confirm))
					setMessage(getString(R.string.are_you_sure_to_remove_this_image))
					setNegativeButton(getString(R.string.no), null)
					setPositiveButton(getString(R.string.yes)) { _, _ ->
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
		binding.postAutoTextVisibility.setAdapter(
			ArrayAdapter(
				this, R.layout.dropdown_text_item, getPostVisibilitiesArray()
			)
		)
		binding.postAutoTextReply.setAdapter(
			ArrayAdapter(
				this, R.layout.dropdown_text_item, getReplyVisibilitiesArray()
			)
		)
	}
	
	private fun getPostVisibilitiesArray(): Array<String> {
		return arrayOf(
			getString(R.string.post_visibilities_all),
			getString(R.string.post_visibilities_follower),
			getString(R.string.post_visibilities_none),
		)
	}
	
	private fun getReplyVisibilitiesArray(): Array<String> {
		return arrayOf(
			getString(R.string.reply_visibilities_all),
			getString(R.string.reply_visibilities_follower),
			getString(R.string.reply_visibilities_none),
		)
	}
	
	private fun ActivityPostBinding.initializeWithDraft(draft: Draft) {
		with(viewModel) {
			content.value = draft.textContent
			tags.value = ArrayList(draft.getTagsList())
			postVisibility.value = draft.postVisibility
			replyVisibility.value = draft.replyLimit
		}
		
		postEditTextContent.setText(draft.textContent)
		
		postAutoTextVisibility.setText(
			getPostVisibilitiesArray()[when (draft.postVisibility) {
				PostVisibility.All -> 0
				PostVisibility.Follower -> 1
				PostVisibility.None -> 2
			}]
		)
		
		postAutoTextReply.setText(
			getReplyVisibilitiesArray()[when (draft.replyLimit) {
				PostVisibility.All -> 0
				PostVisibility.Follower -> 1
				PostVisibility.None -> 2
			}]
		)
		
		for (uri in draft.getImagesUri()) {
			val info = loadImageFromURI(uri)
			viewModel.addImage(info)
		}
	}
	
	private fun showDialog() {
		BottomSheetDialog(this, R.style.CustomizedBottomDialogStyle).apply {
			setOnShowListener {
				Handler(Looper.getMainLooper()).post {
					val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetDialog_root) as? FrameLayout?
					bottomSheet?.let {
						BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
					}
				}
			}
			
			setCanceledOnTouchOutside(true)
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
				tagInputBinding.bottomSheetDialogInputTag.error = when {
					//as long as it is not a-z, A-z or 0-9 (or chinese character)
					arrayListOf('!', '@', '#', '$', '%', '^', '&', '*', '&', '(', ')', '_', '=', '+', '{', '}', '/', '.', '<', '>', '|', '\\', '[', ']', '~', '-', ' ').any { c -> it.toString().contains(c) } -> getString(R.string.no_special_character_allowed)
					it.toString().isBlank() -> getString(R.string.tag_cannot_be_empty)
					it.toString().length > 20 -> getString(R.string.max_length_is_20)
					viewModel.hasTag(it.toString()) -> getString(R.string.already_exists)
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
				viewModel.addTag(tagInputBinding.bottomSheetDialogEditTextTag.text.toString().trim())
				this.dismiss()
				this.hide()
			}
		}
	}
	
	private fun updateTags(tags: List<String>) {
		binding.postChipsTags.removeAllViews()
		
		for (tag in tags) {
			val chip = Chip(this)
			chip.isCloseIconVisible = true
			chip.closeIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_close_24)
			chip.text = tag
			binding.postChipsTags.addView(chip)
			chip.setOnCloseIconClickListener {
				val anim = AlphaAnimation(1f, 0f)
				anim.duration = 250
				anim.setAnimationListener(object : Animation.AnimationListener {
					override fun onAnimationRepeat(animation: Animation?) {}
					
					override fun onAnimationEnd(animation: Animation?) {
						viewModel.removeTag(chip.text.toString())
					}
					
					override fun onAnimationStart(animation: Animation?) {}
				})
				it.startAnimation(anim)
			}
		}
		
		val add = Chip(this)
		add.chipIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_add_24)
		add.iconStartPadding = resources.getDimensionPixelSize(R.dimen.chip_add_padding).toFloat()
		binding.postChipsTags.addView(add)
		add.setOnClickListener {
			showDialog()
		}
		
	}
	
	private fun updateImages(array: Array<PostActivityViewModel.ImageInfo?>) {
		if (array.size < 9) {
			throw Exception("how could this be possible?")
		}
		
		@SuppressLint("SetTextI18n")
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
	
	private fun LayoutPostImageViewBinding.update(array: Array<PostActivityViewModel.ImageInfo?>, index: Int) {
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
			this.postImageViewImage.setImageBitmap(array[index]!!.bitmap)
			this.postImageViewImage.tag = index
		}
	}
	
	private fun complete(send: Boolean) {
		//check content valid
		if (viewModel.getContent().length < 5) {
			binding.postEditTextContent.error = getString(R.string.least_5_characters_are_needed)
			binding.postEditTextContent.requestFocus()
			return
		}
		
		AlertDialog.Builder(this).apply {
			setTitle(getString(if (send) R.string.send else R.string.save_as_draft))
			setMessage(getString(if (send) R.string.send_post_confirm_text else R.string.save_draft_confirm_text))
			setNegativeButton(getString(R.string.no), null)
			setPositiveButton(getString(R.string.yes)) { _, _ ->
				if (send) {
					lifecycleScope.launch(Dispatchers.Main) {
						val dialog = LoadingDialog(this@PostActivity).apply {
							showDialog(getString(R.string.sending_post))
						}
						withContext(Dispatchers.IO) {
							try {
								val post = PostResult(
									application.currentUser.value!!.id,
									viewModel.getContent(),
									viewModel.getByteArrays(),
									viewModel.getExtensions(),
									viewModel.postVisibility.value!!,
									viewModel.replyVisibility.value!!,
									viewModel.tags.value!!
								)
								val multipartTypedOutput = arrayListOf<MultipartBody.Part>()
								
								for (index in post.images.indices) {
									if (post.images[index] == null) {
										continue
									}
									val image = post.images[index]!!.toRequestBody("multipart/form-data".toMediaType())
									multipartTypedOutput.add(MultipartBody.Part.createFormData("post_images", "$index${post.extensions[index]}", image))
								}
								
								val contentBody = post.content.toRequestBody("text/plain".toMediaType())
								val publisherIDBody = post.publisherID.toRequestBody("text/plain".toMediaType())
								val postVisibility = post.visibility.ordinal.toString().toRequestBody("text/plain".toMediaType())
								val replyVisibility = post.replyLimit.ordinal.toString().toRequestBody("text/plain".toMediaType())
								val tags = post.tags.joinToString("&#10;").toRequestBody("text/plain".toMediaType())
								
								val response = RetrofitInstance.api_postMultipart.post(
									publisherIDBody, contentBody, postVisibility, replyVisibility, tags, multipartTypedOutput
								)
								kotlin.runCatching {
									println(response.string())
								}
								
							} catch (ex: Exception) {
								ex.printStackTrace()
							}
						}
						dialog.hideDialog()
					}
				} else {//save to draft
					val new = Draft(
						draft?.id ?: 0,
						application.currentUser.value!!.id,
						DateTime.now().getDateTime(),
						viewModel.getContent(),
						viewModel.postVisibility.value!!,
						viewModel.replyVisibility.value!!,
						Draft.convertTags(viewModel.tags.value!!),
						Draft.convertImagesURI(viewModel.getNotNullURIs())
					)
					lifecycleScope.launch(Dispatchers.IO) {
						if (new.id == 0L) {
							application.roomRepository.insertDraft(new)
						} else {
							application.roomRepository.updateDraft(new)
						}
					}
				}
				
				SuccessBackDialog(this@PostActivity).also { dialog ->
					dialog.showDialog {
						dialog.hideDialog()
						finish()
					}
				}
			}
			create()
			show()
		}
		
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
				complete(false)
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
	
	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		this.setAutoClearEditTextFocus(event)
		return super.dispatchTouchEvent(event)
	}
}