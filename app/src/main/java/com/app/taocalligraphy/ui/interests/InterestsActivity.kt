package com.app.taocalligraphy.ui.interests

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityInterestsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.profile.UserInterestApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class InterestsActivity : BaseActivity<ActivityInterestsBinding>() {

    private val mViewModel: ProfileViewModel by viewModels()

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, InterestsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_interests

    override fun initView() {
        if (isTablet(this)) {
            ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(0, 0, 0, insets.bottom)
                WindowInsetsCompat.CONSUMED
            }
        }
        setupToolbar()
        updateProfile()
        if (isNetwork())
            if(mViewModel.userInterestList.isNullOrEmpty())
            mViewModel.getUserInterestsApi(this, mDisposable)
            else
            updateView()
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

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    override fun observeApiCallbacks() {
        mViewModel.getUserInterestLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { data ->
                    if (data.data != null) {
                        data.data?.let { it ->
                            mViewModel.userInterestList.clear()
                            it.list?.let { it1 -> mViewModel.userInterestList.addAll(it1) }
                            mViewModel.isViewInterests = it.isViewInterest!!
                            updateView()
                        }
                    }
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
                mViewModel.getUserInterestLiveData.postValue(null)
            }
        }

        mViewModel.userInterestLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    for (arrayList in mViewModel.userInterestList) {
                        arrayList?.isChanged = false
                    }
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                    mViewModel.getUserInterestsApi(this, mDisposable)
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
                mViewModel.userInterestLiveData.postValue(null)
            }
        }
    }

    private fun updateView() {
        mBinding.chipGroupBrings2.removeAllViews()
        mBinding.switchInteractiveSession.isChecked = mViewModel.isViewInterests
        for (i in mViewModel.userInterestList.indices) {
            val chip = layoutInflater.inflate(
                R.layout.item_interest,
                mBinding.chipGroupBrings2,
                false
            ) as Chip
            chip.text = mViewModel.userInterestList[i]?.name
            mBinding.chipGroupBrings2.addView(chip)
            chip.isChecked = mViewModel.userInterestList[i]?.isSelected!!
            chip.setOnCheckedChangeListener { chipView, isChecked ->
                chipView.isChecked = isChecked
                val index = mBinding.chipGroupBrings2.indexOfChild(chipView)
                mViewModel.userInterestList[index]?.isChanged = true
                mViewModel.userInterestList[index]?.isSelected = isChecked
                if (mBinding.switchInteractiveSession.isChecked && mViewModel.userInterestList.none { it?.isSelected == true }) {
                    longToast(
                        getString(
                            R.string.error_public_profile_remove_interest,
                            getString(R.string.interests),
                            getString(R.string.interests),
                            getString(R.string.interests)
                        ), ERROR
                    )
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@InterestsActivity)
        }
        mBinding.switchInteractiveSession.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && mViewModel.userInterestList.none { it?.isSelected == true }) {
                longToast(
                    getString(
                        R.string.error_public_profile,
                        getString(R.string.interests)
                    ), ERROR
                )
                mBinding.switchInteractiveSession.isChecked = false
            }
            if (!isChecked) {
                mViewModel.isViewInterests = false
            }
        }
        mBinding.btnUpdateMyInterest.setOnClickListener {
            if (mViewModel.isViewInterests && mViewModel.userInterestList.none { it?.isSelected == true }) {
                longToast(
                    getString(
                        R.string.error_public_profile_remove_interest,
                        getString(R.string.interests),
                        getString(R.string.interests),
                        getString(R.string.interests)
                    ), ERROR
                )
            } else {
                val keywordIds = JsonArray()
                val deletedKeywordIds = JsonArray()
                for (arrayList in mViewModel.userInterestList) {
                    if (arrayList?.isChanged!!) {
                        if (arrayList.isSelected!!)
                            keywordIds.add(arrayList.id)
                        else
                            deletedKeywordIds.add(arrayList.id)
                    }
                }
                logInfo(msg = "keywordIds ==> $keywordIds")
                logInfo(msg = "deletedKeywordIds ==> $deletedKeywordIds")
                if (isNetwork())
                    mViewModel.userInterestApi(
                        mBinding.switchInteractiveSession.isChecked, keywordIds,
                        deletedKeywordIds,
                        this, mDisposable
                    )
            }
        }
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

}