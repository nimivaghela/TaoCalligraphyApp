package com.app.taocalligraphy.base

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.notification.AlarmBroadcastReceiver
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.chat.dialog.ChatNoInternetConnectionDialog
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.ProfileMenuHelper
import com.app.taocalligraphy.utils.extensions.logDebug
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.isNetwork
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.annotations.NotNull


abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), BaseView {

    lateinit var mDisposable: CompositeDisposable
    lateinit var mBinding: VB

    val profileMenuHelper by lazy {
        return@lazy ProfileMenuHelper()
    }

    /**
     * to get Fragment resource file
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * to set fragment option menu
     */
    protected open fun hasOptionMenu(): Boolean = false

    /**
     * to display error message
     */
    abstract fun displayMessage(message: String)

    /**
     * to initialize variables
     */
    abstract fun initView()


    abstract fun observeApiCallbacks()

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    abstract fun noInternetListener(model: IsNetworkAvailableListener)

    var noInternetConnectionDialog: ChatNoInternetConnectionDialog? = null

    fun initDisposable() {
        mDisposable = CompositeDisposable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable.clear()
        mDisposable.dispose()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        setHasOptionsMenu(hasOptionMenu())
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDisposable = CompositeDisposable()
        initView()
        observeApiCallbacks()
        initObserver()
        handleListener()
        initDisposable()
        postInit()
    }

    private lateinit var mToolbar: Toolbar
    protected fun setToolbar(
        @NotNull toolbar: Toolbar, @NotNull title: String, isBackEnabled: Boolean = false,
        backgroundColor: Int = R.color.transparent, textColor: Int = R.color.gold_90
    ) {
        this.mToolbar = toolbar
        this.mToolbar.title = title
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), backgroundColor))
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        if (title.isNotEmpty()) {
            val textView: TextView? = view?.findViewById(R.id.tvTitleToolbar)
            textView?.visibility = View.VISIBLE
            textView?.setTextColor(ContextCompat.getColor(requireContext(), textColor))
            textView?.text = title
        }

        if (isBackEnabled) {
            val imageView: ImageView? = view?.findViewById(R.id.ivBackToolbar)
            imageView?.setOnClickListener { }
            imageView?.visibility = View.VISIBLE
        } else {
            val imageView: ImageView? = view?.findViewById(R.id.ivBackToolbar)
            imageView?.visibility = View.GONE
        }
    }

    fun showProgressIndicator(progressBar: View, isShow: Boolean) {
        progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    /*@SuppressLint("WrongConstant")
    fun longToast(stringResId: String?) {
        if (stringResId == null) return
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            stringResId,
            BaseTransientBottomBar.LENGTH_SHORT
        )
        snackBar.duration = 4000
        val view = snackBar.view
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        val tv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tv.maxLines = 3
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.font_jost_medium)
        tv.typeface = typeface
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.medium_grey))
        snackBar.show()
    }*/

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

    fun longToastState(error: ApiError?) {
        error?.let { errorObj ->
            when (errorObj.errorState) {
                Constants.NETWORK_ERROR ->
                    errorObj.customMessage?.let { activity?.longToast(it, errorObj.type) }
                Constants.CUSTOM_ERROR ->
                    activity?.longToast("" + errorObj.customMessage, errorObj.type)
                else -> {
                }
            }
        }
    }

    fun cancelAllPreviousAlarm() {
        val intent = Intent(requireContext(), AlarmBroadcastReceiver::class.java)
        val alarmManager =
            requireContext().getSystemService(Service.ALARM_SERVICE) as AlarmManager
        for (i in 1 until 9) {
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                i,
                intent,
                PendingIntent.FLAG_IMMUTABLE or
                        PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_CANCEL_CURRENT
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
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
        cancelAllPreviousAlarm()
        userHolder.clearUserHolder()
        LoginManager.getInstance().logOut()
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()
        viewLifecycleOwner.lifecycleScope.launch {
            TaoCalligraphy.instance.meditationDbRepo.deleteAllMeditationContent()
        }
        TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
        cancelAllPreviousAlarm()
        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
        requireActivity().finishAffinity()
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showAuthenticationDialog(tokenExpire: Int) {

    }

    fun showNoInternetDialog() {
        if (noInternetConnectionDialog == null) {
            noInternetConnectionDialog =
                ChatNoInternetConnectionDialog(requireActivity(),
                    object : ChatNoInternetConnectionDialog.InternetReconnectListener {
                        override fun onReconnectClick() {
                            if (!requireActivity().isNetwork())
                                showNoInternetDialog()
                        }
                    })
        }
        noInternetConnectionDialog?.setCancelable(false)
        noInternetConnectionDialog?.show()
        noInternetConnectionDialog?.window?.setBackgroundDrawableResource(R.color.black_25)
    }

    override fun onSubscribeRequired(error: String?) {
        MainActivity.startActivity(requireActivity() as AppCompatActivity, errorMsg = error)
        (requireActivity() as AppCompatActivity).finishAffinity()
    }

}