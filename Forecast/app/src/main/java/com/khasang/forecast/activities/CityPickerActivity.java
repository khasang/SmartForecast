package com.khasang.forecast.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.Maps;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.exceptions.EmptyCurrentAddressException;
import com.khasang.forecast.exceptions.NoAvailableAddressesException;
import com.khasang.forecast.interfaces.IMapDataReceiver;
import com.khasang.forecast.interfaces.IMessageProvider;
import com.khasang.forecast.location.LocationParser;
import com.khasang.forecast.position.Coordinate;
import com.khasang.forecast.Logger;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.adapters.RecyclerAdapter;
import com.khasang.forecast.adapters.etc.HidingScrollListener;
import com.khasang.forecast.adapters.GooglePlacesAutocompleteAdapter;
import com.khasang.forecast.view.DelayedAutoCompleteTextView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by CopyPasteStd on 29.11.15.
 * <p/>
 * Activity для выбора местоположения
 */

public class CityPickerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, IMapDataReceiver, IMessageProvider {
    private final String TAG = this.getClass().getSimpleName();
    public final static String CITY_PICKER_TAG = "com.khasang.forecast.activities.CityPickerActivity";

    private TextView infoTV;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    List<String> cityList;

    private Toolbar toolbar;
    private volatile FloatingActionButton fabBtn;

