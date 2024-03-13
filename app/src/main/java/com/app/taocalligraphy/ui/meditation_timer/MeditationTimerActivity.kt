package com.app.taocalligraphy.ui.meditation_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationTimerBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_timer.MeditationListApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.meditation_timer.adapter.MeditationTimerAdapter
import com.app.taocalligraphy.ui.meditation_timer.viewmodel.MeditationTimerViewModel
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
class MeditationTimerActivity : BaseActivity<ActivityMeditationTimerBinding>(),
    MeditationTimerAdapter.ItemClickListener {

    private var deleteItemPosition = 0
    private val mViewModel: MeditationTimerViewModel by viewModels()

    private val mMeditationTimerAdapter by lazy {
        return@lazy MeditationTimerAdapter(this@MeditationTimerActivity, this)
    }
    private var AddEditMeditationTimerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->

        mBinding.scrollView.scrollTo(0, 0)
        mBinding.shimmerMeditationTimer.showShimmer(true)
        mBinding.shimmerMeditationTimer.visible()
        mBinding.rvMeditationTimer.gone()

        if (isNetwork())
            mViewModel.meditationTimerListApi(this, mDisposable)

        if (result.data?.getBooleanExtra("showSnackbar", true) == true) {
            longToast(
                result.data?.getStringExtra("message") ?: "",
                result.data?.getStringExtra("type") ?: SUCCESS
            )
        }

    }

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, MeditationTimerActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_meditation_timer

    override fun initView() {
        setupToolbar()
        setTimerAdapter()
        updateProfile()
        mBinding.shimmerMeditationTimer.showShimmer(true)
        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.shimmerMeditationTimer.showShimmer(true)
            mBinding.shimmerMeditationTimer.visible()
            mBinding.rvMeditationTimer.gone()
            mBinding.swipeToRefreshLayout.isRefreshing = false
            if (isNetwork())
                mViewModel.meditationTimerListApi(this, mDisposable)
        }
        if (isNetwork() && mViewModel.meditationTimerList.isEmpty() && mViewModel.hasMeditationData)
            mViewModel.meditationTimerListApi(this, mDisposable)
        else
            setMeditationTimerData()

        LocalBroadcastManager.getInstance(this@MeditationTimerActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@MeditationTimerActivity)
            .unregisterReceiver(mAccessLevelReceiver)
    }

    private fun setTimerAdapter() {
        mBinding.rvMeditationTimer.adapter = mMeditationTimerAdapter
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }


    override fun observeApiCallbacks() {
        mViewModel.meditationTimerListLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { data ->
                    data.data?.list?.let { list ->
                        mViewModel.hasMeditationData = list.isNotEmpty()
                        mViewModel.meditationTimerList.clear()
                        mViewModel.meditationTimerList.addAll(list)
                        setMeditationTimerData()
                    }
                }
                longToastState(requestState.error)
            }
            mViewModel.meditationTimerListLiveData.postValue(null)
        }

        mViewModel.meditationTimerDeleteLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                    mMeditationTimerAdapter.deleteItem(deleteItemPosition)
                }
                longToastState(requestState.error)
            }
            mViewModel.meditationTimerDeleteLiveData.postValue(null)
        }

        mViewModel.meditationTimerCloneLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                    if (isNetwork())
                        mViewModel.meditationTimerListApi(this, mDisposable)
                    else
                        longToast(
                            getString(
                                R.string.no_internet,
                                getString(R.string.to_get_meditation_timers)
                            ),
                            ERROR
                        )
                }
                longToastState(requestState.error)
            }
            mViewModel.meditationTimerCloneLiveData.postValue(null)
        }
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun handleListener() {

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(
                this@MeditationTimerActivity,
                MeditationTimerActivity::class.java.simpleName
            )
        }

    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mMeditationTimerAdapter.notifyDataSetChanged()
        }
    }

    override fun onAddTimerItemClick() {
        if (!(UserHolder.EnumUserModulePermission.CREATE_MEDITATION_TIMER.permission?.canAccess
                ?: false)
        ) {
            SubscriptionActivity.startActivityForResult(this@MeditationTimerActivity)
            return
        }

        CreateNewTimerActivity.startActivity(
            this@MeditationTimerActivity,
            null,
            false,
            AddEditMeditationTimerResult
        )
    }

    override fun onPlayTimerItemClick(meditationDetail: MeditationListApiResponse.MeditationDetail) {
        PlayMeditationTimerActivity.startActivity(
            this@MeditationTimerActivity,
            null,
            meditationDetail, false
        )
    }

    override fun onEditTimerItemClick(meditationDetail: MeditationListApiResponse.MeditationDetail) {
        if (!(UserHolder.EnumUserModulePermission.CREATE_MEDITATION_TIMER.permission?.canAccess
                ?: false)
        ) {
            SubscriptionActivity.startActivityForResult(this@MeditationTimerActivity)
            return
        }

        CreateNewTimerActivity.startActivity(
            this@MeditationTimerActivity,
            meditationDetail,
            true,
            AddEditMeditationTimerResult
        )
    }

    override fun onCloneTimerItemClick(meditationDetail: MeditationListApiResponse.MeditationDetail) {
        if (!(UserHolder.EnumUserModulePermission.CREATE_MEDITATION_TIMER.permission?.canAccess
                ?: false)
        ) {
            SubscriptionActivity.startActivityForResult(this@MeditationTimerActivity)
            return
        }

        if (isNetwork())
            meditationDetail.meditationId?.let {
                mViewModel.meditationTimerCloneApi(
                    it,
                    this,
                    mDisposable
                )
            }
    }

    override fun onDeleteTimerItemClick(meditationId: Int, position: Int) {
        deleteItemPosition = position
        deleteConfirmationDialog(meditationId)
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, ERROR)
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    private fun deleteConfirmationDialog(meditationId: Int) {
        val title = ""
        val description = "" + getString(R.string.delete_meditation_timer_message)

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("" + title)
            .setMessage("" + description)
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, _ ->
                dialog?.dismiss()
                if (isNetwork())
                    mViewModel.meditationTimerDeleteApi(meditationId, this, mDisposable)
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, _ -> dialog?.dismiss() }
        val alert = builder.create()
        alert.show()
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

    private fun setMeditationTimerData() {
        mMeditationTimerAdapter.setMeditationTimerData(mViewModel.meditationTimerList)
        mBinding.shimmerMeditationTimer.hideShimmer()
        mBinding.shimmerMeditationTimer.gone()
        mBinding.rvMeditationTimer.visible()
    }
}