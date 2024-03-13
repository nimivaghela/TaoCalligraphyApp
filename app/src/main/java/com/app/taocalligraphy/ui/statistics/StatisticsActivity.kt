package com.app.taocalligraphy.ui.statistics

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityStatisticsBinding
import com.app.taocalligraphy.models.WeekDatesDayListModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.stats_response.FetchMonthStatDataReponse
import com.app.taocalligraphy.models.response.stats_response.FetchWeekStatDataReponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.statistics.viewmodel.StatsViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImageProfile
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


@AndroidEntryPoint
class StatisticsActivity : BaseActivity<ActivityStatisticsBinding>() {
    private val mViewModel: StatsViewModel by viewModels()

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, StatisticsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_statistics

    override fun initView() {
        setupToolbar()
        updateProfile()
        findLast7DatesFromToday()
        mBinding.tvDayName.text = mViewModel.datePassForAPI.getFormattedDayForStats()
        mBinding.tvDayDate.text =
            getddMMMFromDate(mViewModel.datePassForAPI.getFormattedDateForStats())

        if (!isTabletLandScape(this)) {
            when (mViewModel.type) {
                Constants.day -> handleDayClick()
                Constants.week -> {
                    handleWeekClick()
                    changeWeekTab()
                }
                Constants.month -> {
                    handleMonthClick()
                    changeMonthTab()
                }
            }
        } else {
            if (mViewModel.dailyMeditationStats == null) {
                callDailyStatsDataAPI()
            } else {
                setDailyStatsData()
            }

            if (mViewModel.weeklyMeditationStats == null) {
                callWeekStatsDataAPI()
            } else {
                setWeeklyStatsData()
                changeWeekTab()
            }
            mBinding.llWeekData.isVisible = true

            if (mViewModel.monthlyMeditationStats == null) {
                callMonthStatsDataAPI()
            } else {
                setMonthlyStatsData()
                changeMonthTab()
            }
            mBinding.llMonthData.isVisible = true
        }

        if (isTablet(this)) {
            ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(0, 0, 0, insets.bottom)
                WindowInsetsCompat.CONSUMED
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    @SuppressLint("SimpleDateFormat")
    private fun findLast7DatesFromToday() {
        mViewModel.mWeekLast7DayList.clear()
        val today = DateTime().withTimeAtStartOfDay()
        val formatterDate: DateTimeFormatter = DateTimeFormat.forPattern("dd") //"dd/MM/yyyy"
        val formatterDay: DateTimeFormatter = DateTimeFormat.forPattern("EEE")
        for (i in 0..6) {
            val mDate = formatterDate.print(today.minusDays(i))
            val mDay = formatterDay.print(today.minusDays(i))
            mViewModel.mWeekLast7DayList.add(WeekDatesDayListModel(mDate, mDay, (i % 2 == 0)))
        }
    }

    private fun callDailyStatsDataAPI() {
        mViewModel.fetchDailyStatsData(this, mDisposable)
    }

    private fun callWeekStatsDataAPI() {
        mViewModel.fetchWeekStatsData(this, mDisposable)
    }

    private fun callMonthStatsDataAPI() {
        mViewModel.fetchMonthStatsData(this, mDisposable)
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    private fun initializeWeekBarChart(weekDaysList: ArrayList<FetchWeekStatDataReponse.DailyMeditationStatsDetail?>) {
        mBinding.barChartWeek.setViewPortOffsets(0f, 0f, 0f, 50f)
        mBinding.barChartWeek.description.isEnabled = false
        mBinding.barChartWeek.setPinchZoom(false)
        mBinding.barChartWeek.setDrawBarShadow(false)
        mBinding.barChartWeek.setDrawGridBackground(false)
        mBinding.barChartWeek.setDrawBorders(false)
        mBinding.barChartWeek.setDrawValueAboveBar(true)

        mBinding.barChartWeek.axisLeft.axisMinimum = 0f
        val xAxis: XAxis = mBinding.barChartWeek.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.axisLineColor = ContextCompat.getColor(this, R.color.white)

        mBinding.barChartWeek.axisLeft.setDrawGridLines(false)
        mBinding.barChartWeek.axisRight.setDrawGridLines(false)

        mBinding.barChartWeek.axisLeft.isEnabled = true
        mBinding.barChartWeek.axisLeft.axisLineColor = getColor(R.color.transparent)
        mBinding.barChartWeek.axisRight.isEnabled = false
        mBinding.barChartWeek.xAxis.textColor = ContextCompat.getColor(this, R.color.medium_grey)
        mBinding.barChartWeek.xAxis.typeface =
            ResourcesCompat.getFont(this, R.font.font_jost_regular)
        if (isTablet(this))
            if (isTabletLandScape(this)) {
                mBinding.barChartWeek.xAxis.textSize = 11f
            } else {
                mBinding.barChartWeek.xAxis.textSize = 16f
            }
        else
            mBinding.barChartWeek.xAxis.textSize = 11f
        mBinding.barChartWeek.setExtraOffsets(0f, 0f, 0f, 20f)
        mBinding.barChartWeek.axisLeft.textColor = ContextCompat.getColor(this, R.color.white)
        mBinding.barChartWeek.axisLeft.textSize = 12f
        mBinding.barChartWeek.setFitBars(true)
        mBinding.barChartWeek.setTouchEnabled(false)

        if (isTabletLandScape(this))
            mBinding.barChartWeek.setRadius(10)

        mBinding.barChartWeek.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                value.toInt().also {
                    when (it) {
                        0 -> {
                            return mViewModel.mWeekLast7DayList[6].days.uppercase()
                        }
                        1 -> {
                            return mViewModel.mWeekLast7DayList[5].days.uppercase()
                        }
                        2 -> {
                            return mViewModel.mWeekLast7DayList[4].days.uppercase()
                        }
                        3 -> {
                            return mViewModel.mWeekLast7DayList[3].days.uppercase()
                        }
                        4 -> {
                            return mViewModel.mWeekLast7DayList[2].days.uppercase()
                        }
                        5 -> {
                            return mViewModel.mWeekLast7DayList[1].days.uppercase()
                        }
                        6 -> {
                            return mViewModel.mWeekLast7DayList[0].days.uppercase()
                        }
                        else -> {
                            return ""
                        }
                    }
                }
            }
        }

        mBinding.barChartWeek.axisLeft.setDrawGridLines(false)
        mBinding.barChartWeek.legend.isEnabled = false


        val values = ArrayList<BarEntry>()

        weekDaysList.forEachIndexed { index, element ->
            values.add(
                BarEntry(
                    index.toFloat(),
                    getMinutesFromHHmm(element?.meditationTime)
                )
            )
        }

        val set1 = BarDataSet(values, "Data Set")
        set1.setGradientColor(
            ContextCompat.getColor(this, R.color.gold_brown),
            ContextCompat.getColor(this, R.color.dandelion)
        )
        set1.setDrawValues(false)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.barWidth = 0.7f
        mBinding.barChartWeek.data = data

        val maxValue = weekDaysList.maxByOrNull { getMinutesFromHHmm(it?.meditationTime) }
        mBinding.barChartWeek.axisLeft.axisMaximum = getMinutesFromHHmm(maxValue?.meditationTime)

        var total = 0f
        weekDaysList.forEach { total += getMinutesFromHHmm(it?.meditationTime) }
        val averageValue = total / weekDaysList.size

        val averageLimitLine = LimitLine(averageValue, "")
        averageLimitLine.lineWidth = 0.5f
        averageLimitLine.lineColor = getColor(R.color.dandelion)

        mBinding.barChartWeek.axisLeft.addLimitLine(averageLimitLine)

        if (total == 0f) {
            mBinding.divWeeklyMaxLine.gone()
        } else {
            mBinding.divWeeklyMaxLine.visible()
        }

        mBinding.barChartWeek.invalidate()
        mBinding.barChartWeek.animateY(1500)
    }

    private fun initializeMonthLineChart(monthDaysList: ArrayList<FetchMonthStatDataReponse.DailyMeditationStatsDetail?>?) {
        mBinding.lineChart.setViewPortOffsets(0f, 0f, 0f, 0f)
        mBinding.lineChart.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )
        mBinding.lineChart.description.isEnabled = false
        // enable touch gestures
        mBinding.lineChart.setTouchEnabled(true)

        // enable scaling and dragging
        mBinding.lineChart.isDragEnabled = true
        mBinding.lineChart.setScaleEnabled(true)

        mBinding.lineChart.setPinchZoom(false)

        mBinding.lineChart.setDrawGridBackground(false)
        mBinding.lineChart.description.isEnabled = false
        mBinding.lineChart.setPinchZoom(false)
        mBinding.lineChart.setDrawGridBackground(false)
        mBinding.lineChart.setDrawBorders(false)
        //mBinding.lineChart.setMaxHighlightDistance(300f);

        mBinding.lineChart.xAxis.isEnabled = false
        mBinding.lineChart.legend.isEnabled = false
        mBinding.lineChart.setTouchEnabled(false)

        mBinding.lineChart.axisRight.isEnabled = false
        mBinding.lineChart.axisLeft.isEnabled = true
        mBinding.lineChart.axisLeft.gridColor = getColor(R.color.transparent)
        mBinding.lineChart.axisLeft.axisLineColor = getColor(R.color.transparent)
        mBinding.lineChart.axisLeft.axisMinimum = -0.15f
        mBinding.lineChart.axisRight.axisMinimum = -0.15f
        val values: ArrayList<Entry> = ArrayList()

//        for (i in 0 until 10) {
//            val data = (Math.random() * (100 + 1)).toFloat() + 20f
//            values.add(Entry(i.toFloat(), data))
//        }

        monthDaysList?.forEachIndexed { index, element ->
            values.add(
                Entry(
                    index.toFloat(),
                    getMinutesFromHHmm(element?.meditationTime)
                )
            )
        }

        val set1 = LineDataSet(values, "DataSet 1")

        set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawFilled(true)
        set1.setDrawCircles(false)
        set1.lineWidth = 0.5f
        set1.circleRadius = 4f

        set1.setCircleColor(Color.WHITE)
        set1.color = ContextCompat.getColor(this, R.color.gold)
        //set1.fillColor = ContextCompat.getColor(this,R.color.driftwood)
        //set1.fillAlpha = 100
        set1.fillDrawable = ContextCompat.getDrawable(this, R.drawable.bg_gold_gradient_chart)
        set1.setDrawHorizontalHighlightIndicator(false)

        val data = LineData(set1)
        data.setDrawValues(false)

        // set data

        // set data
        mBinding.lineChart.data = data
        mBinding.lineChart.animateXY(1000, 1000)

        val maxValue = monthDaysList?.maxByOrNull { getMinutesFromHHmm(it?.meditationTime) }
        val max = getMinutesFromHHmm(maxValue?.meditationTime)
        if (max <= 0) {
            mBinding.lineChart.axisLeft.axisMinimum = 0f
        }
        mBinding.lineChart.axisLeft.axisMaximum = getMinutesFromHHmm(maxValue?.meditationTime)

        var total = 0f
        monthDaysList?.forEach { total += getMinutesFromHHmm(it?.meditationTime) }
        val averageValue = total / (monthDaysList?.size ?: 1)

        val averageLimitLine = LimitLine(averageValue, "")
        averageLimitLine.lineWidth = 0.5f
        averageLimitLine.lineColor = getColor(R.color.dandelion)

        mBinding.lineChart.axisLeft.addLimitLine(averageLimitLine)

        if (total == 0f) {
            mBinding.divMonthlyMaxLine.gone()
        } else {
            mBinding.divMonthlyMaxLine.visible()
        }
        mBinding.lineChart.axisLeft.resetAxisMinimum()

        // don't forget to refresh the drawing
        mBinding.lineChart.invalidate()
    }