    private DelayedAutoCompleteTextView chooseCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String colorScheme = sp.getString(getString(R.string.pref_color_scheme_key), getString(R.string.pref_color_scheme_teal));
        if (colorScheme.equals(getString(R.string.pref_color_scheme_brown))) {
            setTheme(R.style.AppTheme_CityPicker_Brown);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_teal))) {
            setTheme(R.style.AppTheme_CityPicker_Teal);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_indigo))) {
            setTheme(R.style.AppTheme_CityPicker_Indigo);
        } else if (colorScheme.equals(getString(R.string.pref_color_scheme_purple))) {
            setTheme(R.style.AppTheme_CityPicker_Purple);
        } else {
            setTheme(R.style.AppTheme_CityPicker_Green);
        }
        setContentView(R.layout.activity_city_picker);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.city_list));

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
        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);
        IconicsDrawable icon = new IconicsDrawable(this)
                .color(ContextCompat.getColor(this, R.color.current_weather_color))
                .icon(CommunityMaterial.Icon.cmd_plus);
        fabBtn.setImageDrawable(icon);
        fabBtn.setOnClickListener(this);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        setupFooter();
        createItemList();

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
                swapVisibilityTextOrList();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        fabBtn.startAnimation(animation);
    }

    /**
     * Задает размер для Footer
     */
    private void setupFooter() {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fabBtn.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        recyclerAdapter.setFooterHeight(fabBottomMargin);
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

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fabBtn.getLayoutParams();
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
        PositionManager.getInstance().setMessageProvider(this);
        swapVisibilityTextOrList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PositionManager.getInstance().setMessageProvider(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PositionManager.getInstance().updateFavoritesList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabBtn:
                fabBtn.setEnabled(false);
                showChooseCityDialog();
                break;
            case R.id.starBtn:
                final int pos = recyclerView.getChildAdapterPosition((View) v.getParent().getParent());
                String city = cityList.get(pos - 1);
                int starImageRes = android.R.drawable.btn_star_big_off;
                if (PositionManager.getInstance().flipFavCity(city)) {
                    starImageRes = android.R.drawable.btn_star_big_on;
                }
                ((ImageButton) v).setImageResource(starImageRes);
                break;
            case R.id.recycler_item:
                final int position = recyclerView.getChildAdapterPosition(v);
                Intent answerIntent = new Intent();
                answerIntent.putExtra(CITY_PICKER_TAG, cityList.get(position - 1));
                setResult(RESULT_OK, answerIntent);
                ActivityCompat.finishAfterTransition(this);
                break;
        }
    }

    /**
     * HomeButton for API 14
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_favorite:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.msg_clear_city_list);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CityPickerActivity.this.clearList();
                        recyclerAdapter.notifyDataSetChanged();
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

    private Coordinate getTownCoordinates(String city) {

        Coordinate coordinate;
        if (city.length() <= 0) {
            showToast(R.string.error_empty_location_name);
            return null;
        }
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(city, 3);
            if (addresses.size() == 0) {
                showToast(R.string.coordinates_not_found);
                return null;
            }
            Address currentAddress = addresses.get(0);
            coordinate = new Coordinate();
            coordinate.setLatitude(currentAddress.getLatitude());
            coordinate.setLongitude(currentAddress.getLongitude());
        } catch (IOException e) {
            showToast(R.string.error_geo_service_not_available);
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException | NullPointerException e) {
            showToast(R.string.invalid_lang_long_used);
            e.printStackTrace();
            return null;
        }
        return coordinate;
    }

    // Вспомогательный метод для добавления города в список
    private void addItem(String city, Coordinate coordinate) {
        if (coordinate == null) {
            coordinate = new Coordinate(0, 0);
        }
        if (!PositionManager.getInstance().positionInListPresent(city)) {
            PositionManager.getInstance().addPosition(city, coordinate);
            recyclerAdapter.addCityToNewLocationsList(city);
            cityList.add(city);
            Collections.sort(cityList);
            recyclerAdapter.notifyDataSetChanged();
        } else {
            showMessageToUser(R.string.city_exist, Snackbar.LENGTH_LONG);
        }
        swapVisibilityTextOrList();
    }

    private void clearList() {
        PositionManager.getInstance().removePositions();
        cityList.clear();
        swapVisibilityTextOrList();
    }

    private void closeMap(Maps map) {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.map);
        map.closeDataReceiver();
        if (frag != null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.map)).commit();
        }
    }

    private void setLocationOnMap(Maps maps, String city) {
        try {
            Coordinate coordinate = getTownCoordinates(city);
            double latitude = coordinate != null ? coordinate.getLatitude() : 0;
            double longitude = coordinate != null ? coordinate.getLongitude() : 0;

            maps.deleteAllMarkers();
            maps.setNewMarker(latitude, longitude, city);
            maps.setCameraPosition(latitude, longitude, maps.getDefaultZoom(), 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String ConvertPointToLocation(double latitude, double longitude) {
        String address = "";
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 3);
            address = new LocationParser(addresses).parseList().getAddressLine();
        } catch (IOException e) {
            showToast(R.string.error_service_not_available);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            showToast(R.string.invalid_lang_long_used);
            e.printStackTrace();
        } catch (EmptyCurrentAddressException | NoAvailableAddressesException e) {
            showToast(R.string.no_address_found);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    @Override
    public void setLocationNameFromMap(String name) {
        chooseCity.setText(name);
    }

    @Override
    public String setLocationCoordinatesFromMap(double latitude, double longitude) throws EmptyCurrentAddressException {
        String location = ConvertPointToLocation(latitude, longitude);
        if (location.isEmpty()) {
            chooseCity.setText("");
            throw new EmptyCurrentAddressException();
        }
        chooseCity.setText(location);
        return location;
    }


    private void setBtnClear(View view) {
        view.findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCity.setText("");
            }
        });
    }

    private void setMarkersOnMap(Maps maps) {
        int itemsCount = chooseCity.getAdapter().getCount();
        if ((itemsCount > 0) && (itemsCount < 7)) {
            maps.deleteAllMarkers();
            maps.setNewZoom(1);
            for (int i = 0; i < itemsCount; i++) {
                try {
                    Coordinate coordinate = getTownCoordinates(chooseCity.getAdapter().getItem(i).toString());
                    if (coordinate == null) {
                        showToast(R.string.invalid_lang_long_used);
                        return;
                    }
                    maps.setNewMarker(coordinate.getLatitude(), coordinate.getLongitude(), chooseCity.getAdapter().getItem(i).toString());
                } catch (NullPointerException e) {
                    showToast(R.string.invalid_lang_long_used);
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Метод скрытия клавиатуры
    public void hideSoftKeyboard(Context context) {
        if (chooseCity.isFocused()) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void showChooseCityDialog() {
        final Pattern pattern = Pattern.compile("^[\\w\\s,`'()-]+$");
        final View view = getLayoutInflater().inflate(R.layout.dialog_pick_location, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        setBtnClear(view);
        final GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.autocomplete_city_textview_item);
        final Maps map = new Maps(this, this, this);
        chooseCity = (DelayedAutoCompleteTextView) view.findViewById(R.id.editTextCityName);
        chooseCity.setAdapter(googlePlacesAutocompleteAdapter);
        chooseCity.setLoadingIndicator((ProgressBar) view.findViewById(R.id.autocomplete_progressbar));
        chooseCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);
                chooseCity.setError(null);
                chooseCity.setText(description);
                setLocationOnMap(map, description);
                hideSoftKeyboard(getApplicationContext());
                googlePlacesAutocompleteAdapter.clear();
            }
        });
        builder.setView(view)
                //.setTitle(R.string.title_choose_city)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String positionName = chooseCity.getText().toString().trim();
                        try {
                            addItem(positionName, getTownCoordinates(positionName));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } finally {
                            fabBtn.setEnabled(true);
                            closeMap(map);
                        }
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fabBtn.setEnabled(true);
                        dialog.cancel();
                        closeMap(map);
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                fabBtn.setEnabled(true);
                dialog.cancel();
                closeMap(map);
            }
        });
        chooseCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chooseCity.getText().toString().trim().isEmpty()) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    chooseCity.setError(null);
                } else if (!pattern.matcher(s.toString().trim()).matches()) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    chooseCity.setError(getString(R.string.incorrect_city_error));
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    chooseCity.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (chooseCity.getText().toString().trim().isEmpty()) {
                    return;
                }
                char lastSym = s.toString().toCharArray()[s.length() - 1];
                if ((chooseCity.getText().toString().trim().length() % 4 == 0) || (lastSym == ' ') || (lastSym == '-')) {
                    setMarkersOnMap(map);
                }
                if (s.toString().contains("\n")) {
                    chooseCity.setText(s.toString().replace('\n',' ').trim());
                    hideSoftKeyboard(getApplicationContext());
                }
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            hideSoftKeyboard(getApplicationContext());
                            googlePlacesAutocompleteAdapter.clear();
                            break;
                        case KeyEvent.KEYCODE_BACK:     // 4
                            dialog.cancel();
                            closeMap(map);
                            break;
                        default:
                            return false;
                    }
                }
                return true;
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_city_picker, menu);
        final MenuItem item = menu.findItem(R.id.clear_favorite);
        item.setActionView(R.layout.iv_action_delete_all);

        IconicsDrawable icon = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_delete)
                .color(ContextCompat.getColor(this, R.color.current_weather_color))
                .paddingDp(4);

        ImageView deleteAllBtn = (ImageView) item.getActionView().findViewById(R.id.delete_all_city_button);
        deleteAllBtn.setImageDrawable(icon);
        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.recycler_item:
                final int position = recyclerView.getChildAdapterPosition(v);
                TextView thisCity = (TextView) recyclerView.getChildAt(position).findViewById(R.id.cityTW);
                String cityName = String.valueOf(thisCity.getText());
                Logger.println(TAG, "OnLongClick: город - " + cityName);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void showMessageToUser(CharSequence string, int length) {
        AppUtils.showSnackBar(this, findViewById(R.id.fabBtn), string, length);
    }

    @Override
    public void showMessageToUser(int stringId, int length) {
        showMessageToUser(getString(stringId), length);
    }

    @Override
    public void showToast(int stringId) {
        showToast(getString(stringId));
    }

    @Override
    public void showToast(CharSequence string) {
        Toast toast = AppUtils.showInfoMessage(this, string);
        toast.getView().setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.background_toast));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
