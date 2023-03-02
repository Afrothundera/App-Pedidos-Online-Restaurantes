package com.opdeveloper.pedidosserver.interfaces

import android.view.View

interface OnItemClick {
    fun onClick(view: View, position: Int, isLongClick: Boolean)
}