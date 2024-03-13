package com.app.taocalligraphy.ui.calendar.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogPaidSessionBookingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class PaidSessionBookingDialog(context: Context) :
    BottomSheetDialog(context, R.style.FullScreenBottomSheetDialogTheme) {

    private var contextLang: Context = context

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogPaidSessionBookingBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil: DialogPaidSessionBookingBinding) {
        itemBindingUtil.ivClose.setOnClickListener {
            dismiss()
        }

        itemBindingUtil.btnCancelSession.setOnClickListener {
            dismiss()
        }
    }
}