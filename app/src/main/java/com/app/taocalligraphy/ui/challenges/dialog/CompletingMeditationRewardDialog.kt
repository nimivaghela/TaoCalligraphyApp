package com.app.taocalligraphy.ui.challenges.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogCancelSessionBinding
import com.app.taocalligraphy.databinding.DialogCompletingMeditationRewardBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CompletingMeditationRewardDialog : BottomSheetDialog {

    var contextLang: Context
    var rewardType:Int

    constructor(
        context: Context,
        type: Int
    ) : super(context, R.style.CustomBottomSheetDialogTheme) {
        contextLang = context
        rewardType=type
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogCompletingMeditationRewardBinding.inflate(LayoutInflater.from(contextLang), null, false)
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(true)
        setClickListener(itemBindingUtil)

        when (rewardType) {
            2 -> {
                itemBindingUtil.ffRewardBg.setBackgroundResource(R.drawable.ic_bg_golden_reward)
                itemBindingUtil.ivHeart.setImageResource(R.drawable.ic_golden_heart_reward)
                itemBindingUtil.tvHeartTitle.text=contextLang.getText(R.string.golden_heart)
                itemBindingUtil.tvMeditationPeriod.text=contextLang.getText(R.string.golden_first_complete)

                itemBindingUtil.cardReward.strokeColor = ContextCompat.getColor(contextLang,R.color.gold_semi_light)
                itemBindingUtil.tvPlus.setTextColor(ContextCompat.getColor(contextLang,R.color.gold))
                itemBindingUtil.tvTotalHeart.setTextColor(ContextCompat.getColor(contextLang,R.color.gold))
                itemBindingUtil.tvHeartTitle.setTextColor(ContextCompat.getColor(contextLang,R.color.gold))
                itemBindingUtil.tvMeditationPeriod.setTextColor(ContextCompat.getColor(contextLang,R.color.gold))
            }
            3 -> {
                itemBindingUtil.ffRewardBg.setBackgroundResource(R.drawable.ic_bg_diamond_reward)
                itemBindingUtil.ivHeart.setImageResource(R.drawable.ic_diamond_heart_reward)
                itemBindingUtil.tvHeartTitle.text=contextLang.getText(R.string.diamond_heart)
                itemBindingUtil.tvMeditationPeriod.text=contextLang.getText(R.string.diamond_first_complete)

                itemBindingUtil.cardReward.strokeColor = ContextCompat.getColor(contextLang,R.color.gold_semi_light)
                itemBindingUtil.tvPlus.setTextColor(ContextCompat.getColor(contextLang,R.color.cosmic_cobalt))
                itemBindingUtil.tvTotalHeart.setTextColor(ContextCompat.getColor(contextLang,R.color.cosmic_cobalt))
                itemBindingUtil.tvHeartTitle.setTextColor(ContextCompat.getColor(contextLang,R.color.cosmic_cobalt))
                itemBindingUtil.tvMeditationPeriod.setTextColor(ContextCompat.getColor(contextLang,R.color.cosmic_cobalt))
            }
        }
    }

    private fun setClickListener(itemBindingUtil:  DialogCompletingMeditationRewardBinding) {
        itemBindingUtil.ivClose.setOnClickListener {
            dismiss()
        }
    }
}