package com.app.taocalligraphy.ui.field_healing.fragment

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentPhysicalBodyBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants.INFO
import com.app.taocalligraphy.ui.field_healing_detail.FieldHealingDetailActivity
import com.app.taocalligraphy.utils.extensions.longToast
import com.richpath.RichPath
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PhysicalBodyFragment : BaseFragment<FragmentPhysicalBodyBinding>() {

    override fun getLayoutId() = R.layout.fragment_physical_body

    override fun displayMessage(message: String) {

    }

    override fun initView() {
    }

    override fun observeApiCallbacks() {

    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.ivMale.setOnClickListener {
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemalePhysicalBody.visibility = View.GONE
            mBinding.ivMalePhysicalBody.visibility = View.VISIBLE
        }
        mBinding.ivFemale.setOnClickListener {
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMalePhysicalBody.visibility = View.GONE
            mBinding.ivFemalePhysicalBody.visibility = View.VISIBLE
        }

        mBinding.ivMalePhysicalBody.onPathClickListener = object : RichPath.OnPathClickListener {
            override fun onClick(richPath: RichPath) {
                val pathName = richPath.name
                when {
                    pathName.equals(getString(R.string.mouth_lips_teeth_and_gums)) -> {
                        activity?.longToast(pathName!!, INFO)
//                        activity?.longToast(pathName!!,_root_ide_package_.com.app.taocalligraphy.other.Constants.INFO)
                    }
                    pathName.equals(getString(R.string.neck)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.shoulders)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.elbows)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.wrists)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.hands)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.knees)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.ankles)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.feet)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.hipbones)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.muscles)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.nose)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.ears)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.eyes)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.lower_back)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                }
                FieldHealingDetailActivity.startActivity(requireActivity() as AppCompatActivity)
            }
        }

        mBinding.ivFemalePhysicalBody.onPathClickListener = object : RichPath.OnPathClickListener {
            override fun onClick(richPath: RichPath) {
                val pathName = richPath.name
                when {
                    pathName.equals(getString(R.string.mouth_lips_teeth_and_gums)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.neck)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.shoulders)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.elbows)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.wrists)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.hands)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.knees)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.ankles)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.feet)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.hipbones)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.muscles)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.nose)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.ears)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.eyes)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                    pathName.equals(getString(R.string.lower_back)) -> {
                        activity?.longToast(pathName!!, INFO)
                    }
                }
                FieldHealingDetailActivity.startActivity(requireActivity() as AppCompatActivity)
            }
        }

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