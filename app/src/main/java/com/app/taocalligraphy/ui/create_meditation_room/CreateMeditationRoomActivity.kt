package com.app.taocalligraphy.ui.create_meditation_room

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCreateMeditationRoomBinding
import com.app.taocalligraphy.models.dummy_models.WellnessCategoryListModelDummy
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.create_meditation_room.adapter.CreateRoomImagesAdapter
import com.app.taocalligraphy.ui.create_meditation_room.dialog.PendingApprovalRoomDialog
import com.app.taocalligraphy.ui.create_meditation_room.dialog.UnlockNewBackgroundDialog
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.WellnessCategorySmallAdapter
import com.app.taocalligraphy.utils.extensions.showUnlockImageDialog
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CreateMeditationRoomActivity : BaseActivity<ActivityCreateMeditationRoomBinding>(),
    CreateRoomImagesAdapter.OnImagesItemClickListener {

    private val mWellnessCategoryListModelDummy = mutableListOf<WellnessCategoryListModelDummy>()
    private var mWellnessCategorySmallAdapter: WellnessCategorySmallAdapter? = null
    private val mCreateRoomImagesAdapter by lazy {
        return@lazy CreateRoomImagesAdapter(this)
    }

    companion object {
        private var isCreateNewMeditation = false
        fun startActivity(activity: AppCompatActivity, isCreateNewMeditation: Boolean) {
            val intent = Intent(activity, CreateMeditationRoomActivity::class.java)
            this.isCreateNewMeditation = isCreateNewMeditation
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_create_meditation_room

    override fun initView() {
        setupToolbar()
        setUpData()
    }

    private fun setupToolbar() {
        if (isCreateNewMeditation) {
            setToolbar(mBinding.toolbar.innerToolbar, getString(R.string.create_room), true)
            mBinding.llDeleteRoom.visibility = View.GONE
        } else {
            setToolbar(mBinding.toolbar.innerToolbar, getString(R.string.edit_room), true)
            mBinding.llDeleteRoom.visibility = View.VISIBLE
        }
    }

    private fun setUpData() {
        animateSessionAccess()

        mWellnessCategoryListModelDummy.add(
            WellnessCategoryListModelDummy(
                getString(R.string.health),
                R.drawable.vd_health_icon
            )
        )
        mWellnessCategoryListModelDummy.add(
            WellnessCategoryListModelDummy(
                getString(R.string.relationships),
                R.drawable.vd_relationships_icon
            )
        )
        mWellnessCategoryListModelDummy.add(
            WellnessCategoryListModelDummy(
                getString(R.string.peak_performance),
                R.drawable.vd_peak_performance_icon
            )
        )
        mWellnessCategoryListModelDummy.add(
            WellnessCategoryListModelDummy(
                getString(R.string.business_and_finance),
                R.drawable.vd_business_finances_icon
            )
        )
        mWellnessCategoryListModelDummy.add(
            WellnessCategoryListModelDummy(
                getString(R.string.pregnancy),
                R.drawable.vd_pregnancy_icon
            )
        )
        mWellnessCategoryListModelDummy.add(
            WellnessCategoryListModelDummy(
                getString(R.string.children_and_education),
                R.drawable.vd_children_education_icon
            )
        )
        mBinding.rvSelectCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mWellnessCategorySmallAdapter = WellnessCategorySmallAdapter(
            false,
            mWellnessCategoryListModelDummy,
            object : WellnessCategorySmallAdapter.OnAdapterItemClickListener {
                override fun onAdapterClick(mClickedData: WellnessCategoryListModelDummy) {
                    //Toast.makeText(requireContext(), "${mClickedData.title}", Toast.LENGTH_SHORT).show()
                }
            })
        mBinding.rvSelectCategory.adapter = mWellnessCategorySmallAdapter

        mBinding.rvBannerImagesForCreateRoom.adapter = mCreateRoomImagesAdapter
    }

    override fun observeApiCallbacks() {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun handleListener() {
        mBinding.etSessionDescription.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }

        mBinding.rrAccessHeader.setOnClickListener {
            animateSessionAccess()
        }

        mBinding.tvPublicSession.setOnClickListener {
            animateSessionAccess()
            setTextViewOnSessionAccessTypeChange(
                getString(R.string.public_session),
                mBinding.tvPublicSession,
                mBinding.tvPrivateSession
            )
        }

        mBinding.tvPrivateSession.setOnClickListener {
            animateSessionAccess()
            setTextViewOnSessionAccessTypeChange(
                getString(R.string.private_session),
                mBinding.tvPrivateSession,
                mBinding.tvPublicSession
            )
        }

        mBinding.btnRequestApproval.setOnClickListener {
            showPendingApprovalDialog()
        }

    }

    private fun animateSessionAccess() {
        if (mBinding.llAccessType.isVisible) {
            mBinding.llAccessType.animate().also {
                it.withEndAction {
                    mBinding.llAccessType.isGone = true
                    mBinding.llAccessHeader.isVisible = true
                }
                it.duration = 200
                it.start()
            }
            mBinding.ivSessionAccessArrow.animate().rotation(0.0f).setDuration(200).start()
        } else {
            mBinding.llAccessType.animate().also {
                it.withEndAction {
                    mBinding.llAccessHeader.isGone = true
                    mBinding.llAccessType.isVisible = true
                }
                it.duration = 200
                it.start()
            }
            mBinding.ivSessionAccessArrow.animate().rotation(180.0f).setDuration(200).start()
        }
    }

    private fun setTextViewOnSessionAccessTypeChange(
        text: String,
        activeSession: AppCompatTextView,
        inActiveSession: AppCompatTextView
    ) {
        mBinding.tvSelectedSessionAccess.text = text
        activeSession.setTextColor(ContextCompat.getColor(this, R.color.gold))
        inActiveSession.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
    }

    override fun onImageClick(position: Int) {
        if (position == 3) {
            showUnlockImageDialog(this)
        }
    }


    private fun showPendingApprovalDialog() {
        val dialog = PendingApprovalRoomDialog(this)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
