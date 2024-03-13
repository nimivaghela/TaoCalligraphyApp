package com.app.taocalligraphy.ui.meditation_rooms_detail

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityReportRoomBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.utils.extensions.hideKeyboard
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ReportRoomActivity : BaseActivity<ActivityReportRoomBinding>() {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ReportRoomActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_report_room

    override fun initView() {

    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {

        mBinding.btnSendReport.setOnClickListener {
            if (checkScreenValidation()) {
                hideKeyboard()
                mBinding.llReport.visibility = View.GONE
                mBinding.tvInfo1.visibility = View.VISIBLE
                mBinding.tvInfo2.visibility = View.VISIBLE
                mBinding.cvSendReport.visibility = View.GONE
                mBinding.cvReturnHome.visibility = View.VISIBLE
            }
        }

        mBinding.btnReturnHome.setOnClickListener {
            onBackPressed()
        }

        mBinding.ivClose.setOnClickListener {
            onBackPressed()
        }

    }

    private fun checkScreenValidation(): Boolean {
        if (TextUtils.isEmpty(mBinding.etReportMessage.text.toString().trim())) {
            longToast(getString(R.string.please_enter_any_report_text),ERROR)
            //mBinding.tlReportMessage.error = getString(R.string.please_enter_any_report_text)
            return false
        } else {
            //mBinding.tlReportMessage.error = ""
        }
        return true
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