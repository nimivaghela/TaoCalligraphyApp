package com.app.taocalligraphy.ui.settings

import android.app.AlarmManager
import android.app.Service
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.SensorManager.getOrientation
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.BuildConfig
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivitySettingsBinding
import com.app.taocalligraphy.models.MeditationTimerModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.profile.LanguageListApiResponse
import com.app.taocalligraphy.models.response.profile.UserSettingsApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.favorites.FavoritesActivity
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.notification.dialog.ChooseMeditationDialog
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.settings.adapter.DeviceListAdapter
import com.app.taocalligraphy.ui.settings.adapter.ImportantAdapter
import com.app.taocalligraphy.ui.settings.adapter.SelectSingleLanguageAdapter
import com.app.taocalligraphy.ui.settings.dialog.SendFeedbackDialog
import com.app.taocalligraphy.ui.settings.dialog.SessionTimerSelectionDialog
import com.app.taocalligraphy.ui.settings.dialog.StaticContentDialog
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint

class SettingsActivity : BaseActivity<ActivitySettingsBinding>(),
    SelectSingleLanguageAdapter.SingleSelectItemClickListener,
    ImportantAdapter.ImportantItemClickListener, DeviceListAdapter.DeviceItemClickListener {

    private val mViewModel: ProfileViewModel by viewModels()
    private val mHomeViewModel: HomeViewModel by viewModels()
    private var isThisDevice = false
    private var logoutItemPosition = 0

    private lateinit var dialogFeedback: SendFeedbackDialog
    private lateinit var mAdapter: SelectSingleLanguageAdapter
    private lateinit var mImportantAdapterAdapter: ImportantAdapter
    private lateinit var mDeviceListAdapter: DeviceListAdapter

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_settings

    override fun initView() {
        setupToolbar()
        setupData()
        updateProfile()
        setAccessControlView()
        LocalBroadcastManager.getInstance(this@SettingsActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setAccessControlView()
        }
    }

    private fun setAccessControlView() {
        if(!(UserHolder.EnumUserModulePermission.SEND_APP_FEEDBACK.permission?.canAccess ?: true)){
            mBinding.ivLock.visible()
            mBinding.ivThumbImage.gone()
//            mBinding.btnSendFeedback.icon = resources.getDrawable(R.drawable.ic_lock)
        }else{
            mBinding.ivLock.gone()
            mBinding.ivThumbImage.visible()
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@SettingsActivity).unregisterReceiver(mAccessLevelReceiver)
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    private fun setupData() {
        mBinding.tvAppVersion.text = BuildConfig.VERSION_NAME
        mBinding.tvSession.text = getString(R.string.select)
        mViewModel.mSessionArrayList.clear()
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.select), 0, true))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.one_min), 1, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.three_min), 3, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.five_min), 5, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.ten_min), 10, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.fiften_min), 15, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.twenty_min), 20, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.twenty_five_min), 25, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.thirty_min), 30, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.fourty_five_min), 45, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.one_hour), 60, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.two_hour), 120, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.four_hour), 240, false))
        mViewModel.mSessionArrayList.add(MeditationTimerModel(getString(R.string.eight_hour), 480, false))

        val dividerItemDecoration = com.app.taocalligraphy.utils.DividerItemDecoration(this,R.drawable.rv_view,
            showFirstDivider = false,
            showLastDivider = false)
        mBinding.rvLanguage.addItemDecoration(dividerItemDecoration)
        mAdapter = SelectSingleLanguageAdapter(mutableListOf(), this)
        mBinding.rvLanguage.adapter = mAdapter
        mImportantAdapterAdapter = ImportantAdapter(mutableListOf(), this)
        mBinding.rvImportant.adapter = mImportantAdapterAdapter
        mDeviceListAdapter = DeviceListAdapter(mutableListOf(), this)
        mBinding.rvDevice.adapter = mDeviceListAdapter

        if (isNetwork())
            if(mViewModel.settingsData == null)
                mViewModel.getUserSettingsApi(this, mDisposable)
            else
                updateView()
    }

    override fun observeApiCallbacks() {
        mViewModel.userSettingsLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { data ->
                    mViewModel.settingsData = data
                    updateView()
                }
                longToastState(requestState.error)
                mViewModel.userSettingsLiveData.postValue(null)
            }
        }
        mViewModel.settingsLanguagesLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { data ->
                    if (data.data != null) {
                        data.data?.let { it ->
                            mViewModel.settingsLanguageList.clear()
                            mViewModel.settingsLanguageList.addAll(it.filter { list -> list.isActive!! } as ArrayList<LanguageListApiResponse.Data>)
                            if (mViewModel.settingsLanguageList.size > 0) {
                                mBinding.tvLanguageNotFound.gone()
                                mBinding.rvLanguage.visible()
                                for (i in mViewModel.settingsLanguageList.indices) {
                                    mViewModel.settingsLanguageList[i].isSelected =
                                        mViewModel.selectedAppLanguage == mViewModel.settingsLanguageList[i].languageId
                                }
                                mAdapter.updateList(mViewModel.settingsLanguageList)
                            } else {
                                mBinding.rvLanguage.gone()
                                mBinding.tvLanguageNotFound.visible()
                            }
                        }
                    }
                }
                longToastState(requestState.error)
            }
        }
        mViewModel.userUpdateSettingsLiveData.observe(this)
        { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                    userHolder.is12HourFormat = mViewModel?.is12HourFormat ?: true
                    userHolder.setUserZenModeForMeditation(mViewModel.isSilenceDuringMeditation)
                }
            }
            mViewModel.userUpdateSettingsLiveData.postValue(null)
        }
        mViewModel.userFeedbackLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    if (this::dialogFeedback.isInitialized)
                        dialogFeedback.dismiss()
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                }
                mViewModel.userFeedbackLiveData.postValue(null)
            }
        }
        mViewModel.deleteAccountLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                   cancelAllPreviousAlarm()
                    userHolder.clearUserHolder()
                    LoginManager.getInstance().logOut()
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut()
                    mHomeViewModel.deleteAllDownloads()
                    TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    startActivity(intent)
                    finishAffinity()
                }
                longToastState(requestState.error)
            }
            mViewModel.deleteAccountLiveData.postValue(null)
        }

        mHomeViewModel.userLogoutLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    if (isThisDevice) {
                        this.let {
                            cancelAllPreviousAlarm()
                            userHolder.clearUserHolder()
                            LoginManager.getInstance().logOut()
                            notificationManager?.cancelAll()
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .build()
                            val googleSignInClient = GoogleSignIn.getClient(this, gso)
                            googleSignInClient.signOut()
                            mViewModel.deleteAllDownloads()
                            TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
                            cancelAllPreviousAlarm()
                            //                        WelcomeLoginActivity.isFromLogin = true
                            val intent = Intent(it, WelcomeActivity::class.java)
                            intent.addFlags(
                                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                            startActivity(intent)
                            it.finishAffinity()
                        }
                    } else {
                        mDeviceListAdapter.deleteItem(logoutItemPosition)
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let {
                                longToast(
                                    it, ERROR
                                )
                            }
                        Constants.CUSTOM_ERROR ->
                            longToast(
                                "" + errorObj.customMessage, ERROR
                            )
                        else -> {
                        }
                    }
                }
                mHomeViewModel.userLogoutLiveData.postValue(null)
            }
        }
    }

    private fun updateLanguageView() {
        if (mViewModel.settingsLanguageList.size > 0) {
            mBinding.tvLanguageNotFound.gone()
            mBinding.rvLanguage.visible()
            for (i in mViewModel.settingsLanguageList.indices) {
                mViewModel.settingsLanguageList[i].isSelected =
                    mViewModel.selectedAppLanguage == mViewModel.settingsLanguageList[i].languageId
            }
            mAdapter.updateList(mViewModel.settingsLanguageList)
        } else {
            mBinding.rvLanguage.gone()
            mBinding.tvLanguageNotFound.visible()
        }
    }

    private fun updateView() {
        mViewModel.settingsData?.apply {
            mBinding.tvEmailId.text = userHolder.emailAddress
            mViewModel.isInAppNotificationEnabled =
                isInAppNotificationEnabled!!
            mBinding.switchInAppNotification.isChecked =
                isInAppNotificationEnabled!!
            mViewModel.isEmailNotificationEnabled =
                isEmailNotificationEnabled!!
            mBinding.switchEmailNotification.isChecked =
                isEmailNotificationEnabled!!
            mViewModel.isZenModeEnabled = isZenModeEnabled!!
            mBinding.switchZenMode.isChecked = isZenModeEnabled!!
            if (isZenModeEnabled!!) {
                mBinding.ivZenModeOpenClose.setImageResource(R.drawable.vd_down_arrow_gold)
                mBinding.txtVerticalText.visibility = View.GONE
                mBinding.llZenModeDetails.visibility = View.VISIBLE
            } else {
                mBinding.ivZenModeOpenClose.setImageResource(R.drawable.vd_right_side_arrow_gold)
                mBinding.llZenModeDetails.visibility = View.GONE
                mBinding.txtVerticalText.visibility = View.VISIBLE
            }
            isSilenceDuringMeditation?.let {
                mViewModel.isSilenceDuringMeditation = it
            }
            is12HourFormat?.let {
                if(mViewModel.is12HourFormat == null)
                mViewModel.is12HourFormat = it
            }
            userHolder.is12HourFormat = is12HourFormat ?: !getTimePickerTimeFormat(this@SettingsActivity)
            if (mViewModel.is12HourFormat == true) {
                mBinding.segmentedControlGroup.setSelectedIndex(0, false)
            } else {
                mBinding.segmentedControlGroup.setSelectedIndex(1, false)
            }
            isSilenceDuringMeditation?.let {
                mBinding.switchSilenceDuringMeditation.isChecked =
                    isSilenceDuringMeditation!!
            }
            isSilenceDuringSession?.let {
                mViewModel.isSilenceDuringSession =
                    isSilenceDuringSession!!
            }
            isSilenceDuringSession?.let {
                mBinding.switchSilenceLiveMeditation.isChecked =
                    isSilenceDuringSession!!
            }
            isSilenceDuringOtherApps?.let {
                mViewModel.isSilenceDuringOtherApps =
                    isSilenceDuringOtherApps!!
            }
            isSilenceDuringOtherApps?.let {
                mBinding.switchSilenceOtherMeditation.isChecked =
                    isSilenceDuringOtherApps!!
            }

            appLanguage?.let {
                mViewModel.selectedAppLanguage = it
            }
            isSilenceDuringMeditation?.let { userHolder.setUserZenModeForMeditation(it) }

            sessionReminderTime?.let {
                for (i in mViewModel.mSessionArrayList.indices) {
                    if (it == mViewModel.mSessionArrayList[i].timer) {
                        mBinding.tvSession.text = mViewModel.mSessionArrayList[i].title
                        mViewModel.selectedSession = it
                        mViewModel.mSessionArrayList[i].isSelected = true
                    } else {
                        mViewModel.mSessionArrayList[i].isSelected = false
                    }
                }
            }
            mImportantAdapterAdapter.updateList(userImportantInfo)
            val deviceList = mutableListOf<UserSettingsApiResponse.DeviceData>()
            deviceData?.forEach { data ->
                if (data?.isThisDevice!!) {
                    deviceList.add(0, data)
                }
            }
            deviceData?.remove(deviceList[0])
            deviceData?.add(0, deviceList[0])
            mDeviceListAdapter.updateList(deviceData!!)

            if (isNetwork())
                if(mViewModel.settingsLanguageList.size == 0)
                    mViewModel.getSettingsLanguages(this@SettingsActivity, mDisposable)
                else
                    updateLanguageView()

        }
    }

    override fun handleListener() {
        mBinding.apply {

            mToolbar.ivBackToolbar.clickWithDebounce {
                onBackPressed()
            }

            mToolbar.cardProfile.clickWithDebounce {
                UserMenuActivity.startActivity(this@SettingsActivity, SettingsActivity::class.java.simpleName)
            }
            switchInAppNotification.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.isInAppNotificationEnabled = isChecked
            }
            switchEmailNotification.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.isEmailNotificationEnabled = isChecked
            }
            switchSilenceDuringMeditation.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.isSilenceDuringMeditation = isChecked
