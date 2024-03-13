package com.app.taocalligraphy.ui.create_meditation_room.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogUnlockNewBackgroundBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class UnlockNewBackgroundDialog(context: Context) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    var contextLang: Context = context

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil = DialogUnlockNewBackgroundBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil: DialogUnlockNewBackgroundBinding) {
        itemBindingUtil.btnUnlock.setOnClickListener {
            dismiss()
        }
    }
}