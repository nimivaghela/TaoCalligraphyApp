package com.app.taocalligraphy.ui.field_healing_detail.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogBoostYourHealingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BoostYourHealingDialog : BottomSheetDialog {

    var contextLang: Context = context

    constructor(
        context: Context
    ) : super(context, R.style.CustomBottomSheetDialogTheme) {
        initView()
    }

    private fun initView() {
        val itemBindingUtil = DialogBoostYourHealingBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(false)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil: DialogBoostYourHealingBinding) {
        itemBindingUtil.btnTellMeMore.setOnClickListener {
            dismiss()
        }
        itemBindingUtil.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}