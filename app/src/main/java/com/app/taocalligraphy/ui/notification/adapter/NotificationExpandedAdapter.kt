package com.app.taocalligraphy.ui.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemNotificationCollapseBinding
import com.app.taocalligraphy.models.response.notification_model.FetchNotificationListDataResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.notification.OnItemClicked
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.printDifference
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.getHtmlString
import com.app.taocalligraphy.utils.loadImage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotificationExpandedAdapter(
    var onItemClicked: OnItemClicked,
    var context: Context,
    var onItemDelete: (String) -> Unit,
    var type : String
) :
    RecyclerView.Adapter<NotificationExpandedAdapter.ViewHolder>() {

    var list: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?> =
        arrayListOf()

    var isUnreadNotification = false

    fun addMoreData(dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?>, isUnreadNotification:Boolean) {
        var first = list.size
        list.addAll(dataList)
        this.isUnreadNotification = isUnreadNotification
        notifyItemRangeInserted(first, list.size - first)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemNotificationCollapseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.item.apply {
            tvName.text = data?.title
            getHtmlString(tvDescription, data?.body)
            val date2 = Date()
            val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date1: Date =
                format1.parse(convertUtc2Local(data?.notificationTime)) as Date

            tvTime.text = printDifference(
                date2,
                date1,
                isFromNotification = true
            )
            if (!data!!.image.isNullOrEmpty()) {
                flUserProfile.visible()
                ivUserProfile.visible()

//                if(data.notificationType!= Constants.programs)

                viewShimmerBg.setBackgroundResource(R.drawable.bd_notification_white)

                ivSessionMedia.visible()
                ivProgram.gone()
                viewShimmerBg.gone()
                ivUserProfile.loadImage(
                    data.image,
                    R.drawable.img_default_for_content,
                    true
                )
                ivUserProfile.setPadding(1, 1, 1, 1)

                when (data.notificationType) {
                    Constants.programs -> {
                        ivProgram.visible()
                        ivSessionMedia.gone()
                    }
                    Constants.meditation -> ivSessionMedia.setImageResource(R.drawable.ic_meditation_timer)
                    Constants.subscription -> ivSessionMedia.setImageResource(R.drawable.ic_subscription)
                    else -> ivSessionMedia.setImageResource(R.drawable.ic_daily_wisdom)
                }
            } else {
                flUserProfile.visible()
                ivUserProfile.gone()
                viewShimmerBg.visible()
                ivSessionMedia.visible()
                ivProgram.gone()
                viewShimmerBg.setBackgroundResource(R.drawable.bd_notification_white)

                when (data.notificationType) {
                    Constants.programs -> {
                        ivProgram.visible()
                        ivSessionMedia.gone()
                    }
                    Constants.meditation -> ivSessionMedia.setImageResource(R.drawable.ic_meditation_timer)
                    Constants.subscription -> ivSessionMedia.setImageResource(R.drawable.ic_subscription)
                    else -> ivSessionMedia.setImageResource(R.drawable.ic_daily_wisdom)
                }
            }

            if (data.notificationRead == true) {
                holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_read)
            } else {
                holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_unread)
            }

            if (list.size == 1 && !(list.get(0)?.notificationRead ?: false)) {
                tvUnreadNotificationCount.visible()
                tvUnreadNotificationCount.text = "1"
            } else
                tvUnreadNotificationCount.gone()


            llMain.setOnClickListener {
                when (list[holder.bindingAdapterPosition]?.notificationType) {
                    (Constants.subscription) -> {

                        if (list.size == 1 && !(list.get(0)?.notificationRead ?: false)) {
//                            list.get(0)?.notificationRead = true
                            tvUnreadNotificationCount.gone()
                        }

                        onItemClicked.onInnerItemClicked(
                            holder.bindingAdapterPosition,
                            Constants.subscription
                        )
                    }
                    (Constants.programs) -> {
                        if (list.size == 1 && !(list.get(0)?.notificationRead ?: false)) {
//                            list.get(0)?.notificationRead = true
                            tvUnreadNotificationCount.gone()
                        }
                        onItemClicked.onInnerItemClicked(
                            holder.bindingAdapterPosition,
                            Constants.programs
                        )
                    }
                    (Constants.meditation) -> {
                        if (list.size == 1 && !(list.get(0)?.notificationRead ?: false)) {
                            list.get(0)?.notificationRead = true
                            tvUnreadNotificationCount.gone()
                        }

                        onItemClicked.onInnerItemClicked(
                            holder.bindingAdapterPosition,
                            Constants.meditation
                        )
                    }
                    (Constants.dailyWisdom) -> {
                        if (list.size == 1 && !(list.get(0)?.notificationRead ?: false)) {
                            list.get(0)?.notificationRead = true
                            tvUnreadNotificationCount.gone()
                        }
                        onItemClicked.onInnerItemClicked(
                            holder.bindingAdapterPosition,
                            Constants.dailyWisdom
                        )
                    }
                }
            }

            ivDelete.setOnClickListener {
                deleteNotificationConfirmationDialog(holder)
            }

            if (isUnreadNotification && position == 0) {
                viewUnreadNotification.visible()
            } else {
                viewUnreadNotification.gone()
            }

            if (data.notificationRead == false) {
                onItemClicked.onNotificationRead(arrayListOf(data.id?.toInt() ?: 0))
                data.notificationRead = true
            }
        }
    }

    private fun deleteNotificationConfirmationDialog(
        holder: ViewHolder
    ) {
        val title = ""
        var description = ""

        description = "" + context.getString(R.string.are_you_sure_want_to_clear_notification)
        val builder = AlertDialog.Builder(context, R.style.DialogTheme)

        builder.setTitle("" + title)
            .setMessage("" + description)
            .setCancelable(true)
            .setPositiveButton(
                "" + context.getString(R.string.yes)
            ) { dialog, _ ->
                dialog!!.dismiss()
                val dataList: ArrayList<Int> = arrayListOf()
                dataList.add(list[holder.bindingAdapterPosition]?.id!!.toInt())
                onItemClicked.onDeleteClicked(
                    dataList,
                    list[holder.bindingAdapterPosition]?.notificationType ?: ""
                )
                onItemDelete(type)
                list.removeAt(holder.bindingAdapterPosition)
                notifyItemRemoved(holder.bindingAdapterPosition)
            }
            .setNegativeButton(
                "" + context.getString(R.string.no)
            ) { dialog, which -> dialog!!.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun convertUtc2Local(utcTime: String?): String {
        var time = ""
        if (utcTime != null) {
            val utcFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var gpsUTCDate: Date? = null //from  ww  w.j  a va 2 s  . c  o  m
            try {
                gpsUTCDate = utcFormatter.parse(utcTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val localFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            localFormatter.timeZone = TimeZone.getDefault()
            assert(gpsUTCDate != null)
            time = localFormatter.format(gpsUTCDate!!.time)
        }
        return time
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: ItemNotificationCollapseBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

//    interface OnItemClicked {
//        fun onInnerItemClicked(position: Int)
//        fun onDeleteClicked(position: Int)
//    }
}