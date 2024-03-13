package com.app.taocalligraphy.ui.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLanguageBinding
import com.app.taocalligraphy.models.response.profile.LanguageListApiResponse
import com.app.taocalligraphy.utils.extensions.gone

class SelectSingleLanguageAdapter(
    private var mDataList: MutableList<LanguageListApiResponse.Data>,
    private val mListener: SingleSelectItemClickListener,
) : RecyclerView.Adapter<SelectSingleLanguageAdapter.ItemViewHolder>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_language, parent, false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                tvLanguage.text = language
                if (isSelected) {
                    ivRadio.setImageResource(R.drawable.vd_status_selected)
                } else {
                    ivRadio.setImageResource(R.drawable.vd_status_unselected)
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.llMain.setOnClickListener {
                selectedPosition = bindingAdapterPosition
                for (i in mDataList.indices) {
                    if (i == selectedPosition) {
                        mDataList[selectedPosition].isSelected = true
                    } else {
                        mDataList[i].isSelected = false
                    }
                }
                notifyDataSetChanged()
                mDataList[selectedPosition].languageId?.let { it1 -> mListener.onSingleItemClick(it1) }
            }
        }
    }

    interface SingleSelectItemClickListener {
        fun onSingleItemClick(languageId: Int)
    }

    fun updateList(arrayList: ArrayList<LanguageListApiResponse.Data>) {
        mDataList.clear()
        mDataList.addAll(arrayList)
        notifyDataSetChanged()
    }

}