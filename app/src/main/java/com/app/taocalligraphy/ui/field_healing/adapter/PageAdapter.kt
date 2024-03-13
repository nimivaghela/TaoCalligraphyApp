package com.app.taocalligraphy.ui.field_healing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.utils.extensions.isTablet
import io.github.vejei.carouselview.CarouselAdapter

class PageAdapter(
    private var list: ArrayList<Page>,
    private var onItemClicked: OnItemClicked
) :
    CarouselAdapter<PageAdapter.ViewHolder>() {
    var selectedPos: Int = 0


    data class Page(val content: String)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentTextView: TextView = itemView.findViewById<TextView>(R.id.text_view_content)

        fun bind(page: Page) {
            contentTextView.text = page.content
        }
    }

    override fun onCreatePageViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_page, parent, false)
        )
    }

    override fun onBindPageViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        if (selectedPos == position) {
            holder.contentTextView.background = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.bg_tab_background_select
            )
            if (isTablet(holder.itemView.context)) {
                holder.contentTextView.textSize = 24f
            } else {
                holder.contentTextView.textSize = 16f
            }

            holder.contentTextView.setTextColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.contentTextView.background = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.bg_tab_background_default
            )
            if (isTablet(holder.itemView.context)) {
                holder.contentTextView.textSize = 18f
            } else {
                holder.contentTextView.textSize = 12f
            }

            holder.contentTextView.setTextColor(holder.itemView.context.getColor(R.color.gold))
        }
        holder.itemView.setOnClickListener {
            onItemClicked.onItemClicked(position)
        }
    }

    override fun getPageCount(): Int {
        return list.size
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    interface OnItemClicked {
        fun onItemClicked(position: Int)
    }

}