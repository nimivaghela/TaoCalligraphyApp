package com.app.taocalligraphy.ui.create_meditation_room.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogPendingApprovalRoomBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class PendingApprovalRoomDialog(context: Context) :
    BottomSheetDialog(context, R.style.FullScreenBottomSheetDialogTheme) {

    var contextLang: Context = context

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogPendingApprovalRoomBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil: DialogPendingApprovalRoomBinding) {
        itemBindingUtil.btnBackToRoom.setOnClickListener {
            dismiss()
        }

        itemBindingUtil.ivClose.setOnClickListener {
            dismiss()
        }
    }
}