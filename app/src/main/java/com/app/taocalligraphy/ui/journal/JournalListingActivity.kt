package com.app.taocalligraphy.ui.journal

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityJournalListingBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.journal_data_models.JournalDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.journal.adapter.JournalListAdapter
import com.app.taocalligraphy.ui.journal.viewmodel.JournalViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_journal_listing.*
import okhttp3.internal.filterList
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class JournalListingActivity : BaseActivity<ActivityJournalListingBinding>(),
    JournalListAdapter.JournalItemClickListener {
    override var TAG = "JournalListingActivity-->"
    private val mViewModel: JournalViewModel by viewModels()


    companion object {
        var instance: JournalListingActivity? = null

        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, JournalListingActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_journal_listing
    }

    private val journalListAdapter by lazy {
        return@lazy JournalListAdapter(this@JournalListingActivity)
    }

    private var createJournalResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.data?.getBooleanExtra("showSnackbar", false) == true) {
                longToast(
                    result.data?.getStringExtra("message") ?: "",
                    result.data?.getStringExtra("type") ?: SUCCESS
                )
            }
            callFetchJournalListDataAPI("")
        }

    override fun initView() {
        instance = this
        setupToolbar()
        mBinding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mBinding.etSearch.text.toString().trim().isNotEmpty()) {
//                    callFetchJournalListDataAPI(searchText)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.please_enter_search_text),
                        Toast.LENGTH_LONG
                    ).show()
