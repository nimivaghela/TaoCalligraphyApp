package com.app.taocalligraphy.ui.meditation_rooms_list

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentMyRoomsMeditationRoomsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.create_meditation_room.CreateMeditationRoomActivity
import com.app.taocalligraphy.ui.create_meditation_room.dialog.NotApprovedRoomDialog
import com.app.taocalligraphy.ui.create_meditation_room.dialog.PendingApprovalRoomDialog
import com.app.taocalligraphy.ui.meditation_rooms_detail.TaoCalligraphyFieldsRoomsActivity
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.MyRoomsAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyRoomsMeditationRoomsFragment : BaseFragment<FragmentMyRoomsMeditationRoomsBinding>(),
    MyRoomsAdapter.OnMyRoomsItemClickListener {

    private var mMyRoomsAdapter: MyRoomsAdapter? = null

    override fun getLayoutId() = R.layout.fragment_my_rooms_meditation_rooms

    override fun displayMessage(message: String) {

    }

    override fun initView() {
        setData()
    }
    override fun observeApiCallbacks() {

    }
    private fun setData() {
        mMyRoomsAdapter = MyRoomsAdapter(requireContext(),this)
        mBinding.rvMyRooms.layoutManager = GridLayoutManager(requireContext(), 2)
        mBinding.rvMyRooms.adapter = mMyRoomsAdapter
    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun onMyRoomsAdapterClick(position: Int) {
        when (position) {
            0 -> {
                CreateMeditationRoomActivity.startActivity(
                    requireActivity() as AppCompatActivity,
                    true
                )
            }
            1 -> {
                TaoCalligraphyFieldsRoomsActivity.startActivity(
                    requireActivity() as AppCompatActivity,
                    3
                )
//                CreateMeditationRoomActivity.startActivity(
//                    requireActivity() as AppCompatActivity,
//                    false
//                )
            }
            2 -> {
                TaoCalligraphyFieldsRoomsActivity.startActivity(
                    requireActivity() as AppCompatActivity,
                    0
                )
//                CreateMeditationRoomActivity.startActivity(
//                    requireActivity() as AppCompatActivity,
//                    false
//                )
            }
            3 -> {
                showPendingApprovalDialog()
            }
            4 -> {
                showNotApprovedDialog()
            }
        }
    }

    private fun showPendingApprovalDialog() {
        val dialog = PendingApprovalRoomDialog(requireContext())
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onMyRoomEditClick(position: Int) {
        when (position) {
            0 -> {

            }
            1 -> {
                CreateMeditationRoomActivity.startActivity(
                    requireActivity() as AppCompatActivity,
                    false
                )
            }
            2 -> {
                CreateMeditationRoomActivity.startActivity(
                    requireActivity() as AppCompatActivity,
                    false
                )
            }
            3 -> {

            }
            4 -> {
                CreateMeditationRoomActivity.startActivity(
                    requireActivity() as AppCompatActivity,
                    false
                )
            }
        }
    }

    private fun showNotApprovedDialog() {
        val dialog = NotApprovedRoomDialog(requireContext())
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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