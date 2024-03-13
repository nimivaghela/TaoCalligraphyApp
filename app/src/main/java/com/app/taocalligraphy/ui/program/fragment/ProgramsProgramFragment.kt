package com.app.taocalligraphy.ui.program.fragment

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentProgramsProgramBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.UserProgramApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.ui.program.adapter.ProgramsDaySelectSingleAdapter
import com.app.taocalligraphy.ui.program.adapter.ProgramsDayWiseListAdapter
import com.app.taocalligraphy.ui.program.viewmodel.ProgramViewModel
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@AndroidEntryPoint
class ProgramsProgramFragment :
    BaseFragment<FragmentProgramsProgramBinding>(),
    ProgramsDaySelectSingleAdapter.OnDayClickListener,
    ProgramsDayWiseListAdapter.ProgramClickListener {

    private val mProgramsDaySelectSingleAdapter by lazy {
        return@lazy ProgramsDaySelectSingleAdapter(
            this,
            mViewModel.selectedDayPos
        )
    }
    private val mProgramsDayWiseListAdapter by lazy {
        return@lazy ProgramsDayWiseListAdapter(requireContext(), this)
    }

    private val mViewModel: ProgramViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun getLayoutId() = R.layout.fragment_programs_program

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    companion object {
        var data: UserProgramApiResponse? = null
        var isProgramJoined: Boolean = false
        var isFromHistoryCompletedProgram: Boolean = false
        var isFromQuestionnaires: Boolean = false
        fun newInstance(
            data: UserProgramApiResponse?,
            isProgramJoined: Boolean,
            isFromHistoryCompletedProgram: Boolean,
            isFromQuestionnaires: Boolean,
        ): ProgramsProgramFragment {
            this.data = data
            this.isProgramJoined = isProgramJoined
            this.isFromHistoryCompletedProgram = isFromHistoryCompletedProgram
            this.isFromQuestionnaires = isFromQuestionnaires
            return ProgramsProgramFragment()
        }
    }

    override fun initView() {
        mBinding.rvProgramDate.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollHorizontally(1)) {
                    mBinding.ivTransparentBackground.inVisible()
                } else if ((mViewModel.selectedDayPos) > 2) {
                    mBinding.ivTransparentBackground.inVisible()
                } else {
                    if (!isTabletLandScape(requireActivity()))
                        mBinding.ivTransparentBackground.visible()
                }
            }
        })

        mBinding.ShimmerProgramContent.showShimmer(true)
        mBinding.rvProgramDate.adapter = mProgramsDaySelectSingleAdapter
        data?.daysList?.let {
            mProgramsDaySelectSingleAdapter.updateList(
                it,
                isProgramJoined,
                mViewModel.selectedDayPos
            )
        }
        if (isProgramJoined) {
            if (!isTabletLandScape(requireActivity()))
                mBinding.ivTransparentBackground.visibility = View.VISIBLE

            if (mViewModel.selectedDayPos == 0) {
                val days = data?.daysList?.filterIndexed { index, it ->
                    if (it.programDate == dateFormatter_yyyy_mm_dd.format(Calendar.getInstance().time)) {
                        mViewModel.selectedDayPos = index
                        mProgramsDaySelectSingleAdapter.updateSelectedDay(
                            mViewModel.selectedDayPos
                        )
                        mBinding.rvProgramDate.scrollToPosition(mViewModel.selectedDayPos)
                        if ((mViewModel.selectedDayPos) > 2) {
                            mBinding.ivTransparentBackground.inVisible()
                        } else {
                            if (!isTabletLandScape(requireActivity()))
                                mBinding.ivTransparentBackground.visible()
                        }
                    }
                    it.programDate == dateFormatter_yyyy_mm_dd.format(
                        Calendar.getInstance().time
                    )
                }

                if (!days.isNullOrEmpty()) {
                    val day = days[0]
                    ProgramDetailsActivity.daysList = day
                    if (mViewModel.programContentList.isEmpty()) {
                        mViewModel.userProgramContentDetailsApi(
                            data?.programId.toString(),
                            day.programDay.toString(),
                            isFromHistoryCompletedProgram,
                            this,
                            mDisposable
                        )
                    } else {
                        mViewModel.isFromConfigChanges = false
                        setProgramContentData()
                    }
                } else if (mViewModel.programContentList.isEmpty()) {
                    mViewModel.userProgramContentDetailsApi(
                        data?.programId.toString(),
                        ProgramDetailsActivity.daysList?.programDay.toString(),
                        isFromHistoryCompletedProgram,
                        this,
                        mDisposable
                    )
                } else {
                    mViewModel.isFromConfigChanges = false
                    setProgramContentData()
                }
            }
        } else {
            if (requireActivity().isNetwork() && mViewModel.programContentList.isEmpty()) {
                mViewModel.userProgramContentDetailsApi(
                    data?.programId.toString(),
                    ProgramDetailsActivity.daysList?.programDay.toString(),
                    isFromHistoryCompletedProgram,
                    this,
                    mDisposable
                )
            } else {
                mViewModel.isFromConfigChanges = false
                setProgramContentData()
            }
        }
        mBinding.rvProgramList.adapter = mProgramsDayWiseListAdapter

    }

    override fun postInit() {

    }

    override fun initObserver() {
        mViewModel.userProgramContentListLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { res ->
                    res.data?.let {
                        mViewModel.programContentList.clear()
                        mViewModel.bannerList.clear()
                        Constants.IS_VIDEO_SHOW = false
                        for (i in it.indices) {
                            it[i].currentDate =
                                ProgramDetailsActivity.daysList?.programDate.toString()
                            it[i].backgroundImageMobile?.let { it1 ->
                                mViewModel.bannerList.add(it1)
                            }
                        }
                        (activity as ProgramDetailsActivity).setBannerData()

                        it.forEachIndexed { index, program ->
                            program.combinedDate = Calendar.getInstance()
                            val contentTime =
                                dateFormatter_utc_HH_mm_ss.parse(program.contentAvailabilityTime!!)
                            val calTime = Calendar.getInstance()
                            calTime.time = contentTime!!
                            program.combinedDate.set(
                                Calendar.HOUR_OF_DAY,
                                calTime.get(Calendar.HOUR_OF_DAY)
                            )
                            program.combinedDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))
                            program.combinedDate.set(Calendar.SECOND, calTime.get(Calendar.SECOND))

                            if (isProgramJoined) {
                                val contentDate =
                                    dateFormatter_yyyy_mm_dd.parse(program.currentDate)
                                val calDate = Calendar.getInstance()
                                calDate.time = contentDate!!
                                program.combinedDate.set(Calendar.YEAR, calDate.get(Calendar.YEAR))
                                program.combinedDate.set(
                                    Calendar.MONTH,
                                    calDate.get(Calendar.MONTH)
                                )
                                program.combinedDate.set(Calendar.DATE, calDate.get(Calendar.DATE))
                                program.isPlayEnable =
                                    program.combinedDate.time <= Calendar.getInstance().time

                                if (index == it.size - 1 && program.combinedDate.time.before(
                                        Calendar.getInstance().time
                                    )
                                ) {
                                    mViewModel.isLastContentPlayed =
                                        program.isPlayed ?: false
                                } else {
                                    mViewModel.isLastContentPlayed = false
                                }
                                (activity as ProgramDetailsActivity).showProgramFeedbackScreen()
                            } else {
                                program.isPlayEnable = false
                                mViewModel.isLastContentPlayed = false
                                mBinding.nextProgramDescription.gone()
                            }
                        }

                        mViewModel.programContentList.addAll(it)
                        setProgramContentData()
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { activity?.longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { activity?.longToast(it, errorObj.type) }
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

            mViewModel.userProgramContentListLiveData.postValue(null)
        }
    }

    override fun handleListener() {
    }

    override fun onDaySelected(position: Int) {
        if (position != -1) {
            mBinding.rvProgramDate.scrollToPosition(position)
            mViewModel.selectedDayPos = position

            if (mViewModel.selectedDayPos > 2) {
                mBinding.ivTransparentBackground.inVisible()
            } else {
                if (!isTabletLandScape(requireActivity()))
                    mBinding.ivTransparentBackground.visible()
            }
        }
    }

    override fun onDayClick(position: UserProgramApiResponse.Days, selectedPos: Int) {
        mViewModel.selectedDayPos = selectedPos
        ProgramDetailsActivity.daysList = position
        mBinding.ShimmerProgramContent.showShimmer(true)
        mBinding.ShimmerProgramContent.visible()
        mBinding.rvProgramList.gone()
        mViewModel.userProgramContentDetailsApi(
            data?.programId.toString(),
            position.programDay.toString(),
            isFromHistoryCompletedProgram,
            this,
            mDisposable
        )
    }

    override fun onResume() {
        if (mViewModel.selectedDayPos != -1) {
            val selectedDay = data?.daysList?.get(mViewModel.selectedDayPos)
            mBinding.ShimmerProgramContent.showShimmer(true)
            mBinding.ShimmerProgramContent.visible()
            mBinding.rvProgramList.gone()
            if (mViewModel.isMoveToDetail) {
                mViewModel.isMoveToDetail = false
                mViewModel.userProgramContentDetailsApi(
                    data?.programId.toString(),
                    selectedDay?.programDay.toString(),
                    isFromHistoryCompletedProgram,
                    this,
                    mDisposable
                )
            } else {
                mViewModel.isFromConfigChanges = false
                setProgramContentData()
            }
        }
        super.onResume()
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            activity?.longToast(error, ERROR)
        }
    }

    override fun onProgramPlayClick(contentId: Int, programContentId: Int) {
        activity?.let {
            mViewModel.isMoveToDetail = true
            MeditationDetailActivity.startActivity(
                requireActivity() as AppCompatActivity,
                contentId = contentId.toString(),
                isFromProgram = true,
                programContentId = programContentId.toString(),
                isFromQuestionnaires = isFromQuestionnaires
            )
        }
    }

    override fun onProgramDescMoreClick(contentId: Int, programContentId: Int) {
        activity?.let {
            MeditationDetailActivity.startActivity(
                requireActivity() as AppCompatActivity,
                contentId = contentId.toString(),
                isFromProgram = true,
                programContentId = programContentId.toString(),
                isFromQuestionnaires = isFromQuestionnaires
            )
        }
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

    private fun setProgramNextSession(title: String, programDate: String, programTime: Calendar) {
        val currentTime = Calendar.getInstance().time

        if (currentTime.before(programTime.time) &&
            mProgramsDaySelectSingleAdapter.selectedPosition <= 0
        ) {
            if (dateFormatter_yyyy_mm_dd.format(programTime.time) == programDate
            ) {
                val spanJoining = SpannableStringBuilder()
                spanJoining.let {
                    it.append("${getString(R.string.program_thank_you_for_joining)} ")
                    it.setSpan(
                        TextAppearanceSpan(context, R.style.TextViewJostRegularStyleWithoutColor),
                        0,
                        it.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                val spanTitle = SpannableStringBuilder()
                spanTitle.let {
                    it.append("\"$title.\"")

                    it.setSpan(
                        TextAppearanceSpan(context, R.style.TextViewJostBoldStyle),
                        0,
                        it.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                val spanSession = SpannableStringBuilder()
                spanSession.let {
                    it.append(" ${getString(R.string.program_your_first_session)} ")
                    it.setSpan(
                        TextAppearanceSpan(context, R.style.TextViewJostRegularStyleWithoutColor),
                        0,
                        it.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                val spanProgramDate = SpannableStringBuilder()
                spanProgramDate.let {
                    it.append(programDate.getProgramDayFromDate())
                    it.setSpan(
                        TextAppearanceSpan(context, R.style.TextViewJostBoldStyle),
                        0,
                        it.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                val spanAt = SpannableStringBuilder()
                spanAt.let {
                    it.append(" ${getString(R.string.program_at)} ")
                    it.setSpan(
                        TextAppearanceSpan(context, R.style.TextViewJostRegularStyleWithoutColor),
                        0,
                        it.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                val spanTime = SpannableStringBuilder()
                spanTime.let {
                    it.append("${programTime.getTimeBasedOnTimeFormat(requireActivity())}.")

                    it.setSpan(
                        TextAppearanceSpan(context, R.style.TextViewJostBoldStyle),
                        0,
                        it.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                val spanNotify = SpannableStringBuilder()
                spanNotify.let {
                    it.append(" ${getString(R.string.program_notify_before_begin)}")
                    it.setSpan(
                        TextAppearanceSpan(context, R.style.TextViewJostRegularStyleWithoutColor),
                        0,
                        it.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                val spannableFilteredText = SpannableStringBuilder()
                spannableFilteredText.let {
                    it.append(spanJoining)
                    it.append(spanTitle)
                    it.append(spanSession)
                    it.append(spanProgramDate)
                    it.append(spanAt)
                    it.append(spanTime)
                    it.append(spanNotify)
                }
                mBinding.nextProgramDescription.visible()
                mBinding.txtNextProgramDetails.text = spannableFilteredText

            }
        } else {
            mBinding.nextProgramDescription.gone()
        }
    }

    private fun setProgramContentData() {
        mViewModel.programContentList.let {
            if (it.isEmpty()) {
                mBinding.rvProgramList.gone()
                mBinding.tvNoContent.visible()
            } else {
                mBinding.rvProgramList.visible()
                mBinding.tvNoContent.gone()
            }

            if (it.isNotEmpty()) {
                setProgramNextSession(
                    data?.title ?: "",
                    data?.daysList?.get(0)?.programDate
                        ?: "",
                    mViewModel.programContentList[0].combinedDate
                )

                mProgramsDayWiseListAdapter.updateList(
                    it,
                    isProgramJoined
                )
                mBinding.ShimmerProgramContent.hideShimmer()
                mBinding.ShimmerProgramContent.gone()
                mBinding.rvProgramList.visible()
            }
        }
    }
}