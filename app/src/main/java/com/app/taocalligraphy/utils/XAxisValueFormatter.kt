package com.app.taocalligraphy.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter(private val values: Array<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // "value" represents the position of the label on the axis (x or y)
        return values[value.toInt()]
    }
}