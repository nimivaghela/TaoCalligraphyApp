package com.app.taocalligraphy.ui.meditation_rooms_list.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemCustomTabViewBinding


class TabsSelectSingleAdapter(
    private var context: Context,
    private var mDataList: MutableList<String>,
    private val mListener: SingleSelectItemClickListener,
    selectedTabPos: Int = 0
) : RecyclerView.Adapter<TabsSelectSingleAdapter.ItemViewHolder>() {

    var selectedPosition = selectedTabPos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_custom_tab_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                tvTab.text = mDataList[position]

                if (mDataList[position] == context.getString(R.string.requests)) {
                    tvRequestCount.visibility = View.VISIBLE
                    tvRequestCount.text = context.getString(R.string.one_four)
                } else {
                    tvRequestCount.visibility = View.GONE
                }
                if (selectedPosition == position) {
                    tvTab.setTextColor(ContextCompat.getColor(tvTab.context, R.color.gold))
                    relTab.setBackgroundResource(R.drawable.bg_white_gold_semi_light_50_curve)
                } else {
                    tvTab.setTextColor(
                        ContextCompat.getColor(
                            tvTab.context,
                            R.color.medium_grey
                        )
                    )
                    relTab.setBackgroundResource(R.drawable.bg_grey_95_curve)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemCustomTabViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvTab.setOnClickListener {
                mListener.onSingleItemClick(binding.tvTab.text.toString(), bindingAdapterPosition)
                selectedPosition = bindingAdapterPosition
                notifyDataSetChanged()
            }
        }
    }

    interface SingleSelectItemClickListener {
        fun onSingleItemClick(tabText: String, selectedPos: Int)
    }

    /* @SuppressLint("NotifyDataSetChanged")
     public fun updateList(mDataList: MutableList<QuestionnaireDummyModel>, isAnsGiven: Boolean) {
         this.mDataList.clear()
         this.mDataList.addAll(mDataList)
         this.isAnsGiven = isAnsGiven
         notifyDataSetChanged()
     }*/

}