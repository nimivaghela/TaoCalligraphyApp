package com.app.taocalligraphy.ui.settings.dialog

import android.graphics.Bitmap
import android.graphics.Color
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogStaticContentBinding
import com.app.taocalligraphy.utils.extensions.appendHtml
import com.app.taocalligraphy.utils.extensions.appendHtmlForTablet
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.setPropertiesAndData

class StaticContentDialog : BaseDialogFragment<DialogStaticContentBinding>() {

    companion object {
        const val TAG = "StaticContentDialog"
        var title: String = ""
        var description: String = ""
        fun newInstance(
            title: String,
            description: String
        ): StaticContentDialog {
            this.title = title
            this.description = description
            return StaticContentDialog()
        }
    }

    override fun getResource(): Int {
        return R.layout.dialog_static_content
    }

    private fun setClickListener(itemBindingUtil: DialogStaticContentBinding) {
        itemBindingUtil.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun postInit() {
        dialog?.window?.attributes?.windowAnimations = R.style.bottom_popup_window_animation
        mBinding.webView.setBackgroundColor(Color.TRANSPARENT);
        mBinding.tvTitleOfPolicy.text = title
//        mBinding.tvDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
//        } else {
//            Html.fromHtml(description)
//        }
        setClickListener(mBinding)

        setWebView(description)
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

    private fun setWebView(description: String) {
        if (isTablet(requireContext())) {
            description.let { mBinding.webView.setPropertiesAndData(it.appendHtmlForTablet()) }
        } else {
            description.let { mBinding.webView.setPropertiesAndData(it.appendHtml()) }
        }

    }

    override fun initObserver() {
    }

    override fun handleListener() {
    }

    override fun displayMessage(message: String) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (dialog != null) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT)
            .show()
    }

}