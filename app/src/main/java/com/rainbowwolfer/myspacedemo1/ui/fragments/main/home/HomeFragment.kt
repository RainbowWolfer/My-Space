package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentHomeBinding
import com.rainbowwolfer.myspacedemo1.databinding.LayoutBottomModalPostLimitBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.api.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.models.enums.PostsLimit
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MainListRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivityViewModel
import com.rainbowwolfer.myspacedemo1.ui.activities.post.PostActivity
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.getHttpResponse
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.EOFException

class HomeFragment : Fragment(R.layout.fragment_home) {
	private val binding: FragmentHomeBinding by viewBinding()
	private val viewModel: HomeFragmentViewModel by viewModels()
	private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
	private val application = MySpaceApplication.instance
	
	private val myAdapter by lazy { MainListRecyclerViewAdapter(requireContext()) }
	
	private var triedCount = 0
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

//	override fun onResume() {
//		super.onResume()
//	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.mainRecyvlerViewList.layoutManager = LinearLayoutManager(requireContext())
		binding.mainRecyvlerViewList.adapter = myAdapter
		
		mainActivityViewModel
		
		viewModel.posts.observe(viewLifecycleOwner) {
			if (it.isEmpty()) {
				binding.mainLayoutNothing.visibility = View.VISIBLE
				binding.mainRecyvlerViewList.visibility = View.GONE
				updateList()
			} else {
				binding.mainLayoutNothing.visibility = View.GONE
				binding.mainRecyvlerViewList.visibility = View.VISIBLE
			}
			myAdapter.setData(it)
		}
		
		viewModel.postsLimit.observe(viewLifecycleOwner) {
			updateMainButtonPostsLimit(it)
			when (it) {
				PostsLimit.All -> {}
				PostsLimit.Hot -> {}
				PostsLimit.Random -> {}
				PostsLimit.Following -> {}
				else -> {}
			}
		}
		
		//only works for manually pull down
		binding.mainSwipeRefreshLayout.setOnRefreshListener {
			updateList()
		}
		
		binding.fabAdd.setOnClickListener {
			if (application.hasLoggedIn()) {
				MainActivity.Instance?.postIntentLauncher!!.launch(
					Intent(requireContext(), PostActivity::class.java)
				)
			} else {
				Snackbar.make(view, "You have not signed in", Snackbar.LENGTH_LONG).setAction("Sign in") {
					MainActivity.Instance?.loginIntentLauncher!!.launch(Intent(requireContext(), LoginActivity::class.java))
				}.show()
			}
		}
		
