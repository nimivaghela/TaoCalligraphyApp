package com.app.taocalligraphy.ui.user_menu

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityUserMenuBinding
import com.app.taocalligraphy.models.UserMenuDetails
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.alarm.AlarmActivity
import com.app.taocalligraphy.ui.downloads.DownloadsActivity
import com.app.taocalligraphy.ui.favorites.FavoritesActivity
import com.app.taocalligraphy.ui.history.HistoryActivity
import com.app.taocalligraphy.ui.journal.JournalListingActivity
import com.app.taocalligraphy.ui.meditation_timer.MeditationTimerActivity
import com.app.taocalligraphy.ui.playlist.PlaylistsActivity
import com.app.taocalligraphy.ui.profile.ProfileActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.ProgramsListActivity
import com.app.taocalligraphy.ui.questionary.QuestionnairesResultActivity
import com.app.taocalligraphy.ui.settings.SettingsActivity
import com.app.taocalligraphy.ui.statistics.StatisticsActivity
import com.app.taocalligraphy.ui.user_menu.viewmodel.UserMenuViewModel
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class UserMenuActivity : BaseActivity<ActivityUserMenuBinding>(),
    UserMenuAdapter.OnItemClickListener {

    private val mViewModel: UserMenuViewModel by viewModels()

    private val fromActivityName by lazy {
        return@lazy intent.extras?.getString("fromActivityName", "") ?: ""
    }

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity, fromActivityName: String = "") {
            val intent = Intent(activity, UserMenuActivity::class.java)
            intent.putExtra("fromActivityName", fromActivityName)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int = R.layout.activity_user_menu

    var gridLayoutManager: GridLayoutManager? = null
    private val userMenuAdapter by lazy {
        return@lazy UserMenuAdapter(this)
    }
    var userMenuList: ArrayList<UserMenuDetails> = ArrayList()

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun updateProfile() {
        mBinding.imageProfile.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }


    override fun initView() {

        updateProfile()

        gridLayoutManager = GridLayoutManager(this@UserMenuActivity, 2)
        mBinding.rvUserMenu.layoutManager = gridLayoutManager
        mBinding.rvUserMenu.adapter = userMenuAdapter

        updateSubscriptionBadge()

        mBinding.cardViewProfile.setOnClickListener {
            if(!(UserHolder.EnumUserModulePermission.PROFILE.permission?.canAccess ?: true)){
                SubscriptionActivity.startActivityForResult(
                    this@UserMenuActivity
                )
                return@setOnClickListener
            }
            if (fromActivityName == ProfileActivity::class.java.simpleName) {
                onBackPressed()
            } else {
                ProfileActivity.startActivity(this@UserMenuActivity)
            }
        }

        mBinding.cardViewFavorites.setOnClickListener {
            if (!(UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess
                    ?: true)
            ) {
                SubscriptionActivity.startActivityForResult(
                    this@UserMenuActivity
                )
                return@setOnClickListener
            }

            if (fromActivityName == FavoritesActivity::class.java.simpleName) {
                onBackPressed()
            } else {
                FavoritesActivity.startActivity(this@UserMenuActivity)
            }
        }
        mBinding.cardViewJournal.setOnClickListener {
            if (!(UserHolder.EnumUserModulePermission.JOURNAL.permission?.canAccess ?: true)) {
                SubscriptionActivity.startActivityForResult(
                    this@UserMenuActivity
                )
                return@setOnClickListener
            }
            if (fromActivityName == JournalListingActivity::class.java.simpleName) {
                onBackPressed()
            } else {
                JournalListingActivity.startActivity(this@UserMenuActivity)
            }
        }

        mBinding.clGettingStart.setOnClickListener {
            if (fromActivityName == QuestionnairesResultActivity::class.java.simpleName) {
                onBackPressed()
            } else {
                QuestionnairesResultActivity.startActivity(this@UserMenuActivity)
            }
        }
        mBinding.clInteractiveTutorial.setOnClickListener {
            longToast(getString(R.string.interactive_tutorial), Constants.SUCCESS)
        }
        mBinding.clSFaq.setOnClickListener {
            longToast(getString(R.string.support_faq), Constants.SUCCESS)
        }
        mBinding.clOnenessHeart.setOnClickListener {
            val uri: Uri =
                Uri.parse("https://stageapp.onenessheart.com/")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        mBinding.closeUserMenu.setOnClickListener {
            onBackPressed()
        }
        mBinding.cardViewLogout.setOnClickListener {
            logoutConfirmationDialog()
        }

        LocalBroadcastManager.getInstance(this@UserMenuActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@UserMenuActivity)
            .unregisterReceiver(mAccessLevelReceiver)
        super.onDestroy()
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            updateSubscriptionBadge()
        }
    }


    private fun updateSubscriptionBadge() {
        if (!(UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess ?: true)) {
            mBinding.ivFavouriteLock.visible()
        } else {
            mBinding.ivFavouriteLock.gone()
        }

        if (!(UserHolder.EnumUserModulePermission.JOURNAL.permission?.canAccess ?: true)) {
            mBinding.ivJournalLock.visible()
        } else {
            mBinding.ivJournalLock.gone()
        }

        if (!(UserHolder.EnumUserModulePermission.PROFILE.permission?.canAccess ?: true)) {
            mBinding.ivProfileLock.visible()
        } else {
            mBinding.ivProfileLock.gone()
        }
        userMenuList.clear()
        userMenuList.add(
            UserMenuDetails(
                getString(R.string.programs),
                R.drawable.vd_curated_programs_gold_icon,
                !(UserHolder.EnumUserModulePermission.SEE_PROGRAMS.permission?.canAccess ?: true),
                UserHolder.EnumUserModulePermission.SEE_PROGRAMS.permission?.badge ?: ""
            )
        )
        userMenuList.add(
            UserMenuDetails(
                getString(R.string.playlists),
                R.drawable.vd_playlists_profile,
                false
            )
        )
        userMenuList.add(
            UserMenuDetails(
                getString(R.string.alarm),
                R.drawable.ic_alarm_icon,
                false
            )
        )
        userMenuList.add(
            UserMenuDetails(
                getString(R.string.timer),
                R.drawable.ic_timer_menu,
                !(UserHolder.EnumUserModulePermission.MEDITATION_TIMER.permission?.canAccess
                    ?: true),
                UserHolder.EnumUserModulePermission.MEDITATION_TIMER.permission?.badge ?: ""
            )
        )
        userMenuList.add(
            UserMenuDetails(
                getString(R.string.history),
                R.drawable.vd_history_profile,
                !(UserHolder.EnumUserModulePermission.ACCESS_PERSONAL_HISTORY.permission?.canAccess
                    ?: true),
                UserHolder.EnumUserModulePermission.ACCESS_PERSONAL_HISTORY.permission?.badge ?: ""
            )
        )
        userMenuList.add(
            UserMenuDetails(
                getString(R.string.downloads),
                R.drawable.download_icon,
                !(UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.canAccess
                    ?: true),
                UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.badge ?: ""
            )
        )
        userMenuList.add(
            UserMenuDetails(
                getString(R.string.stats),
                R.drawable.vd_stats_profile,
                !(UserHolder.EnumUserModulePermission.SEE_PERSONAL_STATS.permission?.canAccess
                    ?: true),
                UserHolder.EnumUserModulePermission.SEE_PERSONAL_STATS.permission?.badge ?: ""
            )
        )
        userMenuList.add(UserMenuDetails(getString(R.string.settings), R.drawable.vd_settings_icon,
            !(UserHolder.EnumUserModulePermission.SETTINGS.permission?.canAccess ?: true),
            UserHolder.EnumUserModulePermission.SETTINGS.permission?.badge ?: ""
            ),)
        userMenuAdapter.updateList(userMenuList)
    }

    private fun logoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("")
            .setMessage("" + getString(R.string.logout_msg))
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, which ->
                dialog!!.dismiss()
                if (isNetwork())
                    mViewModel.userLogout("", this@UserMenuActivity, mDisposable)
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, which -> dialog!!.dismiss() }
        builder.create().show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeApiCallbacks() {

        mViewModel.userLogoutLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    cancelAllPreviousAlarm()
                    userHolder.clearUserHolder()
                    LoginManager.getInstance().logOut()
                    notificationManager?.cancelAll()
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut()
                    mViewModel.deleteAllDownloads()
                    TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
                    cancelAllPreviousAlarm()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    startActivity(intent)
                    finishAffinity()
                }
