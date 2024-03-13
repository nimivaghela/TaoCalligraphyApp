package com.app.taocalligraphy.ui.community.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentFriendListBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.ui.profile_view.PublicProfileViewActivity
import com.app.taocalligraphy.ui.referrals.adapter.ReferralsAdapter
import com.app.taocalligraphy.userHolder
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FriendListFragment : BaseFragment<FragmentFriendListBinding>(),
    ReferralsAdapter.OnFollowersClickListener {

    private var menuItemList = ArrayList<IconNamePopupModel>()
    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null

    override fun getLayoutId() = R.layout.fragment_friend_list

    private val referralsAdapter by lazy {
        return@lazy ReferralsAdapter(this)
    }

    override fun displayMessage(message: String) {

    }

    override fun initView() {

        mBinding.rvFriendList.adapter = referralsAdapter

        //menuItemList.add(IconNamePopupModel(true))
        menuItemList.clear()
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.view_profile),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_small_gold_user)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.send_message),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_send_message)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.remove),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_remove_grey)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.block),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_block)
            )
        )
    }

    override fun postInit() {

    }

    override fun observeApiCallbacks() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFollowersClick(itemView: View,index:Int) {
        if (customPowerMenu != null) {
            customPowerMenu = null
            return
        }
        val onMenuItemClickListener: OnMenuItemClickListener<IconNamePopupModel> =
            OnMenuItemClickListener<IconNamePopupModel> { position, item ->
                if (position == 0) {
                    //Currently set own user id
                    PublicProfileViewActivity.startActivity(activity as AppCompatActivity,
                        userHolder.id.toString())
                }
                customPowerMenu?.dismiss()
                customPowerMenu = null
            }

        customPowerMenu = CustomPowerMenu.Builder(requireContext(), CustomMenuItemAdapter())
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            initView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (isAdded) {
                showNoInternetDialog()
            }
        }
    }
}