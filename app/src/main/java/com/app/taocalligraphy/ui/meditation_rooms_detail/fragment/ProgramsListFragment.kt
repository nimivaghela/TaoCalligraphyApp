package com.app.taocalligraphy.ui.meditation_rooms_detail.fragment

import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentProgramsListBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.CategoryBaseProgramListResponse
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.ProgressListAdapter
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.MeditationRoomCatSmallAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProgramsListFragment : BaseFragment<FragmentProgramsListBinding>(),
    ProgressListAdapter.CategoryBaseProgramsSelectListener {

    private var mWellnessCategorySmallAdapter: MeditationRoomCatSmallAdapter? = null
    private val mProgressListAdapter by lazy {
        return@lazy ProgressListAdapter(this)
    }
    private var isCollapsed = true

    override fun getLayoutId() = R.layout.fragment_programs_list

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    override fun initView() {
        setUpData()
    }

    private fun setUpData() {
        mBinding.rvSelectCategoryExperienceMore.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvSelectCategoryExperienceMore.adapter = mWellnessCategorySmallAdapter
        mBinding.rvPrograms.adapter = mProgressListAdapter
    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.llFilterRating.setOnClickListener {
            if (isCollapsed) {
                mBinding.ivDownUpArrow.setImageResource(R.drawable.vd_medium_grey_up_arrow)
                val layoutParams = mBinding.llFilterRating.layoutParams
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                mBinding.llFilterRating.layoutParams = layoutParams
                isCollapsed = false
            } else {
                mBinding.ivDownUpArrow.setImageResource(R.drawable.vd_medium_grey_down_arrow)
                val layoutParams = mBinding.llFilterRating.layoutParams
                layoutParams.height = 80
                mBinding.llFilterRating.layoutParams = layoutParams
                isCollapsed = true
            }
        }

    }

    override fun onCategoryProgramsFavouriteClick(id: String, adapterPosition: Int) {

    }

    override fun onCategoryProgramsItemClick(data: ProgramDataModel?) {

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