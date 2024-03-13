package com.app.taocalligraphy.ui.how_to_meditate

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityHowToMeditateBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.how_to_meditate.adapter.ReadMeditateAdapter
import com.app.taocalligraphy.ui.how_to_meditate.adapter.WatchMeditateAdapter
import com.app.taocalligraphy.ui.how_to_meditate.viewmodel.HowToMeditateViewModel
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class HowToMeditateActivity : BaseActivity<ActivityHowToMeditateBinding>(),
    ReadMeditateAdapter.ReadMeditateClickListener,
    WatchMeditateAdapter.WatchMeditateClickListener {

    private val mViewModel: HowToMeditateViewModel by viewModels()

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, HowToMeditateActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int = R.layout.activity_how_to_meditate

    private val watchMeditateAdapter by lazy {
        return@lazy WatchMeditateAdapter(this)
    }
    private val readMeditateAdapter by lazy {
        return@lazy ReadMeditateAdapter(this, this)
    }

//    val startSubscriptionActivityForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                if (result.data?.getBooleanExtra("isSubscribed", false) == true)
//                    onRefresh()
//            }
//        }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    override fun initView() {
        setupToolbar()
        updateProfile()
        setWatchMeditateAdapter()
        setReadMeditateAdapter()

        if (isNetwork()) {
            if (mViewModel.isWatchSelected) {
                mBinding.rvWatchData.visible()
                mBinding.rvReadData.gone()

                changeTabBackground(mBinding.tvWatch, mBinding.tvRead)
                if (mViewModel.watchDataList.isEmpty())
                    fetchWatchHowToMeditateDataListAPI()
                else
                    setWatchMediationData()
            } else {
                mBinding.rvWatchData.gone()
                mBinding.rvReadData.visible()

                changeTabBackground(mBinding.tvRead, mBinding.tvWatch)
                if (mViewModel.readDataList.isEmpty())
                    fetchReadHowToMeditateDataListAPI()
                else
                    setReadMeditationData()
            }
        } else {
            longToast(
                getString(R.string.no_internet, getString(R.string.to_get_how_to_meditate_data)),
                Constants.ERROR
            )
        }

        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.swipeToRefreshLayout.isRefreshing = false
            onRefresh()
        }
    }

    fun onRefresh() {
        if (mViewModel.isWatchSelected) {
            mViewModel.currentPageWatchMeditation = -1
            if (isNetwork())
                fetchWatchHowToMeditateDataListAPI()
            else
                longToast(
                    getString(
                        R.string.no_internet,
                        getString(R.string.to_get_how_to_meditate_data)
                    ),
                    Constants.ERROR
                )
        } else {
            mViewModel.currentPageReadMeditation = -1
            if (isNetwork())
                fetchReadHowToMeditateDataListAPI()
            else
                longToast(
                    getString(
                        R.string.no_internet,
                        getString(R.string.to_get_how_to_meditate_data)
                    ),
                    Constants.ERROR
                )
        }
    }

    private fun fetchReadHowToMeditateDataListAPI() {
        if (isTablet(this))
            mViewModel.perPageLimit = 6
        else
            mViewModel.perPageLimit = 3
        mViewModel.fetchReadHowToMeditateDataListAPI(
            this,
            mDisposable
        )
    }

    private fun fetchWatchHowToMeditateDataListAPI() {
        mViewModel.fetchWatchHowToMeditateDataListAPI(
            this,
            mDisposable
        )
    }

    private fun setWatchMeditateAdapter() {
        mBinding.rvWatchData.setPadding(0, 0, 0, 0)
        mBinding.rvWatchData.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvWatchData.adapter = watchMeditateAdapter
        mBinding.rvWatchData.isNestedScrollingEnabled = false

    }

    private fun setReadMeditateAdapter() {
        mBinding.rvReadData.setPadding(
            resources.getDimension(R.dimen._10sdp).toInt(),
            0,
            resources.getDimension(R.dimen._10sdp).toInt(),
            0
        )

        mBinding.rvReadData.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        mBinding.rvReadData.adapter = readMeditateAdapter
        mBinding.rvReadData.isNestedScrollingEnabled = false

    }

    private fun setupToolbar() {
        mBinding.mToolbar.cardProfile.visible()
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.toolbarRightViews.gone()
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeApiCallbacks() {
        mViewModel.fetchWatchHowToMeditateResponse.observe(
            this
        ) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageWatchMeditation == 0) {
                    mViewModel.watchDataList.clear()
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                } else {
                    showProgressIndicator(mBinding.llProgress, false)
                    showProgressIndicator(mBinding.progressBarRead, requestState.progress)
                }
                requestState.apiResponse?.data?.let { dataReponse ->
                    dataReponse.watch?.list?.let { list ->
                        mViewModel.isAllWatchDataLoaded =
                            dataReponse.watch?.totalRecords?.toInt() ?: 0
                        mViewModel.watchDataList.addAll(list)

                        setWatchMediationData()
                    }
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchWatchHowToMeditateResponse.postValue(null)
        }

        mViewModel.fetchReadHowToMeditateResponse.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageReadMeditation == 0) {
                    mViewModel.readDataList.clear()
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                } else {
                    showProgressIndicator(mBinding.llProgress, false)
                    showProgressIndicator(mBinding.progressBarRead, requestState.progress)
                }
                requestState.apiResponse?.data?.let { dataReponse ->
                    dataReponse.read?.list?.let { list ->
                        mViewModel.isAllReadDataLoaded =
                            dataReponse.read?.totalRecords?.toInt() ?: 0
                        mViewModel.readDataList.addAll(list)

                        setReadMeditationData()
                    }
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchReadHowToMeditateResponse.postValue(null)
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@HowToMeditateActivity)
        }

        mBinding.nestedScroll.viewTreeObserver.addOnScrollChangedListener {
            val view =
                mBinding.nestedScroll.getChildAt(mBinding.nestedScroll.childCount - 1) as View
            val diff: Int =
                view.bottom - (mBinding.nestedScroll.height + mBinding.nestedScroll.scrollY)
            if (mViewModel.isWatchSelected) {
                if (diff == 0 && mViewModel.isAllWatchDataLoaded != mViewModel.watchDataList.size) {
                    if (isNetwork())
                        fetchWatchHowToMeditateDataListAPI()
                    else
                        longToast(
                            getString(
                                R.string.no_internet,
                                getString(R.string.to_get_how_to_meditate_data)
                            ),
                            Constants.ERROR
                        )
                }
            } else {
                if (diff == 0 && mViewModel.isAllReadDataLoaded != mViewModel.readDataList.size) {
                    if (isNetwork())
                        fetchReadHowToMeditateDataListAPI()
                    else
                        longToast(
                            getString(
                                R.string.no_internet,
                                getString(R.string.to_get_how_to_meditate_data)
                            ),
                            Constants.ERROR
                        )
                }
            }
        }

        mBinding.tvWatch.setOnClickListener {
            if (mViewModel.type != Constants.watch) {
                mViewModel.type = Constants.watch
                mViewModel.isWatchSelected = true
                mViewModel.isReadSelected = false
                mBinding.rvWatchData.visible()
                mBinding.rvReadData.gone()

                mViewModel.currentPageWatchMeditation = -1
                changeTabBackground(mBinding.tvWatch, mBinding.tvRead)
                if (isNetwork())
                    fetchWatchHowToMeditateDataListAPI()
                else
                    longToast(
                        getString(
                            R.string.no_internet,
                            getString(R.string.to_get_how_to_meditate_data)
                        ),
                        Constants.ERROR
                    )

                if (isTablet(this) && requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    mBinding.imageMeditation.setImageResource(R.drawable.bg_read_meditation)
                }
            }
        }

        mBinding.tvRead.setOnClickListener {
            if (mViewModel.type != Constants.read) {
                mViewModel.type = Constants.read
                mViewModel.isReadSelected = true
                mViewModel.isWatchSelected = false
                mViewModel.currentPageReadMeditation = -1
                mBinding.rvWatchData.gone()
                mBinding.rvReadData.visible()
                changeTabBackground(mBinding.tvRead, mBinding.tvWatch)
                if (isNetwork())
                    fetchReadHowToMeditateDataListAPI()
                else
                    longToast(
                        getString(
                            R.string.no_internet,
                            getString(R.string.to_get_how_to_meditate_data)
                        ),
                        Constants.ERROR
                    )

                if (isTablet(this) && requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    mBinding.imageMeditation.setImageResource(R.drawable.bg_read_meditation)
                }
            }
        }
    }

    private fun changeTabBackground(
        activeTextView: AppCompatTextView,
        otherTextView: AppCompatTextView,
    ) {
        otherTextView.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
        otherTextView.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))

        activeTextView.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.gold))
    }

    override fun onReadMeditateClick(data: HowToMeditateDataModel) {
        data.let { dataModel ->
            if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
                //                    GET
                ReadMeditateActivity.startActivity(
                    this@HowToMeditateActivity,
                    dataModel.contentId
                )
                //                        SubscriptionActivity.startActivity(activity as AppCompatActivity)
            } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
                //                    lock
                if (dataModel.unlockDays != null) {
                    if (dataModel.unlockDays!! < 1)
                        showUnlockImageDialog(this) else {
                    }
                } else if (dataModel.unlockDays == null) {
                    showUnlockImageDialog(this)
                } else {

                }
            } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
                //                        Subscribe
                SubscriptionActivity.startActivityForResult(
                    this@HowToMeditateActivity
                )
            } else {
                //                    can access
                ReadMeditateActivity.startActivity(
                    this@HowToMeditateActivity,
                    dataModel.contentId
                )
            }
        }
    }

    override fun onWatchMeditateClick(data: HowToMeditateDataModel?) {
        data?.let { dataModel ->
            if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
                //                    GET
                dataModel.contentId?.let {
                    MeditationDetailActivity.startActivity(
                        this@HowToMeditateActivity,
                        it,
                        isFromMeditate = true
                    )
                }
            } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
                //                    lock
                if (dataModel.unlockDays != null) {
                    if (dataModel.unlockDays!! < 1)
                        showUnlockImageDialog(this) else {
                    }
                } else if (dataModel.unlockDays == null) {
                    showUnlockImageDialog(this)
                } else {

                }
            } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
                //                        Subscribe
                SubscriptionActivity.startActivityForResult(
                    this@HowToMeditateActivity
                )
            } else {
                //                    can access
                dataModel.contentId?.let {
                    MeditationDetailActivity.startActivity(
                        this@HowToMeditateActivity,
                        it,
                        isFromMeditate = true
                    )
                }
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

    private fun setReadMeditationData() {
        if (mViewModel.readDataList.size > 0) {
            mBinding.rvReadData.visible()
            mBinding.lblNoReadFound.gone()
            mBinding.lblNoWatchFound.gone()
        } else {
            mBinding.rvReadData.gone()
            mBinding.lblNoReadFound.visible()
            mBinding.lblNoWatchFound.gone()
        }
        readMeditateAdapter.setReadData(mViewModel.readDataList)
    }

    private fun setWatchMediationData() {
        if (mViewModel.watchDataList.size > 0) {
            mBinding.rvWatchData.visible()
            mBinding.lblNoWatchFound.gone()
            mBinding.lblNoReadFound.gone()
        } else {
            mBinding.rvWatchData.gone()
            mBinding.lblNoWatchFound.visible()
            mBinding.lblNoReadFound.gone()
        }

        watchMeditateAdapter.setWatchData(mViewModel.watchDataList)
    }
}