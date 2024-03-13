package com.app.taocalligraphy.ui.meditation_rooms_list

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentMeditationRoomsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.TabsSelectSingleAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MeditationRoomsFragment : BaseFragment<FragmentMeditationRoomsBinding>(),
    TabsSelectSingleAdapter.SingleSelectItemClickListener {

    private var mTabsList = mutableListOf<String>()
    private var mTabsSelectSingleAdapter: TabsSelectSingleAdapter? = null
    private lateinit var mAllMeditationRoomsFragment: AllMeditationRoomsFragment
    private lateinit var mMyRoomsMeditationRoomsFragment: MyRoomsMeditationRoomsFragment

    override fun getLayoutId() = R.layout.fragment_meditation_rooms

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    override fun initView() {
        setupToolbar()
        setTabs()
    }

    private fun setupToolbar() {
        setToolbar(mBinding.mToolbar.innerToolbar, getString(R.string.meditation_rooms), false)
    }

    private fun setTabs() {
        mAllMeditationRoomsFragment = AllMeditationRoomsFragment()

        mMyRoomsMeditationRoomsFragment = MyRoomsMeditationRoomsFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.root_container, mAllMeditationRoomsFragment)
            .commitAllowingStateLoss()

        mTabsList.add(getString(R.string.all))
        mTabsList.add(getString(R.string.following))
        mTabsList.add(getString(R.string.favorites))
        mTabsList.add(getString(R.string.my_rooms))
        mTabsSelectSingleAdapter = TabsSelectSingleAdapter(requireContext(),mTabsList, this)
        mBinding.rvMeditationTabs.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvMeditationTabs.adapter = mTabsSelectSingleAdapter


    }

    override fun onSingleItemClick(tabText: String, selectedPos: Int) {
        when (tabText) {
            getString(R.string.all) -> {
                mBinding.footerSearch.visibility = View.VISIBLE
                childFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mAllMeditationRoomsFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.following) -> {
                mBinding.footerSearch.visibility = View.GONE
                childFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mAllMeditationRoomsFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.favorites) -> {
                mBinding.footerSearch.visibility = View.GONE
                childFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mAllMeditationRoomsFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.my_rooms) -> {
                mBinding.footerSearch.visibility = View.GONE
                childFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mMyRoomsMeditationRoomsFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

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