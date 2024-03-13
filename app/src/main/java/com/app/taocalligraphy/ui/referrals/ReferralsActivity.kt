package com.app.taocalligraphy.ui.referrals

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityReferralsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.request.ReferralsRequest
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.profile_view.PublicProfileViewActivity
import com.app.taocalligraphy.ui.referrals.adapter.ReferralsAdapter
import com.app.taocalligraphy.ui.referrals.viewmodel.ReferralsViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ReferralsActivity : BaseActivity<ActivityReferralsBinding>(),
    ReferralsAdapter.OnFollowersClickListener {

    private val mViewModel: ReferralsViewModel by viewModels()

    companion object {
        fun startActivity(activity: AppCompatActivity,referralLink : String) {
            val intent = Intent(activity, ReferralsActivity::class.java)
            intent.putExtra(Constants.referralLink,referralLink)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_referrals

    private val referralsAdapter by lazy {
        return@lazy ReferralsAdapter(this)
    }

    override fun initView() {
        if(mViewModel.currPage==-1) {
            mViewModel.currPage = -1
            callReferralsApi()
            mBinding.shimmerReferralLink.visible()
            mBinding.llReferralLink.gone()
            mBinding.rvParticipants.gone()
            mBinding.shimmerUsers.visible()
        }else{
            updateView()
        }
        setupToolbar()
        mBinding.rvParticipants.adapter = referralsAdapter
        mBinding.tvReferralLink.text = mViewModel.referralLink
    }


    private fun callReferralsApi() {
        mViewModel.getReferralData(ReferralsRequest().apply {
            current_page = ++mViewModel.currPage
            per_page = 10
            search = ""
        },this,mDisposable)
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()

        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun observeApiCallbacks() {
        mViewModel.userReferralsLiveData.observe(this){ response ->
            response?.let { requestState ->
//                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    mViewModel.referralLink = it.data?.referralLink ?: ""
                    mBinding.tvReferralLink.text = mViewModel.referralLink
                    mBinding.shimmerReferralLink.gone()
                    mBinding.llReferralLink.visible()

                    mBinding.tvYourReferrals.text = getString(R.string.your_referrals)+" (${it.data?.referralUsersList?.totalRecords ?: 0})"

                    mViewModel.isLoading = false
                    if(mViewModel.currPage == 0){
                        referralsAdapter.list.clear()
                        mBinding.rvParticipants.visible()
                        mBinding.tvNoReferralsFound.gone()
                        mBinding.shimmerUsers.gone()
                        mViewModel.referralUsersList = it.data?.referralUsersList!!
                    }

                    it.data?.referralUsersList?.list?.let { it1 ->
                        referralsAdapter.list.addAll(it1)

                        if(mViewModel.currPage > 0)
                            mViewModel.referralUsersList.list.addAll(it1)
                    }
                    mViewModel.shouldLoadMore = (it.data?.referralUsersList?.totalRecords ?: 0) > referralsAdapter.list.size

                    if(mViewModel.shouldLoadMore)
                        mBinding.progressBarLoadMorePrograms.visible()

                        referralsAdapter.notifyDataSetChanged()

                    if((it.data?.referralUsersList?.list?.size ?: 0) <= 0 && mViewModel.currPage == 0){
                        mBinding.rvParticipants.gone()
                        mBinding.tvNoReferralsFound.visible()
                        mBinding.tvYourReferrals.text = getString(R.string.your_referrals)
                    }
                }
//                showErrorState(requestState.error)
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
            mViewModel.userReferralsLiveData.postValue(null)
        }
    }

    private fun updateView() {
        mBinding.tvReferralLink.text = mViewModel.referralLink
        mBinding.shimmerReferralLink.gone()
        mBinding.llReferralLink.visible()

        mBinding.tvYourReferrals.text = getString(R.string.your_referrals)+" (${mViewModel.referralUsersList.totalRecords ?: 0})"

        mViewModel.isLoading = false

        referralsAdapter.list.clear()
        mBinding.rvParticipants.visible()
        mBinding.tvNoReferralsFound.gone()
        mBinding.shimmerUsers.gone()

        var start = referralsAdapter.list.size
        mViewModel.referralUsersList.list.let { it1 ->
            referralsAdapter.list.addAll(it1)
        }
        mViewModel.shouldLoadMore = (mViewModel.referralUsersList.totalRecords ?: 0) > referralsAdapter.list.size
        if(mViewModel.shouldLoadMore)
            mBinding.progressBarLoadMorePrograms.visible()
        if(start == 0)
            referralsAdapter.notifyDataSetChanged()
        else
            referralsAdapter.notifyItemRangeChanged(start,referralsAdapter.list.size - start)

        if((mViewModel.referralUsersList.list.size ?: 0) <= 0 && mViewModel.currPage == 0){
            mBinding.rvParticipants.gone()
            mBinding.tvNoReferralsFound.visible()
            mBinding.tvYourReferrals.text = getString(R.string.your_referrals)
        }
    }

    override fun handleListener() {

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@ReferralsActivity)
        }

        mBinding.btnShare.setOnClickListener {
            shareReferralLink()
        }

        mBinding.nestedScroll.viewTreeObserver.addOnScrollChangedListener {
            val view: View =
                mBinding.nestedScroll.getChildAt(mBinding.nestedScroll.childCount - 1) as View
            val diff: Int =
                view.bottom - (mBinding.nestedScroll.height + mBinding.nestedScroll.scrollY)

            if (diff == 0) {
                if ((!mViewModel.isLoading) and (mViewModel.shouldLoadMore)) {
                    mViewModel.isLoading = true
                    mBinding.progressBarLoadMorePrograms.visible()
                    callReferralsApi()
                } else {
                    mBinding.progressBarLoadMorePrograms.gone()
                }
            }
        }
    }

    override fun onFollowersClick(itemView: View,index:Int) {
        PublicProfileViewActivity.startActivity(
            this@ReferralsActivity,
            referralsAdapter.list[index].userId.toString()
        )
    }

    private fun shareReferralLink(){
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                mViewModel.referralLink
            )
            putExtra(Intent.EXTRA_TITLE, Constants.appName)
        }
        startActivity(Intent.createChooser(shareIntent, Constants.appName))
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