package com.app.taocalligraphy.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCreateNewChatBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.chat.adapter.ChatFriendCommunitySectionAdapter
import com.app.taocalligraphy.ui.chat.adapter.ChatStartSelectionAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class CreateNewChatActivity : BaseActivity<ActivityCreateNewChatBinding>(),
    ChatFriendCommunitySectionAdapter.FriendSelectionListener,
    ChatStartSelectionAdapter.ChatRemoveFriendListener {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, CreateNewChatActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_create_new_chat
    }

    private val chatFriendCommunitySectionAdapter by lazy {
        return@lazy ChatFriendCommunitySectionAdapter(this)
    }
    private val chatStartSelectionAdapter by lazy {
        return@lazy ChatStartSelectionAdapter(this, R.drawable.vd_clear_search)
    }
    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null
    var menuItemList = ArrayList<IconNamePopupModel>()

    override fun initView() {
        setupToolbar()
        mBinding.rvFriendCommunity.adapter = chatFriendCommunitySectionAdapter
        mBinding.rvSelectedChat.adapter = chatStartSelectionAdapter
        setMenuData()
    }

    private fun setMenuData() {
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.view_profile),
                ContextCompat.getDrawable(this, R.drawable.vd_view_profile)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                true
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.add_friend),
                ContextCompat.getDrawable(this, R.drawable.vd_add_friend)
            )
        )
    }

    private fun setupToolbar() {
        setToolbar(
            mBinding.toolbar.innerToolbar,
            getString(R.string.new_chat),
            true,
        )
        mBinding.toolbar.tvTitleToolbar.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
    }

    override fun observeApiCallbacks() {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun handleListener() {
        mBinding.etSearch.addTextChangedListener {
            chatFriendCommunitySectionAdapter.searchContent = it.toString()
            chatFriendCommunitySectionAdapter.notifyDataSetChanged()
            if (it.isNullOrEmpty()) {
                setFriendSelectionViewVisibility()
                mBinding.rvFriendCommunity.isVisible = true
                mBinding.llContactNotFound.isGone = true
            } else {
                mBinding.rlSelectedFriend.isGone = true
                mBinding.rvFriendCommunity.isGone = true
                mBinding.llContactNotFound.isVisible = true
            }
        }

        mBinding.ivClearSearch.setOnClickListener {
            mBinding.etSearch.text?.clear()
        }

        mBinding.tvStartChat.setOnClickListener {
            ChatMessageActivity.startActivity(this)
        }
    }

    private fun setFriendSelectionViewVisibility() {
        if (chatStartSelectionAdapter.item == 0) {
            mBinding.rlSelectedFriend.isGone = true
        } else {
            mBinding.rlSelectedFriend.isVisible = true
        }
    }

    override fun onFriendItemClick() {
        chatStartSelectionAdapter.item++
        chatStartSelectionAdapter.notifyItemInserted(chatStartSelectionAdapter.item)
        setFriendSelectionViewVisibility()
    }

    override fun onFriendRemoveFromChat(adapterPosition: Int) {
        chatStartSelectionAdapter.item--
        chatStartSelectionAdapter.notifyItemRemoved(adapterPosition)
        setFriendSelectionViewVisibility()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCommunityMoreClick(itemView: View, adapterPosition: Int) {
        if (customPowerMenu != null) {
            customPowerMenu = null
            return
        }
        val onMenuItemClickListener: OnMenuItemClickListener<IconNamePopupModel> =
            OnMenuItemClickListener<IconNamePopupModel> { position, item ->
                customPowerMenu?.dismiss()
                customPowerMenu = null
            }

        val styledContext = ContextThemeWrapper(this, R.style.PopupCardThemeOverlay)

        customPowerMenu = CustomPowerMenu.Builder(this, CustomMenuItemAdapter())
            .setHeaderView(R.layout.item_popup_header) // header used for title
            .setFooterView(R.layout.item_popup_header)
            .addItemList(menuItemList)
            .setIsMaterial(true)
            .setAnimation(MenuAnimation.SHOW_UP_CENTER) // Animation start point (TOP | LEFT).
            .setMenuRadius(30f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setShowBackground(false)
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()

        val layoutManager = mBinding.rvFriendCommunity.layoutManager as LinearLayoutManager
        if (adapterPosition < (layoutManager.findLastVisibleItemPosition() - 1)) {
            customPowerMenu?.showAsDropDown(itemView)
        } else {
            customPowerMenu?.showAsAnchorRightTop(itemView, 0, -300)
        }

        customPowerMenu?.setOnDismissedListener {
            customPowerMenu = null
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            initView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (!isFinishing) {
                showNoInternetDialog()
            }
        }
    }
}