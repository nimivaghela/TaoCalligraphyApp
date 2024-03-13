package com.app.taocalligraphy.utils.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.app.taocalligraphy.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Set up badge with the given number in a menu item.
 * Set up 0 to hide the badge
 */
fun BottomNavigationView.setBadge(tabResId: Int, badgeValue: String) {
    getOrCreateBadge(this, tabResId)?.let { badge ->
        /*badge.visibility = if (badgeValue > 0) {
            badge.text = "$badgeValue"
            View.VISIBLE
        } else {
            View.GONE
        }*/
        badge.text = badgeValue
        badge.visibility = View.VISIBLE
    }
}

fun BottomNavigationView.setSubscripitionBadge(tabResId: Int,text :String) {
    getOrCreateSubscribeBadge(this, tabResId)?.let { badge ->
        /*badge.visibility = if (badgeValue > 0) {
            badge.text = "$badgeValue"
            View.VISIBLE
        } else {
            View.GONE
        }*/
//        badge.text = text
        badge.visibility = View.VISIBLE
    }
}

fun BottomNavigationView.hideBadge(tabResId: Int) {
    getOrCreateBadge(this, tabResId)?.let { badge ->
        badge.visibility = View.GONE
    }
}

fun BottomNavigationView.hideSubscribeBadge(tabResId: Int) {
    getOrCreateSubscribeBadge(this, tabResId)?.let { badge ->
        badge.visibility = View.GONE
    }
}

private fun getOrCreateBadge(bottomBar: View, tabResId: Int): TextView? {
    val parentView = bottomBar.findViewById<ViewGroup>(tabResId)
    return parentView?.let {
        var badge = parentView.findViewById<TextView>(R.id.tvBadge)
        if (badge == null) {
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.layout_notification_badge, parentView, true)
            badge = parentView.findViewById(R.id.tvBadge)
        }
        badge
    }
}

private fun getOrCreateSubscribeBadge(bottomBar: View, tabResId: Int): AppCompatImageView? {
    val parentView = bottomBar.findViewById<ViewGroup>(tabResId)
    return parentView?.let {
        var badge = parentView.findViewById<AppCompatImageView>(R.id.ivSubscriptionLockBadge)
        if (badge == null) {
            LayoutInflater.from(parentView.context).inflate(R.layout.layout_subscription_badge, parentView, true)
            badge = parentView.findViewById(R.id.ivSubscriptionLockBadge)
        }
        badge
    }
}
