package com.app.taocalligraphy.ui.settings.dialog

import android.content.DialogInterface
import android.text.TextUtils
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogSendFeedbackBinding
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.longToast

class SendFeedbackDialog : BaseDialogFragment<DialogSendFeedbackBinding>(){

    companion object {
        const val TAG = "SendFeedbackDialog"
        var feedbackNature = ""
        var selectedNature = ""
        private lateinit var chooseSendFeedbackListener: ChooseSendFeedbackListener
        fun newInstance(
            listener: ChooseSendFeedbackListener,
        ) :SendFeedbackDialog {
            feedbackNature = ""
            selectedNature = ""
            this.chooseSendFeedbackListener = listener
            return SendFeedbackDialog()
        }
    }

    private fun setTextViewOnSessionAccessTypeChange(
        text: String,
        activeSession: AppCompatTextView,
        inActiveSession: AppCompatTextView,
        itemBindingUtil: DialogSendFeedbackBinding
    ) {
        selectedNature = text
        feedbackNature =
            if (text.equals(TaoCalligraphy.instance.getString(R.string.suggestion_for_improvement), true)) {
                "SuggestionForImprovement"
            } else if (text.equals(TaoCalligraphy.instance.getString(R.string.compliment), true)) {
                "Compliment"
            } else {
                ""
            }
        itemBindingUtil.tvSelectedSessionAccess.text = text
        activeSession.setTextColor(ContextCompat.getColor(TaoCalligraphy.instance, R.color.gold))
        inActiveSession.setTextColor(ContextCompat.getColor(TaoCalligraphy.instance, R.color.medium_grey))
    }

    private fun animateSessionAccess(itemBindingUtil: DialogSendFeedbackBinding) {
        if (itemBindingUtil.llAccessType.isVisible) {
            itemBindingUtil.llAccessType.animate().also {
                it.withEndAction {
                    itemBindingUtil.llAccessType.isGone = true
                    itemBindingUtil.llAccessHeader.isVisible = true
                }
                it.duration = 200
                it.start()
            }
            itemBindingUtil.ivSessionAccessArrow.animate().rotation(0.0f).setDuration(200).start()
        } else {
            itemBindingUtil.llAccessType.animate().also {
                it.withEndAction {
                    itemBindingUtil.llAccessHeader.isGone = true
                    itemBindingUtil.llAccessType.isVisible = true
                }
                it.duration = 200
                it.start()
            }
            itemBindingUtil.ivSessionAccessArrow.animate().rotation(180.0f).setDuration(200).start()
        }
    }

    interface ChooseSendFeedbackListener {
        fun onSendFeedback(feedbackNature: String, message: String)
    }

    override fun getResource(): Int = R.layout.dialog_send_feedback

    override fun postInit() {

        mBinding.llAccessType.isVisible = false

        setTextViewOnSessionAccessTypeChange(
            selectedNature,
            mBinding.tvPublicSession,
            mBinding.tvPrivateSession,
            mBinding
        )

    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.rrAccessHeader.setOnClickListener {
            animateSessionAccess(mBinding)
        }

        mBinding.tvPublicSession.setOnClickListener {
            animateSessionAccess(mBinding)
            setTextViewOnSessionAccessTypeChange(
                TaoCalligraphy.instance.getString(R.string.suggestion_for_improvement),
                mBinding.tvPublicSession,
                mBinding.tvPrivateSession,
                mBinding
            )
        }

        mBinding.tvPrivateSession.setOnClickListener {
            animateSessionAccess(mBinding)
            setTextViewOnSessionAccessTypeChange(
                TaoCalligraphy.instance.getString(R.string.compliment),
                mBinding.tvPrivateSession,
                mBinding.tvPublicSession,
                mBinding
            )
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.btnSend.clickWithDebounce {
            if (TextUtils.isEmpty(feedbackNature.trim())) {
                activity?.longToast(TaoCalligraphy.instance.getString(R.string.please_select_nature_of_feedback), Constants.ERROR)
            } else if (TextUtils.isEmpty(mBinding.edMessage.text.toString().trim())) {
                activity?.longToast(TaoCalligraphy.instance.getString(R.string.please_enter_message), Constants.ERROR)
            } else {
                hideKeyboard()
                dismiss()
                chooseSendFeedbackListener.onSendFeedback(
                    feedbackNature,
                    mBinding.edMessage.text.toString()
                )
            }
        }

    }

    override fun displayMessage(message: String) {

    }
}