package com.app.taocalligraphy.utils.extensions

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.app.taocalligraphy.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


fun View.snack(@StringRes msg: Int) {
    Snackbar.make(this, context.getString(msg), Snackbar.LENGTH_SHORT).show()
}

fun View.snack(msg: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, msg, duration).show()
}

fun set10Padding(imageView: ImageView) {
    imageView.setPadding(10, 10, 10, 10)
}

fun set0Padding(imageView: ImageView) {
    imageView.setPadding(0, 0, 0, 0)
}

fun View.snackWithColor(msg: String, duration: Int = Snackbar.LENGTH_LONG) {
    val snackbarview: Snackbar = Snackbar.make(this, msg, duration)
    val snckView: View = snackbarview.view
    val tv = snckView.findViewById(R.id.snackbar_text) as TextView
    tv.setTextColor(Color.RED)
    tv.maxLines = 3
    snckView.setBackgroundColor(ContextCompat.getColor(this.context, android.R.color.white))
    snackbarview.show()
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun View.isInVisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}

fun TextView.setSelectedFilter() {
    this.background = ContextCompat.getDrawable(
        this.context,
        R.drawable.bg_black_rounded_corner
    )
    this.setTextColor(
        ContextCompat.getColor(
            this.context,
            R.color.white
        )
    )
}

fun TextView.setUnSelectedFilter() {
    this.background = ContextCompat.getDrawable(
        this.context,
        R.drawable.bg_gray_filter
    )
    this.setTextColor(
        ContextCompat.getColor(
            this.context,
            R.color.black
        )
    )
}

fun TextInputEditText.getTrimText(): String {
    return this.text?.trim().toString()
}

fun View.expand() {
    val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
        (this.parent as View).width,
        View.MeasureSpec.EXACTLY
    )
    val wrapContentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    this.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight = this.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    this.layoutParams.height = 0
    this.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            this@expand.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            this@expand.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            if (this@expand is TextView) {
                val textView: TextView = this@expand
                textView.isSingleLine = false
                textView.maxLines = 50
                textView.ellipsize = TextUtils.TruncateAt.END
            }
        }

        override fun onAnimationStart(animation: Animation?) {
        }

    })
    // Expansion speed of 1dp/ms
    a.duration = 2000
    a.duration = ((targetHeight / this.context.resources.displayMetrics.density).toLong())
    this.startAnimation(a)
    this.animate().alpha(1.0f)
}

fun View.collapse() {
    val initialHeight = this.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            if (interpolatedTime == 1f) {
                this@collapse.layoutParams.height = 0
                this@collapse.requestLayout()
            } else {
                this@collapse.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                this@collapse.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            if (this@collapse is TextView) {
                val textView: TextView = this@collapse
                textView.isSingleLine = true
                textView.maxLines = 1
                textView.ellipsize = TextUtils.TruncateAt.END
            }
        }

        override fun onAnimationStart(animation: Animation?) {
        }

    })
    // Collapse speed of 1dp/ms
    a.duration = ((initialHeight / this@collapse.context.resources.displayMetrics.density).toLong())
    this@collapse.startAnimation(a)

    this.animate().alpha(0.0f)
}

fun View.clickWithDebounce(debounceTime: Long = 1200L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.getScreenShotFromView(): Bitmap? {
    // create a bitmap object
    var screenshot: Bitmap? = null
    try {
        // inflate screenshot object
        // with Bitmap.createBitmap it
        // requires three parameters
        // width and height of the view and
        // the background color
        screenshot =
            Bitmap.createBitmap(this.measuredWidth, this.measuredHeight, Bitmap.Config.ARGB_8888)
        // Now draw this bitmap on a canvas
        val canvas = Canvas(screenshot)
        canvas.drawColor(Color.WHITE)
        // v.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        this.draw(canvas)
    } catch (e: Exception) {
    }
    // return the bitmap
    return screenshot
}


//create bitmap from the ScrollView
fun Context.getBitmapFromView(view: View, height: Int, width: Int): Uri? {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    view.isDrawingCacheEnabled = true
    view.measure(
        View.MeasureSpec.makeMeasureSpec(canvas.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(canvas.height, View.MeasureSpec.EXACTLY)
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    canvas.drawBitmap(view.drawingCache, 0f, 0f, Paint())

    val bytes = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(this.contentResolver, bmp, "Title", null)

    return Uri.parse(path)
}

fun Bitmap.getImageUri(context: Context): Uri {
    // Generating a file name
    val filename = "${System.currentTimeMillis()}.jpg"
    var imageUri: Uri? = null

    // Output stream
    var fos: OutputStream? = null

    // For devices running android >= Q
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // getting the contentResolver
        context.contentResolver?.also { resolver ->

            // Content resolver will process the contentvalues
            val contentValues = ContentValues().apply {

                // putting file information in content values
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            // Inserting the contentValues to
            // contentResolver and getting the Uri
            imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            // Opening an outputstream with the Uri that we got
            fos = imageUri?.let { resolver.openOutputStream(it) }

        }
    } else {
        // These for devices running on android < Q
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        imageUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName.toString() + ".provider",
            image
        )
        fos = FileOutputStream(image)
        this?.compress(Bitmap.CompressFormat.JPEG, 85, fos)
    }

    fos?.use {
        // Finally writing the bitmap to the output stream that we opened
        this?.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return imageUri!!
}

fun WebView.setPropertiesAndData(data: String) {

    this.setBackgroundColor(Color.TRANSPARENT);
    this.clearHistory()
    this.clearFormData()
    this.clearCache(true)
    this.settings.javaScriptEnabled = true
    this.settings.loadWithOverviewMode = true
    this.settings.javaScriptCanOpenWindowsAutomatically = true
    this.settings.domStorageEnabled = true
    this.settings.allowContentAccess = true
    this.settings.allowFileAccess = true
//        this.loadDataWithBaseURL(null,"" + meditationContent.description,"text/html", "UTF-8",null)

    val s = "<head><meta name='viewport' content='target-densityDpi=device-dpi'/></head>"
    this.loadDataWithBaseURL(
        "",
        s.plus(data),
        "text/html",
        "UTF-8",
        null
    )

    this.webViewClient = object : WebViewClient() {

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            //Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

        }

        override fun onPageFinished(view: WebView, url: String) {
        }

    }
}

fun ViewGroup.applyDim(dimAmount: Float) {
    val dim: Drawable = ColorDrawable(Color.BLACK)
    dim.setBounds(0, 0, this.width, this.height)
    dim.alpha = (255 * dimAmount).toInt()
    val overlay: ViewGroupOverlay = this.overlay
    overlay.add(dim)
}

fun ViewGroup.clearDim() {
    val overlay: ViewGroupOverlay = this.overlay
    overlay.clear()
}

/**
 * Dim the background when PopupWindow shows
 */
fun PopupWindow.dimBehind() {
    val container = contentView.rootView
    val context = contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = 0.3f
    wm.updateViewLayout(container, p)
}

fun View.locateView(): Rect? {
    val loc_int = IntArray(2)
    try {
        this.getLocationOnScreen(loc_int)
    } catch (npe: NullPointerException) {
        //Happens when the view doesn't exist on screen anymore.
        return null
    }
    val location = Rect()
    location.left = loc_int[0]
    location.top = loc_int[1]
    location.right = location.left + this.width
    location.bottom = location.top + this.height
    return location
}
