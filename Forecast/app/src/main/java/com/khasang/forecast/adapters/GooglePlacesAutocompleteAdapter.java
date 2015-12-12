package com.khasang.forecast.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.khasang.forecast.PlaceProvider;

import java.util.ArrayList;

/**
 * Created by xsobolx on 12.12.2015.
 */
public class GooglePlacesAutocompleteAdapter extends ArrayAdapter
        implements Filterable {
    private ArrayList<String> resultList;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
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
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null){
                    resultList = PlaceProvider.autocomplete(constraint.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0){
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
