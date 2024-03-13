package com.app.taocalligraphy.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivitySaveProfilePictureBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImage
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class SaveProfilePictureActivity : BaseActivity<ActivitySaveProfilePictureBinding>() {

    private val mViewModel: ProfileViewModel by viewModels()

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity, uri: Uri) {
            val intent = Intent(activity, SaveProfilePictureActivity::class.java)
            intent.putExtra(Constants.imageUri, uri.toString())
            activity.startActivityWithAnimation(intent)
        }
    }

    private val imageUri: Uri by lazy {
        return@lazy Uri.parse(intent.extras?.getString(Constants.imageUri, "") ?: "")
    }

    override fun getResource(): Int {
        return R.layout.activity_save_profile_picture
    }

    override fun initView() {
        mBinding.ivProfilePicture.loadImage(
            imageUri,
            R.drawable.ic_profile_default,
            true
        )

        mBinding.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun observeApiCallbacks() {
        mViewModel.userProfilePictureEditLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.apply {
                        userHolder.originalProfilePicUrl = originalProfilePicUrl
                        userHolder.thumbProfilePicUrl = thumbProfilePicUrl
                        Constants.IS_PROFILE_PICTURE_UPDATE = true
                        Constants.profileUpdatedMessage = it.message.toString()
                    }
                    finish()
                    onBackPressed()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_401 -> {
                                }
                                Constants.StatusCode.STATUS_404 -> {
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.ivBack.clickWithDebounce {
            onBackPressed()
        }
        mBinding.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this)
        }
        mBinding.btnSave.setOnClickListener {
            mBinding.ivProfilePicture.isDrawingCacheEnabled = true
//            mBinding.ivProfilePicture.buildDrawingCache(true) //this might hamper performance use hardware acc if available. see: http://developer.android.com/reference/android/view/View.html#buildDrawingCache(boolean)
            //create the bitmaps
            val zoomedBitmap = Bitmap.createScaledBitmap(
                mBinding.ivProfilePicture.getDrawingCache(true),
                mBinding.ivProfilePicture.width,
                mBinding.ivProfilePicture.height,
                true
            )
            mBinding.ivProfilePicture.isDrawingCacheEnabled = false
            var profile: MultipartBody.Part? = null
            val filePath = getImageUri(this@SaveProfilePictureActivity, zoomedBitmap)
            logError("file", "filePath ==>$filePath")
            if (!TextUtils.isEmpty(filePath.toString())) {
                val file = File(filePath)
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                profile = MultipartBody.Part.createFormData("multipartFile", file.name, requestFile)
                if (isNetwork())
                    mViewModel.userProfilePictureEdit(profile, this, mDisposable)
                else
                    longToast(
                        getString(R.string.no_internet, getString(R.string.to_edit_proifle_picture)),
                        Constants.ERROR
                    )
            }

        }

        mBinding.btnReset.setOnClickListener {
            mBinding.ivProfilePicture.resetZoomAnimated()
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): String? {
        //create a file to write bitmap data
        val f = File(inContext.cacheDir, "temporary_file_${System.currentTimeMillis()}.jpg")
        f.createNewFile()
//Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()
//write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        return f.absolutePath
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