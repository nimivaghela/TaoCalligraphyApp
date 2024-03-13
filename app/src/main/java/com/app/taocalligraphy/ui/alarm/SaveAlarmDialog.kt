package com.app.taocalligraphy.ui.alarm

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogSaveAlarmBinding
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.google.android.material.bottomsheet.BottomSheetDialog

class SaveAlarmDialog(
    context: Context,
    onSaveAlarmListener: OnSaveAlarmListener
) : BottomSheetDialog(context, R.style.FullScreenBottomSheetDialogTheme) {

    var contextLang: Context = context

    init {
        val itemBindingUtil =
            DialogSaveAlarmBinding.inflate(
                LayoutInflater.from(contextLang),
                null, false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)

        itemBindingUtil.alarmLeave.clickWithDebounce {
            onSaveAlarmListener.onSaveAlarmClick(false)
            dismiss()
        }

        itemBindingUtil.alarmSaveChanges.clickWithDebounce {
            onSaveAlarmListener.onSaveAlarmClick(true)
            dismiss()
        }
    }
}

interface OnSaveAlarmListener {
    fun onSaveAlarmClick(isSave: Boolean)
}