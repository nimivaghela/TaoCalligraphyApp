package com.app.taocalligraphy.ui.meditation.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentPostAssessmentTagsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.meditation.adapter.TagsAdapter
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce

class PostAssessmentTagsFragment : BaseFragment<FragmentPostAssessmentTagsBinding>() {

    companion object {
        var meditationContentResponse: MeditationContentResponse? = null
        var onSaveClickListener: OnSaveClickListener? = null
        var viewModel: MeditationContentViewModel? = null
        fun newInstance(
            meditationContentResponse: MeditationContentResponse?,
            onSaveClickListener: OnSaveClickListener,
            viewModel: MeditationContentViewModel
        ): PostAssessmentTagsFragment {
            this.meditationContentResponse = meditationContentResponse
            this.onSaveClickListener = onSaveClickListener
            this.viewModel = viewModel
            return PostAssessmentTagsFragment()
        }
    }


    private val tagsAdapter: TagsAdapter by lazy {
        TagsAdapter(viewModel)
    }

    override fun getLayoutId() = R.layout.fragment_post_assessment_tags

    override fun displayMessage(message: String) {
    }

    override fun initView() {
        mBinding.rvTags.layoutManager = GridLayoutManager(requireActivity(), 2)
        mBinding.rvTags.adapter = tagsAdapter
        meditationContentResponse?.feedbackTagList?.let { feedbackTags ->
            tagsAdapter.updateTagList(feedbackTags)
        }
        setAccessControlView()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setAccessControlView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mAccessLevelReceiver)
    }

    override fun observeApiCallbacks() {
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    fun setAccessControlView() {
        if (!(UserHolder.EnumUserModulePermission.JOURNAL.permission?.canAccess ?: true)) {
            mBinding.btnSaveAndJournal.icon = resources.getDrawable(R.drawable.ic_lock)
            mBinding.btnSaveAndJournal.setIconTintResource(R.color.dark_grey_80)
        } else {
            mBinding.btnSaveAndJournal.icon = resources.getDrawable(R.drawable.vd_pencil)
            mBinding.btnSaveAndJournal.setIconTintResource(R.color.gold)
        }
    }

    override fun handleListener() {
        mBinding.btnSaveAndJournal.clickWithDebounce {
            onSaveClickListener?.onSaveClicked(
                false,
                viewModel?.selectedExperience,
                mBinding.txtFeedback.text.toString()
            )
        }

        mBinding.btnSaveAndClose.clickWithDebounce {
            onSaveClickListener?.onSaveClicked(
                true,
                viewModel?.selectedExperience,
                mBinding.txtFeedback.text.toString()
            )
        }
    }

    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }
}

interface OnSaveClickListener {
    fun onSaveClicked(
        isSaveAndClose: Boolean,
        selectedExperience: ArrayList<MeditationContentResponse.FeedbackTag>?,
        feedBack: String
    )
}