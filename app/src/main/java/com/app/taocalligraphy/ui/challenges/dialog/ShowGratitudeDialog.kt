package com.app.taocalligraphy.ui.challenges.dialog

import android.os.Bundle
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogShowGratitudeBinding


class ShowGratitudeDialog() :
    BaseDialogFragment<DialogShowGratitudeBinding>() {


    companion object {
        const val TAG = "ShowGratitudeDialogTag"

        fun newInstance(): ShowGratitudeDialog {
            return ShowGratitudeDialog()
        }
    }

    override fun getResource(): Int {
        return R.layout.dialog_show_gratitude
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

        mBinding.btnYes.setOnClickListener {
            dismiss()
            showStepCompleteDialog()
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

    private fun showStepCompleteDialog() {
        val dialog = StepCompletionDialog.newInstance()
        dialog.show(
            getFragmentTransactionFromTag(StepCompletionDialog.TAG),
            StepCompletionDialog.TAG
        )
    }
}