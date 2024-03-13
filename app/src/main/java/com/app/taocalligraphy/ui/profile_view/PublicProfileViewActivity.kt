package com.app.taocalligraphy.ui.profile_view

import android.content.Intent
import android.graphics.Paint
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityPublicProfileViewBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class PublicProfileViewActivity : BaseActivity<ActivityPublicProfileViewBinding>() {

    private val mViewModel: ProfileViewModel by viewModels()

    override fun getResource() = R.layout.activity_public_profile_view
    private val publicUserId by lazy {
        return@lazy intent.extras?.getString(PARAM_USER_ID)
    }

    companion object {
        const val PARAM_USER_ID = "PARAM_USER_ID"
        fun startActivity(
            activity: AppCompatActivity,
            userId: String
        ) {
            val intent = Intent(activity, PublicProfileViewActivity::class.java)
            intent.putExtra(PARAM_USER_ID, userId)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun initView() {
        setupToolbar()
        if (isNetwork())
            if(mViewModel.publicProfileData == null) {
                mViewModel.getUserProfilePublicApi(
                    publicUserId.toString(),
                    this,
                    mDisposable
                )
            }else{
                updateProfileView()
            }
        mBinding.tvLiveSessionOne.paintFlags =
            mBinding.tvLiveSessionOne.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mBinding.tvLiveSessionTwo.paintFlags =
            mBinding.tvLiveSessionTwo.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        if (publicUserId != userHolder.id.toString()) {
            mBinding.rlAddFriend.visible()
        } else {
            mBinding.rlAddFriend.gone()
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
    }

    override fun observeApiCallbacks() {
        mViewModel.getUserProfilePublicLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    if (it.data != null) {
                        it.data?.apply {
                            mViewModel.publicProfileData = this
                            updateProfileView()
                        }
                    }
                    mViewModel.getUserProfilePublicLiveData.postValue(null)
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

    private fun updateProfileView() {
        mViewModel.publicProfileData?.apply {

            mBinding.ivBackgroundImage.loadImageProfile(
                profilePicture,
                R.drawable.ic_profile_default
            )

            mBinding.tvUsername.text = username
            mBinding.tvLiveSessionLabel.text =
                getString(R.string.live_session_profile, username)

            country?.let {
                mBinding.tvCountry.text = it
                if (it.isBlank())
                    mBinding.tvCountry.gone()
                else
                    mBinding.tvCountry.visible()
            } ?: kotlin.run {
                mBinding.tvCountry.gone()
            }

            isViewAboutMe?.let { isAboutMe ->
                if (isAboutMe) {
                    aboutMe?.let {
                        if (it.isNotEmpty()) {
                            mBinding.tvAboutMe.visible()
                            mBinding.tvAboutMe.text = aboutMe
                        } else {
                            mBinding.tvAboutMe.gone()
                        }
                    } ?: kotlin.run {
                        mBinding.tvAboutMe.gone()
                    }
                } else {
                    mBinding.tvAboutMe.gone()
                }
            } ?: kotlin.run {
                mBinding.tvAboutMe.gone()
            }

            userLanguages?.let {
                if (it.isEmpty()) {
                    mBinding.llLanguage.gone()
                } else {
                    val languages = StringBuilder()
                    it.forEach { language ->
                        languages.append("${language?.language}\n")
                    }
                    mBinding.tvLanguage.text = languages.trim()
                    mBinding.llLanguage.visible()
                }
            }

            isViewInterest?.let { isInterest ->
                if (isInterest) {
                    interest?.let {
                        mBinding.chipGroupBrings.removeAllViews()
                        for (i in it.indices) {
                            val chip = layoutInflater.inflate(
                                R.layout.item_interest,
                                mBinding.chipGroupBrings,
                                false
                            ) as Chip
                            chip.text = it[i]?.name
                            mBinding.chipGroupBrings.addView(chip)
                            chip.isChecked = true
                            setChipEnable()
                        }
                        mBinding.tvInterest.visible()
                        mBinding.chipGroupBrings.visible()
                        if (it.indices.isEmpty()) {
                            mBinding.tvInterest.gone()
                            mBinding.chipGroupBrings.gone()
                        }
                    } ?: kotlin.run {
                        mBinding.tvInterest.gone()
                        mBinding.chipGroupBrings.gone()
                    }
                } else {
                    mBinding.tvInterest.gone()
                    mBinding.chipGroupBrings.gone()
                }
            } ?: kotlin.run {
                mBinding.tvInterest.gone()
                mBinding.chipGroupBrings.gone()
            }

            isViewBirthday?.let {
                if (it) {
                    if (!dateOfBirth.isNullOrBlank()) {
                        mBinding.tvBirthday.text = getMonthDayFromDate(dateOfBirth)
                        mBinding.llBirthday.visible()
                    } else {
                        mBinding.llBirthday.gone()
                    }
                } else {
                    mBinding.llBirthday.gone()
                }
            } ?: kotlin.run {
                mBinding.llBirthday.gone()
            }

            isViewGender?.let {
                if (it) {
                    if (!gender.isNullOrBlank()) {
                        mBinding.llGender.visible()
                        mBinding.tvGender.text = gender
                    } else {
                        mBinding.llGender.gone()
                    }
                } else {
                    mBinding.llGender.gone()
                }
            } ?: kotlin.run {
                mBinding.llGender.gone()
            }
        }
    }

    private fun setChipEnable() {
        for (i in 0 until mBinding.chipGroupBrings.childCount) {
            mBinding.chipGroupBrings.getChildAt(i).isEnabled = false
        }
    }

    override fun handleListener() {

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.tvAboutTab.setOnClickListener {
            mBinding.tvAboutTab.setTextColor(ContextCompat.getColor(this, R.color.gold))
            mBinding.tvAboutTab.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
            mBinding.tvReviewsTab.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
            mBinding.tvReviewsTab.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
            mBinding.llReviews.visibility = View.GONE
            mBinding.llAbout.visibility = View.VISIBLE
        }

        mBinding.tvReviewsTab.setOnClickListener {
            mBinding.tvReviewsTab.setTextColor(ContextCompat.getColor(this, R.color.gold))
            mBinding.tvReviewsTab.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
            mBinding.tvAboutTab.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
            mBinding.tvAboutTab.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
            mBinding.llAbout.visibility = View.GONE
            mBinding.llReviews.visibility = View.VISIBLE
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