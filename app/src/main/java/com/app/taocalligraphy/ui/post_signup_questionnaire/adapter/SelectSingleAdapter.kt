package com.app.taocalligraphy.ui.post_signup_questionnaire.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemWelcomeQuestionnaireTypeSingleSelectionBinding
import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.KeywordsDataModel

class SelectSingleAdapter(
    private var mDataList: ArrayList<KeywordsDataModel?>?,
    private val mListener: SingleSelectItemClickListener
) : RecyclerView.Adapter<SelectSingleAdapter.SelectSingleAdapterViewHolder>() {

    lateinit var keywordsDataModel: KeywordsDataModel
    private var selectedPosition: Int = -1
    private var canSelectMultipleOption: Boolean = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectSingleAdapter.SelectSingleAdapterViewHolder {
        return SelectSingleAdapterViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_welcome_questionnaire_type_single_selection,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList?.size ?: 0

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: SelectSingleAdapter.SelectSingleAdapterViewHolder,
        position: Int
    ) {
        keywordsDataModel = mDataList?.get(position)!!

        holder.binding.apply {
            with(keywordsDataModel) {
                tvText.text = this.name ?: ""
                if (!canSelectMultipleOption) {
                    this.isSelected = selectedPosition == position
                }
                if (this.isSelected == true) {
                    tvText.setTextColor(ContextCompat.getColor(tvText.context, R.color.white))
                    tvText.setBackgroundResource(R.drawable.bg_gold_semi_light_curve)
                } else {
                    tvText.setTextColor(ContextCompat.getColor(tvText.context, R.color.dark_grey))
                    tvText.setBackgroundResource(R.drawable.bg_white_gold_semi_light_curve)
                }
                tvText.isClickable = true
            }
        }
        holder.clickHandler()

    }

    @SuppressLint("NotifyDataSetChanged")
    inner class SelectSingleAdapterViewHolder(var binding: ItemWelcomeQuestionnaireTypeSingleSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun clickHandler() {
            binding.root.setOnClickListener {
                Log.d("TAG", "Select Single adapter clicked-->")
                selectedPosition = bindingAdapterPosition
                if (canSelectMultipleOption) {
                    mDataList?.get(bindingAdapterPosition)?.isSelected =
                        mDataList?.get(bindingAdapterPosition)?.isSelected != true
                }
                mDataList?.get(selectedPosition)?.let {
                    mListener.onSingleItemClick(mDataList?.get(bindingAdapterPosition)!!, canSelectMultipleOption)
                }
                notifyDataSetChanged()
            }
        }
    }

    interface SingleSelectItemClickListener {
        fun onSingleItemClick(keywordsDataModel: KeywordsDataModel, canSelectMultipleOption : Boolean)
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun updateList(
        mDataList: ArrayList<KeywordsDataModel?>?,
        selectedPosition: Int,
        canSelectMultipleOption: Boolean
    ) {
        this.mDataList?.clear()
        if (mDataList != null) {
            this.mDataList?.addAll(mDataList)
        }
        this.selectedPosition = selectedPosition
        this.canSelectMultipleOption = canSelectMultipleOption
        notifyDataSetChanged()
    }

}