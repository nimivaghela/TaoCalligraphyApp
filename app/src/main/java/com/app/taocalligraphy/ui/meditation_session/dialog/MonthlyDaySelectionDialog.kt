package com.app.taocalligraphy.ui.meditation_session.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogMonthlyDaySelectionBinding
import com.app.taocalligraphy.databinding.ItemWeeklyRepeatListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MonthlyDaySelectionDialog : BottomSheetDialog {

    var contextLang: Context
    val listener: DaySelectionListener
    val selectedDays: String

    constructor(
        context: Context,
        selectedDays: String,
        moreListener: DaySelectionListener,
    ) : super(context, R.style.CustomBottomSheetDialogTheme) {
        contextLang = context
        this.listener = moreListener
        this.selectedDays = selectedDays
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogMonthlyDaySelectionBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener()

        itemBindingUtil.numberPickerDays.minValue = 0
        itemBindingUtil.numberPickerDays.maxValue = 30

        var daysData = Array<String>(31, { "" })
        for (i in 0 until 31) {
            daysData[i] = (i + 1).toString()
        }
        itemBindingUtil.numberPickerDays.displayedValues = daysData
        itemBindingUtil.numberPickerDays.value = selectedDays.toInt() - 1

        itemBindingUtil.btnDone.setOnClickListener {
            listener.onDaySelect(daysData[itemBindingUtil.numberPickerDays.value])
            dismiss()
        }
    }

    private fun setClickListener() {
    }

    interface DaySelectionListener {
        fun onDaySelect(days: String)
    }
}