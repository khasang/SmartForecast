package com.khasang.forecast.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.khasang.forecast.Coordinate;
import com.khasang.forecast.Logger;
import com.khasang.forecast.PlaceProvider;
import com.khasang.forecast.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.adapters.RecyclerAdapter;
import com.khasang.forecast.adapters.etc.HidingScrollListener;
import com.khasang.forecast.adapters.GooglePlacesAutocompleteAdapter;
import com.khasang.forecast.view.DelayedAutoCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by CopyPasteStd on 29.11.15.
 * <p/>
 * Activity для выбора местоположения
 */
public class CityPickerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private final String TAG = this.getClass().getSimpleName();
    public final static String CITY_PICKER_TAG = "com.khasang.forecast.activities.CityPickerActivity";

    private TextView infoTV;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    List<String> cityList;

    private Toolbar toolbar;
    private ImageButton fabBtn;
    private PlaceProvider mPlaceProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.mipmap.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.back_arrow), PorterDuff.Mode.SRC_ATOP);
        //TODO fix NullPointerException
        //    getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.city_list));
        //TODO Проверить код кнопки HOME - цвет должен быть белый (не работает)
        //toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        infoTV = (TextView) findViewById(R.id.infoTV);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        cityList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(cityList, this, this);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swapVisibilityTextOrList();

        /** Вычисляет степень прокрутки и выполняет нужное действие.*/
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
        fabBtn = (ImageButton) findViewById(R.id.fabBtn);
        fabBtn.setOnClickListener(this);
        createItemList();
        Logger.println(TAG, String.valueOf(PositionManager.getInstance().getPositions()));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = recyclerView.getChildAdapterPosition(viewHolder.itemView) - 1;
                PositionManager.getInstance().removePosition(cityList.get(position));
                cityList.remove(position);
                recyclerAdapter.notifyDataSetChanged();

                //TODO Не работает отображение infoTV при очистке cityList
                Logger.println(TAG, String.valueOf(cityList.size()));
                swapVisibilityTextOrList();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void swapVisibilityTextOrList() {
        if (cityList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            infoTV.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            infoTV.setVisibility(View.GONE);
        }
    }

    // Вспомогательные методы
    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight())
                .setInterpolator(new AccelerateInterpolator(2));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fabBtn.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        fabBtn.animate().translationY(fabBtn.getHeight() +
                fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        fabBtn.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        swapVisibilityTextOrList();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabBtn:
                showChooseCityDialog();
                return;
            case R.id.recycler_item:
                final int position = recyclerView.getChildAdapterPosition(v);
                Intent answerIntent = new Intent();
                answerIntent.putExtra(CITY_PICKER_TAG, cityList.get(position - 1));
                setResult(RESULT_OK, answerIntent);
                finish();
                return;
        }
    }

    /**
     * HomeButton for API 14
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.clear_favorite:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.msg_clear_city_list);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CityPickerActivity.this.clearList();
                        recyclerAdapter.notifyDataSetChanged();
                        swapVisibilityTextOrList();
                    }
                });
                builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Вспомогательный метод для подготовки списка
    private List<String> createItemList() {
        Set<String> cities = PositionManager.getInstance().getPositions();
        for (String city : cities) {
            cityList.add(city);
        }
        Collections.sort(cityList);
        return cityList;
    }

    // Вспомогательный метод для добавления города в список
    private void addItem(String city) {
//        city = city.trim().toLowerCase();

        if (city.length() <= 0) {
//            Log.w(TAG, "Имя города менее одного символа");
            Toast.makeText(this, R.string.error_empty_location_name, Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(city, 3);
            if (addresses.size() == 0){
                Log.i(TAG, "Coordinates not found");
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.coordinates_not_found), city), Toast.LENGTH_SHORT).show();
                return;
            }
            Address currentAddress = addresses.get(0);
            Coordinate coordinate = new Coordinate();
            coordinate.setLatitude(currentAddress.getLatitude());
            coordinate.setLongitude(currentAddress.getLongitude());
            Logger.println(TAG, "Coordinate of " + city + " lat: " + currentAddress.getLatitude() + ", lon: " + currentAddress.getLongitude());
            if (!PositionManager.getInstance().positionIsPresent(city)) {
                PositionManager.getInstance().addPosition(city, coordinate);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        // Делаем каждое слово в имени города с заглавное буквы
        StringBuilder b = new StringBuilder(city);
        int i = 0;
        do {
            b.replace(i, i + 1, b.substring(i,i + 1).toUpperCase());
            i =  b.indexOf(" ", i) + 1;
        } while (i > 0 && i < b.length());
        city = b.toString();
*/
        Intent answerIntent = new Intent();
        answerIntent.putExtra(CITY_PICKER_TAG, city);
        setResult(RESULT_OK, answerIntent);
        finish();
    }

    private void clearList() {
        PositionManager.getInstance().removePositions();
        cityList.clear();
    }

    private void showChooseCityDialog() {
        final View view = getLayoutInflater().inflate(R.layout.choose_city_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DelayedAutoCompleteTextView chooseCity = (DelayedAutoCompleteTextView) view.findViewById(R.id.editTextCityName);
        chooseCity.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.autocomplete_city_textview_item));
        chooseCity.setLoadingIndicator((ProgressBar) view.findViewById(R.id.autocomplete_progressbar));
        chooseCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);
                //String city = description.split(", ")[0];
                //chooseCity.setText(city);
                chooseCity.setText(description);
            }
        });
        builder.setTitle(R.string.title_choose_city)
                .setView(view)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String positionName = chooseCity.getText().toString();
                        try {
                            addItem(positionName);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_city_picker, menu);
        menu.findItem(R.id.clear_favorite).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.recycler_item:
                final int position = recyclerView.getChildAdapterPosition(v);
                TextView thisCity = (TextView) recyclerView.getChildAt(position).findViewById(R.id.cityTW);
                String cityName = String.valueOf(thisCity.getText());
                Logger.println(TAG, "OnLongClick: город - " + cityName);

                //TODO реализовать удаление города через Context Menu
    /*            Toast.makeText(this, "click on " + thisCity.getText(), Toast.LENGTH_SHORT).show();
                cityList.remove(cityName);
                recyclerAdapter.notifyDataSetChanged();
                PositionManager.getInstance().removePosition(cityName);*/

        }
        return true;
    }

//    public static class ErrorDialogFragment extends DialogFragment {
//        public ErrorDialogFragment(){}
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            int errorCode = this.getArguments().getInt(PlaceProvider.DIALOG_ERROR);
//            return GoogleApiAvailability.getInstance().getErrorDialog(
//                    this.getActivity(), errorCode, PlaceProvider.REQUEST_RESOLVE_ERROR);
//        }
//
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            ((CityPickerActivity)getActivity()).mPlaceProvider.onDialogDismissed();
//        }
//    }
}
