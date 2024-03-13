package com.app.taocalligraphy.ui.profile_account_info.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogMeditationTimerBinding
import com.app.taocalligraphy.models.response.LoginResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.ArrayList

class AgeSelectionDialog(
    context: Context,
    var arrayList: ArrayList<LoginResponse.AgeRangeDetail>,
    ageSelectionListener: AgeSelectionListener
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    var contextLang: Context = context
    val listener: AgeSelectionListener = ageSelectionListener
    var positionSelected = 1

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogMeditationTimerBinding.inflate(
                LayoutInflater.from(contextLang),
                null, false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener()

        val dest: Array<String?> = arrayOfNulls(arrayList.size)
        for (i in arrayList.indices) {
            dest[i] = arrayList[i].ageRange
            if (arrayList[i].isSelected)
                positionSelected = i
        }
        itemBindingUtil.numberPickerTimer.minValue = 0
        itemBindingUtil.numberPickerTimer.maxValue = dest.size - 1
        itemBindingUtil.numberPickerTimer.displayedValues = dest
        itemBindingUtil.numberPickerTimer.value = positionSelected
        itemBindingUtil.numberPickerTimer.setOnValueChangedListener { picker, oldVal, newVal ->
            itemBindingUtil.numberPickerTimer.value = newVal
        }

        itemBindingUtil.btnDone.setOnClickListener {
            for (i in arrayList.indices) {
                arrayList[itemBindingUtil.numberPickerTimer.value].isSelected =
                    i == itemBindingUtil.numberPickerTimer.value
            }
            listener.onAgeSelect(itemBindingUtil.numberPickerTimer.value)
            dismiss()
        }
    }

    private fun setClickListener() {
    }

    interface AgeSelectionListener {
        fun onAgeSelect(index: Int)
    }
}