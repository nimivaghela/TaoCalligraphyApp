package com.app.taocalligraphy.ui.community.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentBlockedListBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.ui.referrals.adapter.ReferralsAdapter
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BlockedListFragment : BaseFragment<FragmentBlockedListBinding>(), ReferralsAdapter.OnFollowersClickListener {

    private var menuItemList = ArrayList<IconNamePopupModel>()
    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null

    private val referralsAdapter by lazy {
        return@lazy ReferralsAdapter(this)
    }

    override fun getLayoutId() = R.layout.fragment_blocked_list

    override fun displayMessage(message: String) {

    }

    override fun initView() {

        mBinding.rvBlockList.adapter = referralsAdapter

        //menuItemList.add(IconNamePopupModel(true))
        menuItemList.clear()
        menuItemList.add(IconNamePopupModel(getString(R.string.unblock), ContextCompat.getDrawable(requireContext(), R.drawable.vd_check_circle_green)))
        menuItemList.add(IconNamePopupModel(getString(R.string.report), ContextCompat.getDrawable(requireContext(), R.drawable.vd_alert_triangle)))

    }
    override fun observeApiCallbacks() {

    }
    override fun postInit() {

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