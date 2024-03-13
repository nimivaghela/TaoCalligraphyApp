package com.app.taocalligraphy.ui.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentNotificationBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.notification_model.NotificationListDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.home.HomeFragment
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation_timer.PlayMeditationTimerActivity
import com.app.taocalligraphy.ui.notification.adapter.NotificationAdapter
import com.app.taocalligraphy.ui.notification.dialog.ClearAllNotificationConfirmDialog
import com.app.taocalligraphy.ui.notification.viewmodel.NotificationViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notification.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(),
    OnItemClicked {

    // to save recycler view state on navigation
    private val LIST_STATE_KEY: String = "NOTIFICATION_LIST_STATE"

    private val mViewModel: NotificationViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun getLayoutId(): Int {
        return R.layout.fragment_notification
    }

    private val notificationAdapter by lazy {
        return@lazy NotificationAdapter(this@NotificationFragment, requireContext())
    }

    override fun initView() {
        setupToolbar()
        mBinding.rlMain.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.transparent
            )
        )
        mBinding.rvNotifications.adapter = notificationAdapter

        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            callFetchNotificationListAPI()
            mBinding.swipeToRefreshLayout.isRefreshing = false
        }

        if (mViewModel.notificationDataList.isEmpty() || !mViewModel.isConfigChanges) {
            callFetchNotificationListAPI()
        } else {
            mBinding.rvNotifications.visible()
            mBinding.txClearAll.visible()
            mBinding.llNoNotificationsData.gone()
            setAdapterData()
        }
    }

    private fun callFetchNotificationListAPI() {
        mViewModel.currentPageProgramCount = 0
        mViewModel.currentPageSubscriptionCount = 0
        mViewModel.currentPageModificationTimerCount = 0
        mViewModel.currentPageDailyWisdomCount = 0
        mViewModel.fetchNotificationList(
            this,
            mDisposable
        )
    }

    override fun displayMessage(message: String) {}

    override fun observeApiCallbacks() {
        mViewModel.fetchNotificationListDataResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { fetchNotificationListDataResponse ->
                    mViewModel.notificationDataList.clear()
                    fetchNotificationListDataResponse.programs?.list?.let { dataList ->
                        mViewModel.notificationDataList.add(
                            NotificationListDataModel(
                                Constants.programs,
                                dataList,
                                fetchNotificationListDataResponse.programs.totalUnReadNotificationCount,
                                fetchNotificationListDataResponse.programs.totalRecords
                            )
                        )
                    }
                    fetchNotificationListDataResponse.meditation?.list?.let { dataList ->
                        mViewModel.notificationDataList.add(
                            NotificationListDataModel(
                                Constants.meditation,
                                dataList,
                                fetchNotificationListDataResponse.meditation.totalUnReadNotificationCount,
                                fetchNotificationListDataResponse.meditation.totalRecords

                            )
                        )
                    }
                    fetchNotificationListDataResponse.subscription?.list?.let { dataList ->
                        mViewModel.notificationDataList.add(
                            NotificationListDataModel(
                                Constants.subscription,
                                dataList,
                                fetchNotificationListDataResponse.subscription.totalUnReadNotificationCount,
                                fetchNotificationListDataResponse.subscription.totalRecords
                            )
                        )
                    }
                    fetchNotificationListDataResponse.dailyWisdom?.list?.let { dataList ->
                        mViewModel.notificationDataList.add(
                            NotificationListDataModel(
                                Constants.dailyWisdom,
                                dataList,
                                fetchNotificationListDataResponse.dailyWisdom.totalUnReadNotificationCount,
                                fetchNotificationListDataResponse.dailyWisdom.totalRecords
                            )
                        )
                    }

                    if (fetchNotificationListDataResponse.programs == null &&
                        fetchNotificationListDataResponse.meditation == null &&
                        fetchNotificationListDataResponse.subscription == null &&
                        fetchNotificationListDataResponse.dailyWisdom == null
                    ) {
                        mBinding.rvNotifications.gone()
                        mBinding.txClearAll.gone()
                        mBinding.llNoNotificationsData.visible()
                    } else {
                        mBinding.rvNotifications.visible()
                        mBinding.txClearAll.visible()
                        mBinding.llNoNotificationsData.gone()
                        setAdapterData()
                    }

                    val totalCount: Int =
                        (fetchNotificationListDataResponse.programs?.totalUnReadNotificationCount?.toInt()
                            ?: 0) +
                                (fetchNotificationListDataResponse.dailyWisdom?.totalUnReadNotificationCount?.toInt()
                                    ?: 0) +
                                (fetchNotificationListDataResponse.meditation?.totalUnReadNotificationCount?.toInt()
                                    ?: 0) +
                                (fetchNotificationListDataResponse.subscription?.totalUnReadNotificationCount?.toInt()
                                    ?: 0)

                    (requireActivity() as MainActivity).setNotificationCount(totalCount.toString())

                }
                longToastState(requestState.error)
            }
            mViewModel.fetchNotificationListDataResponse.postValue(null)
        }

        mViewModel.fetchSubscriptionDataList.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { fetchNotificationListDataResponse ->
                    fetchNotificationListDataResponse.subscription?.list?.let { dataList ->
                        mViewModel.notificationDataList.find { it?.type == Constants.subscription }?.dataList?.addAll(
                            dataList
                        )
                        notificationAdapter.addMoreSubscriptionData(
                            dataList,
                            fetchNotificationListDataResponse.subscription.totalUnReadNotificationCount?.toInt() == 0
                        )
                    } ?: kotlin.run {
                        notificationAdapter.hideLoadMore(Constants.subscription)
                    }
                } ?: kotlin.run {
                    notificationAdapter.hideLoadMore(Constants.subscription)
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchSubscriptionDataList.postValue(null)
        }

        mViewModel.fetchProgramDataList.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { fetchNotificationListDataResponse ->
                    fetchNotificationListDataResponse.programs?.list?.let { dataList ->
                        mViewModel.notificationDataList.find { it?.type == Constants.programs }?.dataList?.addAll(
                            dataList
                        )
                        notificationAdapter.addMoreProgramData(
                            dataList,
                            fetchNotificationListDataResponse.programs.totalUnReadNotificationCount?.toInt() == 0
                        )

                    } ?: kotlin.run {
                        notificationAdapter.hideLoadMore(Constants.programs)
                    }
                } ?: kotlin.run {
                    notificationAdapter.hideLoadMore(Constants.programs)
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchProgramDataList.postValue(null)
        }

        mViewModel.fetchMeditationTimerDataList.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, response.progress)
                requestState.apiResponse?.data?.let { fetchNotificationListDataResponse ->
                    fetchNotificationListDataResponse.meditation?.list?.let { dataList ->
                        mViewModel.notificationDataList.find { it?.type == Constants.meditation }?.dataList?.addAll(
                            dataList
                        )
                        notificationAdapter.addMoreMeditationData(
                            dataList,
                            fetchNotificationListDataResponse.meditation.totalUnReadNotificationCount?.toInt() == 0
                        )
                    } ?: kotlin.run {
                        notificationAdapter.hideLoadMore(Constants.meditation)
                    }
                } ?: kotlin.run {
                    notificationAdapter.hideLoadMore(Constants.meditation)
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchMeditationTimerDataList.postValue(null)
        }

        mViewModel.fetchDailyWisdomDataList.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, response.progress)
                requestState.apiResponse?.data?.let { fetchNotificationListDataResponse ->
                    fetchNotificationListDataResponse.dailyWisdom?.list?.let { dataList ->
                        mViewModel.notificationDataList.find { it?.type == Constants.dailyWisdom }?.dataList?.addAll(
                            dataList
                        )
                        notificationAdapter.addMoreDailyWisdomData(
                            dataList,
                            fetchNotificationListDataResponse.dailyWisdom.totalUnReadNotificationCount?.toInt() == 0
                        )
                    } ?: kotlin.run {
                        notificationAdapter.hideLoadMore(Constants.dailyWisdom)
                    }
                } ?: kotlin.run {
                    notificationAdapter.hideLoadMore(Constants.dailyWisdom)
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchDailyWisdomDataList.postValue(null)
        }

        mViewModel.deleteNotificationDataByIdResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, response.progress)
                requestState.apiResponse?.data?.let {
                    val programUnreadCount =
                        requestState.apiResponse?.data?.get("programUnreadCount").toString().toInt()
                    val dailyWisdomUnreadCount =
                        requestState.apiResponse?.data?.get("dailyWisdomUnreadCount").toString()
                            .toInt()
                    val meditationUnreadCount =
                        requestState.apiResponse?.data?.get("meditationUnreadCount").toString()
                            .toInt()
                    val subscriptionUnreadCount =
                        requestState.apiResponse?.data?.get("subscriptionUnreadCount").toString()
                            .toInt()

                    val totalCount: Int =
                        programUnreadCount + dailyWisdomUnreadCount + meditationUnreadCount + subscriptionUnreadCount

                    mViewModel.notificationDataList.find { it?.type == Constants.programs }?.unReadCount =
                        programUnreadCount.toString()
                    mViewModel.notificationDataList.find { it?.type == Constants.dailyWisdom }?.unReadCount =
                        dailyWisdomUnreadCount.toString()
                    mViewModel.notificationDataList.find { it?.type == Constants.meditation }?.unReadCount =
                        meditationUnreadCount.toString()
                    mViewModel.notificationDataList.find { it?.type == Constants.subscription }?.unReadCount =
                        subscriptionUnreadCount.toString()

                    (requireActivity() as MainActivity).setNotificationCount(totalCount.toString())

                }
                longToastState(requestState.error)
            }
            mViewModel.deleteNotificationDataByIdResponse.postValue(null)
        }

        mViewModel.readNotificationDataByIdResponse.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.message?.let {
                    longToastState(requestState.error)
                }

                mViewModel.notificationDataList.find { it!!.type == Constants.programs }?.unReadCount =
                    (response.apiResponse?.data?.programs ?: 0).toString()
                mViewModel.notificationDataList.find { it!!.type == Constants.meditation }?.unReadCount =
                    (response.apiResponse?.data?.meditation ?: 0).toString()
                mViewModel.notificationDataList.find { it!!.type == Constants.dailyWisdom }?.unReadCount =
                    (response.apiResponse?.data?.dailyWisdom ?: 0).toString()
                mViewModel.notificationDataList.find { it!!.type == Constants.subscription }?.unReadCount =
                    (response.apiResponse?.data?.subscription ?: 0).toString()

                var totalCount = 0
                mViewModel.notificationDataList.forEach {
                    totalCount += (it?.unReadCount?.toInt() ?: 0)
                }
                (requireActivity() as MainActivity).setNotificationCount(totalCount.toString())
            }

            mViewModel.readNotificationDataByIdResponse.postValue(null)
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        mViewModel.mListState = rvNotifications.layoutManager!!.onSaveInstanceState()
        state.putParcelable(LIST_STATE_KEY, mViewModel.mListState)
    }

    override fun onResume() {
        super.onResume()
        if (mViewModel.mListState != null) {
            rvNotifications.layoutManager!!.onRestoreInstanceState(mViewModel.mListState)
        }
    }

    private fun setAdapterData() {
        notificationAdapter.setNotificationData(mViewModel.notificationDataList)
    }

    private fun setupToolbar() {
        mBinding.mToolbar.cardProfile.visible()

        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            (requireActivity() as MainActivity).hideWellnessDialog()
            UserMenuActivity.startActivity(activity as MainActivity)
        }

        mBinding.txClearAll.setOnClickListener {
            showClearNotificationDialog()
        }

        mBinding.mToolbar.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showClearNotificationDialog() {
        val dialog = ClearAllNotificationConfirmDialog.newInstance(object :
            ClearAllNotificationConfirmDialog.NotificationClearListener {
            override fun onClear() {
                mBinding.rvNotifications.isGone = true
                mBinding.txClearAll.isGone = true
                mBinding.llNoNotificationsData.isVisible = true
                val list: ArrayList<Int> = arrayListOf()
                callDeleteNotificationById(list, Constants.all)
                mViewModel.notificationDataList.clear()
                setAdapterData()
                (requireActivity() as MainActivity).setNotificationCount(0.toString())
            }
        })
        dialog.show(
            getFragmentTransactionFromTag(ClearAllNotificationConfirmDialog.TAG),
            ClearAllNotificationConfirmDialog.TAG
        )
    }

    override fun loadMoreClicked(type: String) {
        when (type) {
            Constants.programs -> {
                mViewModel.fetchProgramTypeNotificationList(
                    this,
                    mDisposable
                )
            }
            Constants.subscription -> {
                mViewModel.fetchSubscriptionTypeNotificationList(
                    this,
                    mDisposable
                )
            }
            Constants.meditation -> {
                mViewModel.fetchMeditationTimerNotificationList(
                    this,
                    mDisposable
                )
            }
            Constants.dailyWisdom -> {
                mViewModel.fetchDailyWisdomNotificationList(
                    this,
                    mDisposable
                )
            }
        }
    }

    override fun onNotificationRead(idList: List<Int>) {
        callReadNotificationByID(idList as ArrayList<Int>)
    }

    override fun onInnerItemClicked(position: Int, type: String) {
        val notificationList = mViewModel.notificationDataList.find { it?.type == type }
        val item = notificationList?.dataList?.get(position)
        val id = item?.redirectionId ?: "-1"
        val notificationID = notificationList?.dataList?.get(position)?.id

        when (type) {
            Constants.programs -> {
                if (id != "-1") {
                    ProgramDetailsActivity.startActivity(
                        requireActivity() as AppCompatActivity,
                        id,
                        false
                    )
                }
            }
            Constants.meditation -> {
                if (!(UserHolder.EnumUserModulePermission.MEDITATION_TIMER.permission?.canAccess
                        ?: false)
                ) {
                    SubscriptionActivity.startActivityForResult(requireActivity() as AppCompatActivity)
                    return
                }

                if (id != "-1") {
                    PlayMeditationTimerActivity.startActivity(
                        activity as AppCompatActivity,
                        id, null,
                        true
                    )
                }
            }
            Constants.dailyWisdom -> {
                if (notificationID != null) {
                    (activity as MainActivity).mBinding.bottomNav.selectedItemId = R.id.home
                    HomeFragment.isFromNotification = true
                }
            }
            Constants.subscription -> {
//                val intent =
//                    Intent(requireContext(), SubscriptionActivity::class.java)
//                requireActivity().startActivityWithAnimation(intent)

                requireActivity().longToast(getString(R.string.coming_soon), Constants.INFO)
            }
        }

        // Removing count from adapter list also
        if (item?.notificationRead != true) {
            val list = mViewModel.notificationDataList.find { it?.type == type }
            list?.dataList?.get(position)?.notificationRead = true
            list?.unReadCount = ((list?.unReadCount?.toInt() ?: 1) - 1).toString()
        }

    }

    private fun callReadNotificationByID(idList: ArrayList<Int>) {
        mViewModel.readNotificationByID(idList, this, mDisposable)
    }

    override fun onDeleteClicked(idList: ArrayList<Int>, type: String?, isAllDeleted: Boolean) {
        // Removing data from adapter list in case of single item delete for bottom stack view
        idList.forEach { notificationId ->
            val typeIndex =
                mViewModel.notificationDataList.indexOfFirst { it?.type == (type ?: "") }
            val typeList = mViewModel.notificationDataList[typeIndex]
            val ind = typeList?.dataList?.indexOfFirst { it?.id == notificationId.toString() }
            if (ind != null) {
                typeList.dataList?.removeAt(ind)

                // for automatically closing inner recycler view in case of single item left after delete
                if (typeList.dataList?.size == 1) {
                    notificationAdapter.notifyItemChanged(typeIndex)
                }
            }
        }
        callDeleteNotificationById(if (isAllDeleted) arrayListOf() else idList, type)

        if (mViewModel.notificationDataList.sumOf { it?.dataList?.size ?: 0 } == 0) {
            mBinding.rvNotifications.gone()
            mBinding.txClearAll.gone()
            mBinding.llNoNotificationsData.visible()
        } else {
            mBinding.rvNotifications.visible()
            mBinding.txClearAll.visible()
            mBinding.llNoNotificationsData.gone()
        }
    }

    private fun callDeleteNotificationById(list: ArrayList<Int>, type: String?) {
        mViewModel.deleteNotificationByID(list, type, this, mDisposable)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            initView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (isAdded) {
                showNoInternetDialog()
            }
        }
    }

    override fun onDestroyView() {
        mViewModel.isConfigChanges = requireActivity().isChangingConfigurations
        super.onDestroyView()
    }
}