package com.app.taocalligraphy.ui.chat.dialog

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogChatNoInternetConnectionBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChatNoInternetConnectionDialog : BottomSheetDialog {

    private var internetReconnectListener: InternetReconnectListener
    var contextLang: Context?=null
    var itemBindingUtil:
            DialogChatNoInternetConnectionBinding? = null
    var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    constructor(
        context: Context,
        internetReconnectListener: InternetReconnectListener
    ) : super(context, R.style.CustomBottomSheetDialogTheme) {
        contextLang = context
        this.internetReconnectListener = internetReconnectListener
        initView()
    }

    override fun onStart() {
        super.onStart()
        val orientation = contextLang?.resources?.configuration?.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //this expands the bottom sheet even after a config change
            bottomSheetBehavior = BottomSheetBehavior.from<View>(itemBindingUtil?.root?.parent as View)
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initView() {
        itemBindingUtil =
            DialogChatNoInternetConnectionBinding.inflate(
                LayoutInflater.from(contextLang),
                null,
                false
            )
        setContentView(itemBindingUtil!!.root)
        setCanceledOnTouchOutside(false)
        setClickListener(itemBindingUtil)
    }

    private fun setClickListener(itemBindingUtil: DialogChatNoInternetConnectionBinding?) {
        itemBindingUtil?.btnReconnectNow?.setOnClickListener {
            dismiss()
            internetReconnectListener.onReconnectClick()
        }
    }

    interface InternetReconnectListener {
        fun onReconnectClick()
    }
}