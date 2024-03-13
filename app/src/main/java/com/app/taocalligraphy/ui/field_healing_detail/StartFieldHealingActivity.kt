package com.app.taocalligraphy.ui.field_healing_detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityStartFieldHealingBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.field_healing_detail.dialog.BoostYourHealingDialog
import com.app.taocalligraphy.ui.meditation.adapter.MeditationVideoPlaylistAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StartFieldHealingActivity : BaseActivity<ActivityStartFieldHealingBinding>(),
    MeditationVideoPlaylistAdapter.VideoPlaylistItemClickListener {

    private val meditationVideoPlaylistAdapter by lazy {
        return@lazy MeditationVideoPlaylistAdapter(this)
    }

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, StartFieldHealingActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_start_field_healing

    override fun initView() {
        setupToolbar()
        mBinding.rvMeditationVideoPlaylist.adapter = meditationVideoPlaylistAdapter
    }

    private fun setupToolbar() {
        setToolbar(mBinding.toolbar.innerToolbar, "", true)
    }


    override fun observeApiCallbacks() {

    }

    override fun handleListener() {

        mBinding.btnBoostYourHealing.setOnClickListener {
            showDialog()
        }

    }

    private fun showDialog() {
        val dialog = BoostYourHealingDialog(this)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onMediaChanged(position: Int) {

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