package com.khasang.forecast.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.khasang.forecast.PlaceProvider;
import com.khasang.forecast.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xsobolx on 12.12.2015.
 */
public class GooglePlacesAutocompleteAdapter extends ArrayAdapter
        implements Filterable {
    private List<String> resultList;
    private PlaceProvider mPlaceProvider;
    private final static int MAX_RESULT = 10;
    private Context mContext;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
        resultList = new ArrayList<String>();
        mPlaceProvider = new PlaceProvider();
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.autocomplete_city_textview_item, parent, false);
        }
        TextView description = (TextView) convertView.findViewById(R.id.autocomplete_list_item);
        description.setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<String> predictions = mPlaceProvider.autocomplete(constraint.toString());
                    filterResults.values = predictions;
                    filterResults.count = predictions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
