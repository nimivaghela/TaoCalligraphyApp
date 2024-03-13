package com.app.taocalligraphy.ui.notification.dialog

import android.os.Bundle
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogClearAllNotificationBinding


class ClearAllNotificationConfirmDialog :
    BaseDialogFragment<DialogClearAllNotificationBinding>() {


    companion object {
        const val TAG = "clearAllNotificationConfirmDialogTag"
        var notificationClearListener: NotificationClearListener? = null
        fun newInstance(notificationClearListener: NotificationClearListener): ClearAllNotificationConfirmDialog {
            this.notificationClearListener = notificationClearListener
            return ClearAllNotificationConfirmDialog()
        }
    }

    override fun getResource(): Int {
        return R.layout.dialog_clear_all_notification
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
        mBinding.btnYes.setOnClickListener {
            notificationClearListener?.onClear()
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

    interface NotificationClearListener {
        fun onClear()
    }
}