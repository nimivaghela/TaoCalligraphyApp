package com.app.taocalligraphy.ui.meditation_rooms_list

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentAllMeditationRoomsBinding
import com.app.taocalligraphy.models.dummy_models.WellnessCategoryListModelDummy
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.*
import com.app.taocalligraphy.ui.meditation_rooms_detail.TaoCalligraphyFieldsRoomsActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AllMeditationRoomsFragment : BaseFragment<FragmentAllMeditationRoomsBinding>(),
    OfficialRoomsAdapter.OnOfficialRoomsItemClickListener,
    FeaturedRoomsAdapter.OnFeaturedRoomsItemClickListener,
    CommunityRoomsAdapter.OnCommunityRoomsItemClickListener {

    private val mWellnessCategoryListModelDummy = mutableListOf<WellnessCategoryListModelDummy>()
    private var mWellnessCategorySmallAdapter: WellnessCategorySmallAdapter? = null
    private lateinit var mLanguagesList: Array<String>
    private lateinit var mSortList: Array<String>
    private var mOfficialRoomsAdapter: OfficialRoomsAdapter? = null
    private var mFeaturedRoomsAdapter: FeaturedRoomsAdapter? = null
    private var mCommunityRoomsAdapter: CommunityRoomsAdapter? = null

    var selectedLanguage = getString(R.string.all)
    var selectedSortBy = getString(R.string.rating)

    override fun getLayoutId() = R.layout.fragment_all_meditation_rooms

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    override fun initView() {
        setUpData()
    }

    private fun setUpData() {
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
        mBinding.rvSelectCategoryExperienceMore.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mWellnessCategorySmallAdapter = WellnessCategorySmallAdapter(false,
            mWellnessCategoryListModelDummy,
            object : WellnessCategorySmallAdapter.OnAdapterItemClickListener {
                override fun onAdapterClick(mClickedData: WellnessCategoryListModelDummy) {
                    //Toast.makeText(requireContext(), "${mClickedData.title}", Toast.LENGTH_SHORT).show()
                }
            })
        mBinding.rvSelectCategoryExperienceMore.adapter = mWellnessCategorySmallAdapter

        mLanguagesList = arrayOf(getString(R.string.all), getString(R.string.deutsch), getString(R.string.english), getString(
                    R.string.espanol), getString(R.string.franscais), getString(R.string.nederlands))
        mSortList = arrayOf(getString(R.string.rating), getString(R.string.a_to_z), getString(R.string.z_to_a))

        mOfficialRoomsAdapter = OfficialRoomsAdapter(this)
        mBinding.rvOfficialRooms.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvOfficialRooms.adapter = mOfficialRoomsAdapter

        mFeaturedRoomsAdapter = FeaturedRoomsAdapter(this)
        mBinding.rvFeaturedRooms.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvFeaturedRooms.adapter = mFeaturedRoomsAdapter

        mCommunityRoomsAdapter = CommunityRoomsAdapter(this)
        mBinding.rvCommunityRooms.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCommunityRooms.adapter = mCommunityRoomsAdapter
    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.relLanguage.setOnClickListener {
            openLanguageSelectionDialog()
        }

        mBinding.relSort.setOnClickListener {
            openSortBySelectionDialog()
        }
    }

    private fun openSortBySelectionDialog() {
//        val dialog =
//            LanguageSelectionDialog(requireContext(), selectedSortBy,
//                mSortList,
//                object : LanguageSelectionDialog.LanguageSelectionListener {
//                    override fun onLanguageSelect(language: String) {
//                        mBinding.tvSortBy.text = language
//                        selectedSortBy = language
//                    }
//                }
//            )
//        dialog.show()
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun openLanguageSelectionDialog() {
//        val dialog =
//            LanguageSelectionDialog(requireContext(), selectedLanguage,
//                mLanguagesList,
//                object : LanguageSelectionDialog.LanguageSelectionListener {
//                    override fun onLanguageSelect(language: String) {
//                        mBinding.tvLanguage.text = language
//                        selectedLanguage = language
//                    }
//                }
//            )
//        dialog.show()
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onOfficialRoomsAdapterClick(position: Int) {
        TaoCalligraphyFieldsRoomsActivity.startActivity(requireActivity() as AppCompatActivity, 0)
    }

    override fun onFeaturedRoomsAdapterClick(position: Int) {
        TaoCalligraphyFieldsRoomsActivity.startActivity(requireActivity() as AppCompatActivity, 1)
    }

    override fun onCommunityRoomsAdapterClick(position: Int) {
        TaoCalligraphyFieldsRoomsActivity.startActivity(requireActivity() as AppCompatActivity, 2)
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