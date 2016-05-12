package com.khasang.forecast.chart;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.khasang.forecast.AppUtils;
import com.khasang.forecast.R;
import com.khasang.forecast.position.Weather;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Erdeni Erdyneev on 17.04.16.
 *
 * LineChart для отображения графика погоды.
 *
 * Вики по настройкам Chart https://github.com/PhilJay/MPAndroidChart/wiki/Interaction-with-the-Chart
 */
public class WeatherChart extends LineChart {

    public WeatherChart(Context context) {
        super(context);
    }

    public WeatherChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void updateForecast(Map<Calendar, Weather> forecast, boolean hourlyWeatherChart) {
        if (forecast == null) {
            return;
        }
        LineData data = initLineData(forecast, hourlyWeatherChart);

        setData(data); // устанавливаем данные для отображения
        setTouchEnabled(false); // запрещаем все взаимодействия с графиком прикосновениями
        setDescription(""); // убираем описание

        Legend legend = getLegend();
        legend.setEnabled(false); // убираем легенду графика

        getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // устанавливаем шкалу X снизу
        getXAxis().setTextSize(getContext().getResources().getDimension(R.dimen.chart_xaxis_size));
        getXAxis().setTextColor(ContextCompat.getColor(getContext(), R.color.chart_text));

        // отступ между X осью и подписями под графиком
        getXAxis().setYOffset(getContext().getResources().getDimension(R.dimen.chart_y_offset));

        getAxisLeft().setEnabled(false); // убираем шкалу Y слева
        getAxisRight().setEnabled(false); // убираем шкалу Y справа

        animateY(2000, Easing.EasingOption.EaseInOutBack);  // устанавливаем анимацию появления данных
    }

    private LineData initLineData(Map<Calendar, Weather> forecast, boolean hourlyWeatherChart) {
        List<String> xValues = new ArrayList<>();
        List<Entry> yValues = new ArrayList<>();

        List<Calendar> calendars = new ArrayList<>(forecast.keySet());
        for (int i = 0; i < calendars.size(); i++) {
            Calendar calendar = calendars.get(i);
            String time;
            if (hourlyWeatherChart) {
                time = AppUtils.getChartTime(getContext(), calendar);
            } else {
                time = AppUtils.getChartDayName(getContext(), calendar);
            }
            xValues.add(time);

            Weather weather = forecast.get(calendar);
            float temp = (float) weather.getTemperature();
            yValues.add(new Entry(temp, i));
        }

        WeatherLineDataSet set = new WeatherLineDataSet(yValues, getContext());

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData data = new LineData(xValues, dataSets);

        // устанавливаем кастомный форматтер отображения текста над точками графика
        data.setValueFormatter(new ChartValueFormatter());

        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.chart_text));

        return data;
    }
}
