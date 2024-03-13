package com.app.taocalligraphy.ui.settings.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemDeviceBinding
import com.app.taocalligraphy.models.response.profile.UserSettingsApiResponse
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.printDifference
import com.app.taocalligraphy.utils.extensions.visible
import kotlinx.android.synthetic.main.item_device.view.*
import okhttp3.internal.format
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DeviceListAdapter(
    private var mDataList: MutableList<UserSettingsApiResponse.DeviceData?>?,
    private val mListener: DeviceItemClickListener
) : RecyclerView.Adapter<DeviceListAdapter.ItemViewHolder>() {

    lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        mContext = parent.context
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_device, parent, false
            )
        )
    }

    override fun getItemCount(): Int = mDataList!!.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            mDataList?.let { list ->
                if (list[position]?.systemType.equals(mContext.getString(R.string.android), true)) {
                    imgSystemType.setImageResource(R.drawable.ic_android)
//                    tvSystemType.text = "Android"
                } else if (list[position]?.systemType.equals(
                        mContext.getString(R.string.ios),
                        true
                    )
                ) {
//                    tvSystemType.text = "iPhone"
                    imgSystemType.setImageResource(R.drawable.ic_apple)
                } else if (list[position]?.systemType?.contains(
                        mContext.getString(R.string.windows),
                        true
                    ) == true
                ) {
//                    tvSystemType.text = "Windows"
                    imgSystemType.setImageResource(R.drawable.ic_window)
                } else if (list[position]?.systemType?.contains(
                        mContext.getString(R.string.mac),
                        true
                    ) == true
                ) {
//                    tvSystemType.text = "Mac"
                    imgSystemType.setImageResource(R.drawable.ic_mac)
                } else {
//                    tvSystemType.text = "Other"
                    imgSystemType.setImageResource(R.drawable.ic_other_device)
                }

                if (!list[position]?.systemType.isNullOrEmpty()) {
                    tvSystemType.text = list[position]?.systemType
                } else {
                    tvSystemType.text = mContext.getString(R.string.other)
                }

                if (mDataList?.get(position)?.location.isNullOrEmpty()) {
                    tvLocation.gone()
                } else if (mDataList?.get(position)?.location != null) {
                    tvLocation.visible()
                    tvLocation.text = "" + list[position]?.location?.trim()
                } else {
                    tvLocation.visible()
                    tvLocation.text = "" + (list[position]?.location)
                }
                tvBrowserType.text = "" + (list[position]?.browserType)
                if (list[position]?.isThisDevice == true) {
                    tvLastUsed.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.gold
                        )
                    )
                    tvLastUsed.text = mContext.getString(R.string.this_device)
                } else {
                    tvLastUsed.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.medium_grey
                        )
                    )

                    if (isTablet(mContext)) {
                        tvLastUsed.text = mContext.getString(R.string.laset_used) + " "
                    } else {
                        tvLastUsed.text = mContext.getString(R.string.laset_used) + "\n"
                    }
                    try {
                        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date1: Date =
                            format1.parse(convertUtc2Local(list[position]?.lastUsedDateTime)) as Date
                        com.app.taocalligraphy.utils.extensions.logInfo(
                            msg = "convertUtc2Local ==> ${
                                convertUtc2Local(
                                    list[position]?.lastUsedDateTime
                                )
                            }"
                        )
                        val date2 = format1.parse(convertUtc2Local(format1.format(Date())))
//                        if (isYesterday(date1)) {
//                            holder.itemView.tvLastUsed.text = "Last Used:\nYesterday"
//                        } else {
                        if (printDifference(date2, date1).length == 3) {
                            if (isTablet(mContext)) {
                                holder.itemView.tvLastUsed.text =
                                    mContext.getString(R.string.laset_used) + " " + mContext.getString(
                                        R.string.a_min_ago
                                    )
                            } else {
                                holder.itemView.tvLastUsed.text =
                                    mContext.getString(R.string.laset_used) + "\n" + mContext.getString(
                                        R.string.a_min_ago
                                    )

                            }
                        } else {
                            if (isTablet(mContext)) {
                                holder.itemView.tvLastUsed.text =
                                    "Last Used: ${printDifference(date2, date1, true)}"
                            } else {
                                holder.itemView.tvLastUsed.text =
                                    "Last Used:\n${printDifference(date2, date1, true)}"
                            }

                        }
//                        }
                    } catch (e: Exception) {
                    }
                }
                tvLogoutProfile.setOnClickListener {
                    mListener.onLogoutClick(list[position]!!, position)
                }
            }

        }
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

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface DeviceItemClickListener {
        fun onLogoutClick(data: UserSettingsApiResponse.DeviceData, position: Int)
    }

    fun updateList(arrayList: ArrayList<UserSettingsApiResponse.DeviceData?>?) {
//        mDataList?.clear()
//        mDataList?.addAll(arrayList!!)
        mDataList = arrayList
        notifyDataSetChanged()
    }

    fun deleteItem(index: Int) {
        mDataList?.removeAt(index)
        notifyDataSetChanged()
    }
}