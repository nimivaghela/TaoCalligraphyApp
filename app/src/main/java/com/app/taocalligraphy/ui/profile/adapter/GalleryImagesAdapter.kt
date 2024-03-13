package com.app.taocalligraphy.ui.profile.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemGalaryImageListBinding
import com.app.taocalligraphy.utils.loadImageProfile


class GalleryImagesAdapter(val galleryImageClickListener: GalleryImageClickListener) :
    RecyclerView.Adapter<GalleryImagesAdapter.ViewHolder>() {

    var galleryImageList = ArrayList<Uri?>()
    var galleryImageId = ArrayList<Long?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemGalaryImageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = galleryImageList[holder.bindingAdapterPosition]
        val mediaId = galleryImageId[holder.bindingAdapterPosition]
        if (uri == null) {
            holder.item.cardHostImage.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.medium_grey
                )
            )
            holder.item.ivCamera.isVisible = true
            holder.item.ivGalleryImage.isGone = true
        } else {
            holder.item.cardHostImage.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.transparent
                )
            )
            holder.item.ivCamera.isGone = true
            holder.item.ivGalleryImage.isVisible = true

            holder.item.ivGalleryImage.loadImageProfile(
                uri,
                R.drawable.ic_profile_default
            )
        }

        holder.itemView.setOnClickListener {
            if (uri != null) {
                galleryImageClickListener.onGalleryImageClick(uri)
            } else {
                galleryImageClickListener.onCameraClick()
            }
        }
    }

    fun loadThumbnailFromUri(uri: Uri, id: Long, context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.loadThumbnail(
                uri, Size(250, 250),
                CancellationSignal()
            )
        } else {
            val option = BitmapFactory.Options()
            option.outHeight = 250
            option.outWidth = 250
            MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver,
                id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                option
            )
        }
    }

    override fun getItemCount(): Int {
        return galleryImageList.size
    }

    class ViewHolder(view: ItemGalaryImageListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface GalleryImageClickListener {
        fun onGalleryImageClick(uri: Uri)
        fun onCameraClick()
    }
}