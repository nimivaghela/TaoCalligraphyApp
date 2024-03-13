package com.app.taocalligraphy.base

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.utils.extensions.getWidthOfScreen
import com.app.taocalligraphy.utils.extensions.logDebug
import io.reactivex.disposables.CompositeDisposable

abstract class BaseDialogFragment<VB : ViewDataBinding> : DialogFragment(), BaseView {
    lateinit var mBinding: VB
    lateinit var mDisposable: CompositeDisposable
    var isInternetConnected: Boolean = true
    var isFullScreen = false
    var isWellNessDialog = false

    /**
     * layout resource file
     */
    abstract fun getResource(): Int

    /**
     * to call API or bind adapter
     */
    abstract fun postInit()

    /**
     * initialize live data observer
     */
    abstract fun initObserver()

    /**
     * to define all listener
     */
    abstract fun handleListener()

    /**
     * to display error message
     */
    abstract fun displayMessage(message: String)

    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.dispose()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), getResource(), null, true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setContentView(mBinding.root)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        initDisposable()
        initConnectivity()
        initObserver()
        handleListener()
        postInit()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun initDisposable(){
        mDisposable = CompositeDisposable()
    }

    override fun onResume() {
        super.onResume()
        /**
         * setup the width of dialog programmatically
         */
        val params = dialog?.window?.attributes
        params?.let { dialogParams ->
            context?.getWidthOfScreen()?.let {
                if (isFullScreen) {
                    dialogParams.width = (it)
                    dialogParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    dialogParams.width = (it * 0.95).toInt()
                }

                dialog?.window?.attributes = params
            }
        }
        /**
         * Prevent dialog dismiss on outside click
         */
        isCancelable = false
    }

    private fun initConnectivity() {
    }

    fun showLoadingIndicator(progressBar: View, isShow: Boolean) {
        progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun getFragmentTransactionFromTag(tag: String): FragmentTransaction {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val previousFragment =
            requireActivity().supportFragmentManager.findFragmentByTag(tag)
        previousFragment?.let {
            fragmentTransaction.remove(it)
        }
        fragmentTransaction.addToBackStack(null)
        return fragmentTransaction
    }

    override fun onUnknownError(error: String?) {
        error?.let {
            //displayErrorMessage(error)
        }
        generalErrorAction()
    }

    override fun internalServer() {
        logDebug(msg = "Base Activity API Internal server")
        //displayMessage(getString(R.string.text_error_internal_server))
        generalErrorAction()
    }

    override fun onTimeout() {
        logDebug(msg = "Base Activity API Timeout")
        //displayMessage(getString(R.string.text_error_timeout))
        generalErrorAction()
    }

    override fun onNetworkError() {
        logDebug(msg = "Base Activity network error")
        //displayMessage(getString(R.string.text_error_network))
        generalErrorAction()
    }

    override fun onConnectionError() {
        logDebug(msg = "Base Activity internet issue")
        //displayMessage(getString(R.string.text_error_connection))
        generalErrorAction()
    }

    override fun onServerDown() {
        logDebug(msg = "Base Activity Server Connection issue")
        //displayMessage(getString(R.string.text_server_connection))
        generalErrorAction()
    }

    override fun generalErrorAction() {
        logDebug(msg = "This method will use in child class for performing common task for all error")
    }

    override fun autoLogout() {
        logDebug(msg = "AUTO LOGOUT")
    }

    override fun notFound(error: String?) {
        logDebug(msg = "This method will use notFound error")
        //displayMessage(getString(R.string.text_server_connection))
        generalErrorAction()
    }

    override fun unauthorized(error: String?) {
        logDebug(msg = "This method will use unauthorized error")
    }

    override fun forbidden(error: String?) {
        error?.let {
            //displayErrorMessage(error)
        }
        logDebug(msg = "This method will use forbidden error")
    }

    override fun onOTPExpire(error: String?) {
    }

    override fun onVerificationError() {
    }

    fun showProgressIndicator(progressBar: View, isShow: Boolean) {
        progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun hideKeyboard() {
        try {
            val imm =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = requireActivity().currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSubscribeRequired(error: String?) {
        MainActivity.startActivity(requireActivity() as AppCompatActivity, errorMsg = error)
        (requireActivity() as AppCompatActivity).finishAffinity()
    }

}