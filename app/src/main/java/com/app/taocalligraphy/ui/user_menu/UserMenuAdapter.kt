package com.app.taocalligraphy.ui.user_menu

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemUserMenuBinding
import com.app.taocalligraphy.models.UserMenuDetails
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_user_menu.view.*

class UserMenuAdapter(
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<UserMenuAdapter.ViewHolder>() {

    private var list = ArrayList<UserMenuDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemUserMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.ivIcon.loadImage(
            list[position].image,
            R.drawable.img_default_for_content
        )
        holder.itemView.tvTitle.text = "" + data.title

        if(data.canAccess ?: false) {
            holder.itemView.ivLock.visible()
        }
        else
            holder.itemView.ivLock.gone()

        holder.itemView.card.setOnClickListener {
            onItemClickListener.onItemClicked(data)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: ItemUserMenuBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    fun updateList(
        meditationDataList: ArrayList<UserMenuDetails>,
    ) {
        this.list = meditationDataList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(item: UserMenuDetails)
    }
}