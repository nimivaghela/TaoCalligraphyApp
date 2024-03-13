package com.app.taocalligraphy.ui.journal.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.databinding.ItemJournalListBinding
import com.app.taocalligraphy.databinding.ItemJournalTitleSectionBinding
import com.app.taocalligraphy.models.response.journal_data_models.JournalDataModel
import com.app.taocalligraphy.utils.extensions.getDayFromDate
import com.app.taocalligraphy.utils.extensions.getMonthFromDate
import com.app.taocalligraphy.utils.extensions.isTabletLandScape
import com.app.taocalligraphy.utils.loadHtml
import java.util.*

class JournalListAdapter(
    private val journalItemClickListener: JournalItemClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var journalList: ArrayList<JournalDataModel> = arrayListOf()
    val ITEM_TITLE = 0
    val ITEM_DATA = 1
    var date = 31
    var searchText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TITLE) {
            val itemBindingUtil =
                ItemJournalTitleSectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            SectionTitleViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemJournalListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (journalList[position].isTitle) {
            ITEM_TITLE
        } else {
            ITEM_DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SectionTitleViewHolder) {
            holder.item.tvJournalSectionTitle.text = journalList[position].titleName
            if (holder.item.tvJournalSectionTitle.text == holder.itemView.context.getString(R.string.pinned)) {
                if (journalList[position].isPinnedJournalDataAvailable) {
                    holder.item.tvNoPinnedFound.visibility = View.GONE
                } else {
                    holder.item.tvNoPinnedFound.visibility = View.VISIBLE
                }
                holder.item.tvSwipeInfo.text =
                    holder.item.tvSwipeInfo.context.getString(R.string.swipe_right_to_unpin)

            } else {
                holder.item.tvSwipeInfo.text = holder.item.tvSwipeInfo.context.getString(R.string.swipe_right_to_pin)
                holder.item.tvNoPinnedFound.visibility = View.GONE
            }
            
             // to decrease top margin of first title item for landscape
            if(position ==0 && isTabletLandScape(TaoCalligraphy.instance)) {
                val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 5, 0, 0)
                holder.item.llTop.post {
                    holder.item.llTop.layoutParams = params
                }
            }

        } else if (holder is DataHolder) {
            val journalDataModel: JournalDataModel = journalList[position]
            holder.item.apply {
                val startPos: Int =
                    journalDataModel.title?.lowercase(Locale.US)!!.indexOf(
                        searchText.lowercase(
                            Locale.US
                        )
                    )
                val endPos: Int = startPos + searchText.length
                if (startPos != -1) {
                    val spannable: Spannable = SpannableString(journalDataModel.title)
                    val blueColor =
                        ColorStateList(
                            arrayOf(intArrayOf()),
                            intArrayOf(Color.parseColor("#E6B08C3D"))
                        )
                    val highlightSpan =
                        TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null)
                    spannable.setSpan(
                        highlightSpan,
                        startPos,
                        endPos,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tvTitle.text = spannable
                } else {
                    tvTitle.text = journalDataModel.title
                }


                loadHtml(tvDescription, journalDataModel.journalEntry!!)
                val startDescPos: Int =
                    tvDescription.text.toString().lowercase(Locale.US)!!.indexOf(
                        searchText.lowercase(
                            Locale.US
                        )
                    )
                val endDescPos: Int = startDescPos + searchText.length
                Log.e("StartDescPos", startDescPos.toString())
                Log.e("EndDescPos", endDescPos.toString())
                if (startDescPos != -1) {
//                    loadHtml(tvDescription, journalDataModel.journalEntry!!)
                    val spannable: Spannable = SpannableString(tvDescription.text)
                    val blueColor =
                        ColorStateList(
                            arrayOf(intArrayOf()),
                            intArrayOf(Color.parseColor("#E6B08C3D"))
                        )
                    val highlightSpan =
                        TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null)
                    spannable.setSpan(
                        highlightSpan,
                        startDescPos,
                        endDescPos,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tvDescription.text = (spannable)

                } else {
//                    loadHtml(tvDescription, journalDataModel.journalEntry!!)
                }

                tvDay.text = getDayFromDate(journalDataModel.date)
                tvMonth.text = getMonthFromDate(journalDataModel.date)

                rrMain.setOnClickListener {
                    journalItemClickListener.onJournalItemClick(holder.bindingAdapterPosition)
                }

                tvTitle.setOnClickListener {
                    journalItemClickListener.onJournalItemClick(holder.bindingAdapterPosition)
                }

                tvDescription.setOnClickListener {
                    journalItemClickListener.onJournalItemClick(holder.bindingAdapterPosition)
                }

                llDelete.setOnClickListener {
                    journalItemClickListener.onJournalDeleteClick(holder.bindingAdapterPosition)
                }

                llPinUnpin.setOnClickListener {
                    journalItemClickListener.onJournalPinUnpinClick(holder.bindingAdapterPosition)
                }
            }
            if (journalList[holder.bindingAdapterPosition].index % 2 == 0) {
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


            holder.item.rrMain.setOnClickListener {
                journalItemClickListener.onJournalItemClick(holder.bindingAdapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    class DataHolder(view: ItemJournalListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class SectionTitleViewHolder(view: ItemJournalTitleSectionBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface JournalItemClickListener {
        fun onJournalItemClick(adapterPosition: Int)
        fun onJournalDeleteClick(adapterPosition: Int)
        fun onJournalPinUnpinClick(adapterPosition: Int)
    }
}