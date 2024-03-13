package com.app.taocalligraphy.ui.meditation.dialog

import android.content.Context
import android.view.LayoutInflater
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogMeditationSubtitleBinding
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.ui.meditation.adapter.SubtitleAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChooseSubTitleDialog(
    context: Context,
    private val subtitleList: List<MeditationContentResponse.SubtitleWithLanguage?>?,
    private val subtitleSelectionListener: SubtitleSelectionListener
) : BottomSheetDialog(context, R.style.FullScreenBottomSheetDialogTheme) {

    var contextLang: Context = context

    private val subtitleAdapter by lazy {
        return@lazy SubtitleAdapter(subtitleSelectionListener)
    }

    init {
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogMeditationSubtitleBinding.inflate(
                LayoutInflater.from(contextLang),
                null, false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener()

        subtitleAdapter.subtitleList = subtitleList
        itemBindingUtil.rvSubtitle.adapter = subtitleAdapter

        itemBindingUtil.btnCancel.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
        }
    }

    private fun setClickListener() {
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
    }

    interface SubtitleSelectionListener {
        fun onSubtitleSelect(subtitle: MeditationContentResponse.SubtitleWithLanguage?)
    }
}