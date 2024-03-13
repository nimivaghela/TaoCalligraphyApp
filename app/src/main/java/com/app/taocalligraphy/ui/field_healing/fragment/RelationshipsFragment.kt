package com.app.taocalligraphy.ui.field_healing.fragment

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentRelationshipsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.field_healing.adapter.HealingNamesAdapter
import com.app.taocalligraphy.ui.field_healing_detail.FieldHealingDetailActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RelationshipsFragment : BaseFragment<FragmentRelationshipsBinding>(),
    HealingNamesAdapter.OnAdapterItemClickListener {

    override fun getLayoutId() = R.layout.fragment_relationships

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    override fun initView() {

        val namesArray = arrayOf(
            "True Love",
            "Self Love",
            "Parents",
            "Children",
            "Grandparents",
            "Siblings",
            "Pets",
            "Friends",
            "Colleagues"
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