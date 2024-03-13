package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.app.taocalligraphy.R
import kotlinx.android.synthetic.main.item_custom_spinner.view.*

class CustomSpinnerAdapter(ctx: Context, countries: ArrayList<String>) :
    ArrayAdapter<String>(ctx, 0, countries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val data = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_custom_spinner, parent, false)

        data?.let { it ->
            view.tvSpinnerText.text = it
        }
        return view
    }
}