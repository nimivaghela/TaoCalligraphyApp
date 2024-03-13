package com.app.taocalligraphy.ui.challenges.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemInProgressChallengesListBinding

class InProgressChallengesAdapter(private val progressClickListener:ProgressClickListener) :
    RecyclerView.Adapter<InProgressChallengesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemInProgressChallengesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //itemBindingUtil.root.layoutParams.width = (parent.width * 0.7).toInt()
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            progressClickListener.onProgressChallengesClick()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    class ViewHolder(view: ItemInProgressChallengesListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ProgressClickListener{
        fun onProgressChallengesClick()
    }
}