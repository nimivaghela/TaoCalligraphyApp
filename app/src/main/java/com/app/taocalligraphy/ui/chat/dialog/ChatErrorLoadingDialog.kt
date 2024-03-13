package com.app.taocalligraphy.ui.chat.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.NonNull
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogCancelSessionBinding
import com.app.taocalligraphy.databinding.DialogChatErrorLoadingBinding
import com.app.taocalligraphy.databinding.DialogChatNoInternetConnectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChatErrorLoadingDialog : BottomSheetDialog {

    var contextLang: Context

    constructor(
        context: Context
    ) : super(context, R.style.CustomBottomSheetDialogTheme) {
        contextLang = context
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogChatErrorLoadingBinding.inflate(
                LayoutInflater.from(contextLang),
                null,
                false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(false)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil: DialogChatErrorLoadingBinding) {
        itemBindingUtil.btnRetry.setOnClickListener {
            dismiss()
        }
    }
}