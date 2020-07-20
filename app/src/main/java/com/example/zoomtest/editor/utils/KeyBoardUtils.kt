package com.example.zoomtest.editor.utils

import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager

fun showKeyboard(view: View?) {
    val imm: InputMethodManager? = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm?.showSoftInput(view, 0)
}

fun hideKeyboard(view: View?) {
    val imm: InputMethodManager? = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val windowToken: IBinder? = view.windowToken
    imm?.hideSoftInputFromWindow(windowToken, 0)
    view.clearFocus()
}