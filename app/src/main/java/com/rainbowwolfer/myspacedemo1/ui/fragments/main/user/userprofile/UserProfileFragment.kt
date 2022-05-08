package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePickerActivity
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserProfileBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.api.NewUsername
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.callbacks.ActionCallBack
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.UserFragment
import com.rainbowwolfer.myspacedemo1.ui.views.ChangeUsernameDialog
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.InputStream

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
	companion object {
		const val ARGS_USER = "user"
		
		@JvmStatic
		fun newInstance(user: User) = UserProfileFragment().apply {
			arguments = Bundle().apply {
				putParcelable(ARGS_USER, user)
			}
		}
	}
	
	//	private val viewModel: UserProfileViewModel by activityViewModels()
	private lateinit var user: User
	
	private val binding: FragmentUserProfileBinding by viewBinding()
	private val isSelf: Boolean get() = UserFragment.Instance?.isSelf ?: false
	
	private val intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == Activity.RESULT_OK && result.data != null) {
			val uri = result.data!!.data!!
			val iStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
			val bytes: ByteArray = EasyFunctions.getBytes(iStream!!)
			CoroutineScope(Dispatchers.Main).launch {
				lateinit var dialog: LoadingDialog
				try {
					dialog = LoadingDialog(requireContext()).also {
						it.showDialog()
					}
					val body = bytes.toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, bytes.size)
					val data = MultipartBody.Part.createFormData("file", "file", body)
					val response = RetrofitInstance.api_postMultipart.updateAvatar(user.id, data)
					
					User.avatar.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
					val image = binding.userImageAvatar
					image.setImageBitmap(Bitmap.createScaledBitmap(User.avatar.value!!, image.width, image.height, false))
					
				} catch (ex: HttpException) {
					AlertDialog.Builder(requireContext()).apply {
						setCancelable(false)
						setTitle("Error")
						setMessage("Upload Avatar Failed")
						setNegativeButton("Back", null)
						show()
					}
				} finally {
					dialog.hideDialog()
				}
			}
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			user = it.getParcelable(ARGS_USER)!!
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.userTextUsername.text = user.username
		binding.userTextDescription.text = user.profileDescription
		binding.userTextEmail.text = user.email
		
		binding.userLayoutOthers.visibility = if (isSelf) View.GONE else View.VISIBLE
		binding.userLayoutSelf.visibility = if (isSelf) View.VISIBLE else View.GONE
		
		if (isSelf) {
			binding.userImageAvatar.setImageBitmap(User.avatar.value)
		} else {
			//load form api
		}
		
		binding.userButtonEditAvatar.setOnClickListener {
			//ImagePicker
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
				putFloat("extra.crop_x", 1f)
				putFloat("extra.crop_y", 1f)
				putInt("extra.max_width", 1080)
				putInt("extra.max_height", 1080)
				putLong("extra.image_max_size", 1024 * 1024)
				putString("extra.save_directory", null)
			}
			val intent = Intent(requireContext(), ImagePickerActivity::class.java)
			intent.putExtras(bundle)
			intentLauncher.launch(intent)
		}
		binding.userButtonEditUsername.setOnClickListener {
			var dialog: ChangeUsernameDialog? = null
			
			class StringActionCallBack : ActionCallBack<String> {
				override fun action(obj: String) {
					CoroutineScope(Dispatchers.IO).launch {
						try {
							val responde = RetrofitInstance.api.changeUsername(
								NewUsername(user.id, user.username, user.password, obj)
							)
							var success = false
							kotlin.runCatching {
								val str = responde.string().trim()
								val value = str.toInt()
								success = value >= 1
							}
							withContext(Dispatchers.Main) {
								dialog!!.hideDialog()
								if (success) {
									binding.userTextUsername.text = obj
									user.username = obj
								} else {
									AlertDialog.Builder(requireContext()).apply {
										setTitle("Update Username Failed")
										setMessage("Maybe try again later")
										setNegativeButton("Back", null)
										show()
									}
								}
							}
						} catch (ex: HttpException) {
							ex.printStackTrace()
							withContext(Dispatchers.Main) {
								dialog!!.hideDialog()
								AlertDialog.Builder(requireContext()).apply {
									setTitle("Update Username Failed")
									setMessage("Maybe try again later")
									setNegativeButton("Back", null)
									show()
								}
							}
						}
					}
				}
			}
			dialog = ChangeUsernameDialog(requireContext(), StringActionCallBack())
			dialog.showDialog(user.username)
		}
		
	}
}