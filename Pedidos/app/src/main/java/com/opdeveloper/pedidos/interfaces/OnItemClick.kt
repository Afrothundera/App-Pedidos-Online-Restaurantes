package com.opdeveloper.pedidos.interfaces

import android.view.View

interface OnItemClick {
    fun onClick(view: View, position: Int, isLongClick: Boolean)
}