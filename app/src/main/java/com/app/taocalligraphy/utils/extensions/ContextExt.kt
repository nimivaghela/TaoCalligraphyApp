package com.app.taocalligraphy.utils.extensions

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.TOP
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.BuildConfig
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.INFO
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.other.Constants.WARNING
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.io.FileDescriptor


fun Activity.resToast(@StringRes res: Int) {
    Toast.makeText(this, res, Toast.LENGTH_LONG).show()
}

fun Activity.callPhoneIntent(phoneNumber: String, errorMessage: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))

    if (intent.resolveActivity(packageManager) != null)
        startActivity(intent)
    else {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}

fun Activity.longToast(msg: String, type: String) {
    val snackbar = Snackbar.make(
        ((findViewById(android.R.id.content)) as ViewGroup).getChildAt(0),
        "",
        Toast.LENGTH_LONG
    )
    val customSnackView: View =
        LayoutInflater.from(TaoCalligraphy.getAppContext())
            .inflate(R.layout.layout_custom_toast, null, false)

    snackbar.view.apply {
        setBackgroundColor(Color.TRANSPARENT)
    }

    val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    snackbarLayout.setPadding(0, 0, 0, 0)
    snackbar.duration = 3000
    val tvMessage = customSnackView.findViewById<TextView>(R.id.tvCustomMessage)
    tvMessage.text = msg
    val startImage: ImageView = customSnackView.findViewById(R.id.imgStart)
    val closeImage: ImageView = customSnackView.findViewById(R.id.imgClose)
    val llLayout: LinearLayout = customSnackView.findViewById(R.id.llLayout)
    tvMessage.gravity = Gravity.CENTER_HORIZONTAL
    when (type) {
        SUCCESS -> {
            llLayout.background = ContextCompat.getDrawable(
                snackbar.context,
                R.drawable.bg_validation_snackbar_rounded_corner_sucess
            )
            startImage.setImageDrawable(
                ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.vd_validation_success
                )
            )
        }
        INFO -> {
            llLayout.background = ContextCompat.getDrawable(
                snackbar.context,
                R.drawable.bg_validation_snackbar_rounded_corner_info
            )
            startImage.setImageDrawable(
                ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.vd_validation_info
                )
            )
        }
        WARNING -> {
            llLayout.background = ContextCompat.getDrawable(
                snackbar.context,
                R.drawable.bg_validation_snackbar_rounded_corner_warning
            )
            startImage.setImageDrawable(
                ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.vd_validation_warning
                )
            )
        }
        ERROR -> {
            llLayout.background = ContextCompat.getDrawable(
                snackbar.context,
                R.drawable.bg_validation_snackbar_rounded_corner_error
            )
            startImage.setImageDrawable(
                ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.vd_validation_error
                )
            )
        }
        else -> {

        }
    }
    closeImage.setOnClickListener {
        snackbar.dismiss()
    }
    val params =
        if (snackbarLayout.layoutParams is CoordinatorLayout.LayoutParams)
            snackbarLayout.layoutParams as CoordinatorLayout.LayoutParams
        else {
            (snackbarLayout.layoutParams as FrameLayout.LayoutParams).gravity = TOP
            (snackbarLayout.layoutParams as FrameLayout.LayoutParams).gravity = CENTER_HORIZONTAL
            snackbarLayout.layoutParams as FrameLayout.LayoutParams
        }
    params.setMargins(
        params.leftMargin + 10,
        params.topMargin + 30,
        params.rightMargin + 10,
        params.bottomMargin + 10
    )
    snackbarLayout.addView(customSnackView, 1)
    snackbar.show()
}

fun BottomSheetDialog.longToast(msg: String, type: String) {
    this.window?.decorView?.let {
        val snackbar = Snackbar.make(
            it,
            "",
            Toast.LENGTH_LONG
        )
        val customSnackView: View =
            LayoutInflater.from(TaoCalligraphy.getAppContext())
                .inflate(R.layout.layout_custom_toast, null, false)

        snackbar.view.apply {
            setBackgroundColor(Color.TRANSPARENT)
        }

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbar.duration = 3000
        val tvMessage = customSnackView.findViewById<TextView>(R.id.tvCustomMessage)
        tvMessage.text = msg
        val startImage: ImageView = customSnackView.findViewById(R.id.imgStart)
        val closeImage: ImageView = customSnackView.findViewById(R.id.imgClose)
        val llLayout: LinearLayout = customSnackView.findViewById(R.id.llLayout)

        when (type) {
            SUCCESS -> {
                llLayout.background = ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.bg_validation_snackbar_rounded_corner_sucess
                )
                startImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        snackbar.context,
                        R.drawable.vd_validation_success
                    )
                )
            }
            INFO -> {
                llLayout.background = ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.bg_validation_snackbar_rounded_corner_info
                )
                startImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        snackbar.context,
                        R.drawable.vd_validation_info
                    )
                )
            }
            WARNING -> {
                llLayout.background = ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.bg_validation_snackbar_rounded_corner_warning
                )
                startImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        snackbar.context,
                        R.drawable.vd_validation_warning
                    )
                )
            }
            ERROR -> {
                llLayout.background = ContextCompat.getDrawable(
                    snackbar.context,
                    R.drawable.bg_validation_snackbar_rounded_corner_error
                )
                startImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        snackbar.context,
                        R.drawable.vd_validation_error
                    )
                )
            }
            else -> {

            }
        }
        closeImage.setOnClickListener {
            snackbar.dismiss()
        }
