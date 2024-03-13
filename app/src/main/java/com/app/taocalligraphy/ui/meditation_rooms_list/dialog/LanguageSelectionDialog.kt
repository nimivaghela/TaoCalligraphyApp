package com.app.taocalligraphy.ui.meditation_rooms_list.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogLanguageSelectionBinding
import com.app.taocalligraphy.models.response.profile.LanguageListApiResponse
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.MultiLanguageAdapter
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialog

class LanguageSelectionDialog(
    context: Context,
    var arrayList: ArrayList<LanguageListApiResponse.Data>,
    var selectedLangList: ArrayList<Int>,
    languageSelectionListener: LanguageSelectionListener
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme),
    MultiLanguageAdapter.OnSelect {

    var contextLang: Context = context
    val listener: LanguageSelectionListener = languageSelectionListener

    var selectedLang: ArrayList<Int> = ArrayList()

    private val multiLanguageAdapter: MultiLanguageAdapter by lazy {
        MultiLanguageAdapter(this)
    }

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogLanguageSelectionBinding.inflate(
                LayoutInflater.from(contextLang),
                null, false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        if (arrayList.size > 0) {
            itemBindingUtil.tvNotDataFound.gone()
            itemBindingUtil.rvLanguage.visible()
        } else {
            itemBindingUtil.rvLanguage.gone()
            itemBindingUtil.tvNotDataFound.visible()
        }

        itemBindingUtil.rvLanguage.adapter = multiLanguageAdapter
        selectedLang = selectedLangList
        multiLanguageAdapter.updateList(arrayList, selectedLang)

        itemBindingUtil.btnDone.setOnClickListener {
            if (selectedLang.isEmpty()) {
                Toast.makeText(
                    context,
                    contextLang.getString(R.string.select_language_mandatory),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                listener.onLanguageSelect(selectedLang)
                dismiss()
            }
        }
        itemBindingUtil.ivCancel.setOnClickListener {
            dismiss()
        }
    }

    interface LanguageSelectionListener {
        fun onLanguageSelect(selectedLang: ArrayList<Int>)
    }

    override fun clickOnItem(selectedLang: ArrayList<Int>) {
        this.selectedLang = selectedLang
    }
}