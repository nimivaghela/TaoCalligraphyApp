package com.app.taocalligraphy.ui.challenges

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityChallengesListingBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.challenges.adapter.InProgressChallengesAdapter
import com.app.taocalligraphy.ui.challenges.adapter.NewUpcomingChallengesAdapter
import com.app.taocalligraphy.ui.challenges.dialog.CompletingMeditationRewardDialog
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChallengesListingActivity : BaseActivity<ActivityChallengesListingBinding>(),
    InProgressChallengesAdapter.ProgressClickListener {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ChallengesListingActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_challenges_listing
    }

    private val newUpcomingChallengesAdapter by lazy {
        return@lazy NewUpcomingChallengesAdapter()
    }
    private val inProgressChallengesAdapter by lazy {
        return@lazy InProgressChallengesAdapter(this)
    }

    override fun initView() {
        setupToolbar()
        mBinding.rvNewAndUpcomingChallenges.adapter = newUpcomingChallengesAdapter
        mBinding.rvNewAndUpcomingChallenges.layoutManager = object :
            LinearLayoutManager(this, HORIZONTAL, false) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                lp.width = (width * 0.8).toInt()
                return true
            }
        }
        mBinding.rvNewAndUpcomingChallenges.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItem: Int =
                    (mBinding.rvNewAndUpcomingChallenges.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (lastItem == (mBinding.rvNewAndUpcomingChallenges.layoutManager as LinearLayoutManager).itemCount - 1) {
                    mHandler.removeCallbacks(SCROLLING_RUNNABLE)
                    val postHandler = Handler()
                    postHandler.postDelayed({
                        count = 0
                        mBinding.rvNewAndUpcomingChallenges.adapter = null
                        mBinding.rvNewAndUpcomingChallenges.adapter = newUpcomingChallengesAdapter
                        mHandler.postDelayed(SCROLLING_RUNNABLE, 3000)
                    }, 3000)
                }
            }
        })
        mHandler.postDelayed(SCROLLING_RUNNABLE, 3000)

        mBinding.rvInProgressChallenges.adapter = inProgressChallengesAdapter
        mBinding.rvInProgressChallenges.layoutManager = object :
            LinearLayoutManager(this, HORIZONTAL, false) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                lp.width = (width * 0.5).toInt()
                return true
            }
        }
    }

    private fun setupToolbar() {
        setToolbar(
            mBinding.toolbar.innerToolbar,
            getString(R.string.challenges),
            true,
        )
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.llSilverHeart.setOnClickListener {
            showRewardCompleteDialog(1)
        }

        mBinding.llGoldenHeart.setOnClickListener {
            showRewardCompleteDialog(2)
        }

        mBinding.llDiamondHeart.setOnClickListener {
            showRewardCompleteDialog(3)
        }
    }

    override fun onProgressChallengesClick() {
        ChallengesDetailActivity.startActivity(this)
    }

    private fun showRewardCompleteDialog(type: Int) {
        val dialog =
            CompletingMeditationRewardDialog(this, type)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    val duration = 3000
    var count = 0
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val SCROLLING_RUNNABLE: Runnable = object : Runnable {
        override fun run() {
            if (count == newUpcomingChallengesAdapter.itemCount) count = 0
            if (count < newUpcomingChallengesAdapter.itemCount) {
                mBinding.rvNewAndUpcomingChallenges.smoothScrollToPosition(++count)
                mHandler.postDelayed(this, duration.toLong())
            }
        }
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