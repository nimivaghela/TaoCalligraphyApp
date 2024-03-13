package com.app.taocalligraphy.utils.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.app.taocalligraphy.R
import com.app.taocalligraphy.ui.create_meditation_room.dialog.UnlockNewBackgroundDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showUnlockImageDialog(context: Context) {
    val dialog = UnlockNewBackgroundDialog(context)
    dialog.show()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}

fun Context.showCustomDialog(
    message: String,
    negativeButton: String?,
    positiveButton: String?,
    neutralButton: String?,
    customDialogListener: CustomDialogListener
) {
    val dialog = MaterialAlertDialogBuilder(this, R.style.StackedAlertDialogStyle)
        .setMessage(message)

    negativeButton?.let {
        dialog.setNegativeButton(negativeButton) { dialog, which ->
            dialog.dismiss()
            customDialogListener.onNegativeClicked()
        }
    }
    positiveButton?.let {
        dialog.setPositiveButton(positiveButton) { dialog, which ->
            dialog.dismiss()
            customDialogListener.onPositiveClicked()
        }
    }
    neutralButton?.let {
        dialog.setNeutralButton(neutralButton) { dialog, which ->
            dialog.dismiss()
            customDialogListener.onNeutralClicked()
        }
    }
    dialog.show()
}

interface CustomDialogListener {
    fun onPositiveClicked()
    fun onNegativeClicked()
    fun onNeutralClicked()
}