//        val params = snackbarLayout.layoutParams as FrameLayout.LayoutParams
//        params.width = FrameLayout.LayoutParams.MATCH_PARENT
//        params.gravity = TOP
//        params.gravity = Gravity.CENTER_HORIZONTAL
//        params.setMargins(
//            params.leftMargin + 10,
//            params.topMargin + 30,
//            params.rightMargin + 10,
//            params.bottomMargin + 10
//        )
//        snackbarLayout.addView(customSnackView, 1)
//        snackbar.show()
        val params = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        params.gravity = TOP
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.setMargins(
            params.leftMargin + 10,
            params.topMargin + 30,
            params.rightMargin + 10,
            params.bottomMargin + 10
        )
        snackbarLayout.addView(customSnackView, 1)
        snackbar.show()
    }
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun addDelay(runBlock: () -> Unit, timeInMillis: Long) {
    Handler(Looper.getMainLooper()).postDelayed(Runnable(runBlock), timeInMillis)
}

fun Activity.showKeyboard() {
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
//    val imm: InputMethodManager =
//        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//    var view = currentFocus
//    if (view == null) {
//        view = View(this)
//    }
//    imm.showSoftInput(view, 0)
}

fun View.showKeyBoard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun Activity.startActivityWithAnimation(intent: Intent) {
    hideKeyboard()
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun Activity.startActivityWithAnimation(intent: Intent, resultCode: Int) {
    hideKeyboard()
    startActivityForResult(intent, resultCode)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun Activity.startActivityWithReserveAnimation(intent: Intent) {
    hideKeyboard()
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}

fun Activity.startActivityForResultWithAnimation(intent: Intent, reqCode: Int) {
    startActivityForResult(intent, reqCode)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun Dialog.hideKeyboardFromDialog() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(context)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showMapForNavigation(latitude: String, longitude: String) {
    val latLongUri = Uri.parse(String.format("google.navigation:q=%1s,%2s", latitude, longitude))
    val mapIntent = Intent(Intent.ACTION_VIEW, latLongUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(packageManager) != null) {
        startActivity(mapIntent)
    }
}

fun Activity.showAlert(
    title: String,
    msg: String?,
    positiveButtonText: String,
    negativeButtonText: String? = null,
    alertDialogTheme: Int,
    listener: DialogInterface.OnClickListener
) {
    val builder = AlertDialog.Builder(this, alertDialogTheme)
        .setTitle(title)
        .setMessage(msg)
    if (negativeButtonText == null) {
        builder.setNeutralButton(positiveButtonText, listener)
    } else {
        builder.setPositiveButton(positiveButtonText, listener)
            .setNegativeButton(negativeButtonText, listener)
    }

    val dialog = builder.create()

    dialog.setOnShowListener {
        val btnYes = dialog.getButton(DialogInterface.BUTTON_POSITIVE)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 0, 0, 0)
        btnYes.layoutParams = params

        val btnNo = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        btnNo.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        btnNo.setTextColor(ContextCompat.getColor(this, R.color.gold))
    }
    if (!this.isFinishing)
        dialog.show()
}

fun Context.getWidthOfScreen(): Int {
    val displayMetrics = DisplayMetrics()
    val display = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    display.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.bitmapFromUri(uri: Uri): Bitmap? {
    val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver
        .openFileDescriptor(uri, "r")
    parcelFileDescriptor?.let {
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
        val bitmap: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return bitmap
    } ?: let { return null }
}

fun Context.getPrefInstance(prefName: String): SharedPreferences =
    this.getSharedPreferences(prefName, Context.MODE_PRIVATE)

@SuppressLint("HardwareIds")
fun Context.deviceId(): String =
    Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        capitalize(model)
    } else {
        capitalize(manufacturer) + " " + model
    }
}

fun getDeviceVersion(): String {
    return BuildConfig.VERSION_NAME
}

fun capitalize(s: String?): String {
    if (s == null || s.length == 0) {
        return ""
    }
    val first = s[0]
    return if (Character.isUpperCase(first)) {
        s
    } else {
        first.uppercaseChar().toString() + s.substring(1)
    }
}

