package com.app.taocalligraphy.ui.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCalendarBinding
import com.app.taocalligraphy.models.WeekDatesDayListModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.calendar.adapter.CalendarEventsAdapter
import com.app.taocalligraphy.ui.calendar.adapter.CalendarMoreRoomsAdapter
import com.app.taocalligraphy.ui.calendar.dialog.PaidSessionBookingDialog
import com.app.taocalligraphy.ui.wellness.adapter.WeekCalendarAdapter
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImageProfile
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class CalendarActivity : BaseActivity<ActivityCalendarBinding>(),
    WeekCalendarAdapter.OnWeekAdapterItemClickListener,
    CalendarMoreRoomsAdapter.SignupOptionListener {

    private var mWeekDatesDayListModelDummy = mutableListOf<WeekDatesDayListModel>()
    private var mWeekCalendarAdapter: WeekCalendarAdapter? = null

    private val mCalendarEventsAdapter by lazy {
        return@lazy CalendarEventsAdapter(this)
    }


    private val mCalendarMoreRoomsAdapter by lazy {
        return@lazy CalendarMoreRoomsAdapter(this,this)
    }

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, CalendarActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_calendar

    override fun initView() {
        setupToolbar()
        find7DatesFromToday()

        mBinding.rvWeeks.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mWeekCalendarAdapter = WeekCalendarAdapter(mWeekDatesDayListModelDummy, this)
        mBinding.rvWeeks.adapter = mWeekCalendarAdapter

        mBinding.rvCalendarEvents.adapter = mCalendarEventsAdapter
        mBinding.rvMoreRoomsOptions.adapter = mCalendarMoreRoomsAdapter
    }

    private fun setupToolbar() {
        mBinding.mToolbar.cardProfile.visible()
        mBinding.mToolbar.ivBackToolbar.visible()

        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun find7DatesFromToday() {
        mWeekDatesDayListModelDummy.clear()
        val today = DateTime().withTimeAtStartOfDay()
        val formatterDate: DateTimeFormatter = DateTimeFormat.forPattern("dd") //"dd/MM/yyyy"
        val formatterDay: DateTimeFormatter = DateTimeFormat.forPattern("EEE")
        for (i in 0..6) {
            val mDate = formatterDate.print(today.plusDays(i))
            val mDay = formatterDay.print(today.plusDays(i))
            mWeekDatesDayListModelDummy.add(WeekDatesDayListModel(mDate, mDay, (i % 2 == 0)))
        }
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
    }

    override fun onWeekAdapterClick(weekDatesDayListModel: WeekDatesDayListModel) {
    }

    override fun onSignUpClick() {
        val dialog =
            PaidSessionBookingDialog(this)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            initView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (!isFinishing) {
                showNoInternetDialog()
            }
        }
    }
}