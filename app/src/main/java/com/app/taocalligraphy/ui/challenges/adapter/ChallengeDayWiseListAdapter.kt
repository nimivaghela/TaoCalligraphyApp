package com.app.taocalligraphy.ui.challenges.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemProgramsDayWiseListBinding
import com.app.taocalligraphy.utils.MySpannable

class ChallengeDayWiseListAdapter(
    private var context: Context,val stepClickListener:StepClickListener
) : RecyclerView.Adapter<ChallengeDayWiseListAdapter.ViewHolder>() {
    private var mViewLess = "View Less"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemProgramsDayWiseListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tvProgramsDesc.text = fromHtml(context.getString(R.string.lorem_ipsum))
        holder.item.tvProgramsDesc.movementMethod = LinkMovementMethod.getInstance();
        makeTextViewResizable(holder.item.tvProgramsDesc, 3, context.getString(R.string.mViewMore), true)

        when (position) {
            0 -> {
                holder.item.ivBackgroundImage.setImageResource(R.drawable.bg_gold)
                holder.item.tvProgramsName.text = context.getString(R.string.show_gratitude_to_someone)
                holder.item.tvTime.visibility = View.GONE
                holder.item.tvStep.visibility = View.VISIBLE
                holder.item.tvDuration.visibility = View.GONE

                holder.item.ivPlay.visibility = View.GONE
                holder.item.ivDone.visibility = View.GONE
                //holder.item.ivWhiteBackground.visibility = View.GONE
            }
            1 -> {
                holder.item.ivBackgroundImage.setImageResource(R.drawable.ic_evening_gratitude)
                holder.item.tvProgramsName.text = context.getString(R.string.noon_refresh_recharge)
                holder.item.tvTime.visibility = View.VISIBLE
                holder.item.tvTime.text = context.getString(R.string.six_pm)
                holder.item.tvStep.visibility = View.GONE
                holder.item.tvDuration.visibility = View.VISIBLE

                val params = holder.item.fmMain.layoutParams as GridLayoutManager.LayoutParams
                params.setMargins(0, 200, 0, 0)
                holder.item.fmMain.layoutParams = params

                holder.item.ivPlay.visibility = View.VISIBLE
                holder.item.ivDone.visibility = View.GONE
                //holder.item.ivWhiteBackground.visibility = View.VISIBLE
            }
        }

        holder.itemView.setOnClickListener {
            stepClickListener.onStepClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    class ViewHolder(view: ItemProgramsDayWiseListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    @SuppressWarnings("deprecation")
    fun fromHtml(html: String?): Spanned {
        return when {
            html == null -> {
                SpannableString("")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
            }
            else -> {
                Html.fromHtml(html)
            }
        }
    }

    private fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    val lineEndIndex = tv.layout.getLineEnd(0)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE)
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE)
                } else {
                    val lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    val text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText, viewMore), TextView.BufferType.SPANNABLE)
                }
            }
        })
    }

    private fun addClickablePartTextViewResizable(strSpanned: Spanned, tv: TextView, maxLine: Int, spanableText: String, viewMore: Boolean): SpannableStringBuilder? {
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

    interface StepClickListener{
        fun onStepClick(adapterPosition: Int)
    }
}