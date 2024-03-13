package com.app.taocalligraphy.ui.meditation_rooms_detail.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentSessionListBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.calendar.dialog.PaidSessionBookingDialog
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.RoomPastSessionListAdapter
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.RoomUpcomingSessionListAdapter
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.UpcomingSessionsListAdapter
import com.app.taocalligraphy.ui.meditation_session.AudioMeditationSessionActivity
import com.app.taocalligraphy.ui.meditation_session.CreateMeditationSessionActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SessionListFragment : BaseFragment<FragmentSessionListBinding>(),
    UpcomingSessionsListAdapter.SignupOptionListener,
    RoomUpcomingSessionListAdapter.OnSessionClick,
    RoomUpcomingSessionListAdapter.OnCalendarClicked {

    private val mUpcomingSessionsListAdapter by lazy {
        return@lazy UpcomingSessionsListAdapter(this)
    }

    private val mRoomUpcomingSessionListAdapter by lazy {
        return@lazy RoomUpcomingSessionListAdapter(this, this)
    }

    private val mRoomPastSessionListAdapter by lazy {
        return@lazy RoomPastSessionListAdapter()
    }

    override fun observeApiCallbacks() {

    }

    override fun getLayoutId() = R.layout.fragment_session_list

    override fun displayMessage(message: String) {

    }

    override fun initView() {
        mBinding.rvRoomsUpcomingSession.adapter = mUpcomingSessionsListAdapter
        mBinding.rvRoomsUpcomingSessionsList.adapter = mRoomUpcomingSessionListAdapter
        mBinding.rvRoomsPastSessionsList.adapter = mRoomPastSessionListAdapter

        mBinding.lblPastSeeAll.visibility = View.GONE

    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.btnScheduleSession.setOnClickListener {
            CreateMeditationSessionActivity.startActivity(
                requireActivity() as AppCompatActivity,
                true
            )
        }
    }

    override fun onSessionItemClick(isHost: Boolean) {
        AudioMeditationSessionActivity.startActivity(requireActivity() as AppCompatActivity, isHost)
    }

    override fun onSignUpClick() {
        val dialog =
            PaidSessionBookingDialog(requireContext())
        dialog.show()
    }

    override fun onCalendarClicked(position: Int) {

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