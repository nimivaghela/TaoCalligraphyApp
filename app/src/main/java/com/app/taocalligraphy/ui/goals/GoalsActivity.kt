package com.app.taocalligraphy.ui.goals

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.work.Configuration
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityGoalsBinding
import com.app.taocalligraphy.models.MeditationTimerModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.goals.adapter.SelectMultipleGoalsAdapter
import com.app.taocalligraphy.ui.goals.dialog.GoalScreenDialog
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.settings.dialog.SessionTimerSelectionDialog
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class GoalsActivity : BaseActivity<ActivityGoalsBinding>(),
    SelectMultipleGoalsAdapter.MultipleSelectItemClickListener {

    private val mViewModel: ProfileViewModel by viewModels()

    private lateinit var mSelectMultipleGoalsAdapterAdapter: SelectMultipleGoalsAdapter

    companion object {
        var selectedMeditationTarget = 10
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, GoalsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_goals

    override fun initView() {
        setupToolbar()
        updateProfile()

        if (isTablet(this)) {
            ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(0, 0, 0, insets.bottom)
                WindowInsetsCompat.CONSUMED
            }
        }

//        mBinding.rvGoalsList.layoutManager = LinearLayoutManager(this)
//        mSelectMultipleGoalsAdapterAdapter =
//            SelectMultipleGoalsAdapter(mutableListOf(), this, mViewModel.isEditable)
//        mBinding.rvGoalsList.adapter = mSelectMultipleGoalsAdapterAdapter

        if (isNetwork()) {
            if (mViewModel.userGoalsScreenList.size <= 0)
                mViewModel.getUserGoalsScreen2Api(this, mDisposable)
            else
                updateView()
        }

        if (mViewModel.mMeditationTargetArrayList.size == 0) {
            mViewModel.mMeditationTargetArrayList.clear()
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.one_min),
                    1,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.three_mins),
                    3,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.five_mins),
                    5,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.ten_mins),
                    10,
                    true
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.fiften_mins),
                    15,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.twenty_mins),
                    20,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.twenty_five_mins),
                    25,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.thirty_mins),
                    30,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.fourty_five_mins),
                    45,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.one_hour),
                    60,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.two_hours),
                    120,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.four_hours),
                    240,
                    false
                )
            )
            mViewModel.mMeditationTargetArrayList.add(
                MeditationTimerModel(
                    getString(R.string.eight_hours),
                    480,
                    false
                )
            )
        }
//        mViewModel.selectedMeditationTimer = getString(R.string.profile_goal_meditation_time_default)

    }

    private fun setupToolbar() {

        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()

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

    override fun observeApiCallbacks() {
        mViewModel.userGoalsScreen2LiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.list?.let { dataList ->
                    mViewModel.userGoalsScreenList.clear()
                    mViewModel.userGoalsScreenList.addAll(dataList.filter { it?.isSelected ?: false })
                    updateView()
                    val meditationTimer = mViewModel.mMeditationTargetArrayList.find { it.timer.toString() == requestState.apiResponse?.data?.dailyMeditationTarget.toString() }
                    meditationTimer?.let {
                        mViewModel.selectedMeditationTimer = it.title
                        mBinding.tvDailyMeditationTarget.text = mViewModel.selectedMeditationTimer
                        selectedMeditationTarget = (requestState.apiResponse?.data?.dailyMeditationTarget?.toInt() ?: 0)
                    }
                    mViewModel.userGoalsScreen2LiveData.postValue(null)
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.userGoalLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.message?.let { it1 -> longToast(it1, it.type ?: SUCCESS) }
                    mViewModel.userGoalLiveData.postValue(null)
                }
                longToastState(requestState.error)
            }
        }
    }

    private fun updateView() {
        if (mViewModel.userGoalsScreenList.size > 0) {
            mBinding.tvGoalsListNotFound.gone()
            mBinding.nestedScrollChipView.visible()
            mBinding.chipGroupSelectedGoals.removeAllViews()

//            mSelectMultipleGoalsAdapterAdapter.updateList(mViewModel.userGoalsScreenList)

            for (i in mViewModel.userGoalsScreenList.indices) {
                val chip = layoutInflater.inflate(
                    R.layout.item_interest,
                    mBinding.chipGroupSelectedGoals,
                    false
                ) as Chip
                chip.text = mViewModel.userGoalsScreenList[i]?.name
                mBinding.chipGroupSelectedGoals.addView(chip)
                chip.isChecked = mViewModel.userGoalsScreenList[i]?.isSelected!!
                chip.isCheckable = false
            }
        } else {
            mBinding.nestedScrollChipView.gone()
            mBinding.tvGoalsListNotFound.visible()
        }

        mViewModel.mMeditationTargetArrayList.map {
            it.isSelected = it.timer == selectedMeditationTarget
            if(it.isSelected)
            mViewModel.selectedMeditationTimer = it.title
        }

        mBinding.tvDailyMeditationTarget.text = mViewModel.selectedMeditationTimer
    }

    override fun handleListener() {

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@GoalsActivity)
        }

        mBinding.btnUpdateMyGoals.clickWithDebounce {
            showGoalScreenDialog()
        }
        mBinding.llMeditationTarget.clickWithDebounce {
            mViewModel.mMeditationTargetArrayList.map {
                it.isSelected = it.timer == selectedMeditationTarget
            }

            openMeditationTargetSelectionDialog(mViewModel.mMeditationTargetArrayList)
        }
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onMultipleItemClick() {

    }

    private fun showGoalScreenDialog() {
        val dialog = GoalScreenDialog.newInstance(object :
            GoalScreenDialog.GoalScreenListener {
            override fun onClear() {
                mViewModel.userGoalsScreenList.clear()
                mViewModel.userGoalsScreenList.addAll(GoalScreenDialog.userGoalsScreenList.filter { it?.isSelected ?: false })
                updateView()

                recreate()
            }
            override fun onDismiss() {
//                updateView()
            }
        }, selectedMeditationTarget)

        dialog.show(
            getFragmentTransactionFromTag(GoalScreenDialog.TAG),
            GoalScreenDialog.TAG
        )

    }

    private fun openMeditationTargetSelectionDialog(mMeditationTargetArrayList: ArrayList<MeditationTimerModel>) {
        val _dialog =
            SessionTimerSelectionDialog(
                this, mMeditationTargetArrayList,
                object : SessionTimerSelectionDialog.TimerSelectionListener {
                    override fun onTimerSelect(data: MeditationTimerModel) {
                        mBinding.tvDailyMeditationTarget.text = data.title

                        mMeditationTargetArrayList.forEach {
                            it.isSelected = it.timer == data.timer
                        }

                        selectedMeditationTarget = data.timer
                        val keywordIds = JsonArray()
                        val deletedKeywordIds = JsonArray()
                        if (isNetwork())
                            mViewModel.userGoalsApi(
                                deletedKeywordIds,
                                keywordIds,
                                1, selectedMeditationTarget,
                                this@GoalsActivity,
                                mDisposable
                            )
                        else
                            longToast(
                                getString(R.string.no_internet, getString(R.string.to_get_goals)),
                                Constants.ERROR
                            )
                    }
                }, false
            )
        _dialog.setOnShowListener { dialog ->
            val bottomSheet: FrameLayout =
                (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
            val lyout: CoordinatorLayout = bottomSheet.parent as CoordinatorLayout
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                bottomSheet
            )
            behavior.peekHeight = bottomSheet.height
            lyout.parent.requestLayout()
        }
        _dialog.show()
        _dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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