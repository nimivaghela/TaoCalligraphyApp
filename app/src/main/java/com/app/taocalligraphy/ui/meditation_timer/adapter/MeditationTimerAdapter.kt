package com.app.taocalligraphy.ui.meditation_timer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.models.response.meditation_timer.MeditationListApiResponse
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.isVisible
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_meditation_timer.view.*
import kotlinx.android.synthetic.main.item_meditation_timer_add_new.view.*


class MeditationTimerAdapter(
    val context: Context,
    private val mListener: ItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mDataList = ArrayList<MeditationListApiResponse.MeditationDetail?>()
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var showMenuPosition = -1

    private var previousSelectedView: MaterialCardView? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setMeditationTimerData(dataList: ArrayList<MeditationListApiResponse.MeditationDetail?>) {
        mDataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val view: View
        if (viewType == 1) {
            view = mInflater.inflate(R.layout.item_meditation_timer_add_new, parent, false)
            viewHolder = AddNewVH(view)
        } else {
            view = mInflater.inflate(R.layout.item_meditation_timer, parent, false)
            viewHolder = ShowListVH(view)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddNewVH) {
            val cvAddNewTimer = holder.itemView.cvAddNewTimer
            cvAddNewTimer.setOnClickListener {
                mListener.onAddTimerItemClick()
            }
            if (UserHolder.EnumUserModulePermission.CREATE_MEDITATION_TIMER.permission?.canAccess
                    ?: false
            ) {
//                cvAddNewTimer.isEnabled = true
                cvAddNewTimer.alpha = 1f
                holder.itemView.ivSubscribeLock.gone()
            } else {
//                cvAddNewTimer.isEnabled = false
                cvAddNewTimer.alpha = 0.5f
                holder.itemView.ivSubscribeLock.visible()
            }
        } else if (holder is ShowListVH) {
            mDataList[position - 1]?.let { data ->
                holder.itemView.apply {
                    ivBackground.loadImage(
                        data.backgroundImage?.backgroundImageOriginal,
                        R.drawable.bg_placeholder_meditation_timer
                    )
                    tvMeditationName.text = data.name
                    if (data.meditationTime!!.toInt() <= 60) {
                        tvTimer.text = data.meditationTime
                        tvHoursMinute.text = "m"
                    } else {
                        tvTimer.text = (data.meditationTime!!.toInt() / 60).toString()
                        tvHoursMinute.text = "h"
                    }

                    cvViewTimer.clickWithDebounce {
                        llOptionDialog.isGone = true
                        mListener.onPlayTimerItemClick(mDataList[holder.bindingAdapterPosition - 1]!!)
                        try {
                            val handler = Handler(Looper.getMainLooper())
                            handler.post {
                                if (showMenuPosition >= 0)
                                    notifyItemChanged(showMenuPosition)
                            }
                        } catch (e: Exception) {

                        }
                    }

                    if (UserHolder.EnumUserModulePermission.CREATE_MEDITATION_TIMER.permission?.canAccess
                            ?: false
                    ) {
                        ivEdit.setImageResource(R.drawable.ic_pencil_grey)
                        ivClone.setImageResource(R.drawable.vd_clone_icon_gold)
                    } else {
                        ivEdit.setImageResource(R.drawable.ic_lock_gray)
                        ivClone.setImageResource(R.drawable.ic_lock_gray)
                    }

                    if (previousSelectedView != null && previousSelectedView?.isVisible() == true) {
                        previousSelectedView!!.gone()
                        showMenuPosition = -1
                    }

                    ivOption.setOnClickListener {
                        previousSelectedView?.let {
                            if (previousSelectedView != llOptionDialog) {
                                if (it.isVisible())
                                    it.gone()
                            }
                        }

                        previousSelectedView = if (llOptionDialog.isVisible()) {
                            llOptionDialog.gone()
                            showMenuPosition = -1
                            null
                        } else {
                            llOptionDialog.visible()
                            showMenuPosition = position
                            llOptionDialog
                        }
                    }

                    llEdit.setOnClickListener {
                        llOptionDialog.isGone = true
                        mListener.onEditTimerItemClick(mDataList[holder.bindingAdapterPosition - 1]!!)
                    }
                    ivEdit.setOnClickListener {
                        llEdit.performClick()
                    }

                    llClone.setOnClickListener {
                        llOptionDialog.isGone = true
                        mListener.onCloneTimerItemClick(mDataList[holder.bindingAdapterPosition - 1]!!)
                    }
                    ivClone.setOnClickListener {
                        llClone.performClick()
                    }

                    llDelete.setOnClickListener {
                        llOptionDialog.isGone = true
                        mListener.onDeleteTimerItemClick(
                            mDataList[holder.bindingAdapterPosition - 1]?.meditationId!!,
                            position - 1
                        )
                    }
                    ivDelete.setOnClickListener {
                        llDelete.performClick()
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return if (mDataList.size == (mDataList.size + 1)) {
            mDataList.size
        } else {
            mDataList.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 1 else 2
    }

    internal inner class AddNewVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    internal inner class ShowListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface ItemClickListener {
        fun onAddTimerItemClick()
        fun onPlayTimerItemClick(meditationDetail: MeditationListApiResponse.MeditationDetail)
        fun onEditTimerItemClick(meditationDetail: MeditationListApiResponse.MeditationDetail)
        fun onCloneTimerItemClick(meditationDetail: MeditationListApiResponse.MeditationDetail)
        fun onDeleteTimerItemClick(meditationId: Int, position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(productList: ArrayList<MeditationListApiResponse.MeditationDetail?>?) {
        mDataList.clear()
        mDataList.addAll(productList!!)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(index: Int) {
        mDataList.removeAt(index)
        notifyDataSetChanged()
    }

}