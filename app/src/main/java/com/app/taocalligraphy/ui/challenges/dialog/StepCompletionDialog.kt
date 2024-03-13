package com.app.taocalligraphy.ui.challenges.dialog

import android.os.Bundle
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogShowGratitudeBinding
import com.app.taocalligraphy.databinding.DialogStepCompletionBinding


class StepCompletionDialog() :
    BaseDialogFragment<DialogStepCompletionBinding>() {


    companion object {
        const val TAG = "StepCompletionDialogTag"

        fun newInstance(): StepCompletionDialog {
            return StepCompletionDialog()
        }
    }

    override fun getResource(): Int {
        return R.layout.dialog_step_completion
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFullScreen = true
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.btnClose.setOnClickListener {
            dismiss()
        }

        mBinding.btnCloseAndJournal.setOnClickListener {
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