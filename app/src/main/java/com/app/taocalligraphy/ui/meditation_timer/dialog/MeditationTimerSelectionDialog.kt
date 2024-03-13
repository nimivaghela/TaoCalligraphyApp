package com.app.taocalligraphy.ui.meditation_timer.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogMeditationTimerBinding
import com.app.taocalligraphy.models.MeditationTimerModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class MeditationTimerSelectionDialog(
    context: Context,
    var arrayList: ArrayList<MeditationTimerModel>,
    timerSelectionListener: TimerSelectionListener
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    var contextLang: Context = context
    val listener: TimerSelectionListener = timerSelectionListener
    var positionSelected=1

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
            dest[i] = arrayList[i].title
            if (arrayList[i].isSelected)
            positionSelected=i
        }
        itemBindingUtil.numberPickerTimer.minValue = 0
        itemBindingUtil.numberPickerTimer.maxValue = dest.size - 1
        itemBindingUtil.numberPickerTimer.displayedValues =dest
        itemBindingUtil.numberPickerTimer.value =positionSelected
        itemBindingUtil.numberPickerTimer.setOnValueChangedListener { picker, oldVal, newVal ->
            itemBindingUtil.numberPickerTimer.value = newVal
        }

        itemBindingUtil.btnDone.setOnClickListener {
            for (i in arrayList.indices){
                arrayList[itemBindingUtil.numberPickerTimer.value].isSelected = i == itemBindingUtil.numberPickerTimer.value
            }
            listener.onTimerSelect(arrayList[itemBindingUtil.numberPickerTimer.value])
            dismiss()
        }
    }

    private fun setClickListener() {
    }

    interface TimerSelectionListener {
        fun onTimerSelect(meditationTimerModel: MeditationTimerModel)
    }
}