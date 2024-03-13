package com.app.taocalligraphy.ui.chat

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityChatConversationBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.chat.adapter.ChatConversationAdapter
import com.app.taocalligraphy.ui.chat.adapter.ChatFriendAdapter
import com.app.taocalligraphy.ui.chat.dialog.ChatErrorLoadingDialog
import com.app.taocalligraphy.ui.chat.dialog.ChatNoInternetConnectionDialog
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChatConversationActivity : BaseActivity<ActivityChatConversationBinding>(),
    ChatConversationAdapter.ChatConversationClickListener {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ChatConversationActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_chat_conversation
    }

    private val chatFriendAdapter by lazy {
        return@lazy ChatFriendAdapter()
    }
    private val chatConversationAdapter by lazy {
        return@lazy ChatConversationAdapter(this)
    }
    private var chatNoInternetConnectionDialog: ChatNoInternetConnectionDialog? = null

    override fun initView() {
        setupToolbar()

        mBinding.rvFriend.adapter = chatFriendAdapter
        mBinding.rvConversation.adapter = chatConversationAdapter
        mBinding.shimmerChatConversation.showShimmer(true)
        mBinding.shimmerChatConversation.postDelayed(Runnable {
            mBinding.shimmerChatConversation.hideShimmer()
            mBinding.shimmerChatConversation.isGone = true
            mBinding.tvEditConversation.isVisible = true
            mBinding.rvFriend.isVisible = true
            mBinding.rvConversation.isVisible = true
            mBinding.lblFriendRequest.isVisible = true
            mBinding.tvEditConversation.isVisible = true
            mBinding.toolbar.ivNewChat.setImageResource(R.drawable.vd_new_chat)

            checkNetworkConnection()
        }, 3000)
    }

    private fun setupToolbar() {
        setToolbar(
            mBinding.toolbar.innerToolbar,
            getString(R.string.chat),
            true,
        )

        mBinding.toolbar.tvTitleToolbar.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        mBinding.toolbar.ivNewChat.isVisible = true
    }

    private fun checkNetworkConnection() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                chatNoInternetConnectionDialog?.dismiss()
            }

            override fun onLost(network: Network) {
                openNoInternetConnectionDialog()
            }

            override fun onUnavailable() {
                openNoInternetConnectionDialog()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }


    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.toolbar.ivNewChat.setOnClickListener {
            if (!mBinding.shimmerChatConversation.isShimmerVisible) {
                CreateNewChatActivity.startActivity(this)
            }
        }

        mBinding.tvEditConversation.setOnClickListener {
            if (mBinding.shimmerChatConversation.isShimmerVisible) {
                return@setOnClickListener
            }
            chatConversationAdapter.isEdit = !chatConversationAdapter.isEdit
            chatConversationAdapter.notifyDataSetChanged()
            if (chatConversationAdapter.isEdit) {
                mBinding.tvEditConversation.text = getString(R.string.done)
            } else {
                mBinding.tvEditConversation.text = getString(R.string.edit)
            }

            val slide = Slide()
            slide.slideEdge = Gravity.BOTTOM
            slide.duration = 500
            slide.addTarget(mBinding.llEditController)
            TransitionManager.beginDelayedTransition(mBinding.rrMain, slide)
            mBinding.llEditController.visibility =
                if (chatConversationAdapter.isEdit) View.VISIBLE else View.GONE
        }
    }

    fun openNoInternetConnectionDialog() {
        chatNoInternetConnectionDialog?.dismiss()
        if (!isFinishing) {
            chatNoInternetConnectionDialog =
                ChatNoInternetConnectionDialog(this,
                    object : ChatNoInternetConnectionDialog.InternetReconnectListener {
                        override fun onReconnectClick() {
                            openPageRetryDialog()
                        }
                    })
            chatNoInternetConnectionDialog?.setCancelable(false)
            chatNoInternetConnectionDialog?.show()
            chatNoInternetConnectionDialog?.window?.setBackgroundDrawableResource(R.color.black_25)
        }
    }

    fun openPageRetryDialog() {
        val dialog =
            ChatErrorLoadingDialog(this)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.black_25)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.shimmerChatConversation.stopShimmer()
    }

    override fun onConversationClick() {
        ChatMessageActivity.startActivity(this)
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