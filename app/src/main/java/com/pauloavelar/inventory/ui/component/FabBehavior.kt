package com.pauloavelar.inventory.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION", "UNUSED_PARAMETER")
class FabBehavior(c: Context, attrs: AttributeSet?) : FloatingActionButton.Behavior() {

    private var mIsAnimatingOut = false

    override fun onStartNestedScroll(
        layout: CoordinatorLayout, child: FloatingActionButton,
        targetChild: View, target: View, scrollAxes: Int
    ): Boolean = scrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
        super.onStartNestedScroll(layout, child, targetChild, target, scrollAxes)

    override fun onNestedScroll(layout: CoordinatorLayout, child: FloatingActionButton,
                                target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(layout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)

        if (dyConsumed > 0 && !mIsAnimatingOut && child.visibility == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child)
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child)
        }
    }

    // Animation FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private fun animateOut(button: FloatingActionButton) {
        ViewCompat
            .animate(button).scaleX(0.0f).scaleY(0.0f).alpha(0.0f)
            .setInterpolator(INTERPOLATOR).withLayer()
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {
                    mIsAnimatingOut = true
                }

                override fun onAnimationCancel(view: View) {
                    mIsAnimatingOut = false
                }

                override fun onAnimationEnd(view: View) {
                    mIsAnimatingOut = false
                    view.visibility = View.INVISIBLE
                }
            })
            .start()
    }

    // Animation FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private fun animateIn(button: FloatingActionButton) {
        button.visibility = View.VISIBLE
        ViewCompat.animate(button).scaleX(1.0f).scaleY(1.0f).alpha(1.0f)
            .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
            .start()
    }

    companion object {
        private val INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()
    }

}
