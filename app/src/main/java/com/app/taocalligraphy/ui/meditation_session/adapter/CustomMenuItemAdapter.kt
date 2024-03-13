package com.app.taocalligraphy.ui.meditation_session.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.app.taocalligraphy.R
import com.app.taocalligraphy.models.IconNamePopupModel
import com.skydoves.powermenu.MenuBaseAdapter


class CustomMenuItemAdapter : MenuBaseAdapter<IconNamePopupModel>() {

    override fun getView(index: Int, view: View?, viewGroup: ViewGroup?): View {
        val context: Context = viewGroup!!.context
        var newView: View? = null
        newView = if (view == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.item_custom_popup_list, viewGroup, false)
        } else {
            view
        }

        val item: IconNamePopupModel = getItem(index) as IconNamePopupModel
        val icon: ImageView = newView!!.findViewById(R.id.ivIcon)
        val name: TextView = newView.findViewById(R.id.tvName)
        val viewSeparator: View = newView.findViewById(R.id.viewSeparator)
        if (item.showDivider) {
            icon.isGone = true
            name.isGone = true
            viewSeparator.isVisible = true
        } else {
            viewSeparator.isGone = true
            icon.isVisible = true
            name.isVisible = true
            icon.setImageDrawable(item.icon)
            name.text = item.name
        }
        return super.getView(index, newView, viewGroup)
    }

}