//                showErrorState(requestState.error)
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, Constants.ERROR)
                        else -> {
                        }
                    }
                }
            }
        }

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

    override fun onItemClicked(item: UserMenuDetails) {
        when (item.title) {
            getString(R.string.programs) -> {
                if (!(UserHolder.EnumUserModulePermission.SEE_PROGRAMS.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@UserMenuActivity
                    )
                    return
                }

                if (fromActivityName == ProgramsListActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    ProgramsListActivity.startActivity(this@UserMenuActivity)
                }
            }
            getString(R.string.playlists) -> {
                if (fromActivityName == PlaylistsActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    PlaylistsActivity.startActivity(this@UserMenuActivity)
                }
            }
            getString(R.string.alarm) -> {
                if (fromActivityName == AlarmActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    AlarmActivity.startActivity(this@UserMenuActivity)
                }
            }
            getString(R.string.timer) -> {
                if (!(UserHolder.EnumUserModulePermission.MEDITATION_TIMER.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@UserMenuActivity
                    )
                    return
                }
                if (fromActivityName == MeditationTimerActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    MeditationTimerActivity.startActivity(this@UserMenuActivity)
                }
            }
            getString(R.string.history) -> {
                if (!(UserHolder.EnumUserModulePermission.ACCESS_PERSONAL_HISTORY.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@UserMenuActivity
                    )
                    return
                }

                if (fromActivityName == HistoryActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    HistoryActivity.startActivity(this@UserMenuActivity)
                }
            }
            getString(R.string.downloads) -> {
                if (!(UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@UserMenuActivity
                    )
                    return
                }

                if (fromActivityName == DownloadsActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    DownloadsActivity.startActivity(this@UserMenuActivity)
                }
            }
            getString(R.string.stats) -> {
                if (!(UserHolder.EnumUserModulePermission.SEE_PERSONAL_STATS.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@UserMenuActivity
                    )
                    return
                }
                if (fromActivityName == StatisticsActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    StatisticsActivity.startActivity(this@UserMenuActivity)
                }
            }
            getString(R.string.settings) -> {
                if (!(UserHolder.EnumUserModulePermission.SETTINGS.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@UserMenuActivity
                    )
                    return
                }

                if (fromActivityName == SettingsActivity::class.java.simpleName) {
                    onBackPressed()
                } else {
                    SettingsActivity.startActivity(this@UserMenuActivity)
                }
            }
        }

    }
}