package com.app.taocalligraphy.ui.settings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemImportantBinding
import com.app.taocalligraphy.models.response.profile.UserSettingsApiResponse
import kotlinx.android.synthetic.main.item_important.view.*

class ImportantAdapter(
    private var mDataList: MutableList<UserSettingsApiResponse.UserImportantInfo?>?,
    private val mListener: ImportantItemClickListener
) : RecyclerView.Adapter<ImportantAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_important, parent, false
            )
        )
    }

    override fun getItemCount(): Int = mDataList!!.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = mDataList?.get(position)
        holder.binding.apply {
            tvTitle.text = data?.title
            llMain.setOnClickListener {
                if (data != null) {
                    mListener.onItemClick(data)
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemImportantBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    interface ImportantItemClickListener {
        fun onItemClick(userInfo: UserSettingsApiResponse.UserImportantInfo)
    }

    fun updateList(arrayList: List<UserSettingsApiResponse.UserImportantInfo?>?) {
        mDataList?.clear()
        mDataList?.addAll(arrayList!!)
        notifyDataSetChanged()
    }

}