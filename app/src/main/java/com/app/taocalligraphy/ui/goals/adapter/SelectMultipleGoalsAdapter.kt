package com.app.taocalligraphy.ui.goals.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemGoalQuestionnaireTypeBinding
import com.app.taocalligraphy.models.response.profile.UserGoalsScreenApiResponse

class SelectMultipleGoalsAdapter(
    private var mDataList: MutableList<UserGoalsScreenApiResponse.UserGoalsScreenList?>,
    private val mListener: MultipleSelectItemClickListener,
    private var isEditable: Boolean
) : RecyclerView.Adapter<SelectMultipleGoalsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_goal_questionnaire_type,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            mDataList[position]?.let {
                tvText.text = it.name
                if (it.isSelected == true) {
                    if (isEditable) {
                        tvText.setTextColor(
                            ContextCompat.getColor(
                                tvText.context,
                                R.color.white
                            )
                        )
                        tvText.setBackgroundResource(R.drawable.bg_gold_semi_light_curve_50)
                    } else {
                        tvText.setTextColor(
                            ContextCompat.getColor(
                                tvText.context,
                                R.color.white
                            )
                        )
                        tvText.setBackgroundResource(R.drawable.bg_gold_semi_light_curve)
                    }
                } else {
                    if (isEditable) {
                        tvText.setTextColor(
                            ContextCompat.getColor(
                                tvText.context,
                                R.color.dark_grey_50
                            )
                        )
                        tvText.setBackgroundResource(R.drawable.bg_white_gold_semi_light_curve_50)
                    } else {
                        tvText.setTextColor(
                            ContextCompat.getColor(
                                tvText.context,
                                R.color.dark_grey
                            )
                        )
                        tvText.setBackgroundResource(R.drawable.bg_white_gold_semi_light_curve)
                    }
                }
            }
        }

    }

    inner class ItemViewHolder(var binding: ItemGoalQuestionnaireTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvText.setOnClickListener {
                if (isEditable)
                    return@setOnClickListener
                mDataList[bindingAdapterPosition]?.isSelected =
                    !mDataList[bindingAdapterPosition]?.isSelected!!
                notifyItemChanged(bindingAdapterPosition)
                mListener.onMultipleItemClick()
            }
        }
    }

    interface MultipleSelectItemClickListener {
        fun onMultipleItemClick()
    }


    fun updateList(arrayList: ArrayList<UserGoalsScreenApiResponse.UserGoalsScreenList?>) {
        mDataList.clear()
        mDataList.addAll(arrayList)
        notifyDataSetChanged()
    }
}