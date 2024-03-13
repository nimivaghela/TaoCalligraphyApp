package com.app.taocalligraphy.ui.meditation_timer


import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCreateNewTimerBinding
import com.app.taocalligraphy.models.MeditationTimerModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_timer.MeditationListApiResponse
import com.app.taocalligraphy.models.response.meditation_timer.SoundApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.meditation_timer.adapter.MeditationBackImageAdapter
import com.app.taocalligraphy.ui.meditation_timer.dialog.MeditationMusicSelectionDialog
import com.app.taocalligraphy.ui.meditation_timer.dialog.MeditationTimerSelectionDialog
import com.app.taocalligraphy.ui.meditation_timer.viewmodel.MeditationTimerViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageWithBlur
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


@AndroidEntryPoint
class CreateNewTimerActivity : BaseActivity<ActivityCreateNewTimerBinding>() {

    val mViewModel: MeditationTimerViewModel by viewModels()
    var meditationMusicSelectionDialog: MeditationMusicSelectionDialog? = null

    companion object {
        private var isForEdit = false
        private var isTimerClicked = false

        fun startActivity(
            activity: AppCompatActivity,
            meditationDetail: MeditationListApiResponse.MeditationDetail?,
            isFromEdit: Boolean,
            addEditMeditationTimerResult: ActivityResultLauncher<Intent>,
        ) {
            val intent = Intent(activity, CreateNewTimerActivity::class.java)
            intent.putExtra(Constants.Param.isForEdit, isFromEdit)
            intent.putExtra(Constants.Param.meditationList, meditationDetail)
            addEditMeditationTimerResult.launch(intent)
        }
    }

    override fun getResource() = R.layout.activity_create_new_timer

