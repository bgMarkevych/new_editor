package com.example.zoomtest.editor.widget.overlaylayout

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.zoomtest.editor.widget.zoomlayout.ZoomLayout

class PageView : FrameLayout, ZoomLayout.OnSingleTapListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet, 0)

    private val TAG = this::class.java.simpleName

    init {
        addViewToLayout(400f, 400f)
    }

    private var focusedView: View? = null

    private fun addViewToLayout(x: Float, y: Float) {
        val view = ImageView(context)
        view.apply {
            layoutParams = LayoutParams(200, 200)
            this.x = x - 200 / 2
            this.y = y - 200 / 2
            setBackgroundColor(Color.BLACK)
        }
        addView(view)
        log(view.x.toString())
        log(view.y.toString())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        log(event.toString())
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            return focusedView != null && eventToView(event)
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            getParentViewPager(parent as View)!!.isUserInputEnabled = false
            moveView(event)
            return true
        }
        if (event.action == MotionEvent.ACTION_UP) {
            getParentViewPager(parent as View)!!.isUserInputEnabled = true
            return true
        }
        return false
    }

    private fun getParentViewPager(parent: View): ViewPager2? {
        if (parent is ViewPager2) {
            return parent
        }
        return getParentViewPager(parent.parent as View)
    }

    private fun eventToView(event: MotionEvent): Boolean {
        return event.x > focusedView!!.x && event.x < focusedView!!.x + focusedView!!.width && event.y > focusedView!!.y && event.y < focusedView!!.y + focusedView!!.height
    }

    private fun moveView(event: MotionEvent) {
        focusedView.apply {
            this!!.x = event.x - this.width / 2f
            this.y = event.y - this.height / 2f
        }
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

    override fun onTap(ev: MotionEvent?) {
        log("tap " + ev.toString())
        if (ev!!.action == MotionEvent.ACTION_UP) {
            if (focusedView == null) {
                if (eventToViewV2(ev)) {
                    return
                }
                addViewToLayout(ev.x, ev.y)
            } else {
                if (eventToViewV2(ev)) {
                    return
                }
                focusedView!!.clearFocus()
                focusedView = null
            }
            return
        }

    }

    private fun eventToViewV2(ev: MotionEvent): Boolean {
        for (it in 0 until childCount) {
            val child = getChildAt(it)
            val rect = RectF(child.x, child.y, child.x + child.width, child.y + child.height)
            if (rect.contains(ev.x, ev.y)) {
                focusedView = child
                break
            }
        }
        return focusedView != null
    }

}