package com.app.taocalligraphy.ui.meditation_rooms_detail.fragment

import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentAnnouncementsListBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.AnnouncementsListAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AnnouncementsListFragment : BaseFragment<FragmentAnnouncementsListBinding>() {

    private var isBoldSelect = false
    private var isItalicSelect = false
    private var isUnderlineSelect = false
    private var isStrikethroughSelect = false
    private var isListSelect = false
    private var isOrderListSelect = false
    private var isIndentSelect = false
    private var isOutdentSelect = false

    private val mAnnouncementsListAdapter by lazy {
        return@lazy AnnouncementsListAdapter(requireContext())
    }

    override fun getLayoutId() = R.layout.fragment_announcements_list

    override fun displayMessage(message: String) {

    }

    override fun observeApiCallbacks() {

    }

    override fun initView() {

        mBinding.rvAnnouncementsList.adapter = mAnnouncementsListAdapter

        mBinding.editor.setEditorFontColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.secondary_black
            )
        )
        mBinding.editor.setFontSize(12)
        mBinding.editor.setPlaceholder(getString(R.string.enter_here))

    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

        mBinding.ivBold.setOnClickListener {
            isBoldSelect = if (!isBoldSelect) {
                mBinding.ivBold.setImageResource(R.drawable.vd_bold_select)
                true
            } else {
                mBinding.ivBold.setImageResource(R.drawable.vd_bold)
                false
            }
            mBinding.editor.setBold()
        }

        mBinding.ivItalic.setOnClickListener {
            isItalicSelect = if (!isItalicSelect) {
                mBinding.ivItalic.setImageResource(R.drawable.vd_italic_bold)
                true
            } else {
                mBinding.ivItalic.setImageResource(R.drawable.vd_italic)
                false
            }
            mBinding.editor.setItalic()
        }

        mBinding.ivUnderline.setOnClickListener {
            isUnderlineSelect = if (!isUnderlineSelect) {
                mBinding.ivUnderline.setImageResource(R.drawable.vd_underline_select)
                mBinding.viewUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gold
                    )
                )
                true
            } else {
                mBinding.ivUnderline.setImageResource(R.drawable.vd_underline)
                mBinding.viewUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dark_grey
                    )
                )
                false
            }
            mBinding.editor.setUnderline()
        }

        mBinding.ivStrikethrough.setOnClickListener {
            isStrikethroughSelect = if (!isStrikethroughSelect) {
                mBinding.ivStrikethrough.setImageResource(R.drawable.vd_strikethrough_select)
                true
            } else {
                mBinding.ivStrikethrough.setImageResource(R.drawable.vd_strikethrough)
                false
            }
            mBinding.editor.setStrikeThrough()
        }

        mBinding.ivList.setOnClickListener {
            isListSelect = if (!isListSelect) {
                mBinding.ivList.setImageResource(R.drawable.vd_list_select)
                true
            } else {
                mBinding.ivList.setImageResource(R.drawable.vd_list)
                false
            }
            mBinding.editor.setBullets()
        }

        mBinding.ivOrderList.setOnClickListener {
            isOrderListSelect = if (!isOrderListSelect) {
                mBinding.ivOrderList.setImageResource(R.drawable.vd_list_ordered_select)
                true
            } else {
                mBinding.ivOrderList.setImageResource(R.drawable.vd_list_ordered)
                false
            }
            mBinding.editor.setNumbers()
        }

        mBinding.ivIndent.setOnClickListener {
            isIndentSelect = if (!isIndentSelect) {
                mBinding.ivIndent.setImageResource(R.drawable.vd_indent_select)
                true
            } else {
                mBinding.ivIndent.setImageResource(R.drawable.vd_indent)
                false
            }
            mBinding.editor.setIndent()
        }

        mBinding.ivOutdent.setOnClickListener {
            isOutdentSelect = if (!isOutdentSelect) {
                mBinding.ivOutdent.setImageResource(R.drawable.vd_outdent_select)
                true
            } else {
                mBinding.ivOutdent.setImageResource(R.drawable.vd_outdent)
                false
            }
            mBinding.editor.setOutdent()
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