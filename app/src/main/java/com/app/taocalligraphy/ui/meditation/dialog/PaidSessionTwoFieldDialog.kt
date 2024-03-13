package com.app.taocalligraphy.ui.meditation.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogPurchaseAudioBinding
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialog

class PaidSessionTwoFieldDialog(
    context: Context,
    var isUnlockWithHearts: Boolean,
    var payAmount: MeditationContentResponse.Currencies?,
    var payWithHeart: Int,
    var onPayAmountListener: OnPayAmountListener,
    var onPayHeartListener: OnPayHeartListener
) :
    BottomSheetDialog(context, R.style.FullScreenBottomSheetDialogTheme) {
    private var contextLang: Context = context

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogPurchaseAudioBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)
        if (isUnlockWithHearts) {
            itemBindingUtil.tvPayHeart.visible()
            itemBindingUtil.tvPayAmount.gone()
        } else {
            itemBindingUtil.tvPayHeart.gone()
            itemBindingUtil.tvPayAmount.visible()
        }

        payAmount?.let { currencyData ->
            itemBindingUtil.tvPayAmount.text =
                currencyData.symbol + currencyData.price + " " + currencyData.value
        }

        itemBindingUtil.tvPayHeart.text =
            context.getString(R.string.unlock_instantly) + payWithHeart
    }

    private fun setClickListener(itemBindingUtil: DialogPurchaseAudioBinding) {
        itemBindingUtil.tvPayAmount.setOnClickListener {
            onPayAmountListener.onPayAmountClicked()
            dismiss()
        }

        itemBindingUtil.tvPayHeart.setOnClickListener {
            onPayHeartListener.onPayHeartClicked()
            dismiss()
        }
    }

    interface OnPayAmountListener {
        fun onPayAmountClicked()
    }

    interface OnPayHeartListener {
        fun onPayHeartClicked()
    }
}