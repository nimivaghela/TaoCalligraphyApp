package com.app.taocalligraphy.ui.program.fragment

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentProgramsAboutBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.UserProgramApiResponse
import com.app.taocalligraphy.ui.program.adapter.ProgramContentReviewAdapter
import com.app.taocalligraphy.utils.extensions.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProgramsAboutFragment :
    BaseFragment<FragmentProgramsAboutBinding>() {

    private val contentReviewAdapter by lazy {
        return@lazy ProgramContentReviewAdapter()
    }

    override fun getLayoutId() = R.layout.fragment_programs_about

    override fun displayMessage(message: String) {

    }

    companion object {
        var data: UserProgramApiResponse? = null
        fun newInstance(
            data: UserProgramApiResponse?,
        ): ProgramsAboutFragment {
            this.data = data
            return ProgramsAboutFragment()
        }
    }

    override fun initView() {
        mBinding.rvReview.adapter = contentReviewAdapter
        setWebView()
        contentReviewAdapter.reviewList =
            data?.reviewsList as ArrayList<UserProgramApiResponse.Reviews>

        data?.reviewsList?.let {
            if (it.isEmpty()) {
                mBinding.experienceLabel.visible()
                mBinding.noReviews.visible()
                mBinding.rvReview.gone()
            } else {
                mBinding.experienceLabel.visible()
                mBinding.noReviews.gone()
                mBinding.rvReview.visible()
            }
        } ?: kotlin.run {
            mBinding.experienceLabel.visible()
            mBinding.noReviews.visible()
            mBinding.rvReview.gone()
        }
    }

    private fun setWebView() {
        data?.description?.let {
            if (isTablet(requireContext())) {
                mBinding.webView.setPropertiesAndData(
                    it.appendHtmlForTabletTextSize22()
                )
            } else {
                mBinding.webView.setPropertiesAndData(
                    it.appendHtml()
                )
            }
        }

        mBinding.webView.webViewClient = object : WebViewClient() {

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                //Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                showProgressIndicator(mBinding.llProgress, true)
            }

            override fun onPageFinished(view: WebView, url: String) {
                showProgressIndicator(mBinding.llProgress, false)
            }
        }
    }

    override fun postInit() {

    }

    override fun observeApiCallbacks() {

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