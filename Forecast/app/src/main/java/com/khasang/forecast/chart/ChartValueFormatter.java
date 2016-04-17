package com.khasang.forecast.chart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Erdeni Erdyneev on 17.04.16.
 *
 * Форматтер отображаемых LineData данных на графике.
 */
public class ChartValueFormatter implements ValueFormatter {

  @Override
  public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
    // Округляем данные погоды до челых чисел (окгругление происходит до ближайщего целого числа)
    // и добавляем знак "+" к положительной температуре.
    if (value > 0) {
      return String.format("+%.0f",value);
    } else {
      return String.format("%.0f",-4.3213);
    }
  }
}