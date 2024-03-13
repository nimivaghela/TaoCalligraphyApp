package com.app.taocalligraphy.ui.challenges

import android.content.Intent
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityChallengesDetailBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.challenges.fragment.ChallengeProgressFragment
import com.app.taocalligraphy.ui.challenges.fragment.ChallengeRewardsFragment
import com.app.taocalligraphy.ui.challenges.fragment.ChallengeStepsFragment
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.TabsSelectSingleAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChallengesDetailActivity : BaseActivity<ActivityChallengesDetailBinding>(),
    TabsSelectSingleAdapter.SingleSelectItemClickListener {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ChallengesDetailActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_challenges_detail
    }

    val tabsSelectSingleAdapter by lazy {
        return@lazy TabsSelectSingleAdapter(this,mTabsList, this)
    }

    private lateinit var challengeStepsFragment: ChallengeStepsFragment
    private lateinit var challengeProgressFragment: ChallengeProgressFragment
    private lateinit var challengeRewardsFragment: ChallengeRewardsFragment

    private var mTabsList = mutableListOf<String>()

    override fun initView() {
        setAnimationImages()
        setTabs()
    }

    private fun setAnimationImages() {
        val imagesToShow = intArrayOf(
            R.drawable.ic_gratitude_thumb,
            R.drawable.ic_noon_refresh_recharge,
            R.drawable.ic_evening_gratitude,
            R.drawable.ic_sound_sleep
        )
        animateImages(mBinding.ivBannerImages, imagesToShow, 0, true)
    }

    private fun setTabs() {
        challengeStepsFragment = ChallengeStepsFragment()
        challengeProgressFragment = ChallengeProgressFragment()
        challengeRewardsFragment = ChallengeRewardsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, challengeStepsFragment)
            .commitAllowingStateLoss()

        mTabsList.add(getString(R.string.STEPS))
        mTabsList.add(getString(R.string.REWARDS))
        mTabsList.add(getString(R.string.PROGRESS))
        mBinding.rvProgramsTab.adapter = tabsSelectSingleAdapter
    }

    private fun animateImages(
        imageView: ImageView,
        images: IntArray,
        imageIndex: Int,
        forever: Boolean
    ) {
        val fadeInDuration = 1500 // Configure time values here
        val timeBetween = 3000
        val fadeOutDuration = 1500
        imageView.visibility =
            View.INVISIBLE //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex])
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() // add this
        fadeIn.duration = fadeInDuration.toLong()
        val fadeOut: Animation = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() // and this
        fadeOut.startOffset = (fadeInDuration + timeBetween).toLong()
        fadeOut.duration = fadeOutDuration.toLong()
        val animation = AnimationSet(false) // change to false
        animation.addAnimation(fadeIn)
        animation.addAnimation(fadeOut)
        animation.repeatCount = 1
        imageView.animation = animation
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                if (images.size - 1 > imageIndex) {
                    animateImages(
                        imageView,
                        images,
                        imageIndex + 1,
                        forever
                    ) //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animateImages(
                            imageView,
                            images,
                            0,
                            forever
                        ) //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }


    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onSingleItemClick(tabText: String, selectedPos: Int) {
        when (tabText) {
            getString(R.string.STEPS) -> {
                mBinding.cardJoin.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, challengeStepsFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.REWARDS) -> {
                mBinding.cardJoin.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, challengeRewardsFragment)
                    .commitAllowingStateLoss()
            }
            getString(R.string.PROGRESS) -> {
                mBinding.cardJoin.visibility = View.GONE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, challengeProgressFragment)
                    .commitAllowingStateLoss()
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