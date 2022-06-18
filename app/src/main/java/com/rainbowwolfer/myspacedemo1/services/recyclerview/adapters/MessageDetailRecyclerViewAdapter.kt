package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.android.material.shape.CornerFamily
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.MessageDetailRowLayoutLeftBinding
import com.rainbowwolfer.myspacedemo1.databinding.MessageDetailRowLayoutRightBinding
import com.rainbowwolfer.myspacedemo1.models.MessageItem
import com.rainbowwolfer.myspacedemo1.models.MessageSet
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random

class MessageDetailRecyclerViewAdapter(
	private val context: Context,
) : RecyclerView.Adapter<MessageDetailRecyclerViewAdapter.ViewHolder>() {
	
	companion object {
		private const val TYPEID_MESSAGE_SENDER = 0
		private const val TYPEID_MESSAGE_RECEIVER = 1
	}
	
	abstract class ViewHolder(
		protected val parentBinding: ViewBinding,
	) : RecyclerView.ViewHolder(parentBinding.root) {
		abstract val cardView: CircularRevealCardView
		abstract val contentText: TextView
		abstract val avatarImage: CircleImageView
	}
	
	class LeftViewHolder(
		parent: ViewGroup,
	) : ViewHolder(
		MessageDetailRowLayoutLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
	) {
		val binding = super.parentBinding as MessageDetailRowLayoutLeftBinding
		override val cardView: CircularRevealCardView = binding.messageDetailLeftCardviewContent
		override val contentText: TextView = binding.messageDetailLeftTextContent
		override val avatarImage: CircleImageView = binding.messageDetailLeftImageAvatar
	}
	
	class RightViewHolder(
		parent: ViewGroup,
	) : ViewHolder(
		MessageDetailRowLayoutRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
	) {
		val binding = super.parentBinding as MessageDetailRowLayoutRightBinding
		override val cardView: CircularRevealCardView = binding.messageDetailRightCardviewContent
		override val contentText: TextView = binding.messageDetailRightTextContent
		override val avatarImage: CircleImageView = binding.messageDetailRightImageAvatar
	}
	
	private var list = emptyList<MessageItem>()
	private lateinit var set: MessageSet
	
	override fun getItemViewType(position: Int): Int {
		return if (list[position].isSelf) {
			TYPEID_MESSAGE_RECEIVER
		} else {
			TYPEID_MESSAGE_SENDER
		}
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			TYPEID_MESSAGE_SENDER -> RightViewHolder(parent)
			TYPEID_MESSAGE_RECEIVER -> LeftViewHolder(parent)
			else -> LeftViewHolder(parent)
		}
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val radius = context.resources.getDimension(R.dimen.my_corner_radius)
		
		holder.cardView.shapeAppearanceModel = holder.cardView.shapeAppearanceModel
			.toBuilder()
			.setTopLeftCorner(CornerFamily.ROUNDED, radius)
			.setTopRightCorner(CornerFamily.ROUNDED, radius)
			.setBottomRightCorner(CornerFamily.ROUNDED, radius)
			.setBottomLeftCorner(CornerFamily.ROUNDED, radius)
			.build()
		
		holder.contentText.text = EasyFunctions.generateRandomString(Random.nextInt(5, 50))
	}
	
	override fun getItemCount(): Int = list.size
	
	fun setData(set: MessageSet) {
		this.set = set
		val diffUtil = DatabaseIdDiffUtil(list, set.messages)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		list = set.messages
		diffResult.dispatchUpdatesTo(this)
	}
}