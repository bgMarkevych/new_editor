package com.example.zoomtest.editor.widget.zoomlayout

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val FLING_VELOCITY_TOLERANCE = 2000

class ZoomEngine(context: Context,
                 private val zoomLayoutCallback: ZoomLayoutCallback,
                 private val singleTapListener: ZoomLayout.OnSingleTapListener?
) {

    private var maxScale = 3f
    private var minScale = 1f

    private val gestureListener = GestureListener()
    private val scaleGestureListener = ScaleGestureListener()
    private val scaleGestureDetector = ScaleGestureDetector(context, scaleGestureListener)
    private val gestureDetector = GestureDetector(context, gestureListener)
    private val viewPortRect: RectF by lazy {
        val rectF = RectF()
        val view = zoomLayoutCallback.getOverlayLayout()!!
        rectF.set(view.x,
                view.y,
                view.x + view.width.toFloat(),
                view.y + view.height.toFloat())
        return@lazy rectF
    }

    fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            gestureListener.onDown()
        }
        var consumed = scaleGestureDetector.onTouchEvent(event)
        consumed = gestureDetector.onTouchEvent(event) || consumed
        if (event.action == MotionEvent.ACTION_UP) {
            gestureListener.onUp()
        }
        return consumed
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        private val pivotPoint = PointF()
        private var flingRunnable: FlingRunnable? = null

        fun onDown() {
            if (zoomLayoutCallback.hasScale()) {
                zoomLayoutCallback.getViewPager()?.isUserInputEnabled = false
            }
            if (flingRunnable != null && flingRunnable!!.isRunning()) {
                flingRunnable!!.stop()
                flingRunnable = null
            }
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            var overlay = zoomLayoutCallback.getOverlayLayout()
            val childX = e!!.x - overlay!!.left
            val childY = e.y - overlay.top
            e.setLocation(childX, childY)
            singleTapListener?.onTap(e)
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            var consumed = false
            if (e2!!.pointerCount == 1 && !scaleGestureDetector.isInProgress) {
                correctScrollPoints(e1, e2)
                zoomLayoutCallback.onMove(pivotPoint.x, pivotPoint.y)
                consumed = true
            }

            return consumed
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            var consumed = false
            Log.d("ZoomEngine ", "$velocityX $velocityY")
            if (e2!!.pointerCount == 1 && !scaleGestureDetector.isInProgress) {
                flingRunnable = FlingRunnable(velocityX, velocityY)
                flingRunnable!!.run()
                consumed = true
            }
            return consumed
        }

        fun onUp() {
            zoomLayoutCallback.getViewPager()?.isUserInputEnabled = true
        }

        private fun correctScrollPoints(e1: MotionEvent?, e2: MotionEvent?) {
            val newPivotX = zoomLayoutCallback.getPivotX() + e1!!.x - e2!!.x
            val newPivotY = zoomLayoutCallback.getPivotY() + e1.y - e2.y
            pivotPoint.x = max(viewPortRect.left, min(newPivotX, viewPortRect.right))
            pivotPoint.y = max(viewPortRect.top, min(newPivotY, viewPortRect.bottom))
        }

    }

    private inner class ScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {

        private var focusX = 0f
        private var focusY = 0f
        private var scaleFactor = 1f
        private var zoomFixRunnable: ZoomFixRunnable? = null

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            zoomLayoutCallback.getViewPager()?.isUserInputEnabled = false

            focusX = detector!!.focusX
            focusY = detector.focusY

            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            zoomLayoutCallback.getViewPager()?.isUserInputEnabled = true

            if (scaleTolerance()) {
                return
            }
            val correctScaleFactor = max(minScale, min(scaleFactor, maxScale))
            zoomFixRunnable = ZoomFixRunnable(scaleFactor, correctScaleFactor) {
                scaleFactor = correctScaleFactor
            }
            zoomFixRunnable!!.run()
        }

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            if (!isScaleFactorCorrect(detector!!)) {
                return false
            }

            scaleFactor *= detector.scaleFactor

            zoomLayoutCallback.onZoom(scaleFactor, scaleFactor)

            if (scaleFactor < 1f) {
                zoomLayoutCallback.onMove(viewPortRect.width() / 2f, viewPortRect.height() / 2f)
                return true
            }

            zoomLayoutCallback.onMove(
                    max(viewPortRect.left, min(zoomLayoutCallback.getPivotX() + focusX - detector.focusX, viewPortRect.right)),
                    max(viewPortRect.top, min(zoomLayoutCallback.getPivotY() + focusY - detector.focusY, viewPortRect.bottom))
            )

            return true
        }

        private fun isScaleFactorCorrect(detector: ScaleGestureDetector): Boolean {
            return !(detector.scaleFactor.isNaN()
                    || detector.scaleFactor.isInfinite())
        }

        private fun scaleTolerance(): Boolean {
            return scaleFactor > minScale && scaleFactor < maxScale
        }

    }

    private inner class ZoomFixRunnable(scale: Float,
                                        correctScale: Float,
                                        private val animationEndListener: () -> Unit
    ) : Runnable {

        private val animatorScale = ObjectAnimator.ofFloat(scale, correctScale)

        override fun run() {
            animatorScale.apply {
                duration = 300
                interpolator = LinearInterpolator()
                addUpdateListener {
                    zoomLayoutCallback.onZoom(it!!.animatedValue as Float, it.animatedValue as Float)
                }
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        animationEndListener.invoke()
                        zoomLayoutCallback.enableZoomView()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        zoomLayoutCallback.disableZoomView()
                    }

                })
                start()
            }
        }

    }

    private inner class FlingRunnable(
            private val velocityX: Float,
            private val velocityY: Float
    ) : Runnable {

        private val flingAnimationX = FlingAnimation(FloatValueHolder())
        private val flingAnimationY = FlingAnimation(FloatValueHolder())

        override fun run() {

            if (!pivotTolerance()) {
                return
            }

            if (zoomLayoutCallback.getPivotY() == viewPortRect.bottom || zoomLayoutCallback.getPivotY() == viewPortRect.top){
                nextPageTolerance(zoomLayoutCallback.getPivotY())
                return
            }

            flingAnimationX.apply {
                setStartVelocity(-velocityX)
                setStartValue(zoomLayoutCallback.getPivotX())
                setMinValue(viewPortRect.left)
                setMaxValue(viewPortRect.right)
                friction = 0.8f
                addUpdateListener { _, value, _ -> zoomLayoutCallback.onMoveX(value) }
            }

            flingAnimationY.apply {
                setStartVelocity(-velocityY)
                setStartValue(zoomLayoutCallback.getPivotY())
                setMinValue(viewPortRect.top)
                setMaxValue(viewPortRect.bottom)
                friction = 0.8f
                addUpdateListener { _, value, _ ->
                    zoomLayoutCallback.onMoveY(value)
                    nextPageTolerance(value)
                }
            }

            flingAnimationX.start()
            flingAnimationY.start()
        }

        fun nextPageTolerance(value: Float){
            if (abs(velocityY) > FLING_VELOCITY_TOLERANCE) {
                val pager = zoomLayoutCallback.getViewPager()!!
                val nextPage = when {
                    value >= viewPortRect.bottom -> {
                        pager.currentItem + 1
                    }
                    value <= viewPortRect.top -> {
                        pager.currentItem - 1
                    }
                    else -> {
                        Int.MIN_VALUE
                    }
                }
                if (nextPage < pager.adapter!!.itemCount - 1 && nextPage > -1)
                    pager.setCurrentItem(nextPage, true)
            }
        }

        fun stop() {
            if (flingAnimationX.isRunning) {
                flingAnimationX.cancel()
            }
            if (flingAnimationY.isRunning) {
                flingAnimationY.cancel()
            }
        }

        fun isRunning(): Boolean {
            return flingAnimationX.isRunning || flingAnimationY.isRunning
        }

        private fun pivotTolerance(): Boolean {
            return zoomLayoutCallback.getPivotX() > viewPortRect.left
                    && zoomLayoutCallback.getPivotX() < viewPortRect.right
//                    && zoomLayoutCallback.getPivotY() > viewPortRect.top
//                    && zoomLayoutCallback.getPivotY() < viewPortRect.bottom
        }

    }

    interface ZoomLayoutCallback {

        fun onZoom(scaleX: Float, scaleY: Float)

        fun onMove(pivotX: Float, pivotY: Float)

        fun onMoveX(pivotX: Float)

        fun onMoveY(pivotY: Float)

        fun getPivotX(): Float

        fun getPivotY(): Float

        fun getViewPager(): ViewPager2?

        fun enableZoomView()

        fun disableZoomView()

        fun hasScale(): Boolean

        fun getOverlayLayout(): View?

    }

}