package com.app.taocalligraphy.models

import android.graphics.drawable.Drawable


class IconNamePopupModel {

    var icon: Drawable? = null
    var name: String? = null
    var showDivider = false

    constructor()

    constructor(name: String?, icon: Drawable?) : super() {
        this.icon = icon
        this.name = name
    }

    constructor(showDivider:Boolean):super(){
        this.showDivider=showDivider
    }


}