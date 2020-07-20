package com.example.zoomtest.editor.utils

import android.util.DisplayMetrics
import com.example.zoomtest.App
import kotlin.math.roundToInt

class UnitUtils {
    companion object {
        @JvmStatic
        fun dpToPixels(dp: Int): Int {
            val displayMetrics: DisplayMetrics = App.getInstance().resources.displayMetrics
            return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }
    }
}