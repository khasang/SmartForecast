package com.khasang.forecast.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.khasang.forecast.R;

import java.util.List;

/**
 * Created by Erdeni Erdyneev on 15.04.16.
 * <p/>
 * LineDataSet для отображения данных погоды.
 * <p/>
 * index - это индекс Entry элемента в DataSet.
 * xIndex - это индекс элемента по оси X на графике.
 * <p/>
 * Вики по настройкам DataSet https://github.com/PhilJay/MPAndroidChart/wiki/DataSet-classes-in-detail
 */
public class WeatherLineDataSet extends LineDataSet {

    private Context context;
    private Entry yMinEntry;
    private Entry yMaxEntry;

    public WeatherLineDataSet(List<Entry> yVals, Context context) {
        this(yVals, "", context);
    }

    public WeatherLineDataSet(List<Entry> yVals, String label, Context context) {
        super(yVals, label);

        this.context = context;
        initEntries();
        defaultInit();
    }

    private void initEntries() {
        float yMinVal = getYMin();
        yMinEntry = getEntry(yMinVal);

        float yMaxVal = getYMax();
        yMaxEntry = getEntry(yMaxVal);
    }

    private Entry getEntry(float val) {
        List<Entry> entries = getYVals();
        for (Entry entry : entries) {
            if (entry.getVal() == val) {
                return entry;
            }
        }
        return null;
    }

    private void defaultInit() {
        setColor(ContextCompat.getColor(context, R.color.chart_line)); // цвет линии
        setLineWidth(context.getResources().getDimension(R.dimen.chart_line_width)); // толщина линии

        setDrawFilled(true); // разрешаем заливку цвета под графиком
        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.chart_fade);
            setFillDrawable(drawable);
        } else {
            setFillColor(ContextCompat.getColor(context, R.color.accent));
        }

        // разрешаем закругление линии
        // это плохо влияет на производительность https://github.com/PhilJay/MPAndroidChart/wiki/DataSet-classes-in-detail
        // но у нас данных не много, влиять должно не сильно, а смотрится лучше
        setDrawCubic(true);
        setCubicIntensity(0.1f);

        setValueTextSize(context.getResources().getDimension(R.dimen.chart_value_size));    // размер текста
        setDrawCircleHole(false);   // запрещаем кружок внутри кружка

        int[] colors = getColors(getYVals());
        setCircleColors(colors);    // цвета кружков
    }

    private int[] getColors(List<Entry> yValues) {
        int[] colors = new int[yValues.size()];
        for (int i = 0; i < yValues.size(); i++) {
            Entry yValue = yValues.get(i);
            int color;
            if (yValue.equals(yMaxEntry)) {
                color = ContextCompat.getColor(context, R.color.chart_max_temperature);
            } else if (yValue.equals(yMinEntry)) {
                color = ContextCompat.getColor(context, R.color.chart_min_temperature);
            } else {
                color = Color.TRANSPARENT;
            }
            colors[i] = color;
        }
        return colors;
    }
}