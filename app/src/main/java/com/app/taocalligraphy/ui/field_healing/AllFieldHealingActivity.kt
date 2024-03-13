package com.app.taocalligraphy.ui.field_healing

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityAllFieldHealingBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.field_healing.adapter.HealingNamesAdapter
import com.app.taocalligraphy.ui.field_healing.adapter.PageAdapter
import com.app.taocalligraphy.ui.field_healing.adapter.ViewPagerAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.google.android.material.tabs.TabLayoutMediator
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AllFieldHealingActivity : BaseActivity<ActivityAllFieldHealingBinding>() {
    private var adapterData: ArrayList<PageAdapter.Page> = arrayListOf()

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, AllFieldHealingActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_all_field_healing

    override fun initView() {
        setupToolbar()
//        setupTabs()
        setUpCarousealView()

        val firstSlideLeft: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        firstSlideLeft.duration = 400
        mBinding.viewPager.startAnimation(firstSlideLeft)
    }

    private fun setUpCarousealView() {
        /*adapterData = mutableListOf(
            PageAdapter.Page(getString(R.string.physical_body)),
            PageAdapter.Page(getString(R.string.internal_organs)),
            PageAdapter.Page(getString(R.string.emotions)),
            PageAdapter.Page(getString(R.string.relationships_capital)),
            PageAdapter.Page(getString(R.string.body_channels))
        )*/
        var pageData: PageAdapter.Page = PageAdapter.Page(getString(R.string.physical_body))
        adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.internal_organs))
        adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.emotions))
        adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.relationships_capital))
        adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.body_channels))
        adapterData.add(pageData)

//        mBinding.carouselViewSideBySidePreviewScale.start(6, TimeUnit.SECONDS)

        val pageAdapter = PageAdapter(adapterData, object : PageAdapter.OnItemClicked {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClicked(position: Int) {
                mBinding.carouselViewSideBySidePreviewScale.apply {
                    if (viewPager2.currentItem != position) {
                        viewPager2.currentItem = position

                        (adapter as PageAdapter).selectedPos = position
                        adapter.notifyDataSetChanged()
                        manageViewByPosition(position)

                    }
                    mBinding.viewPager.currentItem = position
                }
            }
        }).apply {
//            setData(adapterData)
        }

        mBinding.carouselViewSideBySidePreviewScale.apply {
            adapter = pageAdapter

            viewPager2.currentItem = 0
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    (adapter as PageAdapter).selectedPos = position
                    adapter.notifyDataSetChanged()

                    manageViewByPosition(position)

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
        }
