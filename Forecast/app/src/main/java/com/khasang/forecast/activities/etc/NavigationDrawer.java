package com.khasang.forecast.activities.etc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.khasang.forecast.Logger;
import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.activities.WeatherActivity;
import com.khasang.forecast.position.PositionManager;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Locale;

/**
 * Класс отвечает за логику NavigationDrawer
 *
 * @version alpha
 */
public class NavigationDrawer {
    private static final String TAG = WeatherActivity.class.getSimpleName();

    private Drawer result = null;
    private PrimaryDrawerItem favorites;
    private boolean opened = false;
    private final int subItemIndex = 2000;


    public void init(final WeatherActivity activity, Toolbar toolbar) {
    //public void init(final Activity activity, Toolbar toolbar) {

        /** Инициализация элементов меню */
        final DividerDrawerItem divider = new DividerDrawerItem();
        final PrimaryDrawerItem currentPlace = new PrimaryDrawerItem().withName(R.string.drawer_item_current_place).withIcon(Ionicons.Icon.ion_navigate).withIdentifier(0);
        final PrimaryDrawerItem cityList = new PrimaryDrawerItem().withName(R.string.drawer_item_city_list).withIcon(CommunityMaterial.Icon.cmd_city).withIdentifier(1);
        favorites = new PrimaryDrawerItem().withName(R.string.drawer_item_favorites).withIcon(MaterialDesignIconic.Icon.gmi_star).withBadge(String.valueOf(PositionManager.getInstance().getFavouritesList().size())).withIdentifier(2);
        final SecondaryDrawerItem settings = new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3);
        final SecondaryDrawerItem feedBack = new SecondaryDrawerItem().withName(R.string.drawer_item_feedback).withIcon(GoogleMaterial.Icon.gmd_feedback).withIdentifier(4);

        /** Создание Navigation Drawer */
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withSelectedItem(-1)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        currentPlace,
                        cityList,
                        favorites,
                        divider,
                        settings,
                        feedBack
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View v, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case 0:
                                activity.changeDisplayedCity("");
                                result.closeDrawer();
                                //TODO add unselect item
                                break;
                            case 1:
                                activity.startCityPickerActivity();
                                result.closeDrawer();
                                //TODO add unselect item
                                break;
                            case 2:
                                if (opened) {
                                    for (int i = PositionManager.getInstance().getFavouritesList().size() - 1; i >= 0; i--) {
                                        result.removeItems(subItemIndex + i);

                                    }
                                } else {
                                    int curPos = result.getPosition(drawerItem);
                                    if (!PositionManager.getInstance().getFavouritesList().isEmpty()) {
                                        for (int i = PositionManager.getInstance().getFavouritesList().size() - 1; i >= 0; i--) {
                                            String city = PositionManager.getInstance().getFavouritesList().get(i).split(",")[0];
                                            result.addItemsAtPosition(
                                                    curPos,
                                                    new SecondaryDrawerItem().withLevel(2).withName(city).withIdentifier(subItemIndex + i)
                                            );
                                        }
                                    } else {
                                        Logger.println(TAG, "favCityList is empty");
                                    }
                                }

                                opened = !opened;
                                break;
                            case 3:
//                                Toast.makeText(WeatherActivity.this, "Intent for settings ", Toast.LENGTH_SHORT).show();
                                activity.startSettingsActivity();
                                result.closeDrawer();
                                break;
                            case 4:
                                //TODO add unselect item
                                // FIXME: 31.01.16
                                Intent feedbackIntent = null;
                                switch (Locale.getDefault().getLanguage()) {
                                    case "ru":
                                        feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyApplication.getAppContext().getString(R.string.google_form_ru)));
                                        break;
                                    default:
                                        feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyApplication.getAppContext().getString(R.string.google_form_en)));
                                        break;
                                }
                                activity.startActivity(feedbackIntent);
                                result.closeDrawer();
                                break;
                            default:
                                String newCity = PositionManager.getInstance().getFavouritesList().get(drawerItem.getIdentifier() - subItemIndex);
                                activity.changeDisplayedCity(newCity);
                                result.closeDrawer();
                                break;
                        }
                        return true;
                    }
                })
                .build();

    }


}
