package com.app.taocalligraphy.ui.meditation_rooms_detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityAnnouncementsListsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.AnnouncementsListAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AnnouncementsListsActivity : BaseActivity<ActivityAnnouncementsListsBinding>() {

    private val mAnnouncementsListAdapter by lazy {
        return@lazy AnnouncementsListAdapter(this)
    }

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, AnnouncementsListsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_announcements_lists

    override fun initView() {
        mBinding.rvAnnouncementsList.adapter = mAnnouncementsListAdapter
    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {
        mBinding.ivClose.setOnClickListener {
            onBackPressed()
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