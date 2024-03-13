package com.app.taocalligraphy.ui.how_to_meditate.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreVerticalProgressBinding
import com.app.taocalligraphy.databinding.ItemReadMeditateListBinding
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.MySpannable
import com.app.taocalligraphy.utils.extensions.getSecondsFromTime
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadHtml
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_meditation_result_list.view.*
import kotlinx.android.synthetic.main.item_meditation_search.view.*

class ReadMeditateAdapter(
    private var context: Context,
    private val readMeditateClickListener: ReadMeditateClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mViewMore = context.getString(R.string.m_read_now)

    private var list: ArrayList<HowToMeditateDataModel?> = arrayListOf()

    fun setReadData(dataList: ArrayList<HowToMeditateDataModel?>) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == Constants.ITEM_PROGRESS) {
            val itemBindingUtil =
                ItemLoadMoreVerticalProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ProgressViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemReadMeditateListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ViewHolder(itemBindingUtil)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data: HowToMeditateDataModel? = list[holder.bindingAdapterPosition]
        if (holder is ViewHolder) {
            holder.item.apply {
                val image = if (!data?.thumbnailImage.isNullOrEmpty()) {
                    data?.thumbnailImage
                } else {
                    data?.contentImage
                }
                ivMeditate.loadImage(image, R.drawable.img_default_for_content, true)
                tvMeditateTitle.text = data?.title
                tvTime.text = tvTime.context.getString(R.string.read_time, getSecondsFromTime(data?.time))
                tvMeditateDescription.text = data?.description
                data?.description?.let {
                    loadHtml(tvMeditateDescription, data.description)
                    val description: String = tvMeditateDescription.text.toString()
//                    val parts: ArrayList<String> =
//                        description.split(" ") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
//
//                    val textOne = SpannableStringBuilder()
//                    val textTwo = SpannableStringBuilder()
//
//                    parts.forEachIndexed { index, data ->
//                        if (index == 0 || index == 1) {
//                            textOne.append("$data ")
//                            textOne.setSpan(
//                                StyleSpan(Typeface.BOLD),
//                                0,
//                                textOne.length,
//                                R.style.TextViewJostBoldStyle
//                            )
//
//                        } else {
//                            textTwo.append("$data ")
//                            textTwo.setSpan(
//                                StyleSpan(Typeface.NORMAL),
//                                0,
//                                textTwo.length,
//                                R.style.TextViewJostRegularStyle
//                            )
//                        }
//                    }
//                    val textThree = SpannableStringBuilder()
//                    textThree.append(textOne)
//                    textThree.append(textTwo)

                    tvMeditateDescription.text = description

//                    makeTextViewResizable(holder.item.tvMeditateDescription, 3, mViewMore, true)
                }

                if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == true) {
//                    GET
//                    ivPlay.gone()
                    tVGet.visible()
                    ivSubscribeLock.gone()
                    rlLock.gone()
                } else if (data?.isLocked == true && data.isSubscribed == false && data.unlockDays!! >= 1) {
//                    Unlock within `dynamic` days
                    tVGet.gone()
//                    ivPlay.visible()
                    ivSubscribeLock.visible()
                    rlLock.gone()
                } else if (data?.isLocked == true && data.isSubscribed == false && data.unlockDays != null) {
//                    lock
                    tVGet.gone()
//                    ivPlay.visible()
                    ivSubscribeLock.gone()
                    rlLock.visible()
                } else if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == false) {
//                        Subscribe
                    tVGet.gone()
//                    ivPlay.gone()
                    ivSubscribeLock.visible()
                    rlLock.gone()
                } else {
//                    can access
                    tVGet.gone()
//                    ivPlay.visible()
                    ivSubscribeLock.gone()
                    rlLock.gone()
                }

            }

            if ((holder.bindingAdapterPosition % 4 == 0) or (((holder.bindingAdapterPosition + 1) % 4) == 0)) {
                holder.item.cardReadMeditate.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.cultured_gray
                    )
                )
            } else {
                holder.item.cardReadMeditate.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
            }

            holder.itemView.setOnClickListener {
                list[holder.bindingAdapterPosition]?.let { it1 ->
                    readMeditateClickListener.onReadMeditateClick(it1)
                }
            }
        } else {
            holder.itemView.updateLayoutParams {
                height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) {
            Constants.ITEM_PROGRESS
        } else {
            Constants.ITEM_DATA
        }
    }


    private fun makeTextViewResizable(
        tv: TextView,
        maxLine: Int,
        expandText: String,
        viewMore: Boolean
    ) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = tv.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (maxLine == 0) {
                    val lineEndIndex = tv.layout.getLineEnd(0)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + "..." + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()),
                            tv,
                            maxLine,
                            expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )

                    var description = tv.text.trim().toString()
                    val endPos = description.length
                    val startPos = description.length - mViewMore.length
                    description = description.replace(description.substring(startPos, endPos), "")
                    val parts: ArrayList<String> =
                        description.split(" ") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */

                    val textOne = SpannableStringBuilder()
                    val textTwo = SpannableStringBuilder()
                    parts.forEachIndexed { index, data ->
                        if (index == 0 || index == 1) {
                            textOne.append("$data ")
                            textOne.setSpan(
                                StyleSpan(Typeface.BOLD),
                                0,
                                textOne.length,
                                R.style.TextViewJostBoldStyle
                            )
                        } else {
                            textTwo.append("$data ")
                            textTwo.setSpan(
                                StyleSpan(Typeface.NORMAL),
                                0,
                                textTwo.length,
                                R.style.TextViewJostRegularStyle
                            )
                        }
                    }

                    val textThree = SpannableStringBuilder()
                    textThree.append(mViewMore)
                    textThree.setSpan(
                        ForegroundColorSpan(tv.context.getColor(R.color.gold)),
                        0,
                        mViewMore.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    val textFour = SpannableStringBuilder()
                    textFour.append(textOne)
                    textFour.append(textTwo)
                    textFour.append(textThree)
                    tv.text = textFour
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length)
                        .toString() + "..." + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString(), Html.FROM_HTML_MODE_LEGACY),
                            tv,
                            maxLine,
                            expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )


                    var description = tv.text.trim().toString()

                    val endPos = description.length
                    val startPos = description.length - mViewMore.length
                    description = description.replace(description.substring(startPos, endPos), "")
                    val parts: ArrayList<String> =
                        description.split(" ") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
                    val textOne = SpannableStringBuilder()
                    val textTwo = SpannableStringBuilder()
                    parts.forEachIndexed { index, data ->
                        if (index == 0 || index == 1) {
                            textOne.append("$data ")
                            textOne.setSpan(
                                StyleSpan(Typeface.BOLD),
                                0,
                                textOne.length,
                                R.style.TextViewJostBoldStyle
                            )

                        } else {
                            textTwo.append("$data ")
                            textTwo.setSpan(
                                StyleSpan(Typeface.NORMAL),
                                0,
                                textTwo.length,
                                R.style.TextViewJostRegularStyle
                            )
                        }
                    }
                    val textThree = SpannableStringBuilder()
                    textThree.append(mViewMore)
                    textThree.setSpan(
                        ForegroundColorSpan(tv.context.getColor(R.color.gold)),
                        0,
                        mViewMore.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    val textFour = SpannableStringBuilder()
                    textFour.append(textOne)
                    textFour.append(textTwo)
                    textFour.append(textThree)

                    tv.text = textFour
                } else {
                    val lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    val text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()),
                            tv,
                            lineEndIndex,
                            expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )

                    var description: String = tv.text.trim().toString()

                    val endPos = description.length
                    val startPos = description.length - mViewMore.length

                    description = description.replace(description.substring(startPos, endPos), "")

                    val parts: ArrayList<String> =
                        description.split(" ") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
                    val textOne = SpannableStringBuilder()
                    val textTwo = SpannableStringBuilder()
                    parts.forEachIndexed { index, data ->
                        if (index == 0 || index == 1) {
                            textOne.append("$data ")
                            textOne.setSpan(
                                StyleSpan(Typeface.BOLD),
                                0,
                                textOne.length,
                                R.style.TextViewJostBoldStyle
                            )
                        } else {
                            textTwo.append("$data ")
                            textTwo.setSpan(
                                StyleSpan(Typeface.NORMAL),
                                0,
                                textTwo.length,
                                R.style.TextViewJostRegularStyle
                            )
                        }
                    }
                    val textThree = SpannableStringBuilder()
                    textThree.append(mViewMore)
                    textThree.setSpan(
                        ForegroundColorSpan(tv.context.getColor(R.color.gold)),
                        0,
                        mViewMore.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    val textFour = SpannableStringBuilder()
                    textFour.append(textOne)
                    textFour.append(textTwo)
                    textFour.append(textThree)
                    tv.text = textFour
                }
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned,
        tv: TextView,
        maxLine: Int,
        spanableText: String,
        viewMore: Boolean
    ): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false) {
                override fun onClick(widget: View) {
                    /*if (viewMore) {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, -1, mViewLess, false)
                    } else {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, 3, mViewMore, true)
                    }*/
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    interface ReadMeditateClickListener {
        fun onReadMeditateClick(data: HowToMeditateDataModel)
    }

    class ViewHolder(view: ItemReadMeditateListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ProgressViewHolder(binding: ItemLoadMoreVerticalProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val item = binding
    }
}