//                    callFetchJournalListDataAPI("")
                }
            }
            false
        })
        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect: Rect = Rect()
            mBinding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = mBinding.root.rootView.height
            val keyboardHeight = screenHeight - rect.bottom
            if (keyboardHeight > screenHeight * 0.15) {
                mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_gold_35dp)
            } else {
                if (mBinding.etSearch.text.toString().isEmpty()) {
                    mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_white_35dp)
                }
            }
        }
        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.swipeToRefreshLayout.isRefreshing = false
            mBinding.etSearch.text?.clear()
            callFetchJournalListDataAPI("")
        }
        if(mViewModel.responseJournalDataList.size == 0)
        callFetchJournalListDataAPI("")
        else
        updateView()
    }

    private fun setupToolbar() {
        if (!isTabletLandScape(this))
            mBinding.toolbar.cardNewJournal.visible()
        mBinding.toolbar.ivBackToolbar.visible()
        mBinding.toolbar.cardProfile.visible()

        mBinding.toolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )

        mBinding.etSearch.addTextChangedListener { searchKeyword ->
            searchInJournal(searchKeyword)
//            callSearchJournalAPI(it.toString())
        }

    }

    private fun searchInJournal(searchKeyword: Editable?) {

        journalListAdapter.searchText = searchKeyword.toString()
        mViewModel.searchText = searchKeyword.toString()
        searchKeyword?.length?.let {
            if (it >= 1) {
                mViewModel.searchText = searchKeyword.toString()
                mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_gold_35dp)
                mBinding.ivClearSearch.visible()
            } else {
                mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_white_35dp)
                mBinding.ivClearSearch.gone()
            }
        } ?: kotlin.run {

        }

        if (searchKeyword.toString().isEmpty()) {
//                callSearchJournalAPI("")
            if (mViewModel.responseJournalDataList.isEmpty()) {
                mBinding.rvJournal.gone()
                mBinding.tvNotFound.visible()
                mBinding.tvNotFound.text = getString(R.string.no_journal_found)
            } else {
                setJournalAdapterData(mViewModel.responseJournalDataList)
                mBinding.rvJournal.visible()
                mBinding.tvNotFound.gone()
            }

        } else {
            val searchList = mViewModel.responseJournalDataList.filter {
                (it.title!!.contains(
                    searchKeyword.toString(),
                    true
                )) || (it.journalEntry!!.contains(
                    searchKeyword.toString(), true
                ))
            }
            mViewModel.searchJournalDataList.clear()
            mViewModel.searchJournalDataList.addAll(searchList)
            setJournalAdapterData(mViewModel.searchJournalDataList)

            if (mViewModel.searchJournalDataList.isEmpty()) {
                mBinding.rvJournal.gone()
                mBinding.tvNotFound.visible()
                setNoJournalText(mViewModel.searchText)
//                    callSearchJournalAPI(searchText)
            } else {
                mBinding.rvJournal.visible()
                mBinding.tvNotFound.gone()
            }
        }
    }

    private fun callSearchJournalAPI(searchText: String) {
        callFetchJournalListDataAPI(searchText)
    }

    override fun observeApiCallbacks() {
        mViewModel.fetchJournalListDataResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.progressBar.progressBar, requestState.progress)
                requestState.apiResponse?.data?.journal?.let { journalList ->
                    mViewModel.responseJournalDataList.clear()
                    mViewModel.responseJournalDataList.addAll(journalList)

                    updateView()
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_401 -> {
                                    WelcomeLoginActivity.startActivity(this@JournalListingActivity)
                                    WelcomeLoginActivity.isFromLogin = true
                                    finishAffinity()
                                }
                                Constants.StatusCode.STATUS_404 -> {
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
        }
        mViewModel.updatePinUnpinJournalStatus.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.progressBar.progressBar, requestState.progress)
                requestState.apiResponse?.let {
                    it.message?.let {
                        callFetchJournalListDataAPI("")
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_401 -> {
                                    WelcomeLoginActivity.startActivity(this@JournalListingActivity)
                                    WelcomeLoginActivity.isFromLogin = true
                                    finishAffinity()
                                }
                                Constants.StatusCode.STATUS_404 -> {
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
                mViewModel.updatePinUnpinJournalStatus.postValue(null)
            }
        }

        mViewModel.deleteJournalFromList.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.progressBar.progressBar, requestState.progress)
                requestState.apiResponse?.let {
                    it.message?.let {
                        response.apiResponse = null
                        longToast(
                            response.apiResponse?.message
                                ?: getString(R.string.journal_deleted_successfully),
                            SUCCESS
                        )
                        callFetchJournalListDataAPI("")
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_401 -> {
                                    WelcomeLoginActivity.startActivity(this@JournalListingActivity)
                                    WelcomeLoginActivity.isFromLogin = true
                                    finishAffinity()
                                }
                                Constants.StatusCode.STATUS_404 -> {
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
            mViewModel.deleteJournalFromList.postValue(null)
        }
    }

    private fun updateView() {
        mViewModel.responseJournalDataList.let { journalList ->

            setJournalAdapterData(journalList)

            mBinding.rvJournal.visibility =
                if (mViewModel.responseJournalDataList.isEmpty()) GONE else VISIBLE

            mBinding.tvNotFound.visibility =
                if (mViewModel.responseJournalDataList.isEmpty()) VISIBLE else GONE

            if (TextUtils.isEmpty(mViewModel.searchText.trim()) && !mBinding.rvJournal.isVisible()) {
                mBinding.rlSearchBackground.gone()
                mBinding.tvNotFound.text = getString(R.string.no_journal_found)
            } else {
                mBinding.rlSearchBackground.visible()
                setNoJournalText(mViewModel.searchText)
            }

            if (mBinding.etSearch.text.toString().isNotEmpty() && journalList.size > 0)
                searchInJournal(mBinding.etSearch.text)

        }
    }

    private fun callFetchJournalListDataAPI(searchText: String) {
        if (TextUtils.isEmpty(searchText.trim()))
            hideKeyboard()
        mViewModel.fetchJournalDataById.value = null
        mViewModel.fetchJournalListData(
            searchText ?: "",
            this@JournalListingActivity,
            mDisposable
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setJournalAdapterData(list: ArrayList<JournalDataModel>?/*, isFromSearch: Boolean*/) {
        mViewModel.journalDataList.clear()

        mViewModel.pinJournalList = arrayListOf()
        mViewModel.pinJournalList = list?.filterList {
            isPinned == true
        }!!.map {
            it
        }

        val isDataAvailable = mViewModel.pinJournalList.isNotEmpty()
        if (isDataAvailable) {
            mViewModel.journalDataList.add(
                JournalDataModel(
                    "",
                    "",
                    "",
                    "",
                    false,
                    true,
                    getString(R.string.pinned),
                    isPinnedJournalDataAvailable = isDataAvailable
                )
            )
        }


        mViewModel.pinJournalDataList.clear()

        mViewModel.pinJournalDataList.addAll(mViewModel.pinJournalList)
        mViewModel.pinJournalDataList.forEachIndexed { index, journalDataModel ->
            mViewModel.pinJournalDataList[index].index = index + 1
        }

        mViewModel.journalDataList.addAll(mViewModel.pinJournalDataList)

        mViewModel.unPinJournalList = arrayListOf()
        mViewModel.unPinJournalList = list.filterList {
            isPinned == false
        }.map {
            it
        }

        mViewModel.unPinJournalDataList.clear()

        mViewModel.unPinJournalDataList.addAll(mViewModel.unPinJournalList)

        if (mViewModel.unPinJournalDataList.isNotEmpty()) {
            mViewModel.journalDataList.add(
                JournalDataModel(
                    "", "", "", "", false, true, getString(R.string.by_date),
                    isPinnedJournalDataAvailable = false
                )
            )
        }

        mViewModel.unPinJournalDataList.forEachIndexed { index, _ ->
            mViewModel.unPinJournalDataList[index].index = index + 1
        }

        mViewModel.journalDataList.addAll(mViewModel.unPinJournalDataList)
        mViewModel.searchJournalDataList.clear()
        mViewModel.searchJournalDataList.addAll(mViewModel.journalDataList)
        val callback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                try {
                    val position = viewHolder.bindingAdapterPosition

                    if (direction == ItemTouchHelper.RIGHT) {
                        mViewModel.fetchJournalListDataResponse.value = null
                        callUpdatePinUnpinJournalStatusAPI(mViewModel.journalDataList[position].journalId)
                    } else if (direction == ItemTouchHelper.LEFT) {
                        deleteJournalConfirmationDialog(position)
//                        callDeleteJournalById(journalDataList[position].journalId)
                    }

                } catch (e: Exception) {
                }
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.bindingAdapterPosition
                return if (!mViewModel.journalDataList[position].isTitle) {
                    super.getSwipeDirs(recyclerView, viewHolder)
                } else {
                    0
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (mViewModel.journalDataList[viewHolder.bindingAdapterPosition].isPinned == true) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addSwipeLeftBackgroundColor(
                            ContextCompat.getColor(
                                this@JournalListingActivity,
                                R.color.red
                            )
                        )
                        .addSwipeLeftActionIcon(R.drawable.vd_delete_white)
                        .addSwipeRightBackgroundColor(
                            ContextCompat.getColor(
                                this@JournalListingActivity,
                                R.color.dark_grey
                            )
                        )
                        .addSwipeRightActionIcon(R.drawable.vd_unstick)
                        .setSwipeRightLabelColor(Color.WHITE)
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                } else {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addSwipeLeftBackgroundColor(
                            ContextCompat.getColor(
                                this@JournalListingActivity,
                                R.color.red
                            )
                        )
                        .addSwipeLeftActionIcon(R.drawable.vd_delete_white)
                        .addSwipeRightBackgroundColor(
                            ContextCompat.getColor(
                                this@JournalListingActivity,
                                R.color.dark_grey
                            )
                        )
                        .addSwipeRightActionIcon(R.drawable.vd_stick_top)
                        .setSwipeRightLabelColor(Color.WHITE)
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(mBinding.rvJournal)
        mBinding.rvJournal.adapter = journalListAdapter
        journalListAdapter.journalList.clear()
        journalListAdapter.journalList.addAll(mViewModel.journalDataList)
        journalListAdapter.notifyDataSetChanged()
    }

    override fun handleListener() {
        mBinding.ivClearSearch.clickWithDebounce {
            mBinding.etSearch.setText("")
            mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_white_35dp)
            mBinding.ivClearSearch.visibility = View.GONE

//            callSearchJournalAPI("")
        }
        mBinding.toolbar.cardNewJournal.clickWithDebounce {
            CreateNewJournalActivity.isFromEditJournal = false
            CreateNewJournalActivity.startActivityWithResult(this, createJournalResult)
        }

        mBinding.cardNewJournalView?.clickWithDebounce {
            CreateNewJournalActivity.isFromEditJournal = false
            CreateNewJournalActivity.startActivityWithResult(this, createJournalResult)
        }

        mBinding.toolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.toolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this, JournalListingActivity::class.java.simpleName)
        }
    }

    private fun callUpdatePinUnpinJournalStatusAPI(journalId: String?) {
        mViewModel.updatePinUnpinJournalStatus(
            journalId,
            this@JournalListingActivity,
            mDisposable
        )
    }

    private fun callDeleteJournalById(journalId: String?) {
        mViewModel.deleteJournalFromList(
            journalId,
            this@JournalListingActivity,
            mDisposable
        )
    }

    override fun onRestart() {
        mViewModel.fetchJournalListDataResponse.value = null
        mBinding.etSearch.text?.clear()

        super.onRestart()
    }

    override fun onJournalItemClick(adapterPosition: Int) {
        CreateNewJournalActivity.journalId = mViewModel.journalDataList[adapterPosition].journalId
        CreateNewJournalActivity.isFromEditJournal = true
        CreateNewJournalActivity.startActivityWithResult(this, createJournalResult)
    }

    override fun onJournalDeleteClick(adapterPosition: Int) {
        deleteJournalConfirmationDialog(adapterPosition)
    }

    override fun onJournalPinUnpinClick(adapterPosition: Int) {
        mViewModel.fetchJournalListDataResponse.value = null
        callUpdatePinUnpinJournalStatusAPI(mViewModel.journalDataList[adapterPosition].journalId)
    }

    private fun deleteJournalConfirmationDialog(position: Int) {
        val title = ""
        val desctiption = "" + getString(R.string.are_you_sure_want_to_delete_journal_with_ques)

        val builder = let { AlertDialog.Builder(it, R.style.DialogTheme) }
        if (builder != null) {
            builder.setTitle("" + title)
                .setMessage("" + desctiption)
                .setCancelable(false)
                .setPositiveButton(
                    "" + getString(R.string.yes)
                ) { dialog, _ ->
                    dialog!!.dismiss()
                    if (isNetwork()) callDeleteJournalById(mViewModel.journalDataList[position].journalId)
                }
                .setNegativeButton(
                    "" + getString(R.string.no)
                ) { dialog, _ ->
                    journalListAdapter.journalList.clear()
                    journalListAdapter.journalList.addAll(mViewModel.journalDataList)
                    journalListAdapter.notifyDataSetChanged()
                    dialog!!.dismiss()
                }
        }
        val alert = builder.create()
        alert.show()
    }

    /* private fun openHomeScreen() {
         MainActivity.startActivity(this)
         finishAffinity()
     }*/

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    /* override fun onBackPressed() {
         openHomeScreen()
     }*/

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

    private fun setNoJournalText(searchText: String) {
        val span = SpannableStringBuilder()
        span.append("\"${getString(R.string.no_journal_found_for_search)} ")

        val start = span.length

        span.append(searchText)
        span.setSpan(
            TextAppearanceSpan(this, R.style.TextViewJostRegularItalicStyle),
            start,
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.append("\"")
        mBinding.tvNotFound.text = span
    }
}