    override fun observeApiCallbacks() {
        mViewModel.fetchDailyStatDataReponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.dailyMeditationStats?.let { dailyMeditationStats ->
                    mViewModel.dailyMeditationStats = dailyMeditationStats
                    setDailyStatsData()
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchDailyStatDataReponse.postValue(null)
        }

        mViewModel.fetchWeekStatDataReponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let {
                    mViewModel.weeklyMeditationStats = it
                    setWeeklyStatsData()
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchWeekStatDataReponse.postValue(null)
        }

        mViewModel.fetchMonthStatDataReponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let {
                    mViewModel.monthlyMeditationStats = it
                    setMonthlyStatsData()
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchMonthStatDataReponse.postValue(null)
        }
    }

    private fun setDataAsPerDailyAverageAndTotalForWeekAndMonth(
        isWeek: Boolean, isMonth: Boolean, isDaily: Boolean
    ) {
        if (isWeek) {
            if (isDaily) {
                mBinding.apply {
                    mViewModel.meditationAveragePerDayForWeek?.meditationTimePerDay?.let {
                        val parts = it.split(":")
                        parts.let {
                            if (parts[0] == "00" && parts[1] == "00") {
                                tvWeekTime.text = "0m"
                            } else if (parts[0] == "00" || parts[0] == "0") {
                                tvWeekTime.text =
                                    parts[1] + "m"
                            } else {
                                tvWeekTime.text =
                                    parts[0] + "h " + parts[1] + "m"
                            }
                        }
                    } ?: kotlin.run {
                        tvWeekTime.text = "0m"
                    }

                    tvWeekMeditation.text =
                        mViewModel.meditationAveragePerDayForWeek?.numberOfMeditationsPerDay
                    tvWeekSession.text =
                        mViewModel.meditationAveragePerDayForWeek?.liveSessionCountPerDay
                    lblMeditationWeek.text = getString(R.string.meditation_day)
                    lblSessionWeek.text = getString(R.string.live_session_day)
                    lblTimeForWeek.text = getString(R.string.daily_average_small)
                }
            } else {
                mBinding.apply {
                    mViewModel.meditationAveragePerMonthForWeek?.totalMeditationTime?.let {
                        val parts = it.split(":")
                        if (parts.isNotEmpty()) {
                            if (parts[0] == "00" && parts[1] == "00") {
                                tvWeekTime.text = "0m"
                            } else if (parts[0] == "00" || parts[0] == "0") {
                                tvWeekTime.text =
                                    parts[1] + "m"
                            } else {
                                tvWeekTime.text =
                                    parts[0] + "h " + parts[1] + "m"
                            }
                        } else {
                            tvWeekTime.text = "0m"
                        }
                        tvWeekMeditation.text =
                            mViewModel.meditationAveragePerMonthForWeek?.numberOfMeditations
                        tvWeekSession.text =
                            mViewModel.meditationAveragePerMonthForWeek?.liveSessionCount
                        lblTimeForWeek.text = getString(R.string.total_time)
                        lblMeditationWeek.text = getString(R.string.meditations)
                        lblSessionWeek.text = getString(R.string.live_session_small)
                    }
                }
            }
        } else if (isMonth) {
            if (isDaily) {
                mBinding.apply {
                    mViewModel.meditationAveragePerDayForMonth?.meditationTimePerDay?.let {
                        tvDailyAverageMonthCount.text = it.calculateDurationOnlyHandM()
                    } ?: kotlin.run {
                        tvDailyAverageMonthCount.text = "0m"
                    }

                    tvMonthMeditation.text =
                        mViewModel.meditationAveragePerDayForMonth?.numberOfMeditationsPerDay
                    tvMonthSession.text =
                        mViewModel.meditationAveragePerDayForMonth?.liveSessionCountPerDay
                    lblTimeForMonth.text = getString(R.string.daily_average_small)
                    lblMeditationMonth.text = getString(R.string.meditation_day)
                    lblSessionMonth.text = getString(R.string.live_session_day)
                }
            } else {
                mBinding.apply {
                    mViewModel.meditationAveragePerMonthForMonth?.totalMeditationTime?.let {
                        tvDailyAverageMonthCount.text = it.calculateDurationOnlyHandM()
                    }
                    tvMonthMeditation.text =
                        mViewModel.meditationAveragePerMonthForMonth?.numberOfMeditations
                    tvMonthSession.text =
                        mViewModel.meditationAveragePerMonthForMonth?.liveSessionCount
                    lblTimeForMonth.text = getString(R.string.total_time)
                    lblMeditationMonth.text = getString(R.string.meditations)
                    lblSessionMonth.text = getString(R.string.live_session_small)
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(
                this@StatisticsActivity,
                StatisticsActivity::class.java.simpleName
            )
        }

        mBinding.tvDay.setOnClickListener {
            mViewModel.dailyMeditationStats = null
            mViewModel.datePassForAPI = DateTime().withTimeAtStartOfDay()
            handleDayClick()
        }

        mBinding.tvWeek.setOnClickListener {
            mViewModel.weeklyMeditationStats = null
            handleWeekClick()
        }

        mBinding.tvMonth.setOnClickListener {
            mViewModel.monthlyMeditationStats = null
            handleMonthClick()
        }

        mBinding.tvDailyAverageWeek.setOnClickListener {
            mViewModel.isDailySelectedForWeek = true
            mViewModel.isWeekSelectedForWeek = false
            changeWeekTab()
        }

        mBinding.tvWeekTotal.setOnClickListener {
            setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                isWeek = true, isMonth = false, isDaily = false
            )
            mViewModel.isDailySelectedForWeek = false
            mViewModel.isWeekSelectedForWeek = true
            changeWeekTab()
        }

        mBinding.tvDailyAverageMonth.setOnClickListener {
            mViewModel.isWeekSelectedForMonth = false
            mViewModel.isDailySelectedForMonth = true

            changeMonthTab()
        }

        mBinding.tvMonthTotal.setOnClickListener {
            mViewModel.isWeekSelectedForMonth = true
            mViewModel.isDailySelectedForMonth = false
            setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                isWeek = false, isMonth = true, isDaily = false
            )
            changeMonthTab()
        }

        mBinding.llPreviousDay.setOnClickListener {
            mViewModel.datePassForAPI = mViewModel.datePassForAPI.minusDays(1)

            mBinding.tvDayName.text = mViewModel.datePassForAPI.getFormattedDayForStats()
            mBinding.tvDayDate.text =
                getddMMMFromDate(mViewModel.datePassForAPI.getFormattedDateForStats())

            callDailyStatsDataAPI()

            if (mViewModel.todayDate.getFormattedDateForStats() > mViewModel.datePassForAPI.minusDays(
                    1
                ).getFormattedDateForStats()
            ) {
                mBinding.ivNextDay.alpha = 1.0f
            } else {
                mBinding.ivNextDay.alpha = 0.5f
            }
        }

        mBinding.llNextDay.setOnClickListener {
            checkNextDayWithCurrentDate()
        }
    }

