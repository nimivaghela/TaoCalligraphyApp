package com.app.taocalligraphy.ui.meditation_rooms_detail

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityTaoCalligraphyFieldsRoomsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_rooms_detail.fragment.AboutListFragment
import com.app.taocalligraphy.ui.meditation_rooms_detail.fragment.AnnouncementsListFragment
import com.app.taocalligraphy.ui.meditation_rooms_detail.fragment.ProgramsListFragment
import com.app.taocalligraphy.ui.meditation_rooms_detail.fragment.SessionListFragment
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.TabsSelectSingleAdapter
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import kotlinx.android.synthetic.main.toolbar.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TaoCalligraphyFieldsRoomsActivity : BaseActivity<ActivityTaoCalligraphyFieldsRoomsBinding>(),
    TabsSelectSingleAdapter.SingleSelectItemClickListener {

    private var mTabsList = mutableListOf<String>()
    private var mTabsSelectSingleAdapter: TabsSelectSingleAdapter? = null
    private lateinit var mSessionListFragment: SessionListFragment
    private lateinit var mAnnouncementsListFragment: AnnouncementsListFragment
    private lateinit var mProgramsListFragment: ProgramsListFragment
    private lateinit var mAboutListFragment: AboutListFragment
    private var isFollowed: Boolean = true
    private var isFavToolbarSelected: Boolean = false

    companion object {
        var position = 0
        fun startActivity(activity: AppCompatActivity, position: Int) {
            val intent = Intent(activity, TaoCalligraphyFieldsRoomsActivity::class.java)
            this.position = position
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_tao_calligraphy_fields_rooms

    override fun initView() {
        setupToolbar()
        setTabs()
        setFavSelection()
        when (position) {
            0 -> {
//                TCF DETAIL SCREEN
                mBinding.ivBackgroundImage.setImageResource(R.drawable.ic_background_tcf_rooms_1)
                mBinding.fmTCFMasterImages.visibility = View.VISIBLE
                mBinding.cvFRMasterImage.visibility = View.GONE
                mBinding.cvCRMasterImage.visibility = View.GONE
            }
            1 -> {
//                FEATURED ROOM DETAIL SCREEN
                mBinding.ivBackgroundImage.setImageResource(R.drawable.ic_background_featured_room)
                mBinding.fmTCFMasterImages.visibility = View.GONE
                mBinding.cvFRMasterImage.visibility = View.VISIBLE
                mBinding.cvCRMasterImage.visibility = View.GONE
            }
            2 -> {
//                COMMUNITY ROOM DETAIL
                mBinding.ivBackgroundImage.setImageResource(R.drawable.ic_background_community_room)
                mBinding.fmTCFMasterImages.visibility = View.GONE
                mBinding.cvFRMasterImage.visibility = View.GONE
                mBinding.cvCRMasterImage.visibility = View.VISIBLE
            }
            3 -> {
//                COMMUNITY ROOM DETAIL
                mBinding.ivBackgroundImage.setImageResource(R.drawable.ic_background_community_room)
                mBinding.fmTCFMasterImages.visibility = View.GONE
                mBinding.cvFRMasterImage.visibility = View.GONE
                mBinding.cvCRMasterImage.visibility = View.VISIBLE
            }
        }
    }

    private fun setupToolbar() {
        setToolbar(mBinding.toolbar.innerToolbar, "", true)
        mBinding.toolbar.ivBackToolbar.setImageResource(R.drawable.vd_gold_back_arrow)
        mBinding.toolbar.innerToolbar.ivShareToolbar.visible()
        mBinding.toolbar.innerToolbar.frameLikeToolbar.visibility = View.VISIBLE
        mBinding.toolbar.innerToolbar.llAnnouncementToolbar.visibility = View.VISIBLE

        when (position) {
            0 -> {
                mBinding.toolbar.frameInvite.visibility = View.VISIBLE
            }
            1 -> {
                mBinding.toolbar.frameInvite.visibility = View.GONE
            }
            2 -> {
                mBinding.toolbar.frameInvite.visibility = View.VISIBLE
            }
            3 -> {
                mBinding.toolbar.frameInvite.visibility = View.VISIBLE
            }
        }

    }

    private fun setTabs() {
        mSessionListFragment = SessionListFragment()
        mAnnouncementsListFragment = AnnouncementsListFragment()
        mProgramsListFragment = ProgramsListFragment()
        mAboutListFragment = AboutListFragment(position)

        supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, mSessionListFragment)
            .commitAllowingStateLoss()
        when (position) {
            0 -> {
                mTabsList.add(getString(R.string.sessions))
//                mTabsList.add("ANNOUNCEMENTS")
                mTabsList.add(getString(R.string.programs_capital))
                mTabsList.add(getString(R.string.about_capital))
            }
            1 -> {
                mTabsList.add(getString(R.string.sessions))
//        mTabsList.add("ANNOUNCEMENTS")
                mTabsList.add(getString(R.string.programs_capital))
                mTabsList.add(getString(R.string.about_capital))
            }
            2 -> {
                mTabsList.add(getString(R.string.sessions))
//        mTabsList.add("ANNOUNCEMENTS")
//            mTabsList.add("PROGRAMS")
                mTabsList.add(getString(R.string.about_capital))
            }
            3 -> {
                mTabsList.add(getString(R.string.sessions))
                mTabsList.add(getString(R.string.announcements))
                mTabsList.add(getString(R.string.programs_capital))
                mTabsList.add(getString(R.string.about_capital))
            }
        }

        mTabsSelectSingleAdapter = TabsSelectSingleAdapter(this,mTabsList, this)
        mBinding.rvRoomsTabs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvRoomsTabs.adapter = mTabsSelectSingleAdapter
    }

    override fun onSingleItemClick(tabText: String, selectedPos: Int) {
        when (tabText) {
            getString(R.string.sessions) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mSessionListFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.announcements) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mAnnouncementsListFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.programs_capital) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mProgramsListFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.about_capital) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mAboutListFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {

        mBinding.llOptionMenuForMessageAndRoom.setOnClickListener {
            if (mBinding.llMessageHostAndReportRoom.isVisible) {
                mBinding.llMessageHostAndReportRoom.visibility = View.GONE
            } else {
                mBinding.llMessageHostAndReportRoom.visibility = View.VISIBLE
            }
        }

        mBinding.tvReportRoom.setOnClickListener {
            mBinding.llMessageHostAndReportRoom.visibility = View.GONE
            ReportRoomActivity.startActivity(this)
        }

        mBinding.toolbar.innerToolbar.frameInvite.setOnClickListener {
            InviteUserForMeditationRoomActivity.startActivity(this)
        }

        mBinding.toolbar.innerToolbar.llAnnouncementToolbar.setOnClickListener {
            AnnouncementsListsActivity.startActivity(this)
        }

        mBinding.apply {
            cvFollowUnfollow.setOnClickListener {
                setFollowUnfollowClick()
            }

            toolbar.innerToolbar.frameLikeToolbar.setOnClickListener {
                /*if (!isFavToolbarSelected) {
                    toolbar.innerToolbar.ivLikeToolbar.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@TaoCalligraphyFieldsRoomsActivity,
                            R.drawable.vd_like_gold_icon
                        )
                    )
                } else {
                    toolbar.innerToolbar.ivLikeToolbar.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@TaoCalligraphyFieldsRoomsActivity,
                            R.drawable.vd_like_grey_icon
                        )
                    )
                }*/
                setFavSelection()
            }
        }
    }

    private fun setFavSelection() {
        if (!isFavToolbarSelected) {
            isFavToolbarSelected = true
            mBinding.toolbar.lottieLike.setAnimation("favorite_toggle_off.json")
            mBinding.toolbar.lottieLike.playAnimation()
        } else {
            isFavToolbarSelected = false
            mBinding.toolbar.lottieLike.setAnimation("favorite_toggle_on.json")
            mBinding.toolbar.lottieLike.playAnimation()
        }
    }

    private fun setFollowUnfollowClick() {
        mBinding.apply {
            if (isFollowed) {
                isFollowed = false
                btnFollow.background = (ContextCompat.getDrawable(
                    this@TaoCalligraphyFieldsRoomsActivity,
                    R.drawable.bg_white_curve_gold_border_4dp
                ))
                btnFollow.text = getString(R.string.unfollow)
                btnFollow.setTextColor(getColor(R.color.gold))
            } else {
                isFollowed = true
                btnFollow.background = (ContextCompat.getDrawable(
                    this@TaoCalligraphyFieldsRoomsActivity,
                    R.drawable.bg_light_dark_golden_curve_4dp
                ))
                btnFollow.text = getString(R.string.follow)
                btnFollow.setTextColor(getColor(R.color.white))
            }
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