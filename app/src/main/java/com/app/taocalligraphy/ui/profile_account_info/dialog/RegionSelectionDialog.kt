package com.app.taocalligraphy.ui.profile_account_info.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogMeditationTimerBinding
import com.app.taocalligraphy.models.response.profile.RegionListApiResponse
import com.google.android.material.bottomsheet.BottomSheetDialog

class RegionSelectionDialog(
    context: Context,
    var arrayList: ArrayList<RegionListApiResponse.Data>,
    private val selectedPos: Int,
    regionSelectionListener: RegionSelectionListener
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    var contextLang: Context = context
    val listener: RegionSelectionListener = regionSelectionListener

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
            dest[index] = data.name
        }

        itemBindingUtil.numberPickerTimer.minValue = 0
        itemBindingUtil.numberPickerTimer.maxValue = dest.size - 1
        itemBindingUtil.numberPickerTimer.displayedValues = dest
        itemBindingUtil.numberPickerTimer.value = selectedPos
        itemBindingUtil.numberPickerTimer.setOnValueChangedListener { picker, oldVal, newVal ->
            itemBindingUtil.numberPickerTimer.value = newVal
        }

        itemBindingUtil.btnDone.setOnClickListener {
            arrayList[selectedPos].isSelected = false
            listener.onRegionSelect(itemBindingUtil.numberPickerTimer.value)
            dismiss()
        }
    }

    private fun setClickListener() {
    }

    interface RegionSelectionListener {
        fun onRegionSelect(selectedPos: Int)
    }
}