//        pageAdapter.setData(adapterData)
    }

    private fun manageViewByPosition(position: Int) {
        val internalOrgansList = arrayOf(
            getString(R.string.adrenal_glands),
            getString(R.string.blood),
            getString(R.string.blood_vessels),
            getString(R.string.bones),
            getString(R.string.brain),
            getString(R.string.gallbladder),
            getString(R.string.heart),
            getString(R.string.kidneys),
            getString(R.string.large_intestine),
            getString(R.string.liver),
            getString(R.string.lungs),
            getString(R.string.pancreas),
            getString(R.string.skin),
            getString(R.string.spinal_cord),
            getString(R.string.spleen),
            getString(R.string.stomach),
            getString(R.string.small_intestine)
        )
        val emotionsList = arrayOf(
            getString(R.string.happiness),
            getString(R.string.healing_anger),
            getString(R.string.healing) + "\n" + getString(R.string.depression) + "\n" + getString(R.string.anxiety),
            getString(R.string.healing_worry),
            getString(R.string.healing_sadness),
            getString(R.string.healing_fear),
            getString(R.string.healing) + "\n" + getString(R.string.addication)
        )

        val relationShipList = arrayOf(
            getString(R.string.true_love),
            getString(R.string.self_love),
            getString(R.string.parents),
            getString(R.string.children),
            getString(R.string.grandparents),
            getString(R.string.siblings),
            getString(R.string.pets),
            getString(R.string.friends),
            getString(R.string.colleagues)
        )

        when (position) {
            0 -> {
                mBinding.tvSelections.text = getString(R.string._16_selections)

                mBinding.llPhysicalBody.isVisible = true
                mBinding.llTwoThreeFour.isGone = true
                mBinding.llBodyChannel.isGone = true
            }
            1 -> {
                mBinding.tvSelections.text = getString(R.string._22_selections)

                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isVisible = true
                mBinding.llBodyChannel.isGone = true

                val mAdapter = HealingNamesAdapter(
                    internalOrgansList,
                    object : HealingNamesAdapter.OnAdapterItemClickListener {
                        override fun onNameClick(mName: String) {

                        }
                    })

                mBinding.rvListOfNames.adapter = mAdapter
            }
            2 -> {
                mBinding.tvSelections.text = getString(R.string._7_selections)

                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isVisible = true
                mBinding.llBodyChannel.isGone = true

                val mAdapter = HealingNamesAdapter(
                    emotionsList,
                    object : HealingNamesAdapter.OnAdapterItemClickListener {
                        override fun onNameClick(mName: String) {

                        }
                    })

                mBinding.rvListOfNames.adapter = mAdapter
            }
            3 -> {
                mBinding.tvSelections.text = getString(R.string._9_selections)


                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isVisible = true
                mBinding.llBodyChannel.isGone = true

                val mAdapter = HealingNamesAdapter(
                    relationShipList,
                    object : HealingNamesAdapter.OnAdapterItemClickListener {
                        override fun onNameClick(mName: String) {

                        }
                    })

                mBinding.rvListOfNames.adapter = mAdapter
            }
            4 -> {
                mBinding.tvSelections.text = getString(R.string._3_selections)

                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isGone = true
                mBinding.llBodyChannel.isVisible = true
            }
        }
    }

    private fun setupToolbar() {
        setToolbar(mBinding.toolbar.innerToolbar, getString(R.string.field_healing), true)
    }

    @SuppressLint("ClickableViewAccessibility")
    /*private fun setupTabs() {
        val tabsArray = arrayOf(
            getString(R.string.physical_body),
            getString(R.string.internal_organs),
            getString(R.string.emotions),
            getString(R.string.relationships_capital),
            getString(R.string.body_channels)
        )

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        mBinding.viewPager.adapter = adapter

        mBinding.viewPager.setOnTouchListener(View.OnTouchListener { v, event ->
            return@OnTouchListener true
        })
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

        val tabs = mBinding.tabLayout.getChildAt(0) as ViewGroup

        for (i in 0 until tabs.childCount) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 0f
            layoutParams.marginStart = dpToPx(30.0f, applicationContext)
            layoutParams.marginEnd = dpToPx(30.0f, applicationContext)
            tab.layoutParams = layoutParams
            mBinding.tabLayout.requestLayout()
        }

        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        mBinding.tvSelections.text = getString(R.string._16_selections)
                    }
                    1 -> {
                        mBinding.tvSelections.text = getString(R.string._22_selections)
                    }
                    2 -> {
                        mBinding.tvSelections.text = getString(R.string._7_selections)
                    }
                    3 -> {
                        mBinding.tvSelections.text = getString(R.string._9_selections)
                    }
                    4 -> {
                        mBinding.tvSelections.text = getString(R.string._3_selections)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        *//*mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
          override fun onTabSelected(tab: TabLayout.Tab) {

          }

          override fun onTabUnselected(tab: TabLayout.Tab) {

          }

          override fun onTabReselected(tab: TabLayout.Tab) {}
      })*//*

        mBinding.viewPager.setCurrentItem(1, true)
        mBinding.viewPager.postDelayed(Runnable {
            mBinding.viewPager.animate().also {
                mBinding.viewPager.setCurrentItem(0, true)
            }
        }, 100)

    }*/

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {
        mBinding.ivPBMale.setOnClickListener {
            mBinding.ivPBFemale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivPBMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemalePhysicalBody.visibility = View.GONE
            mBinding.ivMalePhysicalBody.visibility = View.VISIBLE
        }

        mBinding.ivPBFemale.setOnClickListener {
            mBinding.ivPBMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivPBFemale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMalePhysicalBody.visibility = View.GONE
            mBinding.ivFemalePhysicalBody.visibility = View.VISIBLE
        }

        mBinding.ivMale.setOnClickListener {
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemaleInternalOrgans.visibility = View.GONE
            mBinding.ivMaleInternalOrgans.visibility = View.VISIBLE
        }

        mBinding.ivFemale.setOnClickListener {
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMaleInternalOrgans.visibility = View.GONE
            mBinding.ivFemaleInternalOrgans.visibility = View.VISIBLE
        }

        mBinding.ivBodyChannelMale.setOnClickListener {
            mBinding.ivBodyChannelFeMale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivBodyChannelMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemaleBody.visibility = View.GONE
            mBinding.ivMaleBody.visibility = View.VISIBLE
        }

        mBinding.ivBodyChannelFeMale.setOnClickListener {
            mBinding.ivBodyChannelMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivBodyChannelFeMale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMaleBody.visibility = View.GONE
            mBinding.ivFemaleBody.visibility = View.VISIBLE
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