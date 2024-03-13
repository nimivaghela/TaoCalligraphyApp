package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemsMyRoomsBinding

class MyRoomsAdapter(
    private var  context: Context,
    private val mListener: OnMyRoomsItemClickListener
) : RecyclerView.Adapter<MyRoomsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.items_my_rooms,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            when (position) {
                0 -> {
                    //Subscribe
                    ivEdit.visibility = View.GONE
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.gold_semi_light
                        )
                    )
                    cvCardBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.gold_semi_light
                        )
                    )
                    llRequestRooms.visibility = View.VISIBLE
                    llRoomsDetail.visibility = View.GONE
                }
                1 -> {
                    // New
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.shimmer_light
                        )
                    )
                    cvCardBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.white
                        )
                    )
                    llRequestRooms.visibility = View.GONE
                    llRoomsDetail.visibility = View.VISIBLE
                    fmNew.visibility = View.VISIBLE
                    llStatusBackground.setBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.white
                        )
                    )
                    tvName.setTextColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.secondary_black
                        )
                    )
                    llNormal.visibility = View.VISIBLE
                    ivEdit.visibility = View.VISIBLE
                    llApprovalPending.visibility = View.GONE
                    llNotApproved.visibility = View.GONE
                    tvFollowers.text = context.getString(R.string.zero_followers)
                    rbRating.visibility = View.GONE
                    tvRateCount.text = context.getString(R.string.no_ratings_yes)
                }
                2 -> {
                    // Normal
                    ivEdit.visibility = View.VISIBLE
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.shimmer_light
                        )
                    )
                    cvCardBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.white
                        )
                    )
                    llRequestRooms.visibility = View.GONE
                    llRoomsDetail.visibility = View.VISIBLE
                    fmNew.visibility = View.GONE
                    llStatusBackground.setBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.white
                        )
                    )
                    tvName.setTextColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.secondary_black
                        )
                    )
                    llNormal.visibility = View.VISIBLE
                    llApprovalPending.visibility = View.GONE
                    llNotApproved.visibility = View.GONE
                    tvFollowers.text = context.getString(R.string.fourty_six_followers)
                    rbRating.visibility = View.VISIBLE
                    tvRateCount.text = context.getString(R.string.two_seven)
                }
                3 -> {
                    // Pending
                    ivEdit.visibility = View.GONE
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.shimmer_light
                        )
                    )
                    cvCardBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.white
                        )
                    )
                    llRequestRooms.visibility = View.GONE
                    llRoomsDetail.visibility = View.VISIBLE
                    fmNew.visibility = View.GONE
                    llStatusBackground.setBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.dandelion
                        )
                    )
                    tvName.setTextColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.secondary_black
                        )
                    )
                    llNormal.visibility = View.GONE
                    llApprovalPending.visibility = View.VISIBLE
                    llNotApproved.visibility = View.GONE
                }
                4 -> {
                    // Rejected
                    ivEdit.visibility = View.VISIBLE
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.shimmer_light
                        )
                    )
                    cvCardBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.white
                        )
                    )
                    llRequestRooms.visibility = View.GONE
                    llRoomsDetail.visibility = View.VISIBLE
                    fmNew.visibility = View.GONE
                    llStatusBackground.setBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.red
                        )
                    )
                    tvName.setTextColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.white
                        )
                    )
                    llNormal.visibility = View.GONE
                    llApprovalPending.visibility = View.GONE
                    llNotApproved.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class ItemViewHolder(var binding: ItemsMyRoomsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivEdit.setOnClickListener {
                mListener.onMyRoomEditClick(bindingAdapterPosition)
            }
            binding.root.setOnClickListener {
                mListener.onMyRoomsAdapterClick(bindingAdapterPosition)
            }
        }
    }

    interface OnMyRoomsItemClickListener {
        fun onMyRoomsAdapterClick(position: Int)
        fun onMyRoomEditClick(position: Int)
    }
}