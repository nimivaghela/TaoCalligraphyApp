package com.app.taocalligraphy.ui.meditation_session.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.NonNull
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogCancelSessionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CancelSessionDialog : BottomSheetDialog {

    var contextLang: Context

    constructor(
        context: Context
    ) : super(context, R.style.CustomBottomSheetDialogTheme) {
        contextLang = context
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogCancelSessionBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil:  DialogCancelSessionBinding) {
        itemBindingUtil.btnCancelSession.setOnClickListener {
            dismiss()
        }

        itemBindingUtil.btnSkip.setOnClickListener {
            dismiss()
        }
    }
}