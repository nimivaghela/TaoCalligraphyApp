package com.app.taocalligraphy.ui.journal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemJournalListBinding
import com.app.taocalligraphy.models.response.journal_data_models.JournalDataModel
import com.app.taocalligraphy.utils.extensions.getDayFromDate
import com.app.taocalligraphy.utils.extensions.getMonthFromDate
import com.app.taocalligraphy.utils.extensions.setHtmlTextToTextView

class JournalListByDateAdapter(
    private val journalItemClickListener: ByDateJournalItemClickListener,
    private val dataList: ArrayList<JournalDataModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBindingUtil =
            ItemJournalListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataHolder) {
            val journalDataModel: JournalDataModel = dataList[position]
            holder.item.apply {
                tvTitle.text = journalDataModel.title
                tvDescription.text = setHtmlTextToTextView(journalDataModel.journalEntry)

                tvDay.text = getDayFromDate(journalDataModel.date)
                tvMonth.text = getMonthFromDate(journalDataModel.date)

                rrMain.setOnClickListener {
                    journalItemClickListener.onByDateJournalItemClick(holder.adapterPosition)
                }

                llDelete.setOnClickListener {
                    journalItemClickListener.onByDateJournalDeleteClick(holder.adapterPosition)
                }

                llPinUnpin.setOnClickListener {
                    journalItemClickListener.onByDateJournalPinUnpinClick(holder.adapterPosition)
                }
            }
            if (holder.adapterPosition % 2 == 0) {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        android.R.color.transparent
                    )
                )
            } else {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.medium_grey_10
                    )
                )
            }
//            holder.item.tvDay.text = (date - holder.adapterPosition).toString()
            if (holder.adapterPosition == 1) {
                holder.item.ivStick.setImageResource(R.drawable.vd_unstick)
            } else {
                holder.item.ivStick.setImageResource(R.drawable.vd_stick_top)
            }

            holder.item.rrMain.setOnClickListener {
                journalItemClickListener.onByDateJournalItemClick(holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class DataHolder(view: ItemJournalListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ByDateJournalItemClickListener {
        fun onByDateJournalItemClick(adapterPosition: Int)
        fun onByDateJournalDeleteClick(adapterPosition: Int)
        fun onByDateJournalPinUnpinClick(adapterPosition: Int)
    }
}