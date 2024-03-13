package com.app.taocalligraphy.ui.notification.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemNotificationCollapseBinding
import com.app.taocalligraphy.models.response.notification_model.FetchNotificationListDataResponse
import com.app.taocalligraphy.models.response.notification_model.NotificationListDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.notification.OnItemClicked
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.printDifference
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.getHtmlString
import com.app.taocalligraphy.utils.loadImage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    var onItemClicked: OnItemClicked,
    var context: Context
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>(), OnItemClicked {

    private var list: ArrayList<NotificationListDataModel?> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setNotificationData(dataList: ArrayList<NotificationListDataModel?>) {
        list = dataList
        notifyDataSetChanged()
    }

    private val programDataAdapter =
        NotificationExpandedAdapter(this, context, ::onDeleteItem, Constants.programs)
    private val subscriptionDataAdapter =
        NotificationExpandedAdapter(this, context, ::onDeleteItem, Constants.subscription)
    private val meditationDataAdapter =
        NotificationExpandedAdapter(this, context, ::onDeleteItem, Constants.meditation)
    private val dailyWisdomDataAdapter =
        NotificationExpandedAdapter(this, context, ::onDeleteItem, Constants.dailyWisdom)
    lateinit var itemBindingUtil: ItemNotificationCollapseBinding
    lateinit var holder: NotificationViewHolder

    private val tvMoreList = HashMap<String, View>()

    override fun getItemId(position: Int): Long {
        return list[position]?.dataList?.get(0)?.id?.toLong() ?: 0
    }

    fun addMoreProgramData(
        dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?>,
        isUnreadNotification: Boolean
    ) {
        val typeList = list.find { it?.type == Constants.programs }
        typeList?.dataList?.addAll(dataList)

        programDataAdapter.addMoreData(dataList, isUnreadNotification)

        if ((typeList?.totalCount ?: "0").toInt() <= programDataAdapter.list.size)
            hideLoadMore(Constants.programs)
        else
            showLoadMore(Constants.programs)
    }

    fun addMoreSubscriptionData(
        dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?>,
        isUnreadNotification: Boolean
    ) {
        val typeList = list.find { it?.type == Constants.subscription }
        typeList?.dataList?.addAll(dataList)

        subscriptionDataAdapter.addMoreData(dataList, isUnreadNotification)

        if ((typeList?.totalCount ?: "0").toInt() <= subscriptionDataAdapter.list.size)
            hideLoadMore(Constants.subscription)
        else
            showLoadMore(Constants.subscription)
    }

    fun addMoreMeditationData(
        dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?>,
        isUnreadNotification: Boolean
    ) {
        val typeList = list.find { it?.type == Constants.meditation }

        dataList.forEach { it ->
            if (typeList?.dataList?.contains(it) == false) {
                typeList.dataList?.add(it)
            }
        }

        meditationDataAdapter.addMoreData(dataList, isUnreadNotification)

        if ((typeList?.totalCount ?: "0").toInt() <= meditationDataAdapter.list.size)
            hideLoadMore(Constants.meditation)
        else
            showLoadMore(Constants.meditation)
    }

    fun addMoreDailyWisdomData(
        dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?>,
        isUnreadNotification: Boolean
    ) {
        val typeList = list.find { it?.type == Constants.dailyWisdom }
        typeList?.dataList?.addAll(dataList)

        dailyWisdomDataAdapter.addMoreData(dataList, isUnreadNotification)
        if ((typeList?.totalCount ?: "0").toInt() <= dailyWisdomDataAdapter.list.size)
            hideLoadMore(Constants.dailyWisdom)
        else
            showLoadMore(Constants.dailyWisdom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {

        itemBindingUtil =
            ItemNotificationCollapseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NotificationViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        this.holder = holder
        val data: NotificationListDataModel? = list[position]

        when (data?.type) {
            Constants.programs -> {
                holder.item.apply {
                    rrNotificationExpandView.gone()
                    rvNotificationsExpand.gone()
                    //                tvUnreadNotificationCount.visible()
                    viewSecondNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    swipeLayout.visible()
                    llMain.visible()
                    tvLoadMore.gone()

                    val dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?> =
                        arrayListOf()

                    if (data.dataList!!.size > 0) {

                        tvName.text = data.dataList?.get(0)!!.title
                        getHtmlString(tvDescription, data.dataList?.get(0)!!.body)
                        val date2 = Date()
                        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date1: Date =
                            format1.parse(convertUtc2Local(data.dataList?.get(0)!!.notificationTime)) as Date

                        tvTime.text = printDifference(
                            date2,
                            date1,
                            isFromNotification = true
                        )

                        tvUnreadNotificationCount.text =
                            if ((data.unReadCount?.toInt() ?: 0) > 0) data.unReadCount else ""

                        if (!data.dataList?.get(0)!!.image.isNullOrEmpty()) {
                            flUserProfile.visible()
                            ivUserProfile.visible()
                            viewShimmerBg.gone()
                            ivUserProfile.loadImage(
                                data.dataList?.get(0)!!.image,
                                R.drawable.img_default_for_content,
                                true
                            )
                            ivSessionMedia.gone()
                            ivProgram.visible()
                        } else {
                            flUserProfile.visible()
                            ivUserProfile.gone()
                            viewShimmerBg.visible()
                            ivSessionMedia.gone()
                            ivProgram.visible()
                        }

                        if ((data.unReadCount?.toInt() ?: 0) <= 0) {
                            tvUnreadNotificationCount.gone()
                            viewUnreadNotification.gone()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_read)
                        } else {
                            tvUnreadNotificationCount.visible()
                            viewUnreadNotification.visible()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_unread)
                        }
                        dataList.addAll(list[holder.bindingAdapterPosition]?.dataList!!)
                    }

                    programDataAdapter.list.clear()

                    programDataAdapter.list.addAll(dataList)
                    rvNotificationsExpand.adapter = programDataAdapter
                    swipeLayout.setLockDrag(true)

                    setBottomStack(holder, data)

                }
            }
            Constants.subscription -> {

                holder.item.apply {
                    ivProgram.gone()
                    rrNotificationExpandView.gone()
                    rvNotificationsExpand.gone()
                    //                tvUnreadNotificationCount.visible()
                    viewSecondNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    swipeLayout.visible()
                    llMain.visible()
                    tvLoadMore.gone()

                    flUserProfile.visible()
                    viewShimmerBg.visible()
                    ivUserProfile.gone()
                    viewSecondNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    swipeLayout.setLockDrag(true)
                    val dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?> =
                        arrayListOf()

                    if (data.dataList!!.size > 0) {
                        tvName.text = data.dataList?.get(0)!!.title
                        getHtmlString(tvDescription, data.dataList?.get(0)!!.body)

                        val date2 = Date()
                        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date1: Date =
                            format1.parse(convertUtc2Local(data.dataList?.get(0)!!.notificationTime)) as Date

                        tvTime.text = printDifference(
                            date2,
                            date1,
                            isFromNotification = true
                        )
                        tvUnreadNotificationCount.text =
                            if ((data.unReadCount?.toInt() ?: 0) > 0) data.unReadCount else ""

                        ivUserProfile.setImageResource(R.drawable.ic_subscription)
                        if ((data.unReadCount?.toInt() ?: 0) <= 0) {
                            tvUnreadNotificationCount.gone()
                            viewUnreadNotification.gone()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_read)
                        } else {
                            tvUnreadNotificationCount.visible()
                            viewUnreadNotification.visible()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_unread)
                        }
                        dataList.addAll(list[holder.bindingAdapterPosition]?.dataList!!)
                    }

                    viewShimmerBg.setBackgroundResource(R.drawable.bd_notification_white)
                    ivSessionMedia.setImageResource(R.drawable.vd_subscription_icon)
                    ivSessionMedia.visible()

                    subscriptionDataAdapter.list.clear()

                    subscriptionDataAdapter.list.addAll(dataList)
                    rvNotificationsExpand.adapter = subscriptionDataAdapter
                    swipeLayout.setLockDrag(true)

                    setBottomStack(holder, data)
                }
            }
            Constants.meditation -> {

                holder.item.apply {
                    ivProgram.gone()
                    rrNotificationExpandView.gone()
                    rvNotificationsExpand.gone()
                    //                tvUnreadNotificationCount.visible()
                    viewSecondNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    swipeLayout.visible()
                    llMain.visible()
                    tvLoadMore.gone()

                    // Setting image for meditation
                    if (!data.dataList?.get(0)?.image.isNullOrEmpty()) {
                        flUserProfile.visible()
                        ivUserProfile.visible()
                        viewShimmerBg.visible()
                        ivSessionMedia.gone()
                        viewShimmerBg.setBackgroundResource(R.drawable.bd_notification_white)
                        ivUserProfile.loadImage(
                            data.dataList?.get(0)!!.image,
                            R.drawable.img_default_for_content,
                            true
                        )
                        ivUserProfile.setPadding(1, 1, 1, 1)
                    } else {
                        flUserProfile.visible()
                        ivUserProfile.gone()
                        viewShimmerBg.visible()
                        ivSessionMedia.visible()
                        viewShimmerBg.setBackgroundResource(R.drawable.bd_notification_white)
                        ivSessionMedia.setImageResource(R.drawable.vd_meditation_timer_profile)
                    }

                    swipeLayout.setLockDrag(true)

                    val dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?> =
                        arrayListOf()
                    if (data.dataList!!.size > 0) {
                        dataList.addAll(list[holder.bindingAdapterPosition]?.dataList!!)
                        tvName.text = data.dataList?.get(0)!!.title
                        getHtmlString(tvDescription, data.dataList?.get(0)!!.body)
                        val date2 = Date()
                        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date1: Date =
                            format1.parse(convertUtc2Local(data.dataList?.get(0)!!.notificationTime)) as Date

                        tvTime.text = printDifference(
                            date2,
                            date1,
                            isFromNotification = true
                        )
                        tvUnreadNotificationCount.text =
                            if ((data.unReadCount?.toInt() ?: 0) > 0) data.unReadCount else ""

                        if ((data.unReadCount?.toInt() ?: 0) <= 0) {
                            tvUnreadNotificationCount.gone()
                            viewUnreadNotification.gone()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_read)
                        } else {
                            tvUnreadNotificationCount.visible()
                            viewUnreadNotification.visible()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_unread)
                        }
                    }
                    if ((data.dataList?.size ?: 0) == 1) {
                        viewSecondNotificationBg.gone()
                        viewThirdNotificationBg.gone()

                        llMain.gone()
                        rvNotificationsExpand.visible()
                    } else if (data.dataList?.size == 2) {
                        viewSecondNotificationBg.visible()
                        viewThirdNotificationBg.gone()
                    }

                    meditationDataAdapter.list.clear()

                    meditationDataAdapter.list.addAll(dataList)
                    swipeLayout.setLockDrag(true)

                    rvNotificationsExpand.adapter = meditationDataAdapter

                    setBottomStack(holder, data)
                }
            }
            Constants.dailyWisdom -> {
                holder.item.apply {
                    ivProgram.gone()
                    rrNotificationExpandView.gone()
                    rvNotificationsExpand.gone()
                    //                tvUnreadNotificationCount.visible()
                    viewSecondNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    viewThirdNotificationBg.visible()
                    swipeLayout.visible()
                    llMain.visible()
                    tvLoadMore.gone()

                    if (!data.dataList?.get(0)?.image.isNullOrEmpty()) {
                        flUserProfile.visible()
                        ivUserProfile.visible()
                        viewShimmerBg.visible()
                        ivSessionMedia.gone()
                        viewShimmerBg.setBackgroundResource(R.drawable.bd_notification_white)
                        ivUserProfile.loadImage(
                            data.dataList?.get(0)!!.image,
                            R.drawable.img_default_for_content,
                            true
                        )
                        ivUserProfile.setPadding(1, 1, 1, 1)
                    } else {
                        flUserProfile.visible()
                        ivUserProfile.gone()
                        viewShimmerBg.visible()
                        ivSessionMedia.visible()
                        viewShimmerBg.setBackgroundResource(R.drawable.bd_notification_white)
                        ivSessionMedia.setImageResource(R.drawable.ic_daily_wisdom)
                    }
                    swipeLayout.setLockDrag(true)
                    val dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?> =
                        arrayListOf()

                    if (data.dataList!!.size > 0) {
                        tvName.text = data.dataList?.get(0)!!.title
                        getHtmlString(tvDescription, data.dataList?.get(0)!!.body)
                        val date2 = Date()
                        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date1: Date =
                            format1.parse(convertUtc2Local(data.dataList?.get(0)!!.notificationTime)) as Date

                        tvTime.text = printDifference(
                            date2,
                            date1,
                            isFromNotification = true
                        )
                        tvUnreadNotificationCount.text =
                            if ((data.unReadCount?.toInt() ?: 0) > 0) data.unReadCount else ""

                        if ((data.unReadCount?.toInt() ?: 0) <= 0) {
                            tvUnreadNotificationCount.gone()
                            viewUnreadNotification.gone()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_read)
                        } else {
                            tvUnreadNotificationCount.visible()
                            viewUnreadNotification.visible()
                            holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_unread)
                        }

                        dataList.addAll(list[holder.bindingAdapterPosition]?.dataList!!)
                    }

                    swipeLayout.setLockDrag(true)
                    dailyWisdomDataAdapter.list.clear()
                    dailyWisdomDataAdapter.list.addAll(dataList)
                    rvNotificationsExpand.adapter = dailyWisdomDataAdapter

                    setBottomStack(holder, data)

                }
            }
        }

        holder.item.apply {
            txShowLessMessage.clickWithDebounce {
                rrNotificationExpandView.gone()
                rvNotificationsExpand.gone()
//                tvUnreadNotificationCount.visible()
                tvLoadMore.gone()
                swipeLayout.visible()
                llMain.visible()

                tvUnreadNotificationCount.text = data?.unReadCount ?: ""

                if ((data?.unReadCount?.toInt() ?: 0) <= 0) {
                    tvUnreadNotificationCount.gone()
                    viewUnreadNotification.gone()
                    holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_read)
                } else {
                    tvUnreadNotificationCount.visible()
                    viewUnreadNotification.visible()
                    holder.item.llNotificationBg.setBackgroundResource(R.drawable.bg_notification_unread)
                }

                setBottomStack(holder, data!!)
                notifyItemChanged(holder.bindingAdapterPosition)
            }

            llMain.clickWithDebounce {
                if (rrNotificationExpandView.isGone) {
                    swipeLayout.setLockDrag(false)
                    viewSecondNotificationBg.gone()
                    viewThirdNotificationBg.gone()
                    tvUnreadNotificationCount.gone()

                    rvNotificationsExpand.visible()
                    rrNotificationExpandView.visible()

                    swipeLayout.gone()
                    llMain.gone()

//                    expand(rvNotificationsExpand,llNotificationBg.width)

                    if ((data?.totalCount?.toInt()
                            ?: data!!.dataList!!.size) > (data?.dataList?.size ?: 0)
                    )
                        tvLoadMore.visible()
                    else
                        tvLoadMore.gone()

                    tvMoreList[data?.type ?: ""] = tvLoadMore
                }
            }

            tvLoadMore.setOnClickListener {
                list[holder.bindingAdapterPosition]?.type?.let { it1 ->
                    onItemClicked.loadMoreClicked(
                        it1
                    )
                    tvMoreList[it1] = tvLoadMore
                }
            }
            tvClearMessage.setOnClickListener {
                deleteNotificationConfirmationDialog(holder, false)
            }
            ivDelete.setOnClickListener {
                deleteNotificationConfirmationDialog(holder, true)
            }
        }
    }

    private fun setBottomStack(holder: NotificationViewHolder, data: NotificationListDataModel) {
        if (data.dataList?.size == 1) {
            holder.item.viewSecondNotificationBg.gone()
            holder.item.viewThirdNotificationBg.gone()

            holder.item.swipeLayout.gone()
            holder.item.rvNotificationsExpand.visible()
        } else if (data.dataList?.size == 2) {
            holder.item.viewSecondNotificationBg.visible()
            holder.item.viewThirdNotificationBg.gone()
        }
    }

    private fun deleteNotificationConfirmationDialog(
        holder: NotificationViewHolder,
        isFromSingleDelete: Boolean
    ) {
        val title = ""
        val description = "" + context.getString(R.string.are_you_sure_want_to_clear_notification)
        val builder = AlertDialog.Builder(context, R.style.DialogTheme)

        builder.setTitle("" + title)
            .setMessage("" + description)
            .setCancelable(true)
            .setPositiveButton(
                "" + context.getString(R.string.yes)
            ) { dialog, _ ->
                dialog!!.dismiss()
                if (isFromSingleDelete) {
                    val dataList: ArrayList<Int> = arrayListOf()
                    dataList.add(list[holder.bindingAdapterPosition]?.dataList?.get(0)!!.id!!.toInt())
                    list[holder.bindingAdapterPosition]?.dataList?.removeAt(0)
                    val totalCount =
                        list[holder.bindingAdapterPosition]?.totalCount?.toInt()?.minus(1)
                    list[holder.bindingAdapterPosition]?.totalCount = totalCount.toString()
                    onItemClicked.onDeleteClicked(
                        dataList,
                        ""
                    )
                } else {
                    val type = list[holder.bindingAdapterPosition]?.type
                    if (type != null) {
                        val dataList = list[holder.bindingAdapterPosition]?.dataList?.map {
                            it?.id?.toInt()
                        }
                        onItemClicked.onDeleteClicked(dataList as ArrayList<Int>, type, true)
                        list.removeAt(holder.bindingAdapterPosition)
                        notifyItemRemoved(holder.bindingAdapterPosition)
                    }
                }
            }
            .setNegativeButton(
                "" + context.getString(R.string.no)
            ) { dialog, _ -> dialog!!.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class NotificationViewHolder(view: ItemNotificationCollapseBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    fun hideLoadMore(type: String) {
        tvMoreList[type]?.gone()
        //holder.item.tvLoadMore.gone()
    }

    private fun showLoadMore(type: String) {
        tvMoreList[type]?.visible()
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

    override fun onInnerItemClicked(position: Int, type: String) {
        onItemClicked.onInnerItemClicked(position, type)
    }

    override fun onDeleteClicked(idList: ArrayList<Int>, type: String?, isAllDeleted: Boolean) {
        onItemClicked.onDeleteClicked(idList, type)
    }

    override fun loadMoreClicked(type: String) {
    }

    override fun onNotificationRead(idList: List<Int>) {
        onItemClicked.onNotificationRead(idList)
    }

    private fun onDeleteItem(type: String) {
        val subList = list.find { it?.type == type }
//        if (subList?.dataList?.get(pos)?.notificationRead != true)
//            subList?.unReadCount = ((subList?.unReadCount?.toInt() ?: 1) - 1).toString()
//        subList?.dataList?.removeAt(pos)

        val totalCount = (subList?.totalCount ?: "0").toInt() - 1
        subList?.totalCount = totalCount.toString()
    }

    /* fun expand(v: View, width : Int) {

         v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
         val targetHeight = v.measuredHeight

         // Older versions of android (pre API 21) cancel animations for views with a height of 0.
         v.layoutParams.height = 1
         v.visibility = View.VISIBLE

         val va: ValueAnimator = ValueAnimator.ofInt(1, targetHeight)
         va.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
             override fun onAnimationUpdate(animation: ValueAnimator) {
                 v.layoutParams.height = (animation.getAnimatedValue() as Int)
                 v.requestLayout()
             }
         })
         va.addListener(object : Animator.AnimatorListener {
             override fun onAnimationEnd(animation: Animator?) {
                 v.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
             }

             override fun onAnimationStart(animation: Animator?) {}
             override fun onAnimationCancel(animation: Animator?) {}
             override fun onAnimationRepeat(animation: Animator?) {}
         })
         va.setDuration(1500)
         va.setInterpolator(OvershootInterpolator())
         va.start()
     }

     fun collapse(v: View) {
         val initialHeight = v.measuredHeight
         val va: ValueAnimator = ValueAnimator.ofInt(initialHeight, 0)
         va.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
             override fun onAnimationUpdate(animation: ValueAnimator) {
                 v.layoutParams.height = (animation.getAnimatedValue() as Int)
                 v.requestLayout()
             }
         })
         va.addListener(object : Animator.AnimatorListener {
             override fun onAnimationEnd(animation: Animator?) {
                 v.visibility = View.GONE
//                 itemBindingUtil.swipeLayout.visible()
//                 itemBindingUtil.llMain.visible()
             }

             override fun onAnimationStart(animation: Animator?) {}
             override fun onAnimationCancel(animation: Animator?) {}
             override fun onAnimationRepeat(animation: Animator?) {}
         })
         va.setDuration(1000)
         va.setInterpolator(DecelerateInterpolator())
         va.start()
     }*/

}