//                switchZenMode.isChecked = isChecked
            }
            switchSilenceLiveMeditation.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.isSilenceDuringSession = isChecked
            }
            switchSilenceOtherMeditation.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.isSilenceDuringOtherApps = isChecked
            }
            switchZenMode.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    ivZenModeOpenClose.setImageResource(R.drawable.vd_down_arrow_gold)
                    txtVerticalText.visibility = View.GONE
                    llZenModeDetails.visibility = View.VISIBLE
                } else {
                    ivZenModeOpenClose.setImageResource(R.drawable.vd_right_side_arrow_gold)
                    llZenModeDetails.visibility = View.GONE
                    txtVerticalText.visibility = View.VISIBLE
                }
                mViewModel.isZenModeEnabled = isChecked
                if (!mViewModel.isZenModeEnabled) {
                    mViewModel.isSilenceDuringMeditation = false
                    mViewModel.isSilenceDuringSession = false
                    mViewModel.isSilenceDuringOtherApps = false
                    mBinding.switchSilenceDuringMeditation.isChecked = false
                    mBinding.switchSilenceLiveMeditation.isChecked = false
                    mBinding.switchSilenceOtherMeditation.isChecked = false
                }
            }

            segmentedControlGroup.apply {
                setOnSelectedOptionChangeCallback {
                    mViewModel.is12HourFormat = it == 0
                }
            }

            llZenMode.setOnClickListener {
                switchZenMode.isChecked = !switchZenMode.isChecked
//                if (!isZenModeDetailEnabled) {
//                    ivZenModeOpenClose.setImageResource(R.drawable.vd_down_arrow_gold)
//                    txtVerticalText.visibility = View.GONE
//                    llZenModeDetails.visibility = View.VISIBLE
//                    switchZenMode.isChecked = true
//                } else {
//                    ivZenModeOpenClose.setImageResource(R.drawable.vd_right_side_arrow_gold)
//                    llZenModeDetails.visibility = View.GONE
//                    txtVerticalText.visibility = View.VISIBLE
//                    switchZenMode.isChecked = false
//                }
//                isZenModeDetailEnabled = !isZenModeDetailEnabled
            }
            btnSendFeedback.setOnClickListener {
                if(!(UserHolder.EnumUserModulePermission.SEND_APP_FEEDBACK.permission?.canAccess ?: true)){
                    SubscriptionActivity.startActivityForResult(this@SettingsActivity)
                    return@setOnClickListener
                }

                openSendFeedbackDialog()
            }

            btnDeleteAccount.setOnClickListener {
                deleteConfirmationDialog()
            }
            llSession.setOnClickListener {
                openSessionSelectionDialog(mViewModel.mSessionArrayList)
            }
            btnSave.setOnClickListener {
                if (isNetwork())
                    mViewModel.userSettingsApi(
                        mViewModel.selectedAppLanguage,
                        mViewModel.isEmailNotificationEnabled,
                        mViewModel.isInAppNotificationEnabled,
                        mViewModel.isSilenceDuringMeditation,
                        mViewModel.isSilenceDuringOtherApps,
                        mViewModel.isSilenceDuringSession,
                        mViewModel.isZenModeEnabled,
                        mViewModel.is12HourFormat ?: true,
                        mViewModel.selectedSession,
                        this@SettingsActivity,
                        mDisposable
                    )
            }
            checkForUpdateCard.clickWithDebounce {
//                check for update dialog design need to confirm from hardeep
/*
                Toast.makeText(this@SettingsActivity, "OK", Toast.LENGTH_SHORT).show()
*/
                val uri: Uri =
                    Uri.parse("https://play.google.com/store/apps/details?id=com.app.taocalligraphy&hl=en&gl=US")

                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            cvRelaunchTutorial.setOnClickListener {
//                re-launch mascot design
            }
        }
    }

    private fun deleteConfirmationDialog() {
        val title = ""
        val description = "" + getString(R.string.delete_account_msg)

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("" + title)
            .setMessage("" + description)
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, _ ->
                dialog!!.dismiss()
                if (isNetwork())
                    mViewModel.deleteAccountApi(this, mDisposable)
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, _ -> dialog!!.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun openSendFeedbackDialog() {
        dialogFeedback = SendFeedbackDialog.newInstance( object :
            SendFeedbackDialog.ChooseSendFeedbackListener {
            override fun onSendFeedback(feedbackNature: String, message: String) {
                mDisposable = CompositeDisposable()
                if (isNetwork())
                    mViewModel.userFeedbackApi(
                        feedbackNature,
                        message,
                        this@SettingsActivity,
                        mDisposable
                    )
            }
        })
        dialogFeedback.show(
            getFragmentTransactionFromTag(SendFeedbackDialog.TAG),
            SendFeedbackDialog.TAG
        )
    }

    private fun openStaticContentDialog(text: String, description: String) {
        val dialog = StaticContentDialog.newInstance(text, description)
        dialog.show(
            getFragmentTransactionFromTag(StaticContentDialog.TAG),
            StaticContentDialog.TAG
        )
    }

    override fun onSingleItemClick(languageId: Int) {
        mViewModel.selectedAppLanguage = languageId
    }

    private fun openSessionSelectionDialog(mSessionArrayList: ArrayList<MeditationTimerModel>) {
        val dialog =
            SessionTimerSelectionDialog(this, mSessionArrayList,
                object : SessionTimerSelectionDialog.TimerSelectionListener {
                    override fun onTimerSelect(data: MeditationTimerModel) {
                        mBinding.tvSession.text = data.title
                        mViewModel.selectedSession = data.timer
                    }
                },false
            )
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, ERROR)
        }
    }

    override fun onItemClick(userInfo: UserSettingsApiResponse.UserImportantInfo) {
        openStaticContentDialog(userInfo.title!!, userInfo.description!!)
    }

    override fun onLogoutClick(data: UserSettingsApiResponse.DeviceData, position: Int) {
        logoutItemPosition = position
        isThisDevice = data.isThisDevice!!
        logoutConfirmationDialog(data.sessionId.toString())
    }

    private fun logoutConfirmationDialog(sessionId: String) {
        val title = ""
        val description = "" + getString(R.string.logout_msg)

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("" + title)
            .setMessage("" + description)
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, _ ->
                dialog!!.dismiss()
                if (isNetwork())
                    mHomeViewModel.userLogout(sessionId, this@SettingsActivity, mDisposable)
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, _ -> dialog!!.dismiss() }
        val alert = builder.create()
        alert.show()
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