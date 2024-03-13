package com.app.taocalligraphy.ui.how_to_meditate

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityReadMeditateBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ReadMeditateActivity : BaseActivity<ActivityReadMeditateBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            contentId: String?,
        ) {
            val intent = Intent(activity, ReadMeditateActivity::class.java)
            intent.putExtra(Constants.Param.contentId, contentId)
            activity.startActivityWithAnimation(intent)
        }
    }

    private val mViewModel: MeditationContentViewModel by viewModels()

    private val contentId by lazy {
        return@lazy intent.extras?.getString(Constants.Param.contentId)
    }

    override fun getResource(): Int {
        return R.layout.activity_read_meditate
    }

    override fun initView() {
        setupToolbar()
        if (mViewModel.meditationContent == null)
            callMeditationDetailAPI()
        else
            setData()
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.ivShareToolbar.visible()
    }

    private fun callMeditationDetailAPI() {
        mViewModel.getMeditationContent(contentId!!, this, mDisposable)
    }

    override fun observeApiCallbacks() {
        mViewModel.meditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    mViewModel.meditationContent = it
                    setData()
                }
                longToastState(requestState.error)
            }
            mViewModel.meditationContentLiveData.postValue(null)
        }
    }

    private fun setData() {
        mViewModel.meditationContent?.let {
            it.description?.let {
                val readContent =
                    "<html> <head> <style> p { line-height: 1.5; } </style> </head> <body> <p> $it </p> </body> </html>"
                mBinding.webView.setPropertiesAndData(
                    readContent
                )
            }
            mBinding.tvTitle.text = it.title
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.mToolbar.ivShareToolbar.clickWithDebounce {
            mViewModel.meditationContent?.let {
                shareMeditationContent(
                    contentTitle = it.title ?: "",
                    contentDescription = it.description ?: "",
                    contentId = it.id,
                    contentImage = it.backgroundImageMobile ?: "",
                    url = URL_READ_MEDITATION
                )
            }
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

    override fun onBackPressed() {
        if (isTaskRoot) {
            MainActivity.startActivity(this)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}