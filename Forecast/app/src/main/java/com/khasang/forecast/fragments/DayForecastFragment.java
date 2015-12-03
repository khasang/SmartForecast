package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.Weather;
import com.khasang.forecast.adapters.CustomAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

/**
 * Created by qwsa on 24.11.15.
 */
public class DayForecastFragment extends Fragment {

   /* private DayForecast dayForecast;

    public DayForecastFragment() {}

    public void setForecast(DayForecast dayForecast) {

        this.dayForecast = dayForecast;

    }*/

    // Store instance variables
    private String title;
    private int page;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CustomAdapter adapter;
//    private String[] dataset;
//    private Map<Calendar, Weather> dataset;
    ArrayList<String> sDate;
    ArrayList<Weather> weathers;

    // newInstance constructor for creating fragment with arguments
    public static DayForecastFragment newInstance(int page, String title) {
        DayForecastFragment fragmentFirst = new DayForecastFragment();
       /* Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args)*/;
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //page = getArguments().getInt("someInt", 0);
        //title = getArguments().getString("someTitle");

        initDataset();
    }

    private void initDataset() {
//        dataset = new String[]{"ПН", "ВТ", "СР", "ЧТ", "ПН"};
        Map<Calendar, Weather> forecasts = PositionManager.getInstance().getDailyForecast();
        sDate = new ArrayList<>(/*forecasts.size()*/);
        weathers = new ArrayList<>(/*forecasts.size()*/);
        if (forecasts == null) {
            return; // TODO: пока просто выходим
        }
        // TODO: пока заполняю списки просто в цикле
        for (Map.Entry<Calendar, Weather> entry : forecasts.entrySet()) {
            Calendar calendar = entry.getKey();
//            TODO: привести в порядок (строковое выражение дня недели)
//            String dayOfWeek = calendar.get(Calendar.HOUR_OF_DAY);
            int index = calendar.get(Calendar.DAY_OF_WEEK);
//            String dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//
//            Calendar c = Calendar.getInstance();
//            c.set(2011, 7, 1, 0, 0, 0);
//            c.add(Calendar.DAY_OF_MONTH, day);
//            String dayOfWeek = String.format("%ta", index);
            String dayOfWeek = String.valueOf(index);
            sDate.add(dayOfWeek);

            weathers.add(entry.getValue());
        }
//        dataset = Collections.forecasts.
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.dayforecast_fragment, container, false);
        View v = inflater.inflate(R.layout.recycler_view_frag, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

//        adapter = new CustomAdapter(dataset);
        adapter = new CustomAdapter(sDate, weathers);
        recyclerView.setAdapter(adapter);

        return v;
    }

}