		binding.mainRecyvlerViewList.addOnScrollListener(
			object : RecyclerView.OnScrollListener() {
				override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
					super.onScrolled(recyclerView, dx, dy)
					val up = binding.mainRecyvlerViewList.canScrollVertically(-1)
					binding.mainSwipeRefreshLayout.isEnabled = !up || binding.mainSwipeRefreshLayout.isRefreshing
				}
			}
		)
		
		binding.mainButtonPostsLimit.setOnClickListener {
			val dismissDelay = 50L
			BottomSheetDialog(requireContext(), R.style.CustomizedBottomDialogStyle2).apply {
				setCanceledOnTouchOutside(true)
				show()
				val modalBinding = LayoutBottomModalPostLimitBinding.inflate(LayoutInflater.from(requireContext()))
				setContentView(modalBinding.root)
				modalBinding.initialize(viewModel.postsLimit.value ?: PostsLimit.All)
				modalBinding.modalPostsLimitLayoutAll.setOnClickListener {
					modalBinding.checkAll()
					viewModel.postsLimit.value = PostsLimit.All
					CoroutineScope(Dispatchers.Main).launch {
						delay(dismissDelay)
						dismiss()
						hide()
					}
				}
				modalBinding.modalPostsLimitLayoutHot.setOnClickListener {
					modalBinding.checkHot()
					viewModel.postsLimit.value = PostsLimit.Hot
					CoroutineScope(Dispatchers.Main).launch {
						delay(dismissDelay)
						dismiss()
						hide()
					}
				}
				modalBinding.modalPostsLimitLayoutRandom.setOnClickListener {
					modalBinding.checkRandom()
					viewModel.postsLimit.value = PostsLimit.Random
					CoroutineScope(Dispatchers.Main).launch {
						delay(dismissDelay)
						dismiss()
						hide()
					}
				}
				modalBinding.modalPostsLimitLayoutFollowing.setOnClickListener {
					modalBinding.checkFollowing()
					viewModel.postsLimit.value = PostsLimit.Following
					CoroutineScope(Dispatchers.Main).launch {
						delay(dismissDelay)
						dismiss()
						hide()
					}
				}
			}
		}
	}
	
	private fun updateList() {
		if (!application.hasLoggedIn()) {
			binding.mainSwipeRefreshLayout.isRefreshing = false
			return
		}
		CoroutineScope(Dispatchers.Main).launch {
			try {
				binding.mainSwipeRefreshLayout.isRefreshing = true
				val response: Response<List<Post>>
				withContext(Dispatchers.IO) {
					response = RetrofitInstance.api.getPosts(
						application.currentUser.value!!.email,
						application.currentUser.value!!.password,
						viewModel.postsLimit.value!!,
					)
				}
				if (response.isSuccessful) {
					val list = response.body()
					println(list)
					viewModel.posts.value = list
				} else {
					throw Exception()
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
				if (ex is HttpException) {
					val go = ex.response()!!.getHttpResponse()
					println(go)
				} else if (ex is EOFException) {
					
					println("http json is wrong")
				}
			} finally {
				binding.mainSwipeRefreshLayout.isRefreshing = false//better get it sealed up
				delay(100)
				binding.mainSwipeRefreshLayout.isEnabled = !binding.mainRecyvlerViewList.canScrollVertically(-1)
			}
		}
	}
	
	private fun LayoutBottomModalPostLimitBinding.initialize(postsLimit: PostsLimit) {
		when (postsLimit) {
			PostsLimit.All -> this.checkAll()
			PostsLimit.Hot -> this.checkHot()
			PostsLimit.Random -> this.checkRandom()
			PostsLimit.Following -> this.checkFollowing()
		}
		this.modalPostsLimitLayoutFollowing.visibility =
			if (!application.hasLoggedIn()) {
				View.GONE
			} else {
				View.VISIBLE
			}
	}
	
	private fun LayoutBottomModalPostLimitBinding.checkAll() {
		this.modalPostsLimitImageAll.setImageResource(R.drawable.ic_baseline_widgets_24)
		this.modalPostsLimitImageHot.setImageResource(R.drawable.ic_outline_whatshot_24)
		this.modalPostsLimitImageRandom.setImageResource(R.drawable.ic_outline_dice_100)
		this.modalPostsLimitImageFollowing.setImageResource(R.drawable.ic_outline_people_alt_24)
		this.modalPostsLimitCheckAll.visibility = View.VISIBLE
		this.modalPostsLimitCheckHot.visibility = View.GONE
		this.modalPostsLimitCheckRandom.visibility = View.GONE
		this.modalPostsLimitCheckFollowing.visibility = View.GONE
		this.modalPostsLimitImageAll.imageTintList = resources.getColorStateList(R.color.normal_text, requireContext().theme)
		this.modalPostsLimitImageHot.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageRandom.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageFollowing.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitTextAll.setTextColor(resources.getColor(R.color.normal_text, requireContext().theme))
		this.modalPostsLimitTextHot.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextRandom.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextFollowing.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextAll.typeface = Typeface.DEFAULT_BOLD
		this.modalPostsLimitTextHot.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextRandom.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextFollowing.typeface = Typeface.DEFAULT
	}
	
	private fun LayoutBottomModalPostLimitBinding.checkHot() {
		this.modalPostsLimitImageAll.setImageResource(R.drawable.ic_outline_widgets_24)
		this.modalPostsLimitImageHot.setImageResource(R.drawable.ic_baseline_whatshot_24)
		this.modalPostsLimitImageRandom.setImageResource(R.drawable.ic_outline_dice_100)
		this.modalPostsLimitImageFollowing.setImageResource(R.drawable.ic_outline_people_alt_24)
		this.modalPostsLimitCheckAll.visibility = View.GONE
		this.modalPostsLimitCheckHot.visibility = View.VISIBLE
		this.modalPostsLimitCheckRandom.visibility = View.GONE
		this.modalPostsLimitCheckFollowing.visibility = View.GONE
		this.modalPostsLimitImageAll.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageHot.imageTintList = resources.getColorStateList(R.color.normal_text, requireContext().theme)
		this.modalPostsLimitImageRandom.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageFollowing.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitTextAll.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextHot.setTextColor(resources.getColor(R.color.normal_text, requireContext().theme))
		this.modalPostsLimitTextRandom.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextFollowing.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextAll.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextHot.typeface = Typeface.DEFAULT_BOLD
		this.modalPostsLimitTextRandom.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextFollowing.typeface = Typeface.DEFAULT
	}
	
	private fun LayoutBottomModalPostLimitBinding.checkRandom() {
		this.modalPostsLimitImageAll.setImageResource(R.drawable.ic_outline_widgets_24)
		this.modalPostsLimitImageHot.setImageResource(R.drawable.ic_outline_whatshot_24)
		this.modalPostsLimitImageRandom.setImageResource(R.drawable.ic_baseline_dice_100)
		this.modalPostsLimitImageFollowing.setImageResource(R.drawable.ic_outline_people_alt_24)
		this.modalPostsLimitCheckAll.visibility = View.GONE
		this.modalPostsLimitCheckHot.visibility = View.GONE
		this.modalPostsLimitCheckRandom.visibility = View.VISIBLE
		this.modalPostsLimitCheckFollowing.visibility = View.GONE
		this.modalPostsLimitImageAll.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageHot.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageRandom.imageTintList = resources.getColorStateList(R.color.normal_text, requireContext().theme)
		this.modalPostsLimitImageFollowing.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitTextAll.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextHot.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextRandom.setTextColor(resources.getColor(R.color.normal_text, requireContext().theme))
		this.modalPostsLimitTextFollowing.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextAll.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextHot.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextRandom.typeface = Typeface.DEFAULT_BOLD
		this.modalPostsLimitTextFollowing.typeface = Typeface.DEFAULT
	}
	
	private fun LayoutBottomModalPostLimitBinding.checkFollowing() {
		this.modalPostsLimitImageAll.setImageResource(R.drawable.ic_outline_widgets_24)
		this.modalPostsLimitImageHot.setImageResource(R.drawable.ic_outline_whatshot_24)
		this.modalPostsLimitImageRandom.setImageResource(R.drawable.ic_outline_dice_100)
		this.modalPostsLimitImageFollowing.setImageResource(R.drawable.ic_baseline_people_alt_24)
		this.modalPostsLimitCheckAll.visibility = View.GONE
		this.modalPostsLimitCheckHot.visibility = View.GONE
		this.modalPostsLimitCheckRandom.visibility = View.GONE
		this.modalPostsLimitCheckFollowing.visibility = View.VISIBLE
		this.modalPostsLimitImageAll.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageHot.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageRandom.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageFollowing.imageTintList = resources.getColorStateList(R.color.normal_text, requireContext().theme)
		this.modalPostsLimitTextAll.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextHot.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextRandom.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextFollowing.setTextColor(resources.getColor(R.color.normal_text, requireContext().theme))
		this.modalPostsLimitTextAll.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextHot.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextRandom.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextFollowing.typeface = Typeface.DEFAULT_BOLD
	}
	
	private fun updateMainButtonPostsLimit(postsLimit: PostsLimit) {
		when (postsLimit) {
			PostsLimit.All -> {
				binding.mainImagePostsLimit.setImageResource(R.drawable.ic_baseline_widgets_24)
				binding.mainTextPostsLimit.text = "All Posts"
			}
			PostsLimit.Hot -> {
				binding.mainImagePostsLimit.setImageResource(R.drawable.ic_baseline_whatshot_24)
				binding.mainTextPostsLimit.text = "Hot"
			}
			PostsLimit.Random -> {
				binding.mainImagePostsLimit.setImageResource(R.drawable.ic_baseline_dice_100)
				binding.mainTextPostsLimit.text = "Random"
			}
			PostsLimit.Following -> {
				binding.mainImagePostsLimit.setImageResource(R.drawable.ic_baseline_people_alt_24)
				binding.mainTextPostsLimit.text = "Following"
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.hone_menu, menu)
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == R.id.item_refresh) {
			//scroll to top
			updateList()
		} else if (item.itemId == R.id.item_top) {
			//scroll to top
		}
		if (item.itemId == android.R.id.home) {
			MainActivity.Instance?.drawerLayout?.openDrawer(GravityCompat.START)
			return true
		}
		return true
	}
}