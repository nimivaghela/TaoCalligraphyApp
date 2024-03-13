package com.app.taocalligraphy.ui.meditation_session

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCreateMeditationSessionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.meditation_session.adapter.BannerAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.SessionCoHostAddAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.TopicSelectionAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.WeeklyRepeatAdapter
import com.app.taocalligraphy.ui.meditation_session.dialog.CancelSessionDialog
import com.app.taocalligraphy.ui.meditation_session.dialog.MonthlyDaySelectionDialog
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CreateMeditationSessionActivity : BaseActivity<ActivityCreateMeditationSessionBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity, isCreateNewSession: Boolean) {
            val intent = Intent(activity, CreateMeditationSessionActivity::class.java)
            intent.putExtra(Constants.isCreateNewSession, isCreateNewSession)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_create_meditation_session
    }

    private val isCreateNewSession by lazy {
        return@lazy intent.extras?.getBoolean(Constants.isCreateNewSession, true) ?: true
    }

    private val topicSelectionAdapter by lazy {
        return@lazy TopicSelectionAdapter()
    }

    private val bannerAdapter by lazy {
        return@lazy BannerAdapter()
    }

    private val weeklyRepeatAdapter by lazy {
        return@lazy WeeklyRepeatAdapter(this)
    }

    private val sessionCoHostAddAdapter by lazy {
        return@lazy SessionCoHostAddAdapter()
    }

    var selectedRepeatDays = "1"
    var selectedRepeatType = 1

    override fun initView() {
        setupToolbar(if (isCreateNewSession) getString(R.string.new_session) else getString(R.string.edit_session))
        setCalenderData()
        mBinding.rvTopic.adapter = topicSelectionAdapter
        mBinding.rvBanner.adapter = bannerAdapter
        mBinding.rvWeekly.adapter = weeklyRepeatAdapter
        mBinding.rvCoHost.adapter = sessionCoHostAddAdapter

        if (!isCreateNewSession) {
            mBinding.btnScheduleSession.text = getString(R.string.save)
            mBinding.llCancelSession.isVisible = true
        }
    }

    private fun setCalenderData() {
        mBinding.calendarView.state().edit().setMinimumDate(CalendarDay.today()).commit()

        mBinding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                if (day == null) {
                    return true
                }
                day.let {
                    if (it.isBefore(CalendarDay.today())) {
                        return true
                    }
                }
                return false
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun decorate(view: DayViewFacade?) {
                view?.setSelectionDrawable(
                    resources.getDrawable(
                        R.drawable.btn_calender_selector,
                        null
                    )
                )
            }
        })

        mBinding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day?.equals(CalendarDay.today()) ?: false
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun decorate(view: DayViewFacade?) {
                view?.setSelectionDrawable(
                    resources.getDrawable(
                        R.drawable.selector_btn_calender_selector_today,
                        null
                    )
                )
            }
        })
    }

    private fun setupToolbar(title: String) {
        setToolbar(
            mBinding.toolbar.innerToolbar,
            title,
            true,
        )
    }

    override fun observeApiCallbacks() {
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun handleListener() {
        mBinding.etSessionDescription.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }

        mBinding.rrAccessHeader.setOnClickListener {
            animateSessionAccess()
        }

        mBinding.tvPublicSession.setOnClickListener {
            animateSessionAccess()
            setTextViewOnSessionAccessTypeChange(
                getString(R.string.public_session),
                mBinding.tvPublicSession,
                mBinding.tvPrivateSession
            )
        }

        mBinding.tvPrivateSession.setOnClickListener {
            animateSessionAccess()
            setTextViewOnSessionAccessTypeChange(
                getString(R.string.private_session),
                mBinding.tvPrivateSession,
                mBinding.tvPublicSession
            )
        }

        mBinding.llRepeatMain.setOnClickListener {
            if (mBinding.llRepeatController.isVisible) {
                mBinding.llRepeatController.isGone = true
                mBinding.rrRepeatSelectData.isVisible = true
                mBinding.ivRepeatExpandArrow.animate().rotation(270.0f).setDuration(200).start()
                when (selectedRepeatType) {
                    1 -> {
                        mBinding.tvSelectedRepeatData.text = getString(R.string.one_time)
                    }
                    2 -> {
                        var selectedWeeks = ""
                        weeklyRepeatAdapter.selectedList.forEachIndexed { index, selected ->
                            if (selected) {
                                selectedWeeks = if (selectedWeeks.isEmpty()) {
                                    ", " + weeklyRepeatAdapter.imageList[index]
                                } else {
                                    selectedWeeks + " / " + weeklyRepeatAdapter.imageList[index]
                                }
                            }
                        }
                        mBinding.tvSelectedRepeatData.text =
                            getString(R.string.weekly) + selectedWeeks
                    }
                    else -> {
                        mBinding.tvSelectedRepeatData.text =
                            getString(R.string.monthly) + ", $selectedRepeatDays"
                    }
                }
            } else {
                mBinding.llRepeatController.isVisible = true
                mBinding.rrRepeatSelectData.isGone = true
                mBinding.ivRepeatExpandArrow.animate().rotation(0.0f).setDuration(200).start()
            }
        }

        mBinding.tvRepeatOneTime.setOnClickListener {
            changeRepeatTypeHeader(
                mBinding.tvRepeatOneTime,
                mBinding.tvRepeatWeekly,
                mBinding.tvRepeatMonthly,
                1
            )
        }

        mBinding.tvRepeatWeekly.setOnClickListener {
            changeRepeatTypeHeader(
                mBinding.tvRepeatWeekly,
                mBinding.tvRepeatOneTime,
                mBinding.tvRepeatMonthly,
                2
            )
        }

        mBinding.tvRepeatMonthly.setOnClickListener {
            changeRepeatTypeHeader(
                mBinding.tvRepeatMonthly,
                mBinding.tvRepeatOneTime,
                mBinding.tvRepeatWeekly,
                3
            )
        }

        mBinding.tvMonthlyDaySelection.setOnClickListener {
            openMonthlyDaySelectionDialog()
        }

        mBinding.llMaxParticipants.setOnClickListener {
            hideShowMaxParticipantView("")
        }

        mBinding.tvNoLimit.setOnClickListener {
            hideShowMaxParticipantView(getString(R.string.no_limit))
        }

        mBinding.tvLimit100.setOnClickListener {
            hideShowMaxParticipantView("100")
        }

        mBinding.tvLimit50.setOnClickListener {
            hideShowMaxParticipantView("50")
        }

        mBinding.tvLimit20.setOnClickListener {
            hideShowMaxParticipantView("20")
        }

        mBinding.tvLimit10.setOnClickListener {
            hideShowMaxParticipantView("10")
        }

        mBinding.btnAddCoHost.setOnClickListener {
            sessionCoHostAddAdapter.size++
            sessionCoHostAddAdapter.notifyItemInserted(sessionCoHostAddAdapter.size - 1)
        }

        mBinding.llAdvancedHeader.setOnClickListener {
            animateAdvancedHeader()
        }

        mBinding.btnScheduleSession.setOnClickListener {
            onBackPressed()
        }

        mBinding.llCancelSession.setOnClickListener {
            openCancelSessionDialog()
        }
    }

    private fun hideShowMaxParticipantView(limit: String) {
        if (mBinding.llMaxParticipantsData.isVisible) {
            mBinding.llMaxParticipantsData.isGone = true
        } else {
            mBinding.llMaxParticipantsData.isVisible = true
        }
        if (limit.isNotEmpty()) {
            mBinding.tvSelectedParticipantLimit.text = limit
        } else {
            mBinding.tvSelectedParticipantLimit.text = getString(R.string.no_limit)
        }
    }

    private fun changeRepeatTypeHeader(
        activeRepeat: AppCompatTextView,
        disableFirst: AppCompatTextView,
        disableSecond: AppCompatTextView,
        type: Int
    ) {
        activeRepeat.setBackgroundResource(R.drawable.bg_gold_semi_light_curve_30dp)
        disableFirst.setBackgroundResource(R.drawable.bg_white_gold_border_30dp)
        disableSecond.setBackgroundResource(R.drawable.bg_white_gold_border_30dp)

        activeRepeat.setTextColor(ContextCompat.getColor(this, R.color.white))
        disableFirst.setTextColor(ContextCompat.getColor(this, R.color.black))
        disableSecond.setTextColor(ContextCompat.getColor(this, R.color.black))

        selectedRepeatType = type
    }

    private fun animateSessionAccess() {
        if (mBinding.llAccessType.isVisible) {
            mBinding.llAccessType.animate().also {
                it.withEndAction {
                    mBinding.llAccessType.isGone = true
                    mBinding.llAccessHeader.isVisible = true
                }
                it.duration = 200
                it.start()
            }
            mBinding.ivSessionAccessArrow.animate().rotation(0.0f).setDuration(200).start()
        } else {
            mBinding.llAccessType.animate().also {
                it.withEndAction {
                    mBinding.llAccessHeader.isGone = true
                    mBinding.llAccessType.isVisible = true
                }
                it.duration = 200
                it.start()
            }
            mBinding.ivSessionAccessArrow.animate().rotation(180.0f).setDuration(200).start()
        }
    }

    private fun setTextViewOnSessionAccessTypeChange(
        text: String,
        activeSession: AppCompatTextView,
        inActiveSession: AppCompatTextView
    ) {
        mBinding.tvSelectedSessionAccess.text = text
        activeSession.setTextColor(ContextCompat.getColor(this, R.color.gold))
        inActiveSession.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
    }

    private fun animateAdvancedHeader() {
        if (mBinding.llAdvancedData.isVisible) {
            mBinding.llAdvancedData.isGone = true
            mBinding.viewAdvanced.isVisible = true
            mBinding.ivAdvancedArrow.animate().rotation(270.0f).setDuration(200).start()
        } else {
            mBinding.llAdvancedData.isVisible = true
            mBinding.viewAdvanced.isGone = true
            mBinding.ivAdvancedArrow.animate().rotation(0.0f).setDuration(200).start()
        }
    }

    private fun openMonthlyDaySelectionDialog() {
        val dialog =
            MonthlyDaySelectionDialog(this, selectedRepeatDays,
                object : MonthlyDaySelectionDialog.DaySelectionListener {
                    override fun onDaySelect(days: String) {
                        mBinding.tvMonthlyDaySelection.text = days
                        selectedRepeatDays = days
                    }
                })
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun openCancelSessionDialog() {
        val dialog =
            CancelSessionDialog(this)
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