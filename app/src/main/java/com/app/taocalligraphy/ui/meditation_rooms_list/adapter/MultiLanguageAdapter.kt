package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.models.response.profile.LanguageListApiResponse
import kotlinx.android.synthetic.main.item_language.view.*

class MultiLanguageAdapter(private var onSelect: OnSelect) :
    RecyclerView.Adapter<MultiLanguageAdapter.ViewHolder>() {

    private var arrayList: List<LanguageListApiResponse.Data> = ArrayList()
    private var selectedLang: ArrayList<Int> = ArrayList()

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_multi_language, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = arrayList[position]
        holder.itemView.tvLanguage.text = language.language

        if (selectedLang.contains(language.languageId)) {
            holder.itemView.ivRadio.setImageResource(R.drawable.vd_ellipse_tick)
        } else {
            holder.itemView.ivRadio.setImageResource(R.drawable.vd_ellipse_blank)
        }

        holder.itemView.setOnClickListener {
            if (selectedLang.contains(language.languageId)) {
                selectedLang.remove(language.languageId)
                holder.itemView.ivRadio.setImageResource(R.drawable.vd_ellipse_blank)
            } else {
                selectedLang.add(language.languageId ?: 0)
                holder.itemView.ivRadio.setImageResource(R.drawable.vd_ellipse_tick)
            }
            onSelect.clickOnItem(selectedLang)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnSelect {
        fun clickOnItem( selectedLang: ArrayList<Int>)
    }

    fun updateList(
        addressList: ArrayList<LanguageListApiResponse.Data>,
        selectedLang: ArrayList<Int>
    ) {
        arrayList = addressList
        this.selectedLang = selectedLang
        notifyDataSetChanged()
    }
}