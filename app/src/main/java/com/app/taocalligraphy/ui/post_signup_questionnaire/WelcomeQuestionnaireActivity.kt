package com.app.taocalligraphy.ui.post_signup_questionnaire

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityWelcomeQuestionnaireBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.question_data_models.answer_data_model.QuestionnaireAnswerResponse
import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.KeywordsDataModel
import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.QuestionDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.post_signup_questionnaire.adapter.SelectSingleAdapter
import com.app.taocalligraphy.ui.post_signup_questionnaire.viewmodel.QuestionnarieViewModel
import com.app.taocalligraphy.ui.questionary.QuestionnairesResultActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class WelcomeQuestionnaireActivity : BaseActivity<ActivityWelcomeQuestionnaireBinding>(),
    SelectSingleAdapter.SingleSelectItemClickListener {

    private val mViewModel: QuestionnarieViewModel by viewModels()
    private var doubleBackToExitPressedOnce = false
    private val questionDataListForLocal: ArrayList<QuestionDataModel?> = arrayListOf()
    private var answerResponse: QuestionnaireAnswerResponse? = null
    private var ageRange: String = ""
    private var isBackClicked: Boolean = false
    private var gender: String = ""
    private var deletedKeywordsIds: ArrayList<Int> = arrayListOf()
    private var keywordsIds: ArrayList<Int> = arrayListOf()
    private var keywordsIdsSelectedOrNot: ArrayList<Int> = arrayListOf()
    private lateinit var questionDataModel: QuestionDataModel
    private val ANIMATION_DURATION = 300L


    companion object {
        private val questionDataList: ArrayList<QuestionDataModel?> = arrayListOf()
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, WelcomeQuestionnaireActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    private val selectSingleAdapter by lazy {
        return@lazy SelectSingleAdapter(
            arrayListOf(),
            this@WelcomeQuestionnaireActivity
        )
    }

    override fun getResource() = R.layout.activity_welcome_questionnaire

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.rvOne.adapter = selectSingleAdapter
        if (isTablet(this)) {
            mBinding.ivWelcomeText.text =
                getString(R.string.welcome_with_comma) + " " + userHolder.firstName + "!"
            if (isTabletLandScape(this)) {
                mViewModel.mCloudAnimRange = 50f
                mViewModel.mBirdAnimRange = 30f
            } else {
                mViewModel.mCloudAnimRange = 100f
                mViewModel.mBirdAnimRange = 40f
            }
        } else {
            mBinding.ivWelcomeText.text =
                getString(R.string.welcome_with_comma) + "\n" + userHolder.firstName + "!"
            mViewModel.mCloudAnimRange = 80f
            mViewModel.mBirdAnimRange = 30f
        }

        if (mViewModel.mQuestionCount == 0) {
            mViewModel.mQuestionCount++
            mViewModel.getQuestionListData(
                this@WelcomeQuestionnaireActivity,
                mDisposable
            )
        } else {
            setQuestionsView()
            if (mViewModel.mQuestionCount != 0) {
                val lastAttemptedQuestion = mViewModel.mQuestionCount - 2
                mViewModel.mCloudAnimValue =
                    (mViewModel.mCloudAnimRange.toInt() * lastAttemptedQuestion).toFloat()
                mViewModel.mBirdAnimValue =
                    (0 - mViewModel.mBirdAnimRange.toInt() * lastAttemptedQuestion).toFloat()
                animateCloud(true)
                animateBird(true)
            }
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.getQuestionListDataResponse.observe(this) { response ->
            response?.let { requestState ->
                mBinding.btnPersonalize.isEnabled = !requestState.progress
                requestState.apiResponse?.let {
                    it.data?.let { fetchQuestionListResponse ->
                        mViewModel.getQuestionListDataResponse.value?.apiResponse = null
                        fetchQuestionListResponse.lastAttemptedQuestionOrderNo?.let { lastAttemptedQuestion ->
                            mViewModel.mQuestionCount = if (lastAttemptedQuestion != "0") {
                                lastAttemptedQuestion.toInt() + 2
                            } else {
                                1
                            }
                            if (lastAttemptedQuestion != "0") {
                                mViewModel.mCloudAnimValue =
                                    (mViewModel.mCloudAnimRange.toInt() * lastAttemptedQuestion.toInt()).toFloat()
                                mViewModel.mBirdAnimValue =
                                    (0 - mViewModel.mBirdAnimRange.toInt() * lastAttemptedQuestion.toInt()).toFloat()
                                animateCloud(true)
                                animateBird(true)
                            }

                        }

                        fetchQuestionListResponse.list?.let { dataList ->
                            if (dataList.size > 0) {
                                questionDataList.clear()
                                questionDataListForLocal.clear()
                                questionDataList.addAll(dataList)
                                questionDataListForLocal.addAll(dataList)
                                setQuestionsView()
                            }
                        }
                    }
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.giveAnswerDataResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    mViewModel.giveAnswerDataResponse.value?.apiResponse = null
                    answerResponse = it.data

                    if (it.data != null) {
                        if (it.data?.orderNo == "8") {
                            userHolder.isQuestionnaireCompleted = true
                            QuestionnairesResultActivity.startActivity(this@WelcomeQuestionnaireActivity)
                            finishAffinity()
                        } else {
                            mViewModel.mQuestionCount += 1

                            mViewModel.getQuestionListData(
                                this@WelcomeQuestionnaireActivity,
                                mDisposable
                            )
                            animateCloud(true)
                            animateBird(true)
                        }
                    }
                }
                longToastState(requestState.error)

            }
        }
    }

    override fun handleListener() {
        mBinding.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            when (scrollY) {
                0 -> {
                    if (mViewModel.mQuestionCount == 1)
                        mBinding.ivBgWhite.gone()
                    else
                        mBinding.ivBgWhite.visible()
                }
                (v.getChildAt(0).measuredHeight - v.measuredHeight) -> {
                    mBinding.ivBgWhite.gone()
                }
                else -> {
                    if (mViewModel.mQuestionCount == 1)
                        mBinding.ivBgWhite.gone()
                    else
                        mBinding.ivBgWhite.visible()
                }
            }
        })

        mBinding.btnPersonalize.setOnClickListener {
            mBinding.ivBgWhite.visible()
            mViewModel.mQuestionCount += 1
            setQuestionsView()
            animateCloud(true)
            animateBird(true)
        }

        mBinding.btnNext.setOnClickListener {
            val data: QuestionDataModel? = questionDataList[mViewModel.mQuestionCount - 2]
            callSubmitAnswerAPI(data!!)
        }

        mBinding.btnBack.setOnClickListener {
            isBackClicked = true
            if (mViewModel.mQuestionCount == 1) {
                onBackPressed()
            } else {
                mViewModel.mQuestionCount -= 1
                setQuestionsView()
                animateCloud(false)
                animateBird(false)
            }
        }
    }

    private fun callSubmitAnswerAPI(data: QuestionDataModel) {
        getSelectedAndUnSelectedAnswerListData(data)

        if (data.isMandatory == true) {
            if (data.orderNo == "7") {
                gender = ""
                if (ageRange.isNotEmpty()) {
                    submitAnswerAPI(data)
                } else {
                    longToast(getString(R.string.please_select_your_answer), Constants.ERROR)
                }

            } else if (data.orderNo == "8") {
                ageRange = ""
                if (gender.isNotEmpty()) {
                    submitAnswerAPI(data)
                } else {
                    longToast(getString(R.string.please_select_your_answer), Constants.ERROR)
                }
            } else if (data.checkAnswerSelectedOrNot.isNotEmpty()) {
                ageRange = ""
                gender = ""
                submitAnswerAPI(data)
            } else {
                longToast(getString(R.string.please_select_your_answer), Constants.ERROR)
            }
        } else {
            when (data.orderNo) {
                "7" -> {
                    gender = ""
                    submitAnswerAPI(data)
                }
                "8" -> {
                    ageRange = ""
                    submitAnswerAPI(data)
                }
                else -> {
                    ageRange = ""
                    gender = ""
                    submitAnswerAPI(data)
                }
            }
        }
    }

    private fun getSelectedAndUnSelectedAnswerListData(data: QuestionDataModel) {
        val selectedKeywordIdList = data.keywords?.filter {
            it?.isSelectedLocalParam == true
        }?.map {
            it?.keywordId
        }

        keywordsIds.clear()
        keywordsIds.addAll(selectedKeywordIdList as ArrayList<Int>)
        data.selectAnswerList = keywordsIds

        val checkAnswerSelectedOrNotList = data.keywords?.filter {
            it?.isSelected == true
        }?.map {
            it?.keywordId
        }

        keywordsIdsSelectedOrNot.clear()
        keywordsIdsSelectedOrNot.addAll(checkAnswerSelectedOrNotList as ArrayList<Int>)
        data.checkAnswerSelectedOrNot = keywordsIdsSelectedOrNot

        val unSelectedKeywordIdList = data.keywords?.filter {
            it?.isSelectedLocalParam == false
        }?.map {
            it?.keywordId
        }

        deletedKeywordsIds.clear()
        deletedKeywordsIds.addAll(unSelectedKeywordIdList as ArrayList<Int>)

        ageRange = data.keywords?.find {
            it?.isSelectedLocalParam == true
        }?.name ?: ""

        gender = data.keywords?.find {
            it?.isSelectedLocalParam == true
        }?.name ?: ""
    }

    private fun submitAnswerAPI(data: QuestionDataModel) {
        if (data.canSelectMultipleOption == true) {
            data.unSelectAnswerList = deletedKeywordsIds
        } else {
            deletedKeywordsIds = arrayListOf()
            data.unSelectAnswerList = deletedKeywordsIds
        }

        mViewModel.giveAnswerToQuestionAPI(
            this@WelcomeQuestionnaireActivity,
            mDisposable,
            data.questionId!!.toInt(),
            data.orderNo!!.toInt(),
            ageRange,
            gender,
            data.unSelectAnswerList,
            data.selectAnswerList
        )
    }

    private fun animateCloud(isNeedToAdd: Boolean) {
        if (isNeedToAdd) {
            mViewModel.mCloudAnimValue += mViewModel.mCloudAnimRange
        } else {
            mViewModel.mCloudAnimValue -= mViewModel.mCloudAnimRange
        }
        ObjectAnimator.ofFloat(mBinding.rlCloud, "translationX", mViewModel.mCloudAnimValue).apply {
            duration = 1000
            start()
        }
    }

    private fun animateBird(isNeedToAdd: Boolean) {
        if (isNeedToAdd) {
            mViewModel.mBirdAnimValue -= mViewModel.mBirdAnimRange
        } else {
            mViewModel.mBirdAnimValue += mViewModel.mBirdAnimRange
        }
        ObjectAnimator.ofFloat(mBinding.ivBirds, "translationX", mViewModel.mBirdAnimValue).apply {
            duration = 1000
            start()
        }
    }


    private fun animateRightSideShow(view: View, anotherView: View) {
        val animation =
            AnimationUtils.loadAnimation(this, R.anim.fade_out)
        animation.duration = ANIMATION_DURATION
        view.startAnimation(animation)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        animation2.duration = ANIMATION_DURATION
        anotherView.startAnimation(animation2)
    }

    private fun setQuestionsView() {
        when (mViewModel.mQuestionCount) {
            1 -> {
                mBinding.chipGroup.gone()

                mBinding.lottieLines.visibility = View.VISIBLE
                mBinding.ivClouds1.setBackgroundResource(R.drawable.ic_blue_clouds_1)
                mBinding.ivBirds.setBackgroundResource(R.drawable.ic_flying_birds_1)

                mBinding.tvQuestion.text = ""
                mBinding.tvQuestion.gone()
                mBinding.ivWelcomeText.visible()
                mBinding.ivMascot.visible()
                mBinding.ivMascot.setAnimation("mascot_forms_and_greets_user.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = 0

                mBinding.ivMascot.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        mBinding.ivMascot.removeAllAnimatorListeners()
                        mBinding.ivMascot.setAnimation("mascot_standing_idle_loop.json")
                        mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE
                        mBinding.ivMascot.playAnimation()
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationRepeat(p0: Animator?) {

                    }
                })
                if (mViewModel.mQuestionCount == 0) {
                    mBinding.rvOne.gone()
                    mBinding.chipGroup.gone()
                    mBinding.llWelcome1.visible()
                    mBinding.ivBgWhite.gone()
                } else {
//                    animateRightSideShow(mBinding.rvOne, mBinding.llWelcome1)
                }

                mBinding.rvOne.gone()
                mBinding.llWelcome1.visible()
                mBinding.btnPersonalize.visible()
                mBinding.ivBgWhite.gone()
                mBinding.btnNext.text = getString(R.string.next)
                mBinding.llBackNextFinishButtons.gone()

            }
            2 -> {
                mBinding.ivMascot.setAnimation("mascot_standing_idle_loop.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE

                questionDataModel = questionDataList[0]!!
                setQuestionDataHideShow()
                mBinding.tvQuestion.text = questionDataModel.question
                getSelectedListData(questionDataModel)
                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, true)
            }
            3 -> {
                mBinding.ivMascot.setAnimation("mascot_standing_to_levitating.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = 0
                questionDataModel = questionDataList[1]!!
                setQuestionDataHideShow()
                mBinding.tvQuestion.text = questionDataList[1]!!.question
                getSelectedListData(questionDataModel)
                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, false)
                mBinding.ivMascot.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        mBinding.ivMascot.removeAllAnimatorListeners()
                        mBinding.ivMascot.setAnimation("mascot_levitating_loop_eyes_closed.json")
                        mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE
                        mBinding.ivMascot.playAnimation()
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationRepeat(p0: Animator?) {

                    }
                })
            }
            4 -> {
                mBinding.ivMascot.setAnimation("mascot_levitating_loop_eyes_closed.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE

                questionDataModel = questionDataList[2]!!
                setQuestionDataHideShow()

                mBinding.tvQuestion.text = questionDataList[2]!!.question
                getSelectedListData(questionDataModel)
                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, true)

            }
            5 -> {
                mBinding.ivMascot.setAnimation("mascot_levitating_loop_eyes_closed.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE
                questionDataModel = questionDataList[3]!!

                setQuestionDataHideShow()
                getSelectedListData(questionDataModel)
                mBinding.tvQuestion.text = questionDataList[3]!!.question
                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, true)

            }
            6 -> {
                mBinding.ivMascot.setAnimation("mascot_levitating_loop_eyes_closed.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE
                questionDataModel = questionDataList[4]!!

                setQuestionDataHideShow()
                mBinding.tvQuestion.text = questionDataList[4]!!.question
                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, true)
                getSelectedListData(questionDataModel)
            }
            7 -> {
                mBinding.ivMascot.setAnimation("mascot_levitating_loop_eyes_closed.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE

                questionDataModel = questionDataList[5]!!
                setQuestionDataHideShow()
                mBinding.tvQuestion.text = questionDataList[5]!!.question
                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, true)
                getSelectedListData(questionDataModel)
            }
            8 -> {
                mBinding.ivMascot.setAnimation("mascot_levitating_loop_eyes_closed.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE

                questionDataModel = questionDataList[6]!!
                setQuestionDataHideShow()
                mBinding.tvQuestion.text = questionDataList[6]!!.question

                ageRange = ""

                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, false)
            }
            9 -> {
                mBinding.ivMascot.setAnimation("mascot_levitating_loop_eyes_closed.json")
                mBinding.ivMascot.progress = 1f
                mBinding.ivMascot.playAnimation()
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE

                gender = ""

                questionDataModel = questionDataList[7]!!
                val selectedItemPosition: Int = questionDataModel.keywords!!.indexOfFirst {
                    it?.isSelected!!
                }
                setQuestionDataHideShow()
                mBinding.tvQuestion.text = questionDataList[7]!!.question
                setAnswerDataAdapter(questionDataModel, selectedItemPosition, false)
            }
        }
    }

    private fun setChipGroupDataDynamically(questionDataModel: QuestionDataModel) {
        mBinding.chipGroup.removeAllViews()
        mBinding.chipGroup.isSingleSelection = questionDataModel.canSelectMultipleOption != true
        for (i in questionDataModel.keywords!!) {
            val chip: Chip =
                layoutInflater.inflate(R.layout.item_sample_chip, mBinding.chipGroup, false) as Chip
            chip.text = i?.name

            chip.isChecked = i?.isSelected == true
            chip.typeface = Typeface.create(
                ResourcesCompat.getFont(this, R.font.font_jost_regular),
                Typeface.NORMAL
            )

            chip.setOnClickListener {
                if (i?.isSelected == true) {
                    i.isSelected = false
                    i.isSelectedLocalParam = false
                } else {
                    if (questionDataModel.canSelectMultipleOption == false) {
                        questionDataModel.keywords?.map {
                            it?.isSelectedLocalParam = false
                            it?.isSelected = false
                        }
                    }
                    i?.isSelected = true
                    i?.isSelectedLocalParam = true
                }
            }
            val animation: Animation
            if (isBackClicked) {
                isBackClicked = false
                animation =
                    AnimationUtils.loadAnimation(this, R.anim.fade_in)
            } else {
                animation =
                    AnimationUtils.loadAnimation(this, R.anim.fade_in)
            }
            animation.duration = ANIMATION_DURATION
            chip.startAnimation(animation)
            mBinding.chipGroup.addView(chip)
        }
    }

    private fun getSelectedListData(questionDataModel: QuestionDataModel) {
        val selectedKeywordIdList = questionDataModel.keywords!!.filter {
            it?.isSelected == true
        }.map {
            it?.keywordId
        }
        questionDataModel.selectAnswerList = selectedKeywordIdList as ArrayList<Int>
    }

    private fun setQuestionDataHideShow() {
        keywordsIds.clear()
        deletedKeywordsIds.clear()

        mBinding.ivClouds1.setBackgroundResource(R.drawable.ic_blue_clouds_1)
        mBinding.ivBirds.setBackgroundResource(R.drawable.ic_flying_birds_1)

        if (questionDataModel.orderNo == "8")
            mBinding.btnNext.text = getString(R.string.finish)
        else
            mBinding.btnNext.text = getString(R.string.next)

        hideShowLottieLines()
        mBinding.ivWelcomeText.gone()

        mBinding.btnPersonalize.gone()
        mBinding.llWelcome1.gone()


        mBinding.tvQuestion.visible()
        mBinding.llBackNextFinishButtons.visible()

    }

    private fun hideShowLottieLines() {
//        hide lottie animation as per yash's suggestion
        /*if (isTabletLandScape(this)) {
            mBinding.lottieLines.visible()
        } else {
            mBinding.lottieLines.gone()
        }*/

        mBinding.lottieLines.gone()

        /* if (orientation == Configuration.ORIENTATION_PORTRAIT) {
             mBinding.lottieLines.gone()
         } else {
             mBinding.lottieLines.visible()
         }*/
    }

    private fun setAnswerDataAdapter(
        questionDataModel: QuestionDataModel,
        selectedItemPosition: Int,
        showChipGroup: Boolean
    ) {
        mBinding.llWelcome1.gone()
//        if (questionDataModel.canSelectMultipleOption == true) {
        if (showChipGroup) {
            mBinding.rvOne.gone()
            mBinding.chipGroup.visible()
            selectSingleAdapter.updateList(
                arrayListOf(),
                -1,
                questionDataModel.canSelectMultipleOption ?: false
            )
            setChipGroupDataDynamically(questionDataModel)
        } else {
//            val animation: Animation =
//            AnimationUtils.loadAnimation(this, R.anim.fade_in)
//            animation.duration = ANIMATION_DURATION
//            mBinding.rvOne.startAnimation(animation)
            selectSingleAdapter.updateList(
                questionDataModel.keywords,
                selectedItemPosition,
                questionDataModel.canSelectMultipleOption ?: false
            )
            mBinding.chipGroup.gone()
            mBinding.rvOne.visible()

        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (canScroll(mBinding.nestedScroll)) {
                if (mViewModel.mQuestionCount == 1)
                    mBinding.ivBgWhite.gone()
                else
                    mBinding.ivBgWhite.visible()
            } else {
                mBinding.ivBgWhite.gone()
            }

        }, 500)

    }

    private fun canScroll(scrollView: NestedScrollView): Boolean {
        val child = scrollView.getChildAt(0) as View
        if (child != null) {
            val childHeight = child.height
            return scrollView.height < childHeight + scrollView.paddingTop + scrollView.paddingBottom
        }
        return false
    }


    override fun onSingleItemClick(
        keywordsDataModel: KeywordsDataModel,
        canSelectMultipleOption: Boolean
    ) {
        when (questionDataModel.orderNo) {
            "7" -> {
                questionDataModel.keywords?.map {
                    it?.isSelectedLocalParam = false
                    it?.isSelected = false
                }
                ageRange = keywordsDataModel.name ?: ""
                questionDataModel.ageRange = keywordsDataModel.name ?: ""
                keywordsDataModel.isSelectedLocalParam = true
            }
            "8" -> {
                questionDataModel.keywords?.map {
                    it?.isSelectedLocalParam = false
                    it?.isSelected = false
                }
                gender = keywordsDataModel.name ?: ""
                questionDataModel.gender = keywordsDataModel.name ?: ""
                keywordsDataModel.isSelectedLocalParam = true
            }
            else -> {
                val data: KeywordsDataModel = keywordsDataModel
                if (canSelectMultipleOption) {
                    data.isSelectedLocalParam = data.isSelected
                } else {
                    questionDataModel.keywords?.map {
                        it?.isSelectedLocalParam = false
                        it?.isSelected = false
                    }
                    data.isSelectedLocalParam = true
                }
            }
        }
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if (mViewModel.mQuestionCount == 1) {
                super.onBackPressed()
            } else {
                mViewModel.mQuestionCount -= 1
                setQuestionsView()
                animateCloud(false)
                animateBird(false)
            }
            return
        }

        this.doubleBackToExitPressedOnce = true
        showToast(this, getString(R.string.press_again_to_exit))
        addDelay({ doubleBackToExitPressedOnce = false }, 2000)
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