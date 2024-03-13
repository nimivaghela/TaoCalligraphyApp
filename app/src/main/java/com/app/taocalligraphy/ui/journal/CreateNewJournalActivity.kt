package com.app.taocalligraphy.ui.journal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCreateNewJournalBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.journal_data_models.JournalDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.journal.viewmodel.JournalViewModel
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateNewJournalActivity() :
    BaseActivity<ActivityCreateNewJournalBinding>() {

    companion object {
        var isFromPostAssessment = false

        @JvmStatic
        fun startActivityWithResult(
            activity: AppCompatActivity,
            result: ActivityResultLauncher<Intent>? = null,
            title: String = "",
        ) {
            val intent = Intent(activity, CreateNewJournalActivity::class.java)
            intent.putExtra(Constants.Param.title, title)
            result?.launch(intent)
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//            activity.startActivityWithAnimation(intent)
        }

        fun startActivity(
            activity: AppCompatActivity,
            title: String = ""
        ) {
            isFromPostAssessment = true
            val intent = Intent(activity, CreateNewJournalActivity::class.java)
            intent.putExtra(Constants.Param.title, title)
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            activity.startActivityWithAnimation(intent)
        }

        var journalId: String? = ""
        var isFromEditJournal: Boolean = false

    }

    override fun getResource(): Int {
        return R.layout.activity_create_new_journal
    }

    private val mViewModel: JournalViewModel by viewModels()

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun initView() {
        setupToolbar()
        if(mViewModel.initTitle == "")
            mViewModel.initTitle = intent.extras?.getString(Constants.Param.title, "") ?: ""

        mBinding.edtTitle.setText(mViewModel.initTitle)

        mBinding.edtTitle.setOnFocusChangeListener { _, b ->
            if (b) {
                mBinding.llFormatTool.gone()
            }
        }

        mBinding.editor.setOnFocusChangeListener { _, b ->
            if (b) {
                mBinding.llFormatTool.visible()
            }
        }

        if (isFromEditJournal) {
            if(mViewModel.currentText == "")
            fetchJournalDataById()
            else
                mViewModel.journalData?.let { setJournalData(it) }

            mBinding.tvJournalDate.visible()
        } else {
            val currentDate = Date()
            val formattedDate: String = SimpleDateFormat("yyyy-MM-dd").format(currentDate)

            mBinding.tvJournalDate.text =
                getMonthFromDate(formattedDate) + " " +
                        getDayFromDate(formattedDate) +
                        ", " + getYearFromDate(formattedDate)
            mBinding.tvJournalDate.gone()
            mBinding.edtTitle.requestFocus()
        }

        mBinding.editor.setEditorFontColor(
            ContextCompat.getColor(
                this,
                R.color.secondary_black
            )
        )
        mBinding.editor.setEditorBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )
        if (isTablet(baseContext)) {
            mBinding.editor.setFontSize(80)
        } else {
            mBinding.editor.setFontSize(14)
        }

        mBinding.editor.setPlaceholder(getString(R.string.notes))
        mBinding.editor.setEditorHeight(200)

        mBinding.editor.setOnTextChangeListener {
            if (it.isNullOrEmpty() && mBinding.edtTitle.text.isNullOrEmpty()
            ) {
                mBinding.toolbar.ivSaveToolbar.alpha = 0.5f
                mBinding.toolbar.ivSaveToolbar.isEnabled = false
            } else {
                mBinding.toolbar.ivSaveToolbar.alpha = 1f
                mBinding.toolbar.ivSaveToolbar.isEnabled = true
            }
        }
        if (!isFromEditJournal) {
            mBinding.toolbar.ivSaveToolbar.alpha = 0.5f
            mBinding.toolbar.ivSaveToolbar.isEnabled = false
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mBinding.edtTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                mBinding.editor.focusEditor()
                return@setOnEditorActionListener true
            } else
                return@setOnEditorActionListener false
        }

        mBinding.editor.html = mViewModel.currentText

        setFontStyleView()
    }

    private fun fetchJournalDataById() {
        mViewModel.fetchJournalDataById(
            journalId,
            this@CreateNewJournalActivity,
            mDisposable
        )
    }

    private fun setupToolbar() {
        mBinding.toolbar.ivSaveToolbar.visible()
        mBinding.toolbar.ivBackToolbar.visible()

        if (isFromEditJournal) {
            mBinding.toolbar.ivDeleteToolbar.visible()
            mBinding.toolbar.ivShareToolbar.visible()
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.fetchJournalDataById.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { journalData ->
                        mViewModel.journalData = journalData
                        mViewModel.currenttitle = journalData.title ?: ""
                        mViewModel.currentText = journalData.journalEntry ?: ""
                        mViewModel.initTitle = mViewModel.currenttitle
                        mViewModel.initText = mViewModel.currentText

                        setJournalData(journalData)
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
                                    WelcomeLoginActivity.startActivity(this@CreateNewJournalActivity)
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
                mViewModel.fetchJournalDataById.postValue(null)
            }
        }

        mViewModel.createJournalResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.message?.let { journalData ->
                        if (mIsFromBackPress) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("showSnackbar", true)
                            resultIntent.putExtra("message", journalData)
                            resultIntent.putExtra("type", SUCCESS)
                            setResult(Activity.RESULT_OK, resultIntent)
                            if (isFromPostAssessment) {
                                JournalListingActivity.startActivity(this)
                                finish()
                            } else
                                finish()
                        } else {
                            isFromEditJournal = true
                            journalId = it.data?.journalId
                            mViewModel.initTitle = mBinding.edtTitle.text.toString()
                            mViewModel.initText =
                                if (mBinding.editor.html.isNullOrEmpty()) "" else mBinding.editor.html
                            initView()
                            longToast(journalData, SUCCESS)
                        }
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
                                    WelcomeLoginActivity.startActivity(this@CreateNewJournalActivity)
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
                mViewModel.createJournalResponse.postValue(null)
            }
        }

        mViewModel.editJournalResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.message?.let { journalData ->
                        if (mViewModel.isFromShare) {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${mBinding.edtTitle.text.toString()}\n\n${
                                        Html.fromHtml(
                                            mBinding.editor.html
                                        )
                                    }"
                                )
                                putExtra(Intent.EXTRA_TITLE, mBinding.edtTitle.text.toString())
                            }
                            startActivity(Intent.createChooser(shareIntent, null))
                        } else if (mIsFromBackPress) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("showSnackbar", true)
                            resultIntent.putExtra("message", journalData)
                            resultIntent.putExtra("type", SUCCESS)
                            setResult(Activity.RESULT_OK, resultIntent)
                            if (isFromPostAssessment) {
                                JournalListingActivity.startActivity(this)
                                finish()
                            } else
                                finish()
                        } else {
                            mViewModel.initTitle = mBinding.edtTitle.text.toString()
                            mViewModel.initText = mBinding.editor.html
                            longToast(journalData, it.type ?: SUCCESS)
                        }
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
                                    WelcomeLoginActivity.startActivity(this@CreateNewJournalActivity)
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
                mViewModel.editJournalResponse.postValue(null)
            }
        }

        mViewModel.deleteJournalFromList.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.message?.let { it1 ->
                        longToast(it1, it.type ?: SUCCESS)
                        finish()
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
                                    WelcomeLoginActivity.startActivity(this@CreateNewJournalActivity)
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

    @SuppressLint("SetTextI18n")
    private fun setJournalData(journalDataModel: JournalDataModel) {
        journalId = journalDataModel.journalId
        mBinding.edtTitle.setText(mViewModel.currenttitle)
        mBinding.edtTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mBinding.editor.html.isNullOrEmpty() && s.isNullOrEmpty()
                ) {
                    mBinding.toolbar.ivSaveToolbar.alpha = 0.5f
                    mBinding.toolbar.ivSaveToolbar.isEnabled = false
                } else {
                    mBinding.toolbar.ivSaveToolbar.alpha = 1f
                    mBinding.toolbar.ivSaveToolbar.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

//        mViewModel.initTitle = mViewModel.currenttitle
//        mViewModel.initText = mViewModel.currentText
        mBinding.editor.html = mViewModel.currentText
        mBinding.tvJournalDate.text =
            getMonthFromDate(journalDataModel.date).uppercase() + " " +
                    getDayFromDate(journalDataModel.date) +
                    ", " + getYearFromDate(journalDataModel.date)
    }

    private fun setFontStyleView() {
        if(mViewModel.isBoldSelect) {
            mBinding.ivBold.setImageResource(R.drawable.vd_bold_select)
            mBinding.editor.setBold()
        } else {
            mBinding.ivBold.setImageResource(R.drawable.vd_bold)
        }

        if(mViewModel.isItalicSelect) {
            mBinding.ivItalic.setImageResource(R.drawable.vd_italic_bold)
            mBinding.editor.setItalic()
        } else {
            mBinding.ivItalic.setImageResource(R.drawable.vd_italic)
        }

        if (mViewModel.isUnderlineSelect) {
            mBinding.ivUnderline.setImageResource(R.drawable.vd_underline_select)
            mBinding.viewUnderline.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.gold
                )
            )
            mBinding.editor.setUnderline()
        } else {
            mBinding.ivUnderline.setImageResource(R.drawable.vd_underline)
            mBinding.viewUnderline.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.dark_grey
                )
            )
        }

        if (mViewModel.isStrikethroughSelect) {
            mBinding.ivStrikethrough.setImageResource(R.drawable.vd_strikethrough_select)
            mBinding.editor.setStrikeThrough()
        } else {
            mBinding.ivStrikethrough.setImageResource(R.drawable.vd_strikethrough)
        }

    }


    override fun handleListener() {
        mBinding.toolbar.ivBackToolbar.setOnClickListener {
            onBackPressed()
        }


        mBinding.llBold.setOnClickListener {
            mViewModel.isBoldSelect = if (!mViewModel.isBoldSelect) {
                mBinding.ivBold.setImageResource(R.drawable.vd_bold_select)
                true
            } else {
                mBinding.ivBold.setImageResource(R.drawable.vd_bold)
                false
            }
            mBinding.editor.setBold()
        }

        mBinding.llItalic.setOnClickListener {
            mViewModel.isItalicSelect = if (!mViewModel.isItalicSelect) {
                mBinding.ivItalic.setImageResource(R.drawable.vd_italic_bold)
                true
            } else {
                mBinding.ivItalic.setImageResource(R.drawable.vd_italic)
                false
            }
            mBinding.editor.setItalic()
            logInfo("html = ", mBinding.editor.html)
        }

        mBinding.llunderline.setOnClickListener {
            mViewModel.isUnderlineSelect = if (!mViewModel.isUnderlineSelect) {
                mBinding.ivUnderline.setImageResource(R.drawable.vd_underline_select)
                mBinding.viewUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gold
                    )
                )
                true
            } else {
                mBinding.ivUnderline.setImageResource(R.drawable.vd_underline)
                mBinding.viewUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.dark_grey
                    )
                )
                false
            }
            mBinding.editor.setUnderline()
        }

        mBinding.llStrikeThrough.setOnClickListener {
            mViewModel.isStrikethroughSelect = if (!mViewModel.isStrikethroughSelect) {
                mBinding.ivStrikethrough.setImageResource(R.drawable.vd_strikethrough_select)
                true
            } else {
                mBinding.ivStrikethrough.setImageResource(R.drawable.vd_strikethrough)
                false
            }
            mBinding.editor.setStrikeThrough()
        }

        mBinding.llList.setOnClickListener {
            selectBullets()
        }

        mBinding.llOrderList.setOnClickListener {
//            isOrderListSelect = if (!isOrderListSelect) {
//                mBinding.ivOrderList.setImageResource(R.drawable.vd_list_ordered_select)
//                true
//            } else {
//                mBinding.ivOrderList.setImageResource(R.drawable.vd_list_ordered)
//                false
//            }
            mBinding.editor.setNumbers()
        }

        mBinding.llIndent.setOnClickListener {
//            isIndentSelect = if (!isIndentSelect) {
//                mBinding.ivIndent.setImageResource(R.drawable.vd_indent_select)
//                true
//            } else {
//                mBinding.ivIndent.setImageResource(R.drawable.vd_indent)
//                false
//            }
            mBinding.editor.setIndent()
        }

        mBinding.llOutdent.setOnClickListener {
//            isOutdentSelect = if (!isOutdentSelect) {
//                mBinding.ivOutdent.setImageResource(R.drawable.vd_outdent_select)
//                true
//            } else {
//                mBinding.ivOutdent.setImageResource(R.drawable.vd_outdent)
//                false
//            }

            mBinding.editor.setOutdent()
        }

        mBinding.editor.setOnTextChangeListener {
            mViewModel.currentText = it
        }

        mBinding.toolbar.ivDeleteToolbar.setOnClickListener {
            hideKeyboard()
            deleteJournalConfirmationDialog(journalId)
        }
        mBinding.toolbar.ivShareToolbar.clickWithDebounce {
            hideKeyboard()
            mViewModel.isFromShare = true
            callEditJournalAPI()
        }
        mBinding.toolbar.ivSaveToolbar.setOnClickListener {
            mViewModel.isFromShare = false
            callAPI(false)
        }

        if (!isFromEditJournal)
            mBinding.edtTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (mBinding.editor.html.isNullOrEmpty() && s.isNullOrEmpty()
                    ) {
                        mBinding.toolbar.ivSaveToolbar.alpha = 0.5f
                        mBinding.toolbar.ivSaveToolbar.isEnabled = false
                    } else {
                        mBinding.toolbar.ivSaveToolbar.alpha = 1f
                        mBinding.toolbar.ivSaveToolbar.isEnabled = true
                    }
                }
            })
    }

    var mIsFromBackPress: Boolean = false
    private fun callAPI(isFromBackPress: Boolean = false) {
        mIsFromBackPress = isFromBackPress
        hideKeyboard()
        if (!mBinding.edtTitle.text.isNullOrEmpty() || !mBinding.editor.html.isNullOrEmpty()) {
            if (isFromEditJournal)
                callEditJournalAPI()
            else
                callCreateJournalAPI()
        } else if (isFromPostAssessment) {
            JournalListingActivity.startActivity(this)
            finish()
        } else
            super.onBackPressed()
    }

    fun selectBullets() {
//        isListSelect = if (!isListSelect) {
//            mBinding.ivList.setImageResource(R.drawable.vd_list_select)
//            true
//        } else {
//            mBinding.ivList.setImageResource(R.drawable.vd_list)
//            false
//        }
        mBinding.editor.setBullets()

    }

    private fun callEditJournalAPI() {
        mViewModel.editJournalAPI(
            journalId,
            mBinding.edtTitle.text.toString(),
            if (mBinding.editor.html.isNullOrEmpty()) "" else mBinding.editor.html,
            this@CreateNewJournalActivity,
            mDisposable
        )
    }

    private fun deleteJournalConfirmationDialog(journalId: String?) {
        val title = ""
        val desctiption = "" + getString(R.string.are_you_sure_want_to_delete_journal_with_ques)

        val builder = let { AlertDialog.Builder(it, R.style.DialogTheme) }
        builder.setTitle("" + title)
            .setMessage("" + desctiption)
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, _ ->
                dialog!!.dismiss()
                if (isNetwork()) callDeleteJournalById(journalId)
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, _ ->
                dialog!!.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun callCreateJournalAPI() {
        mViewModel.createJournalAPI(
            mBinding.edtTitle.text.toString(),
            if (mBinding.editor.html.isNullOrEmpty()) "" else mBinding.editor.html,
            this@CreateNewJournalActivity,
            mDisposable
        )
    }

    private fun callDeleteJournalById(journalId: String?) {
        mViewModel.deleteJournalFromList(
            journalId,
            this@CreateNewJournalActivity,
            mDisposable
        )
    }

    override fun onBackPressed() {
        if (mViewModel.initTitle != mBinding.edtTitle.text.toString() || mViewModel.initText != mBinding.editor.html)
            callAPI(true)
        else if (isFromPostAssessment) {
            JournalListingActivity.startActivity(this)
            finish()
        } else
            finish()

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