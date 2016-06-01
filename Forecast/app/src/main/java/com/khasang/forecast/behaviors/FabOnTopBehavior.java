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

public class FabOnTopBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private FrameLayout chartLayout;
    private int maxChartHeight;

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
            if (dependency.getBottom() == 0) {
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
                setFabOnBottom(fab);
            }
        });
    }

    private void setFabOnBottom(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fabLayoutParams.setBehavior(new FabOnBottomBehavior(chartLayout, maxChartHeight));
        fabLayoutParams.setAnchorId(R.id.chart_layout);
        fabLayoutParams.anchorGravity = Gravity.TOP | Gravity.END;
        fab.show();

        showChart(chartLayout);
    }

    public void showChart(final View v) {
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = (int) (maxChartHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (maxChartHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}