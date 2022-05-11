package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
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
import com.rainbowwolfer.myspacedemo1.models.enums.PostsLimit
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MainListRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity
import com.rainbowwolfer.myspacedemo1.ui.activities.post.PostActivity
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {
	private val binding: FragmentHomeBinding by viewBinding()
	private val viewModel: HomeFragmentViewModel by viewModels()
	
	private val myAdapter by lazy { MainListRecyclerViewAdapter(requireContext()) }
	
	private var overallYScroll = 0
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.fabAdd.setOnClickListener {
			if (User.current == null) {
				Snackbar.make(view, "You have not signed in", Snackbar.LENGTH_LONG).setAction("Sign in") {
					MainActivity.Instance?.loginIntentLauncher!!.launch(Intent(requireContext(), LoginActivity::class.java))
				}.show()
			} else {
				MainActivity.Instance?.postIntentLauncher!!.launch(Intent(requireContext(), PostActivity::class.java))
			}
		}
		
		binding.mainRecyvlerViewList.layoutManager = LinearLayoutManager(requireContext())
		binding.mainRecyvlerViewList.adapter = myAdapter
		myAdapter.setData(
			listOf(
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
			)
		)
		
		binding.mainRecyvlerViewList.addOnScrollListener(
			object : RecyclerView.OnScrollListener() {
				override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
					super.onScrolled(recyclerView, dx, dy)
					val up = binding.mainRecyvlerViewList.canScrollVertically(-1)
//					println(up)
					binding.mainSwipeRefreshLayout.isEnabled = !up || binding.mainSwipeRefreshLayout.isRefreshing
				}
			}
		)
		
		binding.mainSwipeRefreshLayout.setOnRefreshListener {
			CoroutineScope(Dispatchers.Main).launch {
				delay(2000)
				binding.mainSwipeRefreshLayout.isRefreshing = false
				delay(100)//this is purely just for the animation to play out
				binding.mainSwipeRefreshLayout.isEnabled = !binding.mainRecyvlerViewList.canScrollVertically(-1)
			}
		}
		
		viewModel.postsLimit.observe(viewLifecycleOwner) {
			updateMainButtonPostsLimit(it)
			when (it) {
				PostsLimit.All -> {}
				PostsLimit.Hot -> {}
				PostsLimit.Following -> {}
				else -> {}
			}
		}
		
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
	
	private fun LayoutBottomModalPostLimitBinding.initialize(postsLimit: PostsLimit) {
		when (postsLimit) {
			PostsLimit.All -> this.checkAll()
			PostsLimit.Hot -> this.checkHot()
			PostsLimit.Following -> this.checkFollowing()
		}
		this.modalPostsLimitLayoutFollowing.visibility = if (User.current == null) {
			View.GONE
		} else {
			View.VISIBLE
		}
	}
	
	private fun LayoutBottomModalPostLimitBinding.checkAll() {
		this.modalPostsLimitImageAll.setImageResource(R.drawable.ic_baseline_widgets_24)
		this.modalPostsLimitImageHot.setImageResource(R.drawable.ic_outline_whatshot_24)
		this.modalPostsLimitImageFollowing.setImageResource(R.drawable.ic_outline_people_alt_24)
		this.modalPostsLimitCheckAll.visibility = View.VISIBLE
		this.modalPostsLimitCheckHot.visibility = View.GONE
		this.modalPostsLimitCheckFollowing.visibility = View.GONE
		this.modalPostsLimitImageAll.imageTintList = resources.getColorStateList(R.color.normal_text, requireContext().theme)
		this.modalPostsLimitImageHot.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageFollowing.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitTextAll.setTextColor(resources.getColor(R.color.normal_text, requireContext().theme))
		this.modalPostsLimitTextHot.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextFollowing.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextAll.typeface = Typeface.DEFAULT_BOLD
		this.modalPostsLimitTextHot.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextFollowing.typeface = Typeface.DEFAULT
	}
	
	private fun LayoutBottomModalPostLimitBinding.checkHot() {
		this.modalPostsLimitImageAll.setImageResource(R.drawable.ic_outline_widgets_24)
		this.modalPostsLimitImageHot.setImageResource(R.drawable.ic_baseline_whatshot_24)
		this.modalPostsLimitImageFollowing.setImageResource(R.drawable.ic_outline_people_alt_24)
		this.modalPostsLimitCheckAll.visibility = View.GONE
		this.modalPostsLimitCheckHot.visibility = View.VISIBLE
		this.modalPostsLimitCheckFollowing.visibility = View.GONE
		this.modalPostsLimitImageAll.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageHot.imageTintList = resources.getColorStateList(R.color.normal_text, requireContext().theme)
		this.modalPostsLimitImageFollowing.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitTextAll.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextHot.setTextColor(resources.getColor(R.color.normal_text, requireContext().theme))
		this.modalPostsLimitTextFollowing.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextAll.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextHot.typeface = Typeface.DEFAULT_BOLD
		this.modalPostsLimitTextFollowing.typeface = Typeface.DEFAULT
	}
	
	private fun LayoutBottomModalPostLimitBinding.checkFollowing() {
		this.modalPostsLimitImageAll.setImageResource(R.drawable.ic_outline_widgets_24)
		this.modalPostsLimitImageHot.setImageResource(R.drawable.ic_outline_whatshot_24)
		this.modalPostsLimitImageFollowing.setImageResource(R.drawable.ic_baseline_people_alt_24)
		this.modalPostsLimitCheckAll.visibility = View.GONE
		this.modalPostsLimitCheckHot.visibility = View.GONE
		this.modalPostsLimitCheckFollowing.visibility = View.VISIBLE
		this.modalPostsLimitImageAll.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageHot.imageTintList = resources.getColorStateList(R.color.gray_text, requireContext().theme)
		this.modalPostsLimitImageFollowing.imageTintList = resources.getColorStateList(R.color.normal_text, requireContext().theme)
		this.modalPostsLimitTextAll.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextHot.setTextColor(resources.getColor(R.color.gray_text, requireContext().theme))
		this.modalPostsLimitTextFollowing.setTextColor(resources.getColor(R.color.normal_text, requireContext().theme))
		this.modalPostsLimitTextAll.typeface = Typeface.DEFAULT
		this.modalPostsLimitTextHot.typeface = Typeface.DEFAULT
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
}