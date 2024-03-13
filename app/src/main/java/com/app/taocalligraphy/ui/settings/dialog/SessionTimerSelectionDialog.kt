package com.app.taocalligraphy.ui.settings.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogMeditationTimerBinding
import com.app.taocalligraphy.models.MeditationTimerModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class SessionTimerSelectionDialog(
    context: Context,
    var arrayList: ArrayList<MeditationTimerModel>,
    timerSelectionListener: TimerSelectionListener,
    var boolean: Boolean
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    var contextLang: Context = context
    val listener: TimerSelectionListener = timerSelectionListener
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

        arrayList.forEachIndexed { index, data ->
            dest[index] = data.title
        }

        positionSelected = arrayList.indexOfFirst { it.isSelected }

        itemBindingUtil.numberPickerTimer.wrapSelectorWheel = boolean
        itemBindingUtil.numberPickerTimer.minValue = 0
        itemBindingUtil.numberPickerTimer.maxValue = dest.size - 1
        itemBindingUtil.numberPickerTimer.displayedValues = dest
        itemBindingUtil.numberPickerTimer.value = positionSelected
        itemBindingUtil.numberPickerTimer.setOnValueChangedListener { picker, oldVal, newVal ->
            itemBindingUtil.numberPickerTimer.value = newVal
        }

        itemBindingUtil.btnDone.setOnClickListener {
            listener.onTimerSelect(arrayList[itemBindingUtil.numberPickerTimer.value])
            dismiss()
        }
    }

    private fun setClickListener() {
    }

    interface TimerSelectionListener {
        fun onTimerSelect(data: MeditationTimerModel)
    }
}