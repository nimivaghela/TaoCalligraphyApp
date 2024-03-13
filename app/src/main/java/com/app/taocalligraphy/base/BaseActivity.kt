package com.app.taocalligraphy.base

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.notification.AlarmBroadcastReceiver
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.chat.dialog.ChatNoInternetConnectionDialog
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.ContentDownloadHelper
import com.app.taocalligraphy.utils.GPSTracker
import com.app.taocalligraphy.utils.PermissionHelper
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.annotations.NotNull
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), BaseView {

    open var TAG = "BaseActivity"
    lateinit var mBinding: VB

    open lateinit var mDisposable: CompositeDisposable
    var permissionHelper: PermissionHelper? = null

    private val gpsTracker: GPSTracker? by lazy {
        GPSTracker(this)
    }

    @LayoutRes
    abstract fun getResource(): Int
    abstract fun initView()
    abstract fun observeApiCallbacks()
    abstract fun handleListener()

    @Subscribe(threadMode = ThreadMode.MAIN)
    abstract fun noInternetListener(model: IsNetworkAvailableListener)


    val contentDownloadHelper by lazy {
        return@lazy ContentDownloadHelper()
    }

    val baseViewModel by lazy {
        BaseViewModel()
    }

    var noInternetConnectionDialog: ChatNoInternetConnectionDialog? = null
    var notificationManager: NotificationManager? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        TAG = this.localClassName
        initDisposable()
        setView(getResource())
        if (notificationManager == null) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

   private fun setView(@LayoutRes layoutId: Int) {
        try {
            mBinding = DataBindingUtil.setContentView(this, layoutId)
            mBinding.lifecycleOwner = this
            initView()
            observeApiCallbacks()
            handleListener()
        } catch (e: Exception) {
            logInfo(TAG, "Set View Error:")
            e.printStackTrace()
            longToast(e.localizedMessage ?: "", ERROR)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isNetwork()) {
            showNoInternetDialog()
        } else {
            noInternetConnectionDialog?.dismiss()
        }
    }

    protected fun <T : ViewDataBinding> getBinding(): T {
        @Suppress("UNCHECKED_CAST")
        return mBinding as T
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideKeyboard()
        if (getResource() == R.layout.activity_email_sent || getResource() == R.layout.activity_reset_password) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private lateinit var mToolbar: Toolbar
    protected fun setToolbar(
        @NotNull toolbar: Toolbar, @NotNull title: String, isBackEnabled: Boolean = false,
        backgroundColor: Int = R.color.transparent
    ) {
        this.mToolbar = toolbar
        this.mToolbar.title = title
        super.setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (title.isNotEmpty()) {
            val textView: TextView? = findViewById(R.id.tvTitleToolbar)
            textView?.visibility = View.VISIBLE
            textView?.text = title
        }

        if (isBackEnabled) {
            val imageView: ImageView? = findViewById(R.id.ivBackToolbar)
            imageView?.setOnClickListener { this.onBackPressed() }
            imageView?.visibility = View.VISIBLE
        } else {
            val imageView: ImageView? = findViewById(R.id.ivBackToolbar)
            imageView?.visibility = View.GONE
        }


    }

//    @SuppressLint("WrongConstant")
//    fun longToast(stringResId: String?) {
//        if (stringResId == null) return
//        val snackBar = Snackbar.make(
//            findViewById(android.R.id.content),
//            stringResId,
//            BaseTransientBottomBar.LENGTH_SHORT
//        )
//        snackBar.duration = 4000
//        val view = snackBar.view
//        view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
//        val tv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
//        tv.maxLines = 3
//        val typeface = ResourcesCompat.getFont(this, R.font.font_jost_medium)
//        tv.typeface = typeface
//        tv.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
//        snackBar.show()
//    }

    /**
     * validates password using regex
     */
    fun isValidPassword(password: String): Boolean {
        /*
        ^                      # start-of-string
        (?=.*[0-9])            # a digit must occur at least once
        (?=.*[a-z])            # a lower case letter must occur at least once
        (?=.*[A-Z])            # an upper case letter must occur at least once
        (?=.*[!@#$%^&*+=._-])  # a special character must occur at least once you can replace with your special characters
        (?=\\S+$)              # no whitespace allowed in the entire string
        .{4,}                  # anything, at least six places though
        $                      # end-of-string
        */
        //val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
//        val regex = "^(?=.*[0-9])(?=.*[A-Z-a-z])(?=.*[!@#$%^&*+=._-])(?=\\S+$).{10,}$"
        val regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*+=._-])(?=\\S+$).{10,}$"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    /**
     * validates email using regex
     */
    fun isMobileInvalid(mobile: String): Boolean {
        val regex =
//            "^(?=.*[0-9]).{11,}"
            "^(?=.*[+])(?=.*[0-9]).{11,}"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(mobile)
        return !matcher.matches()
    }


    /**
     * validates email using regex
     */
    fun isEmailInvalid(email: String): Boolean {
        val regex =
            "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(email)
        return !matcher.matches()
    }

    /**
     * Checks if string contains any emoji
     */
    fun isEmoji(text: String): Boolean {
        return !isAsciiPrintable(text)
    }

    private fun isAsciiPrintable(cs: CharSequence?): Boolean {
        if (cs == null) {
            return false
        }
        val sz = cs.length
        for (i in 0 until sz) {
            val ch = cs[i]
            if (!(ch.code in 32..126)) {
                return false
            }
        }
        return true
    }

    fun getFragmentTransactionFromTag(tag: String): FragmentTransaction {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val previousFragment =
            supportFragmentManager.findFragmentByTag(tag)
        previousFragment?.let {
            fragmentTransaction.remove(it)
        }
        fragmentTransaction.addToBackStack(null)
        return fragmentTransaction
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun showProgressIndicator(progressBar: View, isShow: Boolean) {
        progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun initDisposable() {
        this.mDisposable = CompositeDisposable()
        Log.d("Composable", "${this.mDisposable}")
    }

    fun getDisposable() = this.mDisposable

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mDisposable.clear()
        mDisposable.dispose()
    }

    fun cancelAllPreviousAlarm() {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val alarmManager =
            getSystemService(Service.ALARM_SERVICE) as AlarmManager
        for (i in 1..9) {
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                i,
                intent,
                PendingIntent.FLAG_IMMUTABLE or
                        PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_CANCEL_CURRENT
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            NotificationManagerCompat.from(this).cancel(2)
        }
    }

    fun setAlarmInManager(alarmResponse: AlarmResponse) {
        cancelAllPreviousAlarm()

        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        intent.putExtra(Constants.alarmContentId, alarmResponse.contentId)
        intent.putExtra(Constants.alarmContentTitle, alarmResponse.contentTitle)
        intent.putExtra(Constants.alarmContentImageUrl, alarmResponse.thumbnailImage)
        intent.putExtra(Constants.alarmContentFileUrl, alarmResponse.contentFile)
        intent.putExtra(Constants.alarmRepeat, true)

        val timePickerHours = getHoursFromTime(alarmResponse.time).toInt()
        val timePickerMinute = getMinutesFromTime(alarmResponse.time).toInt()

        if (alarmResponse.repeatDays.isNullOrEmpty()) {
            if (!getAlarmTimePassed(timePickerHours, timePickerMinute)) {
                intent.putExtra(Constants.alarmRequestCode, 8)
                val calendar =
                    getAlarmDateTimeFromHourAndMinute(timePickerHours, timePickerMinute, true)

                val alarmManager =
                    getSystemService(Service.ALARM_SERVICE) as AlarmManager
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    8,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )
                //7 * 24 * 60 * 60 * 1000,
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.time.time,
                    pendingIntent
                )
            }
        } else {
            alarmResponse.repeatDays.forEach {
                when (it) {
                    0 -> {
                        intent.putExtra(Constants.alarmRequestCode, 1)
                        getRepeatCalender(
                            timePickerHours,
                            timePickerMinute,
                            Calendar.SUNDAY,
                            1,
                            intent
                        )
                    }
                    1 -> {
                        intent.putExtra(Constants.alarmRequestCode, 2)
                        getRepeatCalender(
                            timePickerHours,
                            timePickerMinute,
                            Calendar.MONDAY,
                            2,
                            intent
                        )
                    }
                    2 -> {
                        intent.putExtra(Constants.alarmRequestCode, 3)
                        getRepeatCalender(
                            timePickerHours,
                            timePickerMinute,
                            Calendar.TUESDAY,
                            3,
                            intent
                        )
                    }
                    3 -> {
                        intent.putExtra(Constants.alarmRequestCode, 4)
                        getRepeatCalender(
                            timePickerHours,
                            timePickerMinute,
                            Calendar.WEDNESDAY,
                            4,
                            intent
                        )
                    }
                    4 -> {
                        intent.putExtra(Constants.alarmRequestCode, 5)
                        getRepeatCalender(
                            timePickerHours,
                            timePickerMinute,
                            Calendar.THURSDAY,
                            5,
                            intent
                        )
                    }
                    5 -> {
                        intent.putExtra(Constants.alarmRequestCode, 6)
                        getRepeatCalender(
                            timePickerHours,
                            timePickerMinute,
                            Calendar.FRIDAY,
                            6,
                            intent
                        )
                    }
                    6 -> {
                        intent.putExtra(Constants.alarmRequestCode, 7)
                        getRepeatCalender(
                            timePickerHours,
                            timePickerMinute,
                            Calendar.SATURDAY,
                            7,
                            intent
                        )
                    }
                }
            }
        }

        val contentFile: String? = if (!alarmResponse.contentFileForDownload.isNullOrEmpty()) {
            alarmResponse.contentFileForDownload
        } else {
            alarmResponse.contentFile
        }

        val renderersFactory = TaoCalligraphy.instance
            .buildRenderersFactory()
        TaoCalligraphy.instance.getDownloadTracker()
            ?.downloadFile(
                alarmResponse.contentTitle,
                Uri.parse(contentFile),
                "",
                renderersFactory,
                baseViewModel
            )
    }

    private fun getRepeatCalender(
        timePickerHours: Int,
        timePickerMinute: Int,
        dayOfWeek: Int,
        requestCode: Int,
        intent: Intent
    ) {
        val alarmManager =
            getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timePickerHours)
        calendar.set(Calendar.MINUTE, timePickerMinute)
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        if (calendar.time.time < Calendar.getInstance().time.time) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        //7 * 24 * 60 * 60 * 1000,
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.time.time,
            pendingIntent
        )
    }

    override fun onUnknownError(error: String?) {
        error?.let {
            //displayErrorMessage(error)
        }
        generalErrorAction()
    }

    override fun internalServer() {
        logDebug(msg = "Base Activity API Internal server")
        //displayMessage(getString(R.string.text_error_internal_server))
        generalErrorAction()
    }

    override fun onTimeout() {
        logDebug(msg = "Base Activity API Timeout")
        //displayMessage(getString(R.string.text_error_timeout))
        generalErrorAction()
    }

    override fun onNetworkError() {
        logDebug(msg = "Base Activity network error")
        //displayMessage(getString(R.string.text_error_network))
        generalErrorAction()
    }

    override fun onConnectionError() {
        logDebug(msg = "Base Activity internet issue")
        //displayMessage(getString(R.string.text_error_connection))
        generalErrorAction()
    }

    override fun onServerDown() {
        logDebug(msg = "Base Activity Server Connection issue")
        //displayMessage(getString(R.string.text_server_connection))
        generalErrorAction()
    }

    override fun generalErrorAction() {
        logDebug(msg = "This method will use in child class for performing common task for all error")
    }

    override fun autoLogout() {
        cancelAllPreviousAlarm()
        userHolder.clearUserHolder()
        LoginManager.getInstance().logOut()
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        baseViewModel.deleteAllDownloads()
        TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
        cancelAllPreviousAlarm()
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

    override fun notFound(error: String?) {
        logDebug(msg = "This method will use notFound error")
        //displayMessage(getString(R.string.text_server_connection))
        generalErrorAction()
    }

    override fun onSubscribeRequired(error: String?) {
        MainActivity.startActivity(this, errorMsg = error)
        finishAffinity()
    }

    override fun unauthorized(error: String?) {
        this.let {
            cancelAllPreviousAlarm()
            userHolder.clearUserHolder()
            LoginManager.getInstance().logOut()
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()
            baseViewModel.deleteAllDownloads()
            TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
            cancelAllPreviousAlarm()
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
        logDebug(msg = "This method will use unauthorized error")
    }

    override fun forbidden(error: String?) {
        error?.let {
            //displayErrorMessage(error)
        }
        logDebug(msg = "This method will use forbidden error")
    }

    override fun onOTPExpire(error: String?) {
    }

    override fun onVerificationError() {
    }

    fun longToastState(error: ApiError?) {
        error?.let { errorObj ->
            when (errorObj.errorState) {
                Constants.NETWORK_ERROR -> {
                    //errorObj.customMessage?.let { longToast(it, type = errorObj.type) }
                }
                Constants.CUSTOM_ERROR ->
                    longToast("" + errorObj.customMessage, ERROR)
                else -> {
                }
            }
        }
    }

    fun setMeditationFavouriteStatus(
        lottieLike: LottieAnimationView,
        isFavourites: Boolean?,
        shouldPlayAnimation: Boolean = false
    ) {
        if (isFavourites == true) {
            lottieLike.setAnimation("favorite_toggle_on.json")
            lottieLike.progress = 1f
        } else {
            lottieLike.setAnimation("favorite_toggle_off.json")
            lottieLike.progress = 1f
        }
        if (shouldPlayAnimation) {
            lottieLike.playAnimation()
        }
    }

    fun setWhiteLottieFavouriteStatus(
        lottieLike: LottieAnimationView,
        isFavourites: Boolean?,
        shouldPlayAnimation: Boolean = false
    ) {
        if (isFavourites == true) {
            lottieLike.setAnimation("favorite_toggle_on_white.json")
            lottieLike.progress = 1f
        } else {
            lottieLike.setAnimation("favorite_toggle_off_white.json")
            lottieLike.progress = 1f
        }
        if (shouldPlayAnimation) {
            lottieLike.playAnimation()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showAuthenticationDialog(tokenExpire: Int) {
    }

    fun checkToken() {
        if (TextUtils.isEmpty(userHolder.mAuthToken)) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        checkToken()
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    userHolder.mAuthToken = task.result
                })
        }
    }

    private fun getCompleteAddressString(
        LATITUDE: Double,
        LONGITUDE: Double
    ): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val cityName = returnedAddress.locality
                val stateName = returnedAddress.adminArea
                val countryName = returnedAddress.countryName
                strAdd = "$cityName, $stateName, $countryName"
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return strAdd
    }

    fun getGpsLocationData(): String {
        if (checkGPSStatus()) {
            gpsTracker?.let {
                return getCompleteAddressString(it.latitude, it.longitude)
            }
        } else {
            displayLocationSettingsRequest(this)
        }
        return ""
    }

    private fun checkGPSStatus(): Boolean {
        val manager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    var REQUEST_CHECK_SETTINGS = 101
    private fun displayLocationSettingsRequest(context: Context?) {
        val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context!!)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2
        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {}
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(
                            this,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
            }
        }
    }

    fun autoContentDownload() {

    }

    fun showNoInternetDialog() {
        if (noInternetConnectionDialog == null) {
            noInternetConnectionDialog =
                ChatNoInternetConnectionDialog(this,
                    object : ChatNoInternetConnectionDialog.InternetReconnectListener {
                        override fun onReconnectClick() {
                            if (!isNetwork())
                                showNoInternetDialog()
                        }
                    })
        }
        noInternetConnectionDialog?.setCancelable(false)
        noInternetConnectionDialog?.show()
        noInternetConnectionDialog?.window?.setBackgroundDrawableResource(R.color.black_25)
    }
}