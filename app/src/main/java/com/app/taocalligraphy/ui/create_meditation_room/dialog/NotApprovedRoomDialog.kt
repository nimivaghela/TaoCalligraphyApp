package com.app.taocalligraphy.ui.create_meditation_room.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogNotApprovedRoomBinding
import com.app.taocalligraphy.databinding.DialogPendingApprovalRoomBinding
import com.app.taocalligraphy.ui.create_meditation_room.CreateMeditationRoomActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class NotApprovedRoomDialog(context: Context) :
    BottomSheetDialog(context, R.style.FullScreenBottomSheetDialogTheme) {

    var contextLang: Context = context

    init {
        initView()
    }

    private fun initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window?.statusBarColor = context.getColor(R.color.white_80)
        val itemBindingUtil =
            DialogNotApprovedRoomBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil: DialogNotApprovedRoomBinding) {
        itemBindingUtil.btnEditRoom.setOnClickListener {
            dismiss()
        }

        itemBindingUtil.ivClose.setOnClickListener {
            dismiss()
        }
    }
}