package com.app.taocalligraphy.ui.notification.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemMeditationResultListBinding
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.isVisible
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_meditation_result_list.view.*

class MeditationResultAdapter(
    private val allowedMultipleSelection: Boolean,
    private val onMeditationContentSelectListener: OnMeditationContentSelectListener
) :
    RecyclerView.Adapter<MeditationResultAdapter.ViewHolder>() {

    private var meditationDataList = ArrayList<PlaylistContentFilterApiResponse.ContentList>()
    var selectedMeditationList = ArrayList<PlaylistContentFilterApiResponse.ContentList>()
    private var previousSelected: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemMeditationResultListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = meditationDataList[position]
        holder.itemView.imgMeditationImage.loadImage(
            meditationDataList[position].thumbnailImage,
            R.drawable.img_default_for_content
        )
        holder.itemView.tvName.text = "" + meditationDataList[position].contentName
        if (selectedMeditationList.any { selectedId -> selectedId.id == meditationDataList[position].id }) {
            holder.itemView.rlSelected.visible()
            holder.itemView.tvName.setTextColor(
                ContextCompat.getColor(holder.itemView.tvName.context, R.color.gold)
            )
            val params = FrameLayout.LayoutParams(holder.itemView.imgMeditationImage.layoutParams)
            params.setMargins(5, 0, 5, 0)
            holder.itemView.imgMeditationImage.layoutParams = params
            holder.itemView.imgMeditationImage.alpha = 0.5f
            if (!allowedMultipleSelection)
                previousSelected = holder.itemView
        } else {
            holder.itemView.rlSelected.gone()
            holder.itemView.tvName.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.tvName.context,
                    R.color.dark_grey
                )
            )
            val params = FrameLayout.LayoutParams(holder.itemView.imgMeditationImage.layoutParams)
            params.setMargins(0, 0, 0, 0)
            holder.itemView.imgMeditationImage.layoutParams = params
            holder.itemView.imgMeditationImage.alpha = 1f
        }

        // for Content
        if ((data?.subscription?.isAccessible
                ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false
        ) {
//                    GET
            holder.itemView.tVGet.visible()
            holder.itemView.ivSubscribeLock.gone()
        } else if ((data?.subscription?.isAccessible ?: true) == false) {
//                        Subscribe
            holder.itemView.tVGet.gone()
            holder.itemView.ivSubscribeLock.visible()
        } else {
//                    can access
            holder.itemView.tVGet.gone()
            holder.itemView.ivSubscribeLock.gone()
        }

        holder.itemView.setOnClickListener {
            if (selectedMeditationList.any { selectedId -> selectedId.id == meditationDataList[position].id }) {
                val pos =
                    selectedMeditationList.indexOfFirst { selectedId -> selectedId.id == meditationDataList[position].id }
                selectedMeditationList.removeAt(pos)
                holder.itemView.rlSelected.gone()
                holder.itemView.tvName.setTextColor(
                    ContextCompat.getColor(holder.itemView.tvName.context, R.color.dark_grey)
                )
                holder.itemView.imgMeditationImage.alpha = 1f
                val params = FrameLayout.LayoutParams(holder.itemView.imgMeditationImage.layoutParams)
                params.setMargins(0, 0, 0, 0)
                holder.itemView.imgMeditationImage.layoutParams = params
            } else {
                if (allowedMultipleSelection) {
                    selectedMeditationList.add(meditationDataList[position])

                    holder.itemView.rlSelected.visible()
                    holder.itemView.tvName.setTextColor(
                        ContextCompat.getColor(holder.itemView.tvName.context, R.color.gold)
                    )
                    holder.itemView.imgMeditationImage.alpha = 0.5f
                    val params = FrameLayout.LayoutParams(holder.itemView.imgMeditationImage.layoutParams)
                    params.setMargins(5, 0, 5, 0)
                    holder.itemView.imgMeditationImage.layoutParams = params
                } else {
                    if (holder.itemView.tVGet.isVisible()) {
                        Toast.makeText(
                            holder.itemView.tvName.context,
                            holder.itemView.tvName.context.getString(R.string.alarm_purchase_for_content),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (holder.itemView.ivSubscribeLock.isVisible()) {
                        Toast.makeText(
                            holder.itemView.tvName.context,
                            holder.itemView.tvName.context.getString(R.string.alarm_subscribe_for_content),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        selectedMeditationList.clear()
                        selectedMeditationList.add(meditationDataList[position])
                        previousSelected?.let {
                            it.rlSelected.gone()
                            it.tvName.setTextColor(
                                ContextCompat.getColor(
                                    holder.itemView.tvName.context,
                                    R.color.dark_grey
                                )
                            )
                            it.imgMeditationImage.alpha = 1f
                            val params = FrameLayout.LayoutParams(holder.itemView.imgMeditationImage.layoutParams)
                            params.setMargins(0, 0, 0, 0)
                            holder.itemView.imgMeditationImage.layoutParams = params
                        }
                        previousSelected = holder.itemView

                        holder.itemView.rlSelected.visible()
                        holder.itemView.tvName.setTextColor(
                            ContextCompat.getColor(holder.itemView.tvName.context, R.color.gold)
                        )
                        holder.itemView.imgMeditationImage.alpha = 0.5f
                        val params = FrameLayout.LayoutParams(holder.itemView.imgMeditationImage.layoutParams)
                        params.setMargins(5, 0, 5, 0)
                        holder.itemView.imgMeditationImage.layoutParams = params
                    }
                }
            }

            onMeditationContentSelectListener.onMeditationContentSelected(selectedMeditationList)
        }
    }

    override fun getItemCount(): Int {
        return meditationDataList.size
    }

    class ViewHolder(view: ItemMeditationResultListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    fun updateList(
        meditationDataList: ArrayList<PlaylistContentFilterApiResponse.ContentList>,
        selectedMeditationList: ArrayList<PlaylistContentFilterApiResponse.ContentList>
    ) {
        this.meditationDataList = meditationDataList
        this.selectedMeditationList = selectedMeditationList
        notifyDataSetChanged()
    }

    interface OnMeditationContentSelectListener {
        fun onMeditationContentSelected(selectedMeditationList: ArrayList<PlaylistContentFilterApiResponse.ContentList>)
    }
}