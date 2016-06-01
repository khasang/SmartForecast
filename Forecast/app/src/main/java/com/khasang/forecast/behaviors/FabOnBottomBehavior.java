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
                hideFab(child);
                return true;
            }
            return false;
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void hideFab(FloatingActionButton fab) {
        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
                super.onHidden(fab);
                hideChart(fab);
            }
        });
    }

    private void hideChart(final FloatingActionButton fab) {
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    chartLayout.getLayoutParams().height = 0;
                    setFabOnTop(fab);
                } else {
                    chartLayout.getLayoutParams().height = maxChartHeight - (int) (maxChartHeight * interpolatedTime);
                    chartLayout.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (maxChartHeight / chartLayout.getContext().getResources().getDisplayMetrics().density));
        chartLayout.startAnimation(a);
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