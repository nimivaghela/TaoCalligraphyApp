package com.app.taocalligraphy.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityChooseProfilePictureBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.profile.adapter.GalleryImagesAdapter
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImageProfile
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class ChooseProfilePictureActivity : BaseActivity<ActivityChooseProfilePictureBinding>(),
    GalleryImagesAdapter.GalleryImageClickListener {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ChooseProfilePictureActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_choose_profile_picture
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    private val readExternalStoragePermissionResult: ActivityResultLauncher<Array<String>> by lazy {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val permissionGranted = permissions.entries.all { entries ->
                entries.value
            }
            if (permissionGranted) {
                getGalleryImages()
            }
        }
    }

    private val cameraPermissionResult: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val permissionGranted = permissions.entries.all { entries ->
                entries.value
            }
            if (permissionGranted) {
                openCamera()
            }
        }

    private val mCameraActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mFile?.let {
                    val fileUri = Uri.fromFile(mFile)
                    onGalleryImageClick(fileUri)
                }
            }
        }

    private val galleryImagesAdapter by lazy {
        return@lazy GalleryImagesAdapter(this)
    }

    var mFile: File? = null

    override fun initView() {
        updateProfile()
        val spannedGridLayoutManager =
            SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 3)
        spannedGridLayoutManager.spanSizeLookup =
            SpannedGridLayoutManager.SpanSizeLookup { position ->
                if (position == 0) {
                    SpanSize(2, 2)
                } else {
                    SpanSize(1, 1)
                }
            }
        mBinding.rvPlaylist.layoutManager = spannedGridLayoutManager
        mBinding.rvPlaylist.adapter = galleryImagesAdapter
        setupToolbar()
        if (hasRequiredPermissionForReadExternalStorage()) {
            getGalleryImages()
        } else {
            readExternalStoragePermissionResult.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun getGalleryImages() {
        GlobalScope.launch(Dispatchers.IO) {
            //val contactList = ArrayList<ContactModel>()
            runOnUiThread {
                showProgressIndicator(mBinding.llProgress, true)
            }
            val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val cursor: Cursor?
            val columnIndexID: Int
            val listOfAllImages: MutableList<Uri?> = mutableListOf()
            val listOfAllImagesId: MutableList<Long?> = mutableListOf()
            listOfAllImages.add(null)
            listOfAllImagesId.add(null)
            val projection = arrayOf(MediaStore.Images.Media._ID)
            var imageId: Long
            cursor = contentResolver.query(
                uriExternal,
                projection,
                null,
                null,
                MediaStore.Images.Media._ID + " DESC"
            )
            if (cursor != null) {
                columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    imageId = cursor.getLong(columnIndexID)
                    val uriImage = Uri.withAppendedPath(uriExternal, "" + imageId)
                    listOfAllImages.add(uriImage)
                    listOfAllImagesId.add(imageId)
                }
                cursor.close()
            }
            runOnUiThread {
                showProgressIndicator(mBinding.llProgress, false)
                galleryImagesAdapter.galleryImageList = listOfAllImages as ArrayList<Uri?>
                galleryImagesAdapter.galleryImageId = listOfAllImagesId as ArrayList<Long?>
                galleryImagesAdapter.notifyDataSetChanged()
                println(listOfAllImages)
            }
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this)
        }

    }

    private fun hasRequiredPermissionForReadExternalStorage(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasRequiredPermissionForTakeImage(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                )
    }

    private fun requestPermissionForTakeImage() {
        cameraPermissionResult.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            mFile = createImageFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (mFile != null) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val fileUri = FileProvider.getUriForFile(
                this, packageName + ".provider",
                mFile!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            mCameraActivityResult.launch(intent)
        }
    }

    override fun onGalleryImageClick(uri: Uri) {
        SaveProfilePictureActivity.startActivity(this, uri)
        finish()
    }

    override fun onCameraClick() {
        if (hasRequiredPermissionForTakeImage()) {
            openCamera()
        } else {
            requestPermissionForTakeImage()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".png", storageDir)
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