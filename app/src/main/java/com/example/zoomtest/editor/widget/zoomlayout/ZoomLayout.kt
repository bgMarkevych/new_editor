package com.example.zoomtest.editor.widget.zoomlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.zoomtest.editor.widget.overlaylayout.PageView


const val TAG = "MyZoomLayout"

class ZoomLayout : FrameLayout, ZoomEngine.ZoomLayoutCallback {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val zoomEngine: ZoomEngine by lazy {
        ZoomEngine(context, this, singleTapListener)
    }

    private var singleTapListener: OnSingleTapListener? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return zoomEngine.onTouchEvent(event)
    }

    fun setSingleTapListener(listener: OnSingleTapListener) {
        this.singleTapListener = listener
    }

    private fun getParentViewPager(parent: View): ViewPager2? {
        if (parent is ViewPager2) {
            return parent
        }
        return getParentViewPager(parent.parent as View)
    }

    override fun onZoom(scaleX: Float, scaleY: Float) {
        log(getOverlayLayout()!!.width.toString())
        this.scaleX = scaleX
        this.scaleY = scaleY
    }

    override fun onMove(pivotX: Float, pivotY: Float) {
        this.pivotX = pivotX
        this.pivotY = pivotY
    }

    override fun onMoveX(pivotX: Float) {
        this.pivotX = pivotX
    }

    override fun onMoveY(pivotY: Float) {
        this.pivotY = pivotY
    }

    override fun getViewPager(): ViewPager2? {
        return getParentViewPager(parent as View)
    }

    override fun enableZoomView() {
        isEnabled = true
    }

    override fun disableZoomView() {
        isEnabled = false
    }

    override fun hasScale(): Boolean {
        return scaleX != 1f && scaleY != 1f
    }

    override fun getOverlayLayout(): View? {
        for (it in 0 until childCount) {
            val child = getChildAt(it)
            if (child is PageView) {
                return child
            }
        }
        return null
    }

    interface OnSingleTapListener {
        fun onTap(ev: MotionEvent?)
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

}