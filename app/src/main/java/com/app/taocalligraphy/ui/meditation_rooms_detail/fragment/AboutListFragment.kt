package com.app.taocalligraphy.ui.meditation_rooms_detail.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil.setContentView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.DialogManageParticipantsSessionBinding
import com.app.taocalligraphy.databinding.FragmentAboutListBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.HostsListAdapter
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.ManageFollowersAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AboutListFragment(
    var position: Int
) : BaseFragment<FragmentAboutListBinding>(), ManageFollowersAdapter.OnFollowersClickListener {

    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null
    var menuItemList = ArrayList<IconNamePopupModel>()

    private val mManageFollowersAdapter by lazy {
        return@lazy ManageFollowersAdapter(requireContext(),this)
    }

    private val mHostsListAdapter by lazy {
        return@lazy HostsListAdapter(requireContext())
    }

    override fun getLayoutId() = R.layout.fragment_about_list

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    override fun initView() {

        initPopupDialog()

        when (position) {
            0 -> {
                mBinding.llHostDetail.visibility = View.GONE
                mBinding.llHostListDetail.visibility = View.VISIBLE
            }
            1 -> {
                mBinding.llHostDetail.visibility = View.VISIBLE
                mBinding.llHostListDetail.visibility = View.GONE
            }
            2 -> {
                mBinding.llHostDetail.visibility = View.VISIBLE
                mBinding.llHostListDetail.visibility = View.GONE
            }
        }

        mBinding.rvHostList.adapter = mHostsListAdapter
        mBinding.rvFollowersList.adapter = mManageFollowersAdapter

    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    private fun initPopupDialog() {
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.view_profile),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_small_gold_user)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.send_message),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_chat_small)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.make_co_host),
                ContextCompat.getDrawable(requireContext(), R.drawable.vd_session_host_icon)
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFollowersClick(itemView: View) {
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