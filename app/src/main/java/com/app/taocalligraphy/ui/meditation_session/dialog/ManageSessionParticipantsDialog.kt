package com.app.taocalligraphy.ui.meditation_session.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.DialogManageParticipantsSessionBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.ManageParticipantsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.skydoves.powermenu.*


class ManageSessionParticipantsDialog : BottomSheetDialog,
    ManageParticipantsAdapter.ParticipantsClickListener {

    private val manageParticipantsAdapter by lazy {
        return@lazy ManageParticipantsAdapter(this)
    }

    var contextLang: Context
    var isHost: Boolean
    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null
    var menuItemList = ArrayList<IconNamePopupModel>()

    constructor(
        context: Context,
        isHost: Boolean
    ) : super(context, R.style.FullScreenBottomSheetDialogTheme) {
        contextLang = context
        this.isHost = isHost
        initView()
    }

    private fun initView() {
        val itemBindingUtil =
            DialogManageParticipantsSessionBinding.inflate(
                LayoutInflater.from(contextLang),
                null,
                false
            )
        setContentView(itemBindingUtil.root)
        setCanceledOnTouchOutside(false)
        setClickListener(itemBindingUtil)

        itemBindingUtil.rvParticipants.adapter = manageParticipantsAdapter
        if (!isHost) {
            itemBindingUtil.tvHeader.text = contextLang.getString(R.string.view_participants)
            itemBindingUtil.llMuteUnMuteParticipants.isGone = true
            menuItemList.add(
                IconNamePopupModel(
                    contextLang.getString(R.string.add_friend),
                    ContextCompat.getDrawable(contextLang, R.drawable.vd_add_friend)
                )
            )
        } else {
            menuItemList.add(
                IconNamePopupModel(
                    contextLang.getString(R.string.send_message),
                    ContextCompat.getDrawable(contextLang, R.drawable.vd_chat_small)
                )
            )
            menuItemList.add(
                IconNamePopupModel(
                    contextLang.getString(R.string.make_co_host),
                    ContextCompat.getDrawable(contextLang, R.drawable.vd_session_host_icon)
                )
            )
            menuItemList.add(
                IconNamePopupModel(
                    contextLang.getString(R.string.mute),
                    ContextCompat.getDrawable(contextLang, R.drawable.vd_unmute_icon_gold)
                )
            )
            menuItemList.add(
                IconNamePopupModel(
                    true
                )
            )
            menuItemList.add(
                IconNamePopupModel(
                    contextLang.getString(R.string.remove),
                    ContextCompat.getDrawable(contextLang, R.drawable.vd_remove_participants)
                )
            )
            menuItemList.add(
                IconNamePopupModel(
                    contextLang.getString(R.string.ban),
                    ContextCompat.getDrawable(contextLang, R.drawable.vd_cross_red)
                )
            )
        }
    }

    private fun setClickListener(itemBindingUtil: DialogManageParticipantsSessionBinding) {
        itemBindingUtil.ivDown.setOnClickListener {
            dismiss()
        }

        itemBindingUtil.btnMuteAllParticipants.setOnClickListener {
            setMuteUnMuteController(
                itemBindingUtil.btnMuteAllParticipants,
                itemBindingUtil.btnUnMuteAllParticipants
            )
        }

        itemBindingUtil.btnUnMuteAllParticipants.setOnClickListener {
            setMuteUnMuteController(
                itemBindingUtil.btnUnMuteAllParticipants,
                itemBindingUtil.btnMuteAllParticipants
            )
        }
    }

    private fun setMuteUnMuteController(
        activeButton: MaterialButton,
        inActiveButton: MaterialButton
    ) {
        activeButton.setBackgroundColor(ContextCompat.getColor(contextLang, R.color.dark_grey))
        activeButton.strokeColor =
            ColorStateList.valueOf(ContextCompat.getColor(contextLang, R.color.dark_grey))
        activeButton.iconTint =
            ColorStateList.valueOf(ContextCompat.getColor(contextLang, R.color.white))
        activeButton.setTextColor(ContextCompat.getColor(contextLang, R.color.white))

        inActiveButton.setBackgroundColor(ContextCompat.getColor(contextLang, R.color.white))
        inActiveButton.strokeColor =
            ColorStateList.valueOf(ContextCompat.getColor(contextLang, R.color.gold))
        inActiveButton.iconTint =
            ColorStateList.valueOf(ContextCompat.getColor(contextLang, R.color.gold))
        inActiveButton.setTextColor(ContextCompat.getColor(contextLang, R.color.gold))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onParticipantsClick(itemView: View) {
        if (customPowerMenu != null) {
            customPowerMenu = null
            return
        }
        val onMenuItemClickListener: OnMenuItemClickListener<IconNamePopupModel> =
            OnMenuItemClickListener<IconNamePopupModel> { position, item ->
                customPowerMenu?.dismiss()
                customPowerMenu = null
            }

        customPowerMenu = CustomPowerMenu.Builder(contextLang, CustomMenuItemAdapter())
            .setHeaderView(R.layout.item_popup_header) // header used for title
            .setFooterView(R.layout.item_popup_header)
            .addItemList(menuItemList)
            .setAnimation(MenuAnimation.SHOW_UP_CENTER) // Animation start point (TOP | LEFT).
            .setMenuRadius(30f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setShowBackground(false)
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()

        customPowerMenu?.showAsDropDown(itemView)

        customPowerMenu?.setTouchInterceptor { view, motionEvent ->
            return@setTouchInterceptor false
        }
    }
}