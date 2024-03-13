package com.app.taocalligraphy.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityAlarmBinding
import com.app.taocalligraphy.models.AlarmContent
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.request.AddAlarmRequest
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.notification.AlarmBroadcastReceiver
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.notification.dialog.ChooseMeditationDialog
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImage
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AlarmActivity : BaseActivity<ActivityAlarmBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, AlarmActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    private val mAlarmViewModel: AlarmViewModel by viewModels()
    private val mViewModel: HomeViewModel by viewModels()


    override fun getResource() = R.layout.activity_alarm

    override fun initView() {

        if (!mAlarmViewModel.isConfigChange) {
            Log.d(TAG, "isConfigChange initView: ")
            mAlarmViewModel.repeatSunday = false
            mAlarmViewModel.repeatMonday = false
            mAlarmViewModel.repeatTuesday = false
            mAlarmViewModel.repeatWednesday = false
            mAlarmViewModel.repeatThursday = false
            mAlarmViewModel.repeatFriday = false
            mAlarmViewModel.repeatSaturday = false
            mAlarmViewModel.isCancelWithoutSaving = false
            mAlarmViewModel.contentDetailList.clear()
            mBinding.timePicker.setIs24HourView(!userHolder.is12HourFormat)

        } else {
            setDaysRepeatSelection()
            mBinding.timePicker.hour = mAlarmViewModel.timePickerHours
            mBinding.timePicker.minute = mAlarmViewModel.timePickerMinute
        }

        setupToolbar()

//        mBinding.timePicker.setIs24HourView(getTimePickerTimeFormat(this@AlarmActivity))

        if (mAlarmViewModel.isDataSelected) {
            setDataFromViewModel()
        } else {
//          If home data api alarm response not set to preference that time alarm get data from api & set to preference
            if (!mAlarmViewModel.isConfigChange) {
                callActiveAlarmAPI()
            } else {
                setAlarmDataInController()
            }
        }
    }

    private fun setDataFromViewModel() {
        mBinding.ivMeditationImage.visible()
        mBinding.ivMeditationImage.loadImage(
            mAlarmViewModel.content?.thumbnailImage,
            R.drawable.img_default_for_content,
            true
        )
        mBinding.tvMeditateTitle.text = mAlarmViewModel.content?.contentName
        mBinding.btnChangeMeditation.visible()
        mAlarmViewModel.alarmContent = AlarmContent()
        mAlarmViewModel.alarmContent?.setAlarmDataFromChooseContent(mAlarmViewModel.content!!)

        checkAlarmChanged()
    }

    override fun observeApiCallbacks() {
        mAlarmViewModel.addAlarmLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    longToast(
                        requestState.apiResponse?.message ?: "",
                        Constants.SUCCESS
                    )
                    setAlarmButtonEnabled(false)
                    userHolder.setAlarmData(it)
                    mAlarmViewModel.addAlarmLiveData.postValue(null)
                }
                longToastState(requestState.error)
            }
        }

        mAlarmViewModel.alarmOnOffLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    userHolder.setAlarmData(it)
                    setAlarmDataInController()
                    mAlarmViewModel.alarmOnOffLiveData.postValue(null)
                    setAlarmButtonEnabled(true)
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.activeAlarmResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    if (it.isWakeUpWithMeditation == false) {
                        cancelAllPreviousAlarm()
                    } else {
                        if ((userHolder.getAlarmData() != null) and (it.contentId != null)) {
                            val alarmData = userHolder.getAlarmData()
                            if (alarmData?.contentId != it.contentId) {
                                setAlarmInManager(it)
                            } else if (alarmData?.time != it.time) {
                                setAlarmInManager(it)
                            } else if (alarmData?.repeatDays?.size != it.repeatDays?.size) {
                                setAlarmInManager(it)
                            } else if (alarmData?.repeatDays?.size == it.repeatDays?.size) {
                                alarmData?.repeatDays?.let { alarmList ->
                                    it.repeatDays?.let { repeatDays ->
                                        if ((!alarmList.containsAll(repeatDays)) and (!repeatDays.containsAll(
                                                alarmList
                                            ))
                                        ) {
                                            setAlarmInManager(it)
                                        }
                                    }
                                }
                            }
                        } else if ((userHolder.getAlarmData() == null) and (it.contentId != null)) {
                            setAlarmInManager(it)
                        }
                    }
                    userHolder.setAlarmData(it)
                    setAlarmDataInController()
                }
                longToastState(requestState.error)
            }
            mViewModel.activeAlarmResponse.postValue(null)
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this, AlarmActivity::class.java.simpleName)
        }

        mBinding.alarmSwitch.setOnClickListener {
            if (mBinding.alarmSwitch.isChecked) {
                setWakeUpMeditationState(true)
                mAlarmViewModel.alarmOnOff(this, mDisposable)
                cancelAllPreviousAlarm()
                disableAlarmView(true)
            } else {
                mBinding.alarmSwitch.isChecked = true
                cancelAlarmConfirmationDialog()
            }
        }

        mBinding.llSunday.setOnClickListener {
            callSundayRepeat()
            checkAlarmChanged()
        }

        mBinding.llMonday.setOnClickListener {
            callMondayRepeat()
            checkAlarmChanged()
        }

        mBinding.llTuesday.setOnClickListener {
            callTuesdayRepeat()
            checkAlarmChanged()
        }

        mBinding.llWednesday.setOnClickListener {
            callWednesdayRepeat()
            checkAlarmChanged()
        }

        mBinding.llThursday.setOnClickListener {
            callThursdayRepeat()
            checkAlarmChanged()
        }

        mBinding.llFriday.setOnClickListener {
            callFridayRepeat()
            checkAlarmChanged()
        }

        mBinding.llSaturday.setOnClickListener {
            callSaturdayRepeat()
            checkAlarmChanged()
        }

        mBinding.tvMeditateTitle.setOnClickListener {
            openChooseMeditationDialog()
        }

        mBinding.ivMeditationImage.setOnClickListener {
            openChooseMeditationDialog()
        }

        mBinding.btnSetAlarm.setOnClickListener {
            setAlarm()
        }

        mBinding.btnChangeMeditation.setOnClickListener {
            openChooseMeditationDialog()
        }

        mBinding.timePicker.setOnTimeChangedListener { _, _, _ ->
            mAlarmViewModel.timePickerHours = mBinding.timePicker.hour
            mAlarmViewModel.timePickerMinute = mBinding.timePicker.minute
            checkAlarmChanged()
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

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()

        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    private fun setAlarmButtonEnabled(enable: Boolean) {
        if (enable && mBinding.alarmSwitch.isChecked) {
            mAlarmViewModel.isAlarmChanged = true
            mBinding.btnSetAlarm.alpha = 1f
            mBinding.btnSetAlarm.isEnabled = true
        } else {
            mAlarmViewModel.isAlarmChanged = false
            mBinding.btnSetAlarm.alpha = 0.5f
            mBinding.btnSetAlarm.isEnabled = false
        }
    }

    private fun setAlarm() {
        cancelAllPreviousAlarm()
        if (!checkDaysSelected()) {
            //set alarm for given time, if time is passed then set for next day
            setAlarmInManager(false)
        } else if (mAlarmViewModel.alarmContent == null) {
            showToast(this, getString(R.string.choose_meditation))
        } else {
            setAlarmInManager(true)
        }
    }

    private fun disableAlarmView(isEnabled: Boolean) {
        mBinding.ivMeditationImage.isEnabled = isEnabled
        mBinding.btnChangeMeditation.isEnabled = isEnabled
        mBinding.btnSetAlarm.isEnabled = isEnabled
        mBinding.tvMeditateTitle.isEnabled = isEnabled
        mBinding.llSunday.isEnabled = isEnabled
        mBinding.llMonday.isEnabled = isEnabled
        mBinding.llTuesday.isEnabled = isEnabled
        mBinding.llWednesday.isEnabled = isEnabled
        mBinding.llThursday.isEnabled = isEnabled
        mBinding.llFriday.isEnabled = isEnabled
        mBinding.llSaturday.isEnabled = isEnabled
        mBinding.timePicker.isEnabled = isEnabled

        if (isEnabled) {
            changeMainBackgroundColor(R.color.transparent)
            mBinding.btnChangeMeditation.visible()
        } else {
            changeMainBackgroundColor(R.color.white)
            mBinding.btnChangeMeditation.gone()
        }
    }

    private fun changeMainBackgroundColor(color: Int) {
        mBinding.root.setBackgroundColor(
            ContextCompat.getColor(
                this,
                color
            )
        )
    }

    private fun checkAlarmChanged() {
        var timePickerHours = 0
        var timePickerMinute = 0

        timePickerHours = mAlarmViewModel.timePickerHours
        timePickerMinute = mAlarmViewModel.timePickerMinute


        val time = String.format(
            "%02d:%02d", timePickerHours, timePickerMinute
        )

        if (userHolder.getAlarmData()?.time != time) {
            setAlarmButtonEnabled(true)
        } else if (userHolder.getAlarmData()?.contentId != mAlarmViewModel.alarmContent?.alarmContentId) {
            setAlarmButtonEnabled(true)
        } else {
            userHolder.getAlarmData()?.repeatDays?.let {
                if (it.isEmpty() && (mAlarmViewModel.repeatSaturday || mAlarmViewModel.repeatFriday || mAlarmViewModel.repeatThursday ||
                            mAlarmViewModel.repeatWednesday || mAlarmViewModel.repeatTuesday || mAlarmViewModel.repeatMonday || mAlarmViewModel.repeatSunday)
                ) {
                    setAlarmButtonEnabled(true)
                } else {
                    setAlarmButtonEnabled(
                        (it.contains(6) && !mAlarmViewModel.repeatSaturday) || (!it.contains(6) && mAlarmViewModel.repeatSaturday) ||
                                (it.contains(5) && !mAlarmViewModel.repeatFriday) || (!it.contains(5) && mAlarmViewModel.repeatFriday) ||
                                (it.contains(4) && !mAlarmViewModel.repeatThursday) || (!it.contains(
                            4
                        ) && mAlarmViewModel.repeatThursday) ||
                                (it.contains(3) && !mAlarmViewModel.repeatWednesday) || (!it.contains(
                            3
                        ) && mAlarmViewModel.repeatWednesday) ||
                                (it.contains(2) && !mAlarmViewModel.repeatTuesday) || (!it.contains(
                            2
                        ) && mAlarmViewModel.repeatTuesday) ||
                                (it.contains(1) && !mAlarmViewModel.repeatMonday) || (!it.contains(1) && mAlarmViewModel.repeatMonday) ||
                                (it.contains(0) && !mAlarmViewModel.repeatSunday) || (!it.contains(0) && mAlarmViewModel.repeatSunday)
                    )
                }
            }
        }
    }

    private fun cancelAlarmConfirmationDialog() {
        val title = ""
        val description = "" + getString(R.string.cancel_alarm_msg)

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.let {
            it.setTitle("" + title)
                .setMessage("" + description)
                .setCancelable(false)
                .setPositiveButton(
                    "" + getString(R.string.yes)
                ) { dialog, _ ->
                    dialog!!.dismiss()
                    setWakeUpMeditationState(false)
                    disableAlarmView(false)
                    mAlarmViewModel.alarmOnOff(this, mDisposable)
                    userHolder.alarmResponse = ""
                    setAlarmDataInController()
                    cancelAllPreviousAlarm()
                }
                .setNegativeButton(
                    "" + getString(R.string.no)
                ) { dialog, _ ->
                    dialog!!.dismiss()
                    mBinding.alarmSwitch.isChecked = true
                }
        }
        val alert = builder.create()
        alert.show()
    }

    private fun checkDaysSelected(): Boolean {
        if (mAlarmViewModel.repeatSunday)
            return true
        if (mAlarmViewModel.repeatMonday)
            return true
        if (mAlarmViewModel.repeatTuesday)
            return true
        if (mAlarmViewModel.repeatWednesday)
            return true
        if (mAlarmViewModel.repeatThursday)
            return true
        if (mAlarmViewModel.repeatFriday)
            return true
        if (mAlarmViewModel.repeatSaturday)
            return true
        return false
    }

    private fun setAlarmInManager(isRepeatSelected: Boolean) {
        if (mBinding.alarmSwitch.isChecked) {
            val timePickerHours = mBinding.timePicker.hour
            val timePickerMinute = mBinding.timePicker.minute
            val repeatDaysList = ArrayList<Int>()

            val intent = Intent(this, AlarmBroadcastReceiver::class.java)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            intent.putExtra(Constants.alarmContentId, mAlarmViewModel.alarmContent?.alarmContentId)
            intent.putExtra(
                Constants.alarmContentTitle,
                mAlarmViewModel.alarmContent?.alarmContentTitle
            )
            intent.putExtra(
                Constants.alarmContentImageUrl,
                mAlarmViewModel.alarmContent?.alarmContentImageUrl
            )
            intent.putExtra(
                Constants.alarmContentFileUrl,
                mAlarmViewModel.alarmContent?.alarmContentFileUrl
            )
            intent.putExtra(Constants.alarmRepeat, isRepeatSelected)
            if (isRepeatSelected) {
                if (mAlarmViewModel.repeatSunday) {
                    intent.putExtra(Constants.alarmRequestCode, 1)
                    getRepeatCalender(timePickerHours, timePickerMinute, Calendar.SUNDAY, 1, intent)
                    repeatDaysList.add(0)
                }
                if (mAlarmViewModel.repeatMonday) {
                    intent.putExtra(Constants.alarmRequestCode, 2)
                    getRepeatCalender(
                        timePickerHours,
                        timePickerMinute,
                        Calendar.MONDAY,
                        2,
                        intent
                    )
                    repeatDaysList.add(1)
                }
                if (mAlarmViewModel.repeatTuesday) {
                    intent.putExtra(Constants.alarmRequestCode, 3)
                    getRepeatCalender(
                        timePickerHours,
                        timePickerMinute,
                        Calendar.TUESDAY,
                        3,
                        intent
                    )
                    repeatDaysList.add(2)
                }
                if (mAlarmViewModel.repeatWednesday) {
                    intent.putExtra(Constants.alarmRequestCode, 4)
                    getRepeatCalender(
                        timePickerHours,
                        timePickerMinute,
                        Calendar.WEDNESDAY,
                        4,
                        intent
                    )
                    repeatDaysList.add(3)
                }
                if (mAlarmViewModel.repeatThursday) {
                    intent.putExtra(Constants.alarmRequestCode, 5)
                    getRepeatCalender(
                        timePickerHours,
                        timePickerMinute,
                        Calendar.THURSDAY,
                        5,
                        intent
                    )
                    repeatDaysList.add(4)
                }
                if (mAlarmViewModel.repeatFriday) {
                    intent.putExtra(Constants.alarmRequestCode, 6)
                    getRepeatCalender(
                        timePickerHours,
                        timePickerMinute,
                        Calendar.FRIDAY,
                        6,
                        intent
                    )
                    repeatDaysList.add(5)
                }
                if (mAlarmViewModel.repeatSaturday) {
                    intent.putExtra(Constants.alarmRequestCode, 7)
                    getRepeatCalender(
                        timePickerHours,
                        timePickerMinute,
                        Calendar.SATURDAY,
                        7,
                        intent
                    )
                    repeatDaysList.add(6)
                }
            } else {
                intent.putExtra(Constants.alarmRequestCode, 8)
                val calendar =
                    getAlarmDateTimeFromHourAndMinute(timePickerHours, timePickerMinute, false)

                val alarmManager =
                    this.getSystemService(Service.ALARM_SERVICE) as AlarmManager
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    8,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or
                            PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_CANCEL_CURRENT
                )
                //7 * 24 * 60 * 60 * 1000,
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.time.time,
                    pendingIntent
                )
            }

            val renderersFactory = TaoCalligraphy.instance
                .buildRenderersFactory()
            TaoCalligraphy.instance.getDownloadTracker()
                ?.downloadFile(
                    mAlarmViewModel.alarmContent?.alarmContentTitle,
                    Uri.parse(mAlarmViewModel.alarmContent?.alarmContentFileUrl),
                    "",
                    renderersFactory,
                    mAlarmViewModel
                )
            val time = String.format(
                "%02d:%02d", timePickerHours, timePickerMinute
            )
            val addAlarmRequest = AddAlarmRequest().apply {
                this.time = time
                this.contentId = mAlarmViewModel.alarmContent?.alarmContentId ?: "0"
                this.repeatDays = repeatDaysList
            }

            mAlarmViewModel.setAlarm(addAlarmRequest, this, mDisposable)
        }
    }

    private fun getRepeatCalender(
        timePickerHours: Int,
        timePickerMinute: Int,
        dayOfWeek: Int,
        requestCode: Int,
        intent: Intent
    ) {
        val alarmManager =
            this.getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timePickerHours)
        calendar.set(Calendar.MINUTE, timePickerMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        if (calendar.time.time < Calendar.getInstance().time.time) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or
                    PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_CANCEL_CURRENT
        )
        //7 * 24 * 60 * 60 * 1000,
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.time.time,
            pendingIntent
        )
    }

    private fun setRepeatSelection(
        linearLayout: LinearLayout,
        textView: AppCompatTextView,
        isRepeat: Boolean
    ) {
        if (isRepeat) {
            linearLayout.setBackgroundResource(R.drawable.bg_gold_round)
            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        } else {
            linearLayout.setBackgroundResource(R.drawable.bg_white_light_grey_circle)
            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.gold
                )
            )
        }
    }

    private fun setWakeUpMeditationState(isChecked: Boolean) {
        if (isChecked) {
            mBinding.fmAlarm.alpha = 1f
        } else {
            mBinding.fmAlarm.alpha = 0.3f
        }
    }

    private fun setAlarmDataInController() {
        mAlarmViewModel.contentDetailList.clear()
        userHolder.getAlarmData()?.let {
            mBinding.ivMeditationImage.loadImage(
                it.thumbnailImage,
                R.drawable.ic_dummy_morning_freshen_up
            )
            val categoryDetailsList =
                mutableListOf<PlaylistContentFilterApiResponse.ContentList.CategoryDetails>()
            it.contentId?.let { contentId ->
                mAlarmViewModel.contentDetailList.add(
                    PlaylistContentFilterApiResponse.ContentList(
                        "", "", "", "", "", "", "", contentId, false, "",
                        categoryDetailsList, "", 0, null, false, false, false
                    )
                )
            }
            if (!it.contentTitle.isNullOrEmpty()) {
                mBinding.tvMeditateTitle.text = it.contentTitle
            } else {
                mBinding.tvMeditateTitle.text = getString(R.string.set_alarm)
            }


            mBinding.ivMeditationImage.visible()
            mAlarmViewModel.alarmContent = AlarmContent()
            mAlarmViewModel.alarmContent?.setAlarmDataFromResponse(it)
            if (mAlarmViewModel.isConfigChange) {
                mBinding.timePicker.hour = mAlarmViewModel.timePickerHours
                mBinding.timePicker.minute = mAlarmViewModel.timePickerMinute
            } else {
                it.repeatDays?.forEach { days ->
                    when (days) {
                        0 -> {
                            callSundayRepeat()
                        }
                        1 -> {
                            callMondayRepeat()
                        }
                        2 -> {
                            callTuesdayRepeat()
                        }
                        3 -> {
                            callWednesdayRepeat()
                        }
                        4 -> {
                            callThursdayRepeat()
                        }
                        5 -> {
                            callFridayRepeat()
                        }
                        6 -> {
                            callSaturdayRepeat()
                        }
                    }
                }

                if (!it.time.isNullOrEmpty()) {
                    mBinding.timePicker.hour = getHoursFromTime(it.time).toInt()
                    mBinding.timePicker.minute = getMinutesFromTime(it.time).toInt()

                    mAlarmViewModel.timePickerHours = mBinding.timePicker.hour
                    mAlarmViewModel.timePickerMinute = mBinding.timePicker.minute
                }
            }

            mBinding.alarmSwitch.isChecked = it.isWakeUpWithMeditation ?: true
            setWakeUpMeditationState(mBinding.alarmSwitch.isChecked)
            disableAlarmView(mBinding.alarmSwitch.isChecked)
        } ?: kotlin.run {
            mBinding.ivMeditationImage.gone()
            mBinding.btnChangeMeditation.gone()
            mBinding.tvMeditateTitle.text = getString(R.string.set_alarm)
            mBinding.timePicker.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            mBinding.timePicker.minute = Calendar.getInstance().get(Calendar.MINUTE)
            mAlarmViewModel.alarmContent = null
            clearRepeat()
            disableAlarmView(false)
            mBinding.alarmSwitch.isChecked = false
            setWakeUpMeditationState(mBinding.alarmSwitch.isChecked)
            setAlarmButtonEnabled(false)
        }
    }

    private fun clearRepeat() {
        mAlarmViewModel.repeatSunday = true
        mAlarmViewModel.repeatMonday = true
        mAlarmViewModel.repeatTuesday = true
        mAlarmViewModel.repeatWednesday = true
        mAlarmViewModel.repeatThursday = true
        mAlarmViewModel.repeatFriday = true
        mAlarmViewModel.repeatSaturday = true
        callSundayRepeat()
        callMondayRepeat()
        callTuesdayRepeat()
        callWednesdayRepeat()
        callThursdayRepeat()
        callFridayRepeat()
        callSaturdayRepeat()
    }

    private fun setDaysRepeatSelection() {
        setRepeatSelection(mBinding.llSunday, mBinding.tvSunday, mAlarmViewModel.repeatSunday)
        setRepeatSelection(mBinding.llMonday, mBinding.tvMonday, mAlarmViewModel.repeatMonday)
        setRepeatSelection(mBinding.llTuesday, mBinding.tvTuesday, mAlarmViewModel.repeatTuesday)
        setRepeatSelection(
            mBinding.llWednesday,
            mBinding.tvWednesday,
            mAlarmViewModel.repeatWednesday
        )
        setRepeatSelection(mBinding.llThursday, mBinding.tvThursday, mAlarmViewModel.repeatThursday)
        setRepeatSelection(mBinding.llFriday, mBinding.tvFriday, mAlarmViewModel.repeatFriday)
        setRepeatSelection(mBinding.llSaturday, mBinding.tvSaturday, mAlarmViewModel.repeatSaturday)
    }

    private fun callSundayRepeat() {
        mAlarmViewModel.repeatSunday = !mAlarmViewModel.repeatSunday
        setRepeatSelection(mBinding.llSunday, mBinding.tvSunday, mAlarmViewModel.repeatSunday)
    }

    private fun callMondayRepeat() {
        mAlarmViewModel.repeatMonday = !mAlarmViewModel.repeatMonday
        setRepeatSelection(mBinding.llMonday, mBinding.tvMonday, mAlarmViewModel.repeatMonday)
    }

    private fun callTuesdayRepeat() {
        mAlarmViewModel.repeatTuesday = !mAlarmViewModel.repeatTuesday
        setRepeatSelection(mBinding.llTuesday, mBinding.tvTuesday, mAlarmViewModel.repeatTuesday)
    }

    private fun callWednesdayRepeat() {
        mAlarmViewModel.repeatWednesday = !mAlarmViewModel.repeatWednesday
        setRepeatSelection(
            mBinding.llWednesday,
            mBinding.tvWednesday,
            mAlarmViewModel.repeatWednesday
        )
    }

    private fun callThursdayRepeat() {
        mAlarmViewModel.repeatThursday = !mAlarmViewModel.repeatThursday
        setRepeatSelection(mBinding.llThursday, mBinding.tvThursday, mAlarmViewModel.repeatThursday)
    }

    private fun callFridayRepeat() {
        mAlarmViewModel.repeatFriday = !mAlarmViewModel.repeatFriday
        setRepeatSelection(mBinding.llFriday, mBinding.tvFriday, mAlarmViewModel.repeatFriday)
    }

    private fun callSaturdayRepeat() {
        mAlarmViewModel.repeatSaturday = !mAlarmViewModel.repeatSaturday
        setRepeatSelection(mBinding.llSaturday, mBinding.tvSaturday, mAlarmViewModel.repeatSaturday)
    }

    private fun callActiveAlarmAPI() {
        mViewModel.getActiveAlarm(this, mDisposable)
    }

    private fun openChooseMeditationDialog() {
        val dialog = ChooseMeditationDialog.newInstance(object :
            ChooseMeditationDialog.ChooseMeditationScreenListener {
            override fun onChooseMeditationSelect(selectedMeditations: ArrayList<PlaylistContentFilterApiResponse.ContentList>) {
                mAlarmViewModel.contentDetailList = selectedMeditations
                mAlarmViewModel.isDataSelected = true
                recreate()
                if (selectedMeditations.isNotEmpty()) {
                    mAlarmViewModel.content = selectedMeditations.first()
                    setDataFromViewModel()
                }
            }
        }, false, ArrayList(mAlarmViewModel.contentDetailList))
        dialog.show(
            getFragmentTransactionFromTag(ChooseMeditationDialog.TAG),
            ChooseMeditationDialog.TAG
        )
    }

    override fun onBackPressed() {
        if (mAlarmViewModel.isAlarmChanged && !mAlarmViewModel.isCancelWithoutSaving) {
            showSaveAlarmDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showSaveAlarmDialog() {
        val dialog = SaveAlarmDialog(this, object : OnSaveAlarmListener {
            override fun onSaveAlarmClick(isSave: Boolean) {
                if (isSave) {
                    setAlarm()
                } else {
                    mAlarmViewModel.isCancelWithoutSaving = true
                    onBackPressed()
                }
            }
        })
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroy() {
        super.onDestroy()
        mAlarmViewModel.isConfigChange = isChangingConfigurations
    }
}