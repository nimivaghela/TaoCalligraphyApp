package com.app.taocalligraphy.ui.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityChatMessageBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.chat.adapter.ChatMessageAdapter
import com.app.taocalligraphy.ui.chat.adapter.ChatMultiFriendConversationAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChatMessageActivity : BaseActivity<ActivityChatMessageBinding>(),
    ChatMessageAdapter.ChatMessageListener {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ChatMessageActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_chat_message
    }

    private val chatMessageAdapter by lazy {
        return@lazy ChatMessageAdapter(this)
    }
    private val chatMultiFriendConversationAdapter by lazy {
        return@lazy ChatMultiFriendConversationAdapter()
    }

    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null
    var menuItemList = ArrayList<IconNamePopupModel>()

    private val addNewFriendActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedUser = result.data?.getIntExtra(Constants.selectedUser, 0) ?: 0
                chatMultiFriendConversationAdapter.itemList =
                    chatMultiFriendConversationAdapter.itemList + selectedUser
                chatMultiFriendConversationAdapter.notifyDataSetChanged()
            }
        }

    override fun initView() {
        mBinding.rvMessage.adapter = chatMessageAdapter
        mBinding.rvChatConversation.adapter = chatMultiFriendConversationAdapter
        setMenuData()
    }

    private fun setMenuData() {
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.edit),
                ContextCompat.getDrawable(this, R.drawable.vd_edit_gray)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                true
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.delete),
                ContextCompat.getDrawable(this, R.drawable.vd_delete_gray_new)
            )
        )
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.ivBackToolbar.setOnClickListener {
            onBackPressed()
        }

        mBinding.etMessage.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                mBinding.ivSendMessage.setImageResource(R.drawable.ic_send_gray)
            } else {
                mBinding.ivSendMessage.setImageResource(R.drawable.ic_send_gold)
            }
        }

        mBinding.ivAddNew.setOnClickListener {
            AddFriendInChatActivity.startResult(this, addNewFriendActivityResult)
        }

        mBinding.apply {
            ivSendMessage.setOnClickListener {
                etMessage.text!!.clear()
            }
        }
    }

    override fun onChatMessageClick(itemView: View, adapterPosition: Int) {
        if (customPowerMenu != null) {
            customPowerMenu = null
            return
        }
        val onMenuItemClickListener: OnMenuItemClickListener<IconNamePopupModel> =
            OnMenuItemClickListener<IconNamePopupModel> { _, _ ->
                customPowerMenu?.dismiss()
                customPowerMenu = null
            }

        customPowerMenu = CustomPowerMenu.Builder(this, CustomMenuItemAdapter())
            .setHeaderView(R.layout.item_popup_header) // header used for title
            .setFooterView(R.layout.item_popup_header)
            .addItemList(menuItemList)
            .setAnimation(MenuAnimation.SHOW_UP_CENTER) // Animation start point (TOP | LEFT).
            .setMenuRadius(30f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setShowBackground(false)
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()

        val layoutManager = mBinding.rvMessage.layoutManager as LinearLayoutManager
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