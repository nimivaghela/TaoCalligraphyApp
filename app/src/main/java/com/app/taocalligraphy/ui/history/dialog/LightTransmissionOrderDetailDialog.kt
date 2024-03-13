package com.app.taocalligraphy.ui.history.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogLightTransmissionOrderDetailBinding
import com.app.taocalligraphy.databinding.DialogPaidSessionBookingBinding
import com.app.taocalligraphy.ui.history.adapter.LightTransmissionsOrderItemAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class LightTransmissionOrderDetailDialog(context: Context) :
    BottomSheetDialog(context, R.style.FullScreenBottomSheetDialogTheme) {

    private var contextLang: Context = context

    private val lightTransmissionsOrderItemAdapter by lazy {
        return@lazy LightTransmissionsOrderItemAdapter()
    }

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogLightTransmissionOrderDetailBinding.inflate(
                LayoutInflater.from(contextLang),
                null,
                false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)
        itemBindingUtil.rvItems.adapter = lightTransmissionsOrderItemAdapter
    }

    private fun setClickListener(itemBindingUtil: DialogLightTransmissionOrderDetailBinding) {
        itemBindingUtil.ivClose.setOnClickListener {
            dismiss()
        }
    }
}