package com.app.taocalligraphy.ui.field_healing.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentEmotionsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.field_healing.adapter.HealingNamesAdapter
import com.app.taocalligraphy.ui.field_healing_detail.FieldHealingDetailActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EmotionsFragment : BaseFragment<FragmentEmotionsBinding>(),
    HealingNamesAdapter.OnAdapterItemClickListener {

    override fun getLayoutId() = R.layout.fragment_emotions

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    override fun initView() {

        val namesArray = arrayOf(
            getString(R.string.happiness),
            getString(R.string.healing_anger),
            getString(R.string.healing) + "\n" + getString(R.string.depression) + "\n" + getString(R.string.anxiety),
            getString(R.string.healing_worry),
            getString(R.string.healing_sadness),
            getString(R.string.healing_fear),
            getString(R.string.healing) + "\n" + getString(R.string.addication)
        )
        val mAdapter = HealingNamesAdapter(namesArray, this)
        mBinding.rvListOfNames.adapter = mAdapter

    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

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

    }

    override fun onNameClick(mName: String) {
        Toast.makeText(requireContext(), mName, Toast.LENGTH_LONG).show()
        FieldHealingDetailActivity.startActivity(requireActivity() as AppCompatActivity)
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