    private fun handleMonthClick() {
        mViewModel.type = Constants.month
        if (mViewModel.monthlyMeditationStats == null) {
            callMonthStatsDataAPI()
        } else {
            setMonthlyStatsData()
        }
        changeTabBackground(
            activeTextView = mBinding.tvMonth,
            otherTextView = arrayOf(mBinding.tvDay, mBinding.tvWeek),
        )
        mBinding.llMonthData.isVisible = true
    }

    private fun handleWeekClick() {
        mViewModel.type = Constants.week
        if (mViewModel.weeklyMeditationStats == null) {
            callWeekStatsDataAPI()
        } else {
            setWeeklyStatsData()
        }
        arrayOf(mBinding.tvDay, mBinding.tvMonth).let { it1 ->
            changeTabBackground(
                activeTextView = mBinding.tvWeek,
                otherTextView = it1,
            )
        }

        mBinding.llWeekData.isVisible = true
    }

    private fun handleDayClick() {
        mViewModel.type = Constants.day
        mBinding.tvDay.let { it1 ->
            changeTabBackground(
                activeTextView = it1,
                otherTextView = arrayOf(mBinding.tvWeek, mBinding.tvMonth),
            )
        }
        mBinding.clDayData.isVisible = true
        if (mViewModel.dailyMeditationStats == null) {
            callDailyStatsDataAPI()
        } else {
            setDailyStatsData()
        }
    }

