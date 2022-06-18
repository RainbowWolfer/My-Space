package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePickerActivity
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserProfileBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.api.NewUserFollow
import com.rainbowwolfer.myspacedemo1.models.api.NewUsername
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.callbacks.ActionCallBack
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.UserFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.viewmodels.UserFragmentViewModel
import com.rainbowwolfer.myspacedemo1.ui.views.ChangeUsernameDialog
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getHttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.InputStream

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
	companion object {
		private const val ARGS_USER_ID = "user_id"
		
		@JvmStatic
		fun newInstance(user_id: String) = UserProfileFragment().apply {
			arguments = Bundle().apply {
				putString(ARGS_USER_ID, user_id)
			}
		}
	}
	
	private val viewModel: UserFragmentViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	private val application = MySpaceApplication.instance
	
	private lateinit var userID: String
//	private var user: User? = null
	
	private val binding: FragmentUserProfileBinding by viewBinding()
	private val isSelf: Boolean
		get() {
			if (userID == application.currentUser.value?.id) {
				return true
			}
			return UserFragment.instance?.isSelf ?: false
		}
	
	private val intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == Activity.RESULT_OK && result.data != null) {
			val uri = result.data!!.data!!
			val iStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
			val bytes: ByteArray = EasyFunctions.getBytes(iStream!!)
			lifecycleScope.launch(Dispatchers.Main) {
				lateinit var dialog: LoadingDialog
				try {
					dialog = LoadingDialog(requireContext()).apply {
						showDialog()
					}
					val body = bytes.toRequestBody("multipart/form-data".toMediaType(), 0, bytes.size)
					val data = MultipartBody.Part.createFormData("file", "file", body)
					RetrofitInstance.api_postMultipart.updateAvatar(userID, data)
					application.currentAvatar.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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
			userID = it.getString(ARGS_USER_ID)!!
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.initializeLoadingState()
		binding.userLayoutOthers.visibility = if (isSelf) View.GONE else View.VISIBLE
		binding.userLayoutSelf.visibility = if (isSelf) View.VISIBLE else View.GONE
		
		if (isSelf) {
			application.currentAvatar.observe(viewLifecycleOwner) {
				binding.userImageAvatar.setImageBitmap(it)
			}
			application.currentUser.observe(viewLifecycleOwner) {
				if (it != null) {
					binding.updateUserInfo(it)
				} else {
					System.err.println("it cannot be this way")
				}
			}
		} else {
			val found = application.usersPool.findUserInfo(userID)
			if (found?.user != null) {
				viewModel.userInfo.value = found
			} else {
				application.findOrGetUserInfo(userID, {
					viewModel.userInfo.value = application.usersPool.findUserInfo(userID)
				}, {
					viewModel.userInfo.value = application.usersPool.findUserInfo(userID)
				})
			}
			
			viewModel.userInfo.observe(viewLifecycleOwner) {
				if (it?.user == null) {
					return@observe
				}
				
				updateFollowState(it.user!!.isFollowing)
				binding.updateUserInfo(it.user!!)
				binding.userImageAvatar.setImageBitmap(it.avatar)
			}
		}
		
		if (isSelf) {
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
				if (application.currentUser.value == null) {
					return@setOnClickListener
				}
				val user = application.currentUser.value!!
				var dialog: ChangeUsernameDialog? = null
				
				class StringActionCallBack : ActionCallBack<String> {
					override fun action(obj: String) {
						lifecycleScope.launch(Dispatchers.IO) {
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
										throw Exception()
									}
								}
							} catch (ex: HttpException) {
								ex.printStackTrace()
								val errorMessage = when (ex.response()?.getHttpResponse()?.errorCode) {
									1 -> "Username already exists"
									else -> "Maybe try again later"
								}
								withContext(Dispatchers.Main) {
									dialog!!.hideDialog()
									AlertDialog.Builder(requireContext()).apply {
										setTitle("Update Username Failed")
										setMessage(errorMessage)
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
		} else {// not self
			binding.userFollowButton.setOnClickListener {
				val user = viewModel.userInfo.value?.user ?: return@setOnClickListener
				lifecycleScope.launch(Dispatchers.Main) {
					try {
						updateFollowState(null)
						RetrofitInstance.api.postUserFollow(
							NewUserFollow(
								email = application.currentUser.value?.email ?: "",
								password = application.currentUser.value?.password ?: "",
								targetID = user.id,
								cancel = user.isFollowing,
							)
						)
					} catch (ex: Exception) {
						ex.printStackTrace()
					} finally {
						kotlin.runCatching {
							user.isFollowing = !user.isFollowing
							updateFollowState(user.isFollowing)
						}
					}
				}
			}
			
			binding.userButtonMessage.setOnClickListener {
			
			}
		}
	}
	
	private fun updateFollowState(following: Boolean?) {
		when (following) {
			true -> {
				binding.userFollowButton.isEnabled = true
				binding.userFollowButton.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_24, requireContext().theme)
				binding.userFollowButton.text = getString(R.string.Following)
			}
			false -> {
				binding.userFollowButton.isEnabled = true
				binding.userFollowButton.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_border_24, requireContext().theme)
				binding.userFollowButton.text = getString(R.string.Follow)
			}
			else -> {//loading
				binding.userFollowButton.isEnabled = false
				binding.userFollowButton.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_more_horiz_24, requireContext().theme)
				binding.userFollowButton.text = getString(R.string.Loading)
			}
		}
	}
	
	
	private fun FragmentUserProfileBinding.updateUserInfo(user: User) {
		this.userTextUsername.text = user.username
		this.userTextDescription.text = user.profileDescription
		this.userTextEmail.text = user.email
		
		this.userButtonMessage.isEnabled = true
		this.userFollowButton.isEnabled = true
		this.userButtonEditAvatar.isEnabled = true
		this.userButtonEditUsername.isEnabled = true
		
		(requireActivity() as AppCompatActivity).supportActionBar?.title = "User ${user.username}"
	}
	
	private fun FragmentUserProfileBinding.initializeLoadingState() {
		this.userImageAvatar.setImageDrawable(null)
		this.userTextUsername.text = "..."
		this.userTextEmail.text = "..."
		this.userTextDescription.text = "..."
		
		this.userButtonMessage.isEnabled = false
		this.userFollowButton.isEnabled = false
		this.userButtonEditAvatar.isEnabled = false
		this.userButtonEditUsername.isEnabled = false
	}
}