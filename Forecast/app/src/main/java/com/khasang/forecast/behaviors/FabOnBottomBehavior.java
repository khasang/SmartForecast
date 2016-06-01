package com.khasang.forecast.behaviors;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.khasang.forecast.R;

public class FabOnBottomBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private static final int APPBAR_MIN_HEIGHT = 50;

    private FrameLayout chartLayout;
    private int maxChartHeight;
    private boolean alreadyHide;

    public FabOnBottomBehavior(FrameLayout chartLayout, int maxChartHeight) {
        super();
        this.chartLayout = chartLayout;
        this.maxChartHeight = maxChartHeight;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof AppBarLayout || super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            if (dependency.getBottom() > APPBAR_MIN_HEIGHT && !alreadyHide) {
                alreadyHide = true;
                hideChart(child);
                return true;
            }
            return false;
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void hideChart(final FloatingActionButton fab) {
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                chartLayout.getLayoutParams().height = maxChartHeight - (int) (maxChartHeight * interpolatedTime);
                chartLayout.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms должно быть (как и при появлении графика), но зрительно происходит быстрее, поэтому умножили на 2
        a.setDuration((int) (2 * maxChartHeight / chartLayout.getContext().getResources().getDisplayMetrics().density));
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                chartLayout.getLayoutParams().height = 0;
                hideFab(fab);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        chartLayout.startAnimation(a);
    }

    private void hideFab(FloatingActionButton fab) {
        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
                super.onHidden(fab);
                setFabOnTop(fab);
            }
        });
    }

    private void setFabOnTop(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab
                .getLayoutParams();
        fabLayoutParams.setBehavior(new FabOnTopBehavior(chartLayout, maxChartHeight));
        fabLayoutParams.setAnchorId(R.id.appbar);
        fabLayoutParams.anchorGravity = Gravity.BOTTOM | Gravity.END;
        fab.show();
        fab.requestLayout();
    }
}