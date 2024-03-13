package com.app.taocalligraphy.ui.meditation_session.dialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogLeaveSessionBinding
import com.app.taocalligraphy.ui.meditation_session.HostEndSessionActivity
import com.app.taocalligraphy.ui.meditation_session.MeditationSessionFeedbackActivity


class LeaveEndSessionDialog :
    BaseDialogFragment<DialogLeaveSessionBinding>() {


    companion object {
        const val TAG = "leaveEndSessionDialogTag"
        var isLeaveSession: Boolean = false
        fun newInstance(isLeaveSession: Boolean): LeaveEndSessionDialog {
            this.isLeaveSession = isLeaveSession
            return LeaveEndSessionDialog()
        }
    }

    override fun getResource(): Int {
        return R.layout.dialog_leave_session
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFullScreen = true
    }

    override fun postInit() {
        if (isLeaveSession) {
            mBinding.tvSessionReason.text = getString(R.string.leave_session)
        } else {
            mBinding.tvSessionReason.text = getString(R.string.end_session)
        }
    }

    override fun initObserver() {
    }

    override fun handleListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.btnYes.setOnClickListener {
            if (isLeaveSession) {
                MeditationSessionFeedbackActivity.startActivity(requireActivity() as AppCompatActivity)
            } else {
                HostEndSessionActivity.startActivity(requireActivity() as AppCompatActivity)
            }
            dismiss()
        }

        mBinding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    override fun displayMessage(message: String) {
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }
}