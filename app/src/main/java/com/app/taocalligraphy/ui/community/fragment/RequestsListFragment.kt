package com.app.taocalligraphy.ui.community.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentRequestsListBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.ui.referrals.adapter.ReferralsAdapter
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RequestsListFragment : BaseFragment<FragmentRequestsListBinding>() {

    private var menuItemList = ArrayList<IconNamePopupModel>()
    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null

    override fun getLayoutId() = R.layout.fragment_requests_list

    override fun displayMessage(message: String) {
    }

    override fun initView() {
        menuItemList.clear()
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.accept),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_user_check_green)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.decline),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_remove_grey)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.report),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_alert_triangle)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.block),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_block)
            )
        )

        val mReferralsAdapterReceived =
            ReferralsAdapter(object : ReferralsAdapter.OnFollowersClickListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onFollowersClick(itemView: View,index:Int) {
                    if (customPowerMenu != null) {
                        customPowerMenu = null
                        return
                    }
                    val onMenuItemClickListener: OnMenuItemClickListener<IconNamePopupModel> =
                        OnMenuItemClickListener<IconNamePopupModel> { _, _ ->
                            customPowerMenu?.dismiss()
                            customPowerMenu = null
                        }

                    customPowerMenu =
                        CustomPowerMenu.Builder(requireContext(), CustomMenuItemAdapter())
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

                    customPowerMenu?.setTouchInterceptor { _, _ ->
                        return@setTouchInterceptor false
                    }
                }
            })
        mBinding.rvReceivedList.adapter = mReferralsAdapterReceived


        val mReferralsAdapterSent =
            ReferralsAdapter(object : ReferralsAdapter.OnFollowersClickListener {
                override fun onFollowersClick(itemView: View,index:Int) {

                }
            })
        mBinding.rvSentList.adapter = mReferralsAdapterSent

    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun observeApiCallbacks() {

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