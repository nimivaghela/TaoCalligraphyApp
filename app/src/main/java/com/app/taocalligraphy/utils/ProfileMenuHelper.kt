package com.app.taocalligraphy.utils

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.taocalligraphy.R
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.databinding.ItemProfileDialogBinding
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.calendar.CalendarActivity
import com.app.taocalligraphy.ui.chat.ChatConversationActivity
import com.app.taocalligraphy.ui.community.CommunityActivity
import com.app.taocalligraphy.ui.downloads.DownloadsActivity
import com.app.taocalligraphy.ui.history.HistoryActivity
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.journal.JournalListingActivity
import com.app.taocalligraphy.ui.meditation_timer.MeditationTimerActivity
import com.app.taocalligraphy.ui.playlist.PlaylistsActivity
import com.app.taocalligraphy.ui.profile.ProfileActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.settings.SettingsActivity
import com.app.taocalligraphy.ui.statistics.StatisticsActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.isVisible
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.visible
import io.reactivex.disposables.CompositeDisposable

class ProfileMenuHelper {
    var activity: Activity? = null
    var view: ItemProfileDialogBinding? = null
    var profileView: View? = null
    var chatBadgeCount: TextView? = null
    var viewModel: BaseViewModel? = null
    var baseView: BaseView? = null
    var disposable: CompositeDisposable? = null

    fun initializeComponent(
        activity: Activity,
        view: ItemProfileDialogBinding,
        profileView: View,
        chatBadgeCount: TextView,
        viewModel: BaseViewModel,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        this.activity = activity
        this.view = view
        this.profileView = profileView
        this.viewModel = viewModel
        this.baseView = baseView
        this.disposable = disposable
        this.chatBadgeCount = chatBadgeCount
        setProfileImage()
        setProfileMenuClickListener()

        if (userHolder.getChatCount!!.isNotEmpty()) {
            if (userHolder.getChatCount!!.toInt() > 9) {
                view.tvChatBadge.text = "9+"
                chatBadgeCount.visible()
                view.tvChatBadge.visible()
                chatBadgeCount.text = "9+"
            } else {
                if (userHolder.getChatCount == "0") {
                    view.tvChatBadge.gone()
                    chatBadgeCount.gone()
                } else {
                    view.tvChatBadge.text = userHolder.getChatCount
                    chatBadgeCount.text = userHolder.getChatCount
                    view.tvChatBadge.gone()
                    chatBadgeCount.gone()
                }
            }
        }
    }


    private fun setProfileImage() {
        activity?.let {
            view?.ivProfileImageProfile?.loadImageProfile(
                userHolder.originalProfilePicUrl,
                R.drawable.ic_profile_default
            )
        }
    }

    private fun setProfileMenuClickListener() {
        profileView?.setOnClickListener {
            showHideProfileMenu()
        }

        view?.apply {
            tvProfileProfile.setOnClickListener {
                ProfileActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvChatProfile.setOnClickListener {
                ChatConversationActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvPlaylistsProfile.setOnClickListener {
                PlaylistsActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvDownloadProfile.setOnClickListener {
                DownloadsActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvJournalProfile.setOnClickListener {
                if(!(UserHolder.EnumUserModulePermission.JOURNAL.permission?.canAccess ?: true)){
//                    SubscriptionActivity.startActivityForResult(
//                        this@ProfileMenuHelper
//                    )
                    return@setOnClickListener
                }
                JournalListingActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvCalendarProfile.setOnClickListener {
                CalendarActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvMeditationTimerProfile.setOnClickListener {
                MeditationTimerActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvCommunityProfile.setOnClickListener {
                CommunityActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvSettingProfile.setOnClickListener {
                SettingsActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvStatsProfile.setOnClickListener {
                StatisticsActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvHistoryProfile.setOnClickListener {
                HistoryActivity.startActivity(activity as MainActivity)
                hideProfileMenu()
            }
            tvLogoutProfile.setOnClickListener {
                logoutConfirmationDialog()
            }
        }
    }

    private fun hideProfileMenu() {
        view?.fmRoot?.gone()
    }

    private fun showHideProfileMenu() {
        if (view?.fmRoot?.isVisible() == true) {
            view?.fmRoot?.gone()

            if (userHolder.getChatCount == "0")
                chatBadgeCount?.gone()
            else
                chatBadgeCount?.visible()
        } else {
            activity?.let {
                if (it.isNetwork()) {
                    chatBadgeCount?.gone()
                    view?.fmRoot?.visible()
                }
                (it as MainActivity).hideWellnessDialog()
            }
        }
    }

    private fun logoutConfirmationDialog() {
        val title = ""
        val desctiption = "" + activity?.getString(R.string.logout_msg)

        val builder = activity?.let { AlertDialog.Builder(it, R.style.DialogTheme) }

        builder?.setTitle("" + title)?.setMessage("" + desctiption)?.setCancelable(true)
            ?.setPositiveButton(
                "" + activity?.getString(R.string.yes)
            ) { dialog, which ->
                dialog!!.dismiss()
                if (activity?.isNetwork() == true)
                    viewModel?.userLogout("", baseView!!, disposable!!)
            }?.setNegativeButton(
                "" + activity?.getString(R.string.no)
            ) { dialog, _ -> dialog!!.dismiss() }
        val alert = builder?.create()
        alert?.show()
    }

}