package com.example.zoomtest.editor.widget

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.zoomtest.editor.model.PdfPage
import com.example.zoomtest.editor.widget.overlaylayout.PageView
import com.example.zoomtest.editor.widget.zoomlayout.ZoomLayout
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

class EditorPageUIBuilder(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private lateinit var zoomLayout: WeakReference<ZoomLayout>
    private lateinit var page: PdfPage

    fun setContainer(zoomLayout: ZoomLayout): EditorPageUIBuilder {
        this.zoomLayout = WeakReference(zoomLayout)
        return this
    }

    fun setPage(page: PdfPage): EditorPageUIBuilder {
        this.page = page
        return this
    }

    fun build() {
        if (!::zoomLayout.isInitialized) {
            throw IllegalArgumentException("set Container first!")
        }
        if (!::page.isInitialized) {
            throw IllegalArgumentException("pdf page is null!")
        }
        if (zoomLayout.get()!!.childCount > 0) {
            zoomLayout.get()!!.removeAllViews()
        }
        val correctPageSize = getCorrectSizeOfPage()

        val overlayLayout = PageView(context)
        overlayLayout.background = BitmapDrawable(context.resources, page.page)

        zoomLayout.get()!!.setSingleTapListener(overlayLayout)

        overlayLayout.layoutParams = FrameLayout.LayoutParams(correctPageSize.x, correctPageSize.y, Gravity.CENTER)
        zoomLayout.get()!!.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        zoomLayout.get()!!.addView(overlayLayout)

    }

    private fun getCorrectSizeOfPage(): Point {
        var pageToScreenHeight: Int
        var pageToScreenWidth: Int
        val screenPoints = getScreenSize()
        val screenHeight = screenPoints.y
        val screenWidth = screenPoints.x

        val screenToPageHeightRatio = screenHeight.toFloat() / page.page.height.toFloat()
        val screenToPageWidthRatio = screenWidth.toFloat() / page.page.width.toFloat()
        val pageSidesRatio = page.page.height.toFloat() / page.page.width.toFloat()

        if (page.page.width > page.page.height) {
            if (screenHeight > screenWidth) {
                pageToScreenHeight = (page.page.height * screenToPageHeightRatio).roundToInt()
                pageToScreenWidth = (pageToScreenHeight / pageSidesRatio).roundToInt()
            } else {
                pageToScreenWidth = (page.page.width * screenToPageHeightRatio).roundToInt()
                pageToScreenHeight = (pageToScreenWidth / pageSidesRatio).roundToInt()
            }
        } else {
            if (screenHeight > screenWidth) {
                pageToScreenWidth = (page.page.width * screenToPageWidthRatio).roundToInt()
                pageToScreenHeight = (pageToScreenWidth * pageSidesRatio).roundToInt()
            } else {
                pageToScreenHeight = (page.page.height * screenToPageHeightRatio).roundToInt()
                pageToScreenWidth = (pageToScreenHeight / pageSidesRatio).roundToInt()
            }

        }

//        if (screenHeight < screenWidth) {
//            val temp = pageToScreenWidth
//            pageToScreenWidth = pageToScreenHeight
//            pageToScreenHeight = temp
//        }

        return Point(pageToScreenWidth, pageToScreenHeight)
    }

    private fun getScreenSize(): Point {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return Point(metrics.widthPixels, metrics.heightPixels)
    }


}