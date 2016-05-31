package com.khasang.forecast.chart;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.khasang.forecast.R;

public class ChartAppearance {

    private FloatingActionButton fab;
    private FrameLayout chatLayout;
    private int chartHeight;

    public ChartAppearance(Context context, FloatingActionButton fab, FrameLayout chatLayout) {
        this.fab = fab;
        this.chatLayout = chatLayout;
        this.chartHeight = (int) context.getResources().getDimension(R.dimen.chart_height);

        setFabOnTopBehavior();
    }

    private void setFabOnTopBehavior() {
        CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fabLayoutParams.setBehavior(new FabOnTopBehavior());
        fabLayoutParams.setAnchorId(R.id.appbar);
        fabLayoutParams.anchorGravity = Gravity.BOTTOM | Gravity.END;

        chatLayout.getLayoutParams().height = 0;
        fab.show();
    }

    private void setFabOnBottomBehavior() {
        CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fabLayoutParams.setBehavior(new FabOnBottomBehavior());
        fabLayoutParams.setAnchorId(R.id.chart_layout);
        fabLayoutParams.anchorGravity = Gravity.TOP | Gravity.END;

        chatLayout.getLayoutParams().height = chartHeight;
        fab.show();
    }

    public class FabOnTopBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

        public FabOnTopBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FabOnTopBehavior() {
            super();
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            return dependency instanceof AppBarLayout || super.layoutDependsOn(parent, child, dependency);
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            if (dependency instanceof AppBarLayout) {
                if (dependency.getBottom() == 0) {
                    child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onHidden(fab);
                            setFabOnBottomBehavior();
                        }
                    });
                    return true;
                }
                return false;
            }
            return super.onDependentViewChanged(parent, child, dependency);
        }
    }

    public class FabOnBottomBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

        public FabOnBottomBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FabOnBottomBehavior() {
            super();
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            return dependency instanceof AppBarLayout || super.layoutDependsOn(parent, child, dependency);
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            if (dependency instanceof AppBarLayout) {
                if (dependency.getBottom() > 0) {
                    child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onHidden(fab);
                            setFabOnTopBehavior();
                        }
                    });
                    return true;
                }
                return false;
            }
            return super.onDependentViewChanged(parent, child, dependency);
        }
    }
}
