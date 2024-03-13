package com.app.taocalligraphy.ui.notification

interface OnItemClicked {
    fun onInnerItemClicked(position: Int, type: String)
    fun onDeleteClicked(idList: ArrayList<Int>, type: String?, isAllDeleted:Boolean = false)
    fun loadMoreClicked(type: String)
    fun onNotificationRead(idList: List<Int>)
}