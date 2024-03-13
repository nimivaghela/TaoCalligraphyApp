package com.app.taocalligraphy.ui.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityAddFriendInChatBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.chat.adapter.ChatFriendSelectionAdapter
import com.app.taocalligraphy.ui.chat.adapter.ChatStartSelectionAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AddFriendInChatActivity : BaseActivity<ActivityAddFriendInChatBinding>(),
    ChatFriendSelectionAdapter.FriendSelectionListener,
    ChatStartSelectionAdapter.ChatRemoveFriendListener {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, AddFriendInChatActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }

        fun startResult(
            activity: AppCompatActivity,
            addMemberResultLauncher: ActivityResultLauncher<Intent>,
        ) {
            val intent = Intent(activity, AddFriendInChatActivity::class.java)
            addMemberResultLauncher.launch(
                intent,
                ActivityOptionsCompat.makeCustomAnimation(
                    activity,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            )
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_add_friend_in_chat
    }

    private val chatFriendSelectionAdapter by lazy {
        return@lazy ChatFriendSelectionAdapter(this)
    }
    private val chatStartSelectionAdapter by lazy {
        return@lazy ChatStartSelectionAdapter(this, R.drawable.vd_remove_item_gray)
    }

    override fun initView() {
        setupToolbar()
        mBinding.rvSelectedChat.adapter = chatStartSelectionAdapter
        mBinding.rvFriend.adapter = chatFriendSelectionAdapter
    }

    private fun setupToolbar() {
        setToolbar(
            mBinding.toolbar.innerToolbar,
            getString(R.string.add_to_chat),
            true,
        )
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.tvDone.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra(
                Constants.selectedUser,
                chatStartSelectionAdapter.item)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
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

    override fun onCommunityMoreClick(itemView: View, adapterPosition: Int) {
    }

    override fun onFriendRemoveFromChat(adapterPosition: Int) {
        chatStartSelectionAdapter.item--
        chatStartSelectionAdapter.notifyItemRemoved(adapterPosition)
        setFriendSelectionViewVisibility()
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