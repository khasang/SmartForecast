package com.khasang.forecast.chart;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.khasang.forecast.Logger;
import com.khasang.forecast.R;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Erdeni Erdyneev on 15.04.16.
 *
 * Кастомный LineDataSet для отображения данных погоды по часам.
 * Создан для переопредления прорисовки точек на графике, чтобы можно было отрисовать только
 * несколько нужных точек (нижняя температура, верхняя температура и текущее время).
 *
 * index - это индекс Entry элемента в DataSet.
 * xIndex - это индекс элемента по оси X на графике.
 *
 * При прорисовке графика последовательно вызываются методы #getCircleColor(int),
 * #getCircleRadius().
 *
 * Так как метод #getCircleRadius() не передает index, приходится запоминать последний
 * использованный
 *
 * TODO: при резких изменениях Y-значений в загругленном графике (setDrawCubic(true)), точка может
 * находится не на вершине закругления
 */
public class HourlyLineDataSet extends LineDataSet {

  private static final String TAG = HourlyLineDataSet.class.getSimpleName();

  private Context context;
  private int index;
  private int indexForYMin;
  private int indexForYMax;
  private int indexCurrentTime;

  public HourlyLineDataSet(List<Entry> yVals, Context context) {
    this(yVals, "", context);
  }

  public HourlyLineDataSet(List<Entry> yVals, String label, Context context) {
    super(yVals, label);

    this.context = context;
    initIndexes();
    defaultInit();
  }

  private void initIndexes() {
    float yMinVal = getYMin();
    Entry yMinEntry = getEntry(yMinVal);
    indexForYMin = getEntryIndex(yMinEntry);
    Logger.println(TAG, "indexForYMin: " + indexForYMin);

    float yMaxVal = getYMax();
    Entry yMaxEntry = getEntry(yMaxVal);
    indexForYMax = getEntryIndex(yMaxEntry);
    Logger.println(TAG, "indexForYMax: " + indexForYMax);

    Calendar calendar = Calendar.getInstance();
    indexCurrentTime = calendar.get(Calendar.HOUR_OF_DAY);
    int minutes = calendar.get(Calendar.MINUTE);
    if (minutes > 30) {
      indexCurrentTime++;
    }
    Logger.println(TAG, "indexCurrentTime: " + indexCurrentTime);
  }

  private void defaultInit() {
    setColor(Color.BLACK); // цвет линии
    setLineWidth(context.getResources().getDimension(R.dimen.chart_line_width)); // толщина линии
    setValueTextSize(0f); // убираем приписки величины в каждой точке
    setDrawFilled(true); // разрешаем заливку цвета под графиком
    // разрешаем закругление линии
    // это плохо влияет на производительность https://github.com/PhilJay/MPAndroidChart/wiki/DataSet-classes-in-detail
    // но у нас данных не много, влиять должно не сильно, а смотрится лучше
    setDrawCubic(true);
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

  @Override public int getCircleColor(int index) {
    this.index = index;
    if (index == indexForYMin) {
      return ContextCompat.getColor(context, R.color.chart_min_temperature);
    }
    if (index == indexForYMax) {
      return ContextCompat.getColor(context, R.color.chart_max_temperature);
    }
    if (index == indexCurrentTime) {
      return ContextCompat.getColor(context, R.color.chart_current_time);
    }
    return super.getCircleColor(index);
  }

  @Override public float getCircleRadius() {
    if (index == indexForYMin || index == indexForYMax || index == indexCurrentTime) {
      return context.getResources().getDimension(R.dimen.chart_circle_radius);
    }
    return 0f;
  }

  /**
   * Не используйте этот метод. Никакого эффекта не будет.
   * Определение размера точки инкапсулировано внутри.
   */
  @Deprecated @Override public void setCircleRadius(float radius) {
  }

  /**
   * Не используйте этот метод. Никакого эффекта не будет.
   * Определение цвета точки инкапсулировано внутри.
   */
  @Deprecated @Override public void resetCircleColors() {
    super.resetCircleColors();
  }

  /**
   * Не используйте этот метод. Никакого эффекта не будет.
   * Определение цвета точки инкапсулировано внутри.
   */
  @Deprecated @Override public void setCircleColor(int color) {
  }

  /**
   * Не используйте этот метод. Никакого эффекта не будет.
   * Определение цвета точки инкапсулировано внутри.
   */
  @Deprecated @Override public void setCircleColors(List<Integer> colors) {
    super.setCircleColors(colors);
  }

  /**
   * Не используйте этот метод. Никакого эффекта не будет.
   * Определение цвета точки инкапсулировано внутри.
   */
  @Deprecated @Override public void setCircleColors(int[] colors) {
    super.setCircleColors(colors);
  }

  /**
   * Не используйте этот метод. Никакого эффекта не будет.
   * Определение цвета точки инкапсулировано внутри.
   */
  @Deprecated @Override public void setCircleColors(int[] colors, Context c) {
    super.setCircleColors(colors, c);
  }
}