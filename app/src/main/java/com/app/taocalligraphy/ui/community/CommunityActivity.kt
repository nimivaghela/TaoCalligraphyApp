package com.app.taocalligraphy.ui.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCommunityBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.community.fragment.BlockedListFragment
import com.app.taocalligraphy.ui.community.fragment.FriendListFragment
import com.app.taocalligraphy.ui.community.fragment.RequestsListFragment
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.TabsSelectSingleAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CommunityActivity : BaseActivity<ActivityCommunityBinding>(),
    TabsSelectSingleAdapter.SingleSelectItemClickListener {

    private var mTabsList = mutableListOf<String>()
    private var mTabsSelectSingleAdapter: TabsSelectSingleAdapter? = null
    private lateinit var mFriendListFragment: FriendListFragment
    private lateinit var mRequestsListFragment: RequestsListFragment
    private lateinit var mBlockedListFragment: BlockedListFragment

    override fun getResource() = R.layout.activity_community

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, CommunityActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun initView() {
        setupToolbar()
        setTabs()
    }

    private fun setupToolbar() {
        setToolbar(mBinding.toolbar.innerToolbar, getString(R.string.community), true)
    }

    private fun setTabs() {
        mFriendListFragment = FriendListFragment()
        mRequestsListFragment = RequestsListFragment()
        mBlockedListFragment = BlockedListFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, mFriendListFragment)
            .commitAllowingStateLoss()

        mTabsList.add(getString(R.string.FRIEND_LIST))
        mTabsList.add(getString(R.string.REQUESTS))
        mTabsList.add(getString(R.string.BLOCKED))
        mTabsSelectSingleAdapter = TabsSelectSingleAdapter(this,mTabsList, this)
        mBinding.rvProgramsTab.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvProgramsTab.adapter = mTabsSelectSingleAdapter
    }

    override fun onSingleItemClick(tabText: String, selectedPos: Int) {
        when (tabText) {
            getString(R.string.FRIEND_LIST) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mFriendListFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.REQUESTS) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mRequestsListFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.BLOCKED) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mBlockedListFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {

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