    private fun setTimePickerInterval(timePicker: TimePicker) {
        try {
            val minutePicker: NumberPicker = timePicker.findViewById(
                Resources.getSystem().getIdentifier(
                    "minute", "id", "android"
                )
            ) as NumberPicker
            minutePicker.minValue = 0
            minutePicker.maxValue = 3
            minutePicker.displayedValues = arrayOf(
                getString(R.string.zero_zero), getString(R.string.one_five), getString(
                    R.string.three_zero
                ), getString(R.string.four_five)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initView() {
        mBinding.timePicker.setIs24HourView(!userHolder.is12HourFormat)
        setTimePickerInterval(mBinding.timePicker)
        getIntentData()
        setupToolbar()
        setTimerModel()

        mBinding.etName.addTextChangedListener {
            mBinding.tlName.isErrorEnabled = false
        }

        if (isForEdit) {
            setEditTimerModel()
        } else {
            mBinding.timePicker.minute =
                getMinuteFromCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE))
        }

        if (isNetwork()) {
            if (mViewModel.backgroundImageData == null) {
                mViewModel.backgroundImageListApi(this, mDisposable)
            } else {
                setBackgroundImageData()
            }

            if (mViewModel.soundsList.isEmpty()) {
                mViewModel.soundApi(Constants.START_SOUND, this, mDisposable)
            } else {
                setStarEndSoundData()
            }

            if (mViewModel.ambientSoundList.isEmpty()) {
                mViewModel.soundAmbientApi(Constants.AMBIENT_SOUND, this, mDisposable)
            } else {
                setAmbientSoundData()
            }
        }

        if (mViewModel.selectedTimer != "") {
            setTimeInPicker(mViewModel.selectedTimer)
        }
    }

    private fun getIntentData() {
        val intent = intent
        if (intent != null) {
            if (intent.hasExtra(Constants.Param.isForEdit)) {
                isForEdit = intent.getBooleanExtra(Constants.Param.isForEdit, false)
            }
            if (intent.hasExtra(Constants.Param.meditationList)) {
                mViewModel.meditationDetail =
                    intent.getSerializableExtra(Constants.Param.meditationList) as MeditationListApiResponse.MeditationDetail?
            }
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()

        if (isForEdit) {
            mBinding.tvTitleToolbar.text = getString(R.string.edit_timer)
        } else {
            mBinding.tvTitleToolbar.text = getString(R.string.new_timer)
        }
    }

    private fun setTimerModel() {
        mViewModel.mTimerArrayList.clear()
        mViewModel.mTimerArrayList.add(MeditationTimerModel(getString(R.string.one_min), 1, false))
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.three_min),
                3,
                false
            )
        )
        mViewModel.mTimerArrayList.add(MeditationTimerModel(getString(R.string.five_min), 5, false))
        mViewModel.mTimerArrayList.add(MeditationTimerModel(getString(R.string.ten_min), 10, false))
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.fiften_min),
                15,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.twenty_min),
                20,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.twenty_five_min),
                25,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.thirty_min),
                30,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.fourty_five_min),
                45,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.one_hour),
                60,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.two_hours),
                120,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.four_hours),
                240,
                false
            )
        )
        mViewModel.mTimerArrayList.add(
            MeditationTimerModel(
                getString(R.string.eight_hours),
                480,
                false
            )
        )

        setMeditationSoundTimer(
            if (mViewModel.selectedSoundTimer == 0 && !isForEdit) mViewModel.mTimerArrayList[2].timer.toString()
            else mViewModel.selectedSoundTimer.toString()
        )
    }

    private fun setEditTimerModel() {
        mViewModel.meditationDetail?.let {
            mBinding.etName.setText(it.name)

            if (mViewModel.selectedAmbientSound == -1) {
                mBinding.tvAmbitSound.text = it.ambientSound?.name
                mViewModel.selectedAmbientSound = it.ambientSound?.soundId!!
            }
            if (mViewModel.selectedStartSound == -1) {
                mBinding.tvStartSound.text = it.startSound?.name
                mViewModel.selectedStartSound = it.startSound?.soundId!!
            }
            if (mViewModel.selectedEndSound == -1) {
                mBinding.tvEndSound.text = it.endSound?.name
                mViewModel.selectedEndSound = it.endSound?.soundId!!
            }

            it.reminderTime?.let { reminderTime ->
                setTimeInPicker(if (mViewModel.selectedTimer != "") mViewModel.selectedTimer else reminderTime)
            } ?: kotlin.run {
                if (mBinding.tvTime.text.equals(getString(R.string.not_set))) {
                    mBinding.ivResetTimer.gone()
                    mBinding.ivArrowTime.visible()
                } else {
                    mBinding.ivResetTimer.visible()
                    mBinding.ivArrowTime.gone()
                }
            }

            setMeditationSoundTimer(
                if (mViewModel.selectedSoundTimer != 0) mViewModel.selectedSoundTimer.toString()
                else it.meditationTime!!
            )

            mViewModel.backgroundId = it.backgroundImage?.backgroundImageId!!
        }
    }

    private fun setMeditationSoundTimer(meditationSoundTime: String) {
        mViewModel.mTimerArrayList.map {
            if (it.timer.toString() == meditationSoundTime) it.isSelected = true
        }

        mViewModel.mTimerArrayList.filter { it.isSelected }.also {
            if (it.isNotEmpty()) {
                mBinding.tvMeditationTimer.text = it.first().title
                mViewModel.selectedSoundTimer = it.first().timer
            }
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.soundLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.list?.let { soundList ->
                    mViewModel.soundsList = soundList
                    setStarEndSoundData()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, ERROR)
                        else -> {
                        }
                    }
                }
            }
            mViewModel.soundLiveData.postValue(null)
        }

        mViewModel.soundLiveDataAmbient.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.list?.let { list ->
                    mViewModel.ambientSoundList.clear()
                    mViewModel.ambientSoundList.addAll(list)
                    setAmbientSoundData()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, ERROR)
                        else -> {
                        }
                    }
                }
            }
            mViewModel.soundLiveDataAmbient.postValue(null)
        }

        mViewModel.meditationTimerLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    val resultIntent = Intent()
                    resultIntent.putExtra("showSnackbar", true)
                    resultIntent.putExtra("message", it.message.toString())
                    resultIntent.putExtra("type", it.type ?: SUCCESS)
                    setResult(Activity.RESULT_OK, resultIntent)
                    onBackPressed()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, ERROR)
                        else -> {
                        }
                    }
                }
            }
        }

        mViewModel.meditationTimerEditLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)

                requestState.apiResponse?.let {
                    val resultIntent = Intent()
                    resultIntent.putExtra("showSnackbar", true)
                    resultIntent.putExtra("message", it.message.toString())
                    resultIntent.putExtra("type", it.type ?: SUCCESS)
                    setResult(Activity.RESULT_OK, resultIntent)
                    onBackPressed()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, ERROR)
                        else -> {
                        }
                    }
                }
            }
        }

        mViewModel.backgroundImageLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    it.data?.let {
                        mViewModel.backgroundImageData = it
                        setBackgroundImageData()
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, ERROR)
                        else -> {
                        }
                    }
                }
            }

            mViewModel.backgroundImageLiveData.postValue(null)
        }
    }

    override fun handleListener() {

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.llMeditationTimer.clickWithDebounce {
            hideKeyboard()
            openMeditationTimerSelectionDialog()
        }

        mBinding.btnSave.clickWithDebounce {
            hideKeyboard()

            if (checkScreenValidation()) {
                if (mViewModel.selectedTimer != "" && mViewModel.selectedTimer != mViewModel.meditationDetail?.reminderTime) {
                    mViewModel.selectedTimer = mViewModel.remindDailyTime.getLocalTimeInUTC(this)
                }

                if (isNetwork()) {
                    if (isForEdit) {
                        mViewModel.meditationDetail?.apply {
                            mViewModel.meditationTimerEditApi(
                                meditationId!!,
                                mViewModel.selectedSoundTimer,
                                mBinding.etName.text.toString(),
                                mViewModel.selectedStartSound,
                                mViewModel.selectedEndSound,
                                mViewModel.selectedAmbientSound,
                                mViewModel.selectedTimer,
                                mViewModel.backgroundId,
                                this@CreateNewTimerActivity,
                                mDisposable
                            )
                        }
                    } else {
                        mViewModel.meditationTimerAddApi(
                            mViewModel.selectedSoundTimer,
                            mBinding.etName.text.toString(),
                            mViewModel.selectedStartSound,
                            mViewModel.selectedEndSound,
                            mViewModel.selectedAmbientSound,
                            mViewModel.selectedTimer,
                            mViewModel.backgroundId,
                            this,
                            mDisposable
                        )
                    }
                } else
                    longToast(
                        getString(
                            R.string.no_internet,
                            getString(R.string.to_ad_edit_meditation_timer)
                        ), ERROR
                    )
            }
        }

        mBinding.llSound.setOnClickListener {
            hideKeyboard()
            if (mBinding.llSoundsDetails.isVisible) {
                mBinding.viewSoundHorizontalLine.visibility = View.VISIBLE
                mBinding.ivSoundsOpenClose.setImageResource(R.drawable.vd_right_side_arrow_gold)
                mBinding.llSoundsDetails.visibility = View.GONE
            } else {
                mBinding.viewSoundHorizontalLine.visibility = View.GONE
                mBinding.ivSoundsOpenClose.setImageResource(R.drawable.vd_down_arrow_gold)
                mBinding.llSoundsDetails.visibility = View.VISIBLE
            }
        }

        mBinding.llStartSound.clickWithDebounce {
            hideKeyboard()
            showProgressIndicator(mBinding.llProgress, true)
            mViewModel.soundFrom = Constants.START_SOUND
            openMeditationSoundSelectionDialog(mViewModel.startSoundList)
        }

        mBinding.llEndSound.clickWithDebounce {
            hideKeyboard()
            showProgressIndicator(mBinding.llProgress, true)
            mViewModel.soundFrom = Constants.END_SOUND
            openMeditationSoundSelectionDialog(mViewModel.endSoundList)
        }

        mBinding.llAmbientSound.clickWithDebounce {
            hideKeyboard()
            showProgressIndicator(mBinding.llProgress, true)
            mViewModel.soundFrom = Constants.AMBIENT_SOUND
            openMeditationSoundSelectionDialog(mViewModel.ambientSoundList)
        }

        mBinding.ivResetTimer.setOnClickListener {
            hideKeyboard()
            mBinding.tvTime.text = getString(R.string.not_set)
            mBinding.ivArrowTime.visible()
            mBinding.ivArrowTime.setImageResource(R.drawable.vd_down_arrow_grey)
            mBinding.ivResetTimer.gone()
            mViewModel.remindDailyTime = getString(R.string.not_set)
            val calendar: Calendar = Calendar.getInstance()
            mBinding.timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            mBinding.timePicker.minute = getMinuteFromCurrentMinute(calendar.get(Calendar.MINUTE))
            mViewModel.selectedTimer = ""
        }

        mBinding.llReminderTime.setOnClickListener {
            hideKeyboard()
            if (!isTimerClicked) {
                isTimerClicked = true
                mBinding.llReminderTime.setBackgroundResource(R.drawable.bg_white_gold_small_radius)
                mBinding.ivArrowTime.setImageResource(R.drawable.vd_up_arrow_gold)
                mBinding.timePicker.visible()
                mBinding.rlCancelSet.visible()
            } else {
                isTimerClicked = false
                mBinding.llReminderTime.setBackgroundResource(R.drawable.bg_white_medium_grey_border_8dp)
                mBinding.ivArrowTime.setImageResource(R.drawable.vd_down_arrow_grey)
                mBinding.timePicker.gone()
                mBinding.rlCancelSet.gone()
            }
        }

        mBinding.tvCancel.clickWithDebounce {
            hideKeyboard()
            isTimerClicked = true
            mBinding.llReminderTime.setBackgroundResource(R.drawable.bg_white_medium_grey_border_8dp)
            mBinding.ivArrowTime.setImageResource(R.drawable.vd_down_arrow_grey)
            mBinding.timePicker.gone()
            mBinding.rlCancelSet.gone()
        }

        mBinding.btnCancel.clickWithDebounce {
            hideKeyboard()
            onBackPressed()
        }

        mBinding.tvSet.clickWithDebounce {
            hideKeyboard()
            isTimerClicked = true
            mBinding.llReminderTime.setBackgroundResource(R.drawable.bg_white_medium_grey_border_8dp)
            mBinding.ivResetTimer.visible()
            mBinding.ivArrowTime.gone()
            mBinding.timePicker.gone()
            mBinding.rlCancelSet.gone()

            getTimeFromPicker(mBinding.timePicker.hour, mBinding.timePicker.minute)
            mBinding.tvTime.text = mViewModel.remindDailyTime
        }

        mBinding.sliderTimer.addOnChangeListener { _, value, _ ->
            hideKeyboard()
            mBinding.tvMeditationTimer.text = value.toInt().toString()
        }
    }

    private fun getTimeFromPicker(hourOfDay: Int, minute: Int) {
        mViewModel.remindDailyTime = when (minute) {
            0 -> ("$hourOfDay:" + getString(R.string.zero_zero)).getLocalTime(this@CreateNewTimerActivity)
            1 -> ("$hourOfDay:" + getString(R.string.one_five)).getLocalTime(this@CreateNewTimerActivity)
            2 -> ("$hourOfDay:" + getString(R.string.three_zero)).getLocalTime(this@CreateNewTimerActivity)
            3 -> ("$hourOfDay:" + getString(R.string.four_five)).getLocalTime(this@CreateNewTimerActivity)
            else -> ("$hourOfDay:" + getString(R.string.zero_zero)).getLocalTime(this@CreateNewTimerActivity)
        }
        mViewModel.selectedTimer = mViewModel.remindDailyTime.getLocalTimeInUTC(this)
    }

    private fun getMinuteFromCurrentMinute(currentMinute: Int): Int {
        val minute: Int
        if (isForEdit) {
            minute = when (currentMinute) {
                in (1..15) -> 1
                in (16..30) -> 2
                in (31..45) -> 3
                in (45..59) -> 3
                else -> 0
            }
        } else {
            when (currentMinute) {
                in (1..15) -> minute = 1
                in (16..30) -> minute = 2
                in (31..45) -> minute = 3
                in (45..59) -> {
                    val hour = mBinding.timePicker.hour + 1
                    mBinding.timePicker.hour = hour
                    minute = 0
                }
                else -> minute = 0
            }
        }
        return minute
    }

    private fun openMeditationTimerSelectionDialog() {
        val dialog =
            MeditationTimerSelectionDialog(this, mViewModel.mTimerArrayList,
                object : MeditationTimerSelectionDialog.TimerSelectionListener {
                    override fun onTimerSelect(meditationTimerModel: MeditationTimerModel) {
                        mBinding.tvMeditationTimer.text = meditationTimerModel.title
                        mViewModel.selectedSoundTimer = meditationTimerModel.timer
                    }
                }
            )


        dialog.setOnShowListener {
            val bottomSheet: FrameLayout =
                (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
            val layout: CoordinatorLayout = bottomSheet.parent as CoordinatorLayout
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                bottomSheet
            )
            behavior.peekHeight = bottomSheet.height
            layout.parent.requestLayout()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun openMeditationSoundSelectionDialog(soundList: ArrayList<SoundApiResponse.SoundList?>) {
        if (meditationMusicSelectionDialog != null && meditationMusicSelectionDialog!!.isShowing) {
            meditationMusicSelectionDialog!!.dismiss()
        }
        when (mViewModel.soundFrom) {
            Constants.AMBIENT_SOUND -> {
                mViewModel.selectedSoundId = mViewModel.selectedAmbientSound
            }
            Constants.START_SOUND -> {
                mViewModel.selectedSoundId = mViewModel.selectedStartSound
            }
            Constants.END_SOUND -> {
                mViewModel.selectedSoundId = mViewModel.selectedEndSound
            }
        }
        meditationMusicSelectionDialog =
            MeditationMusicSelectionDialog(this@CreateNewTimerActivity,
                mViewModel.selectedSoundId,
                soundList,
                object : MeditationMusicSelectionDialog.SoundSelectionListener {
                    override fun onSoundSelect(data: SoundApiResponse.SoundList) {
                        when (mViewModel.soundFrom) {
                            Constants.AMBIENT_SOUND -> {
                                mBinding.tvAmbitSound.text = data.name
                                mViewModel.selectedAmbientSound = data.soundId!!
                            }
                            Constants.START_SOUND -> {
                                mBinding.tvStartSound.text = data.name
                                mViewModel.selectedStartSound = data.soundId!!
                            }
                            Constants.END_SOUND -> {
                                mBinding.tvEndSound.text = data.name
                                mViewModel.selectedEndSound = data.soundId!!
                            }
                        }
                    }
                }
            )


        meditationMusicSelectionDialog!!.setOnShowListener { dialog ->
            val bottomSheet: FrameLayout =
                (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
            val layout: CoordinatorLayout = bottomSheet.parent as CoordinatorLayout
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                bottomSheet
            )
            behavior.peekHeight = bottomSheet.height
            layout.parent.requestLayout()
        }

        if (meditationMusicSelectionDialog?.isShowing == false) {
            meditationMusicSelectionDialog?.show()
        }

        meditationMusicSelectionDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        showProgressIndicator(mBinding.llProgress, false)
    }

    private fun checkScreenValidation(): Boolean {
        if (TextUtils.isEmpty(mBinding.etName.text.toString().trim())) {
            mBinding.tlName.error = getString(R.string.please_enter_meditation_name)
            return false
        } else {
            mBinding.tlName.isErrorEnabled = false
        }
        if (mViewModel.selectedStartSound < 0) {
            longToast(getString(R.string.please_select_start_sound), ERROR)
            return false
        }
        if (mViewModel.selectedEndSound < 0) {
            longToast(getString(R.string.please_select_end_sound), ERROR)
            return false
        }
        if (mViewModel.selectedAmbientSound < 0) {
            longToast(getString(R.string.please_select_ambient_sound), ERROR)
            return false
        }
        if (mViewModel.backgroundId < 0) {
            longToast(getString(R.string.please_select_background_image), ERROR)
            return false
        }
        return true
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, ERROR)
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

    private fun setBackgroundImageData() {
        mViewModel.backgroundImageData?.let {
            val meditationBackImageAdapter = MeditationBackImageAdapter(it)
            mBinding.rvSoundBackgroundImage.adapter = meditationBackImageAdapter
            if (isForEdit) {
                for (i in it.indices) {
                    if (it[i].backgroundId == mViewModel.backgroundId) {
                        mBinding.imgMeditationTimerBack?.loadImageWithBlur(
                            it[i].backgroundImageOriginal,
                            R.drawable.bg_placeholder_meditation_timer
                        )
                        it[i].isDefault = true
                    } else {
                        it[i].isDefault = false
                    }
                }

            } else {
                for (i in it.indices) {
                    if (it[i].isDefault!!) {
                        mViewModel.backgroundId = it[i].backgroundId!!
                        mBinding.imgMeditationTimerBack?.loadImageWithBlur(
                            it[i].backgroundImageOriginal,
                            R.drawable.bg_placeholder_meditation_timer
                        )
                        break
                    }
                }
            }
            meditationBackImageAdapter.select =
                object : MeditationBackImageAdapter.OnSelectSoundBackItem {
                    override fun onSelectSoundBackItem(position: Int) {
                        hideKeyboard()
                        mViewModel.backgroundId = it[position].backgroundId!!
                        for (i in it.indices) {
                            if (i == position) {
                                it[position].isDefault = true
                                meditationBackImageAdapter.notifyItemChanged(position)
                                mBinding.imgMeditationTimerBack?.loadImageWithBlur(
                                    it[position].backgroundImageOriginal,
                                    R.drawable.bg_placeholder_meditation_timer
                                )
                            } else {
                                if (it[i].isDefault!!)
                                    meditationBackImageAdapter.notifyItemChanged(i)
                                it[i].isDefault = false
                            }
                        }
                    }
                }
        }
    }

    private fun setAmbientSoundData() {
        mViewModel.ambientSoundList.let { list ->
            if (mViewModel.selectedAmbientSound == -1) {
                val sound = list.find {
                    it?.isDefaultForAmbient == true
                }

                if (sound?.isDefaultForAmbient == true) {
                    if (isForEdit) {
                        if (sound.soundId == mViewModel.meditationDetail?.ambientSound?.soundId) {
                            sound.isSelected = true
                            mViewModel.selectedAmbientSound = sound.soundId ?: 0
                            val soundName = list.find {
                                it?.soundId == mViewModel.selectedAmbientSound
                            }?.name

                            mBinding.tvAmbitSound.text = soundName
                        }
                    } else {
                        val soundName = list.find {
                            it?.isDefaultForAmbient == true
                        }?.name

                        list.filter {
                            it?.isDefaultForAmbient == true
                        }.map {
                            it?.isSelected = true
                        }
                        mViewModel.selectedAmbientSound = list.find {
                            it?.isDefaultForAmbient == true
                        }?.soundId ?: -1

                        mBinding.tvAmbitSound.text = soundName
                    }
                }
            } else {
                val sound = list.find {
                    it?.soundId == mViewModel.selectedAmbientSound
                }

                list.map { it?.isSelected = it?.soundId == mViewModel.selectedAmbientSound }

                mBinding.tvAmbitSound.text = sound?.name
            }
        }
    }

    private fun setStarEndSoundData() {
        mViewModel.soundsList.let { soundList ->
            mViewModel.startSoundList.clear()
            mViewModel.endSoundList.clear()
            soundList.forEach { dataSound ->
                dataSound?.let { sound ->
                    if (sound.isDefaultForStart == true && mViewModel.selectedStartSound == -1) {
                        if (isForEdit) {
                            mViewModel.startSoundList.add(sound)
                            mViewModel.endSoundList.add(sound)
                            if (sound.soundId == mViewModel.meditationDetail?.startSound?.soundId) {
                                sound.isSelected = true
                                mViewModel.selectedStartSound = sound.soundId ?: 0
                            }
                        } else {
                            sound.isSelected = true
                            mViewModel.startSoundList.add(sound)
                            sound.isSelected = false
                            mViewModel.endSoundList.add(sound)
                            mBinding.tvStartSound.text = sound.name
                            mViewModel.selectedStartSound = sound.soundId!!
                        }
                    } else if (sound.isDefaultForEnd == true && mViewModel.selectedEndSound == -1) {
                        if (isForEdit) {
                            mViewModel.startSoundList.add(sound)
                            mViewModel.endSoundList.add(sound)
                            if (sound.soundId == mViewModel.meditationDetail?.endSound?.soundId) {
                                sound.isSelected = true
                                mViewModel.selectedEndSound = sound.soundId!!
                            }
                        } else {
                            sound.isSelected = false
                            mViewModel.startSoundList.add(sound)
                            sound.isSelected = true
                            mViewModel.endSoundList.add(sound)
                            mBinding.tvEndSound.text = sound.name
                            mViewModel.selectedEndSound = sound.soundId!!
                        }
                    } else {
                        if (sound.soundId == mViewModel.selectedStartSound) {
                            sound.isSelected = true
                            mViewModel.selectedStartSound = sound.soundId ?: 0
                            mBinding.tvStartSound.text = sound.name
                        } else {
                            sound.isSelected = false
                        }
                        mViewModel.startSoundList.add(sound)

                        if (sound.soundId == mViewModel.selectedEndSound) {
                            sound.isSelected = true
                            mViewModel.selectedEndSound = sound.soundId ?: 0
                            mBinding.tvEndSound.text = sound.name
                        } else {
                            sound.isSelected = false
                        }
                        mViewModel.endSoundList.add(sound)
                    }
                }
            }
        }
    }

    private fun setTimeInPicker(reminderTime: String) {
        val time = reminderTime.getUTCTimeInLocal()
        mBinding.timePicker.hour = time.substringBeforeLast(":").toInt()
        mBinding.timePicker.minute =
            getMinuteFromCurrentMinute(time.substringAfterLast(":").toInt())

        getTimeFromPicker(mBinding.timePicker.hour, mBinding.timePicker.minute)
        mBinding.tvTime.text = mViewModel.remindDailyTime
    }
}