package com.app.taocalligraphy.ui.meditation_rooms_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemRoomUpcomingSessionsListBinding

class UpcomingSessionsListAdapter(
    private val signupOptionListener: SignupOptionListener
) :
    RecyclerView.Adapter<UpcomingSessionsListAdapter.ViewHolder>() {

    private var isCollapsed = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemRoomUpcomingSessionsListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 0) {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.item.llMain.context,
                    R.color.white
                )
            )
        } else {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.item.llMain.context,
                    R.color.medium_grey_10
                )
            )
        }

        holder.item.btnSignUp.setOnClickListener {
            signupOptionListener.onSignUpClick()
        }

        /*holder.item.cvGetRooms.setOnClickListener {
            if (isCollapsed) {
//                holder.item.ivArrow.setImageResource(R.drawable.vd_up_arrow_gold)
                val layoutParams = holder.item.cvGetRooms.layoutParams
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                holder.item.cvGetRooms.layoutParams = layoutParams
                isCollapsed = false
            } else {
//                holder.item.ivArrow.setImageResource(R.drawable.vd_down_arrow_gold)
                val layoutParams = holder.item.cvGetRooms.layoutParams
                layoutParams.height = 78
                holder.item.cvGetRooms.layoutParams = layoutParams
                isCollapsed = true
            }
        }*/

        when {
            holder.adapterPosition == 0 -> {

            }
            position == 1 -> {

            }
            position == 2 -> {

            }
            else -> {
                // noting to do
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    class ViewHolder(view: ItemRoomUpcomingSessionsListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface SignupOptionListener {
        fun onSignUpClick()
    }
}