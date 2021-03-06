package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentHomeBinding
import com.rainbowwolfer.myspacedemo1.databinding.LayoutBottomModalPostLimitBinding
import com.rainbowwolfer.myspacedemo1.models.enums.PostsLimit
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MainListRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.activities.login.LoginActivity
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivityViewModel
import com.rainbowwolfer.myspacedemo1.ui.activities.post.PostActivity
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class HomeFragment : Fragment(R.layout.fragment_home) {
	companion object {
		var instance: HomeFragment? = null
		const val RELOAD_THRESHOLD = 3
		var updateScrollPosition: Boolean = false
		
		fun popupNotLoggedInHint(view: View? = null) {
			var v: View? = view
			if (v == null) {
				v = instance?.requireView()
			}
			if (v != null) {
				Snackbar.make(v, v.context.getString(R.string.you_have_not_signed_in), Snackbar.LENGTH_LONG).setAction(v.context.getString(R.string.sign_in)) {
					MainActivity.Instance?.loginIntentLauncher!!.launch(Intent(instance!!.requireContext(), LoginActivity::class.java))
				}.show()
			}
		}
	}
	
	init {
		instance = this
	}
	
	private val binding: FragmentHomeBinding by viewBinding()
	private val viewModel: MainActivityViewModel by activityViewModels()
	private val application = MySpaceApplication.instance
	
	private val listAdapter by lazy { MainListRecyclerViewAdapter(requireContext(), viewLifecycleOwner) }
	
	private var isLoading = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}
	
	override fun onDetach() {
		super.onDetach()
		updateScrollPosition = true
	}
	
	override fun onDestroy() {
		super.onDestroy()
		updateScrollPosition = false
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.mainRecyclerViewList.layoutManager = LinearLayoutManager(requireContext())
		binding.mainRecyclerViewList.adapter = listAdapter
		
		application.currentUser.observe(viewLifecycleOwner) {
			if (viewModel.posts.value?.size == 0) {
				updateList(true)
			}
			MainActivity.Instance?.drawerLayout?.closeDrawers()
		}
		
		viewModel.posts.observe(viewLifecycleOwner) {
			if (it.isEmpty()) {
				updateList(false)
				binding.mainLayoutNothing.visibility = View.VISIBLE
				binding.mainRecyclerViewList.visibility = View.GONE
			} else {
				binding.mainLayoutNothing.visibility = View.GONE
				binding.mainRecyclerViewList.visibility = View.VISIBLE
				listAdapter.setData(it)
			}
			
			if (viewModel.lastViewPosition.value != -1 && updateScrollPosition) {
				binding.mainRecyclerViewList.scrollToPosition(viewModel.lastViewPosition.value!!)
			}
		}
		
		viewModel.postsLimit.observe(viewLifecycleOwner) {
			updateMainButtonPostsLimit(it)
		}
		
		//only works for manually pull down
		binding.mainSwipeRefreshLayout.setOnRefreshListener {
			updateList(true)
		}
		
		binding.fabAdd.setOnClickListener {
			if (application.hasLoggedIn()) {
				MainActivity.Instance?.postIntentLauncher!!.launch(
					Intent(requireContext(), PostActivity::class.java)
				)
			} else {
				popupNotLoggedInHint()
			}
		}
		
		binding.mainRecyclerViewList.addOnScrollListener(
			object : RecyclerView.OnScrollListener() {
				override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
					super.onScrolled(recyclerView, dx, dy)
					val up = binding.mainRecyclerViewList.canScrollVertically(-1)
					binding.mainSwipeRefreshLayout.isEnabled = !up || binding.mainSwipeRefreshLayout.isRefreshing
					val layout = (binding.mainRecyclerViewList.layoutManager as LinearLayoutManager)
					val lastPosition = layout.findLastVisibleItemPosition()
					val firstPosition = layout.findFirstVisibleItemPosition()
					viewModel.lastViewPosition.value = firstPosition
					if (lastPosition >= listAdapter.itemCount - 2) {
						updateList(false, scrollToTop = false, showSwipeRefreshing = false)
					}
				}
			}
		)
		
		
		binding.mainButtonPostsLimit.setOnClickListener {
			BottomSheetDialog(requireContext(), R.style.CustomizedBottomDialogStyle2).apply {
				setCanceledOnTouchOutside(true)
				show()
				val modalBinding = LayoutBottomModalPostLimitBinding.inflate(LayoutInflater.from(requireContext()))
				setContentView(modalBinding.root)
				modalBinding.initialize(viewModel.postsLimit.value ?: PostsLimit.All)
				modalBinding.modalPostsLimitLayoutAll.setOnClickListener {
					modalBinding.checkAll()
					viewModel.postsLimit.value = PostsLimit.All
					delayToHideAndUpdate()
				}
				modalBinding.modalPostsLimitLayoutHot.setOnClickListener {
					modalBinding.checkHot()
					viewModel.postsLimit.value = PostsLimit.Hot
					delayToHideAndUpdate()
				}
				modalBinding.modalPostsLimitLayoutRandom.setOnClickListener {
					modalBinding.checkRandom()
					viewModel.postsLimit.value = PostsLimit.Random
					delayToHideAndUpdate()
				}
				modalBinding.modalPostsLimitLayoutFollowing.setOnClickListener {
					modalBinding.checkFollowing()
					viewModel.postsLimit.value = PostsLimit.Following
					delayToHideAndUpdate()
				}
			}
		}
		
		binding.mainEditSearch.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				performSearch(binding.mainEditSearch.text.toString().trim())
			}
			false
		}
	}
	
	private fun performSearch(content: String) {
		if (content.isBlank()) {
			return
		}
		viewModel.searchContent.value = content
		updateList(true)
		
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				delay(100)
				binding.mainEditSearch.clearFocus()
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
	}
	
	private fun BottomSheetDialog.delayToHideAndUpdate() {
		val dismissDelay = 50L
		viewModel.searchContent.value = ""
		binding.mainEditSearch.setText("")
		lifecycleScope.launch(Dispatchers.Main) {
			delay(dismissDelay)
			dismiss()
			hide()
			updateList(true)
		}
	}
	
	private fun updateList(refresh: Boolean, scrollToTop: Boolean = true, showSwipeRefreshing: Boolean = true) {
		if (isLoading) {
			return
		}
		isLoading = true
		var success = true
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				if (showSwipeRefreshing) {
					binding.mainSwipeRefreshLayout.isRefreshing = true
				}
				delay(200)//wait fot animation to pop up
				if (refresh) {
					viewModel.listOffset.value = 0
					viewModel.lastViewPosition.value = 0
					viewModel.randomSeed.value = Random.nextInt()
				}
				
				EasyFunctions.stackLoading(refresh, viewModel.posts, viewModel.listOffset) {
					if (viewModel.searchContent.value?.isBlank() == false) {//search
						RetrofitInstance.api.getPostsBySearch(
							email = application.getCurrentEmail(),
							password = application.getCurrentPassword(),
							search = viewModel.searchContent.value!!,
							offset = viewModel.listOffset.value!!
						)
					} else {
						RetrofitInstance.api.getPosts(
							email = application.currentUser.value?.email ?: "",
							password = application.currentUser.value?.password ?: "",
							offset = viewModel.listOffset.value!!,
							postsLimit = viewModel.postsLimit.value!!,
							seed = viewModel.randomSeed.value!!,
						)
					}
				}
			} catch (ex: Exception) {
				success = false
				Snackbar.make(binding.root, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).setAction(getString(R.string.dismiss)) {}.show()
				ex.printStackTrace()
				if (ex is ResponseException) {
					ex.printResponseException()
				}
			} finally {
				try {
					lifecycleScope.launch {
						try {
							delay(100)
							if (scrollToTop) {
								binding.mainRecyclerViewList.smoothScrollToPosition(0)
							}
						} catch (ex: Exception) {
						}
					}
					if (showSwipeRefreshing) {
						binding.mainSwipeRefreshLayout.isRefreshing = false//better get it sealed up
					}
					delay(100)
					binding.mainSwipeRefreshLayout.isEnabled = !binding.mainRecyclerViewList.canScrollVertically(-1)
					isLoading = false
					if (refresh && success) {
						Toast.makeText(requireContext(), getString(R.string.refresh_successful), Toast.LENGTH_SHORT).show()
					}
				} catch (ex: Exception) {
				}
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
				binding.mainTextPostsLimit.text = getString(R.string.all_posts)
			}
			PostsLimit.Hot -> {
				binding.mainImagePostsLimit.setImageResource(R.drawable.ic_baseline_whatshot_24)
				binding.mainTextPostsLimit.text = getString(R.string.hot)
			}
			PostsLimit.Random -> {
				binding.mainImagePostsLimit.setImageResource(R.drawable.ic_baseline_dice_100)
				binding.mainTextPostsLimit.text = getString(R.string.random)
			}
			PostsLimit.Following -> {
				binding.mainImagePostsLimit.setImageResource(R.drawable.ic_baseline_people_alt_24)
				binding.mainTextPostsLimit.text = getString(R.string.following)
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.hone_menu, menu)
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.item_refresh -> {
				updateList(true)
			}
			R.id.item_top -> {
				binding.mainRecyclerViewList.smoothScrollToPosition(0)
			}
			android.R.id.home -> {
				MainActivity.Instance?.drawerLayout?.openDrawer(GravityCompat.START)
			}
		}
		return true
	}
}