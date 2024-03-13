package com.app.taocalligraphy.ui.program

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProgramFeedbackSuggestionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.ForYouProgramListResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.adapter.SuggestedProgramsAdapter
import com.app.taocalligraphy.ui.program.viewmodel.ProgramViewModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.showUnlockImageDialog
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ProgramFeedbackSuggestionsActivity :
    BaseActivity<ActivityProgramFeedbackSuggestionBinding>(),
    SuggestedProgramsAdapter.SuggestedProgramsListener {

    private val programId by lazy {
        return@lazy intent?.extras?.getInt(Constants.Param.programId) ?: 0
    }

    private val isFromHistoryCompletedProgram by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromHistoryCompletedProgram, false)
            ?: false
    }

    private val mViewModel: ProgramViewModel by viewModels()

    private val adapter: SuggestedProgramsAdapter by lazy {
        SuggestedProgramsAdapter(this)
    }

    companion object {
        fun startActivity(
            activity: Activity,
            programId: Int,
            isFromHistoryCompletedProgram: Boolean
        ) {
            val intent = Intent(activity, ProgramFeedbackSuggestionsActivity::class.java)
            intent.putExtra(Constants.Param.programId, programId)
            intent.putExtra(
                Constants.Param.isFromHistoryCompletedProgram,
                isFromHistoryCompletedProgram
            )
            activity.startActivityWithAnimation(intent)
            activity.finish()
        }
    }

    override fun getResource() = R.layout.activity_program_feedback_suggestion

//    val startSubscriptionActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            if(result.data?.getBooleanExtra("isSubscribed",false) == true)
//            onRefresh()
//        }
//
//    }


    override fun initView() {
        mBinding.rvSuggestedList.layoutManager = GridLayoutManager(this, 2)
        mBinding.rvSuggestedList.adapter = adapter

        if (mViewModel.linkedProgramData == null)
            mViewModel.getLinkedProgram(programId, isFromHistoryCompletedProgram, this, mDisposable)
    }

    override fun observeApiCallbacks() {
        mViewModel.linkedProgramDataRes.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { linkProgram ->
                        mViewModel.linkedProgramData = linkProgram
                        adapter.programsList = linkProgram.extraLinkedProgramList
                        adapter.notifyDataSetChanged()
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            errorObj.customMessage?.let { longToast(it, Constants.ERROR) }
                        }
                    }
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.ivClose.clickWithDebounce {
            finish()
        }

        mBinding.btnHome.clickWithDebounce {
            MainActivity.startActivity(this)
            finishAffinity()
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

    override fun onSuggestedProgramsItemClick(data: ForYouProgramListResponse.ForYouProgramList.Program?) {
        data?.let { dataModel ->
            if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
//                    GET
                dataModel.id?.let {
                    openProgramDetailScreen(it)
                }
            } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
//                    lock
                if (dataModel.unlockDays != null) {
                    if (dataModel.unlockDays ?: 0 < 1)
                        showUnlockImageDialog(this) else {
                    }
                } else if (dataModel.unlockDays == null) {
                    showUnlockImageDialog(this)
                } else {

                }
            } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
//                        Subscribe
                SubscriptionActivity.startActivityForResult(this)
            } else {
//                    can access
                dataModel.id?.let {
                    openProgramDetailScreen(it)
                }
            }
        }
    }

    private fun openProgramDetailScreen(programId: String) {
        ProgramDetailsActivity.startActivity(this, programId, false)
        finish()
    }

}