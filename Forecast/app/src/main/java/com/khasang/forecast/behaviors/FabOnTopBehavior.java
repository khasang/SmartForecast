package com.khasang.forecast.behaviors;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.khasang.forecast.R;

public class FabOnTopBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private FrameLayout chartLayout;
    private int maxChartHeight;
    private boolean alreadyHide;

    public FabOnTopBehavior(FrameLayout chartLayout, int maxChartHeight) {
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
            if (dependency.getBottom() == 0 && !alreadyHide) {
                alreadyHide = true;
                Log.d("onDependentViewChanged", "dependency.getBottom() == 0");
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
                Log.d("hideFab", "onHidden");
                showChart(fab);
            }
        });
    }

    public void showChart(final FloatingActionButton fab) {
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        chartLayout.getLayoutParams().height = 1;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                chartLayout.getLayoutParams().height = (int) (maxChartHeight * interpolatedTime);
                chartLayout.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (maxChartHeight / chartLayout.getContext().getResources().getDisplayMetrics().density));
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("showChart", "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("showChart", "onAnimationEnd");
                setFabOnBottom(fab);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        chartLayout.startAnimation(a);
    }

    private void setFabOnBottom(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fabLayoutParams.setBehavior(new FabOnBottomBehavior(chartLayout, maxChartHeight));
        fabLayoutParams.setAnchorId(R.id.chart_layout);
        fabLayoutParams.anchorGravity = Gravity.TOP | Gravity.END;
        fab.show();
    }
}