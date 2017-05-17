package com.pauloavelar.inventory.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

@SuppressWarnings({"unused", "WeakerAccess"})
public class FabBehavior extends FloatingActionButton.Behavior {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    public FabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(
                                final CoordinatorLayout layout, final FloatingActionButton child,
                                final View targetChild, final View target, final int scrollAxes) {
        // Ensure we react to vertical scrolling
        return scrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                    super.onStartNestedScroll(layout, child, targetChild, target, scrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout layout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(layout, child, target,
                             dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child);
        }
    }

    // Animation FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void animateOut(final FloatingActionButton button) {
        ViewCompat
            .animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
            .setInterpolator(INTERPOLATOR).withLayer()
            .setListener(new ViewPropertyAnimatorListener() {
                public void onAnimationStart(View view) {
                    FabBehavior.this.mIsAnimatingOut = true;
                }

                public void onAnimationCancel(View view) {
                    FabBehavior.this.mIsAnimatingOut = false;
                }

                public void onAnimationEnd(View view) {
                    FabBehavior.this.mIsAnimatingOut = false;
                    view.setVisibility(View.INVISIBLE);
                }
            })
            .start();
    }

    // Animation FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
            .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
            .start();
    }

}