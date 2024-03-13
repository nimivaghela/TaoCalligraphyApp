package com.app.taocalligraphy.ui.program.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemContentReviewBinding
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.models.response.program.UserProgramApiResponse

class ProgramContentReviewAdapter : RecyclerView.Adapter<ProgramContentReviewAdapter.ViewHolder>() {

    var reviewList = ArrayList<UserProgramApiResponse.Reviews>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemContentReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviewList[holder.bindingAdapterPosition]
        holder.item.tvReviewTitle.text = review.tagValue
        holder.item.tvReviewCount.text = (review.count ?: 0).toString()
        val maxRating = reviewList.maxOf {
            it.count ?: 0
        }

        val progress: Double = (review.count?.toDouble() ?: 0.0) / maxRating.toDouble()
        holder.item.progressRating.progress = 100

        holder.item.progressRating.post(Runnable {
            holder.item.progressRating.getLayoutParams().width =
                (holder.item.progressRating.width * progress).toInt()
            holder.item.progressRating.requestLayout()
        })
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    class ViewHolder(view: ItemContentReviewBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

}