    private fun checkNextDayWithCurrentDate() {
        if (mViewModel.todayDate.getFormattedDateForStats() >=
            mViewModel.datePassForAPI.plusDays(1).getFormattedDateForStats()
        ) {
            if (mViewModel.todayDate.getFormattedDateForStats() ==
                mViewModel.datePassForAPI.plusDays(1).getFormattedDateForStats()
            ) {
                mBinding.ivNextDay.alpha = 0.5f
            }
            mViewModel.datePassForAPI = mViewModel.datePassForAPI.plusDays(1)
            mBinding.tvDayName.text = mViewModel.datePassForAPI.getFormattedDayForStats()
            mBinding.tvDayDate.text =
                getddMMMFromDate(mViewModel.datePassForAPI.getFormattedDateForStats())
            callDailyStatsDataAPI()
        }
    }

    private fun changeMonthTab() {
        if (mViewModel.isDailySelectedForMonth) {
            setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                isWeek = false, isMonth = true, isDaily = true
            )
            mBinding.tvDailyAverageMonth.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
            mBinding.tvDailyAverageMonth.setTextColor(ContextCompat.getColor(this, R.color.gold))

            mBinding.tvMonthTotal.setBackgroundResource(R.drawable.bg_medium_gray_two_side_corner_right)
            mBinding.tvMonthTotal.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))

        }
        if (mViewModel.isWeekSelectedForMonth) {
            setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                isWeek = false, isMonth = true, isDaily = false
            )
            mBinding.tvMonthTotal.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
            mBinding.tvMonthTotal.setTextColor(ContextCompat.getColor(this, R.color.gold))

            mBinding.tvDailyAverageMonth.setBackgroundResource(R.drawable.bg_medium_gray_two_side_corner_left)
            mBinding.tvDailyAverageMonth.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.medium_grey
                )
            )
        }
    }

    private fun changeTabBackground(
        activeTextView: AppCompatTextView?,
        vararg otherTextView: AppCompatTextView?
    ) {
        for (textview in otherTextView) {
            textview?.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
            textview?.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
        }

        activeTextView?.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
        activeTextView?.setTextColor(ContextCompat.getColor(this, R.color.gold))
        mBinding.clDayData.isGone = true
        mBinding.llWeekData.isGone = true
        mBinding.llMonthData.isGone = true
    }

    private fun changeWeekTab() {
        if (mViewModel.isDailySelectedForWeek) {
            setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                isWeek = true, isMonth = false, isDaily = true
            )

            mBinding.tvDailyAverageWeek.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
            mBinding.tvDailyAverageWeek.setTextColor(ContextCompat.getColor(this, R.color.gold))

            mBinding.tvWeekTotal.setBackgroundResource(R.drawable.bg_medium_gray_two_side_corner_right)
            mBinding.tvWeekTotal.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
        }
        if (mViewModel.isWeekSelectedForWeek) {
            setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                isWeek = true, isMonth = false, isDaily = false
            )

            mBinding.tvWeekTotal.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
            mBinding.tvWeekTotal.setTextColor(ContextCompat.getColor(this, R.color.gold))

            mBinding.tvDailyAverageWeek.setBackgroundResource(R.drawable.bg_medium_gray_two_side_corner_left)
            mBinding.tvDailyAverageWeek.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.medium_grey
                )
            )
        }
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

    private fun setDailyStatsData() {
        mViewModel.dailyMeditationStats?.let { dailyMeditationStats ->
            mBinding.apply {
                tvGoal.text =
                    dailyMeditationStats.goalPercentage.toString() + "% " + getString(R.string.of_goal)

                dailyMeditationStats.meditationTime?.let {
                    val data = dailyMeditationStats.meditationTime
                    tvMinutes.text = getStatsTime(data)

                } ?: kotlin.run {
                    tvMinutes.text =
                        getString(R.string.daily_meditation_time_only_minute, "0")
                }

                tvMeditationCount.text = dailyMeditationStats.numberOfMeditations
                tvSessionCount.text = dailyMeditationStats.liveSessionCount
                dailyChart.progress = dailyMeditationStats.goalPercentage!!.toFloat()

                if (mViewModel.todayDate.getFormattedDateForStats() >=
                    mViewModel.datePassForAPI.getFormattedDateForStats()
                ) {
                    if (mViewModel.todayDate.getFormattedDateForStats() ==
                        mViewModel.datePassForAPI.getFormattedDateForStats()
                    ) {
                        mBinding.ivNextDay.alpha = 0.5f
                    } else {
                        mBinding.ivNextDay.alpha = 1.0f
                    }
                } else {
                    mBinding.ivNextDay.alpha = 0.5f
                }
            }
        }
    }

    private fun setWeeklyStatsData() {
        mViewModel.weeklyMeditationStats?.let { data ->
            data.meditationAveragePerDay?.let { meditationAveragePerDay ->
                mViewModel.meditationAveragePerDayForWeek = meditationAveragePerDay
            }
            data.weeklyMeditationStats?.let {
                mViewModel.meditationAveragePerMonthForWeek = data.weeklyMeditationStats
                setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                    isWeek = true, isMonth = false, isDaily = false
                )
            }
            data.dailyMeditationStatsDetails?.let { dataList ->
                mBinding.apply {
                    if (dataList.size > 0) {
                        tvWeekDaysRange.text =
                            getddMMMFromDate(dataList[0]?.date) + " - " + getddMMMFromDate(
                                dataList[dataList.size - 1]?.date
                            )
                        initializeWeekBarChart(dataList)
                    }
                }
            }
        }
    }

    private fun setMonthlyStatsData() {
        mViewModel.monthlyMeditationStats?.let { data ->
            data.meditationAveragePerDay?.let { meditationAveragePerDay ->
                mViewModel.meditationAveragePerDayForMonth =
                    meditationAveragePerDay
            }
            data.dailyMeditationStatsDetails?.let { dataList ->
                mBinding.apply {
                    tvStartDate.text = getddMMMFromDate(dataList[0]?.date)
                    tvEndDate.text = getddMMMFromDate(dataList[dataList.size - 1]?.date)
                    tvMonthDayRange.text =
                        getddMMMFromDate(dataList[0]?.date) + " - " + getddMMMFromDate(dataList[dataList.size - 1]?.date)
                    initializeMonthLineChart(dataList)
                }
            }
            data.monthlyMeditationStats?.let {
                mViewModel.meditationAveragePerMonthForMonth = data.monthlyMeditationStats
                setDataAsPerDailyAverageAndTotalForWeekAndMonth(
                    isWeek = false,
                    isMonth = true,
                    isDaily = false
                )
            }
        }
    }
}