package com.khasang.forecast.activities.etc;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.khasang.forecast.Logger;
import com.khasang.forecast.PermissionChecker;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import java.util.List;

import static com.khasang.forecast.PermissionChecker.RuntimePermissions.PERMISSION_REQUEST_FINE_LOCATION;

/**
 * Класс отвечает за логику NavigationDrawer
 */
public class NavigationDrawer implements Drawer.OnDrawerItemClickListener {

    private final String TAG = this.getClass().getSimpleName();

    public static final int NAVIGATION_CURRENT_PLACE = 0;
    public static final int NAVIGATION_CITY_LIST = 1;
    public static final int NAVIGATION_FAVORITES = 2;
    public static final int NAVIGATION_SETTINGS = 3;
    public static final int NAVIGATION_FEEDBACK = 4;
    public static final int NAVIGATION_APP_NAME = 5;

    public static final int SUB_ITEMS_BASE_INDEX = 2000;

    private Activity activity;
    private PrimaryDrawerItem currentPlace;
    private PrimaryDrawerItem favorites;
    private Drawer result;
    private boolean opened;
    private OnNavigationItemClickListener navigationItemClickListener;
    private int activeIdentifier = NAVIGATION_CURRENT_PLACE;

    public NavigationDrawer(Activity activity, Toolbar toolbar) {
        this.activity = activity;

        /** Инициализация элементов меню */
        DividerDrawerItem divider = new DividerDrawerItem();
        currentPlace = new PrimaryDrawerItem().withName(R.string.drawer_item_current_place)
            .withIcon(Ionicons.Icon.ion_navigate)
            .withIdentifier(NAVIGATION_CURRENT_PLACE);

        PrimaryDrawerItem cityList = new PrimaryDrawerItem().withName(R.string.drawer_item_city_list)
            .withIcon(CommunityMaterial.Icon.cmd_city)
            .withIdentifier(NAVIGATION_CITY_LIST);

        favorites = new PrimaryDrawerItem().withName(R.string.drawer_item_favorites)
            .withIcon(MaterialDesignIconic.Icon.gmi_star)
            .withIdentifier(NAVIGATION_FAVORITES);

        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName(R.string.drawer_item_settings)
            .withIcon(FontAwesome.Icon.faw_cog)
            .withIdentifier(NAVIGATION_SETTINGS);

        SecondaryDrawerItem feedBack = new SecondaryDrawerItem().withName(R.string.drawer_item_feedback)
            .withIcon(GoogleMaterial.Icon.gmd_feedback)
            .withIdentifier(NAVIGATION_FEEDBACK);

        PrimaryDrawerItem footer = new PrimaryDrawerItem().withName(R.string.app_name)
            .withEnabled(false)
            .withIdentifier(NAVIGATION_APP_NAME);

        /** Создание Navigation Drawer */
        result = new DrawerBuilder().withActivity(activity)
            .withToolbar(toolbar)
            .withSelectedItem(-1)
            .withActionBarDrawerToggle(true)
            .withHeader(R.layout.drawer_header)
            .addDrawerItems(currentPlace, cityList, favorites, divider, settings, feedBack)
            .addStickyDrawerItems(footer)
            .withOnDrawerItemClickListener(this)
            .build();

        result.setSelection(activeIdentifier);
    }

    /**
     * Обработчик кликов по Navigation Drawer
     */
    @Override
    public boolean onItemClick(View v, int position, IDrawerItem drawerItem) {
        if (drawerItem == null) {
            return false;
        }
        int identifier = drawerItem.getIdentifier();
        if (navigationItemClickListener != null) {
            navigationItemClickListener.OnNavigationItemClicked(identifier);
        }
        switch (identifier) {
            case NAVIGATION_CURRENT_PLACE:
                activeIdentifier = NAVIGATION_CURRENT_PLACE;
                result.closeDrawer();
                break;
            case NAVIGATION_CITY_LIST:
                setSelection(activeIdentifier);
                result.closeDrawer();
                break;
            case NAVIGATION_FAVORITES:
                List<String> favCities = PositionManager.getInstance().getFavouritesList();
                if (opened) {
                    for (int i = favCities.size() - 1; i >= 0; i--) {
                        result.removeItems(SUB_ITEMS_BASE_INDEX + i);
                    }
                } else {
                    if (favCities.isEmpty()) {
                        Logger.println(TAG, "favCityList is empty");
                    } else {
                        for (int i = favCities.size() - 1; i >= 0; i--) {
                            String city = favCities.get(i).split(",")[0];
                            result.addItemsAtPosition(position, new SecondaryDrawerItem().withLevel(2)
                                .withName(city)
                                .withIdentifier(SUB_ITEMS_BASE_INDEX + i));
                        }
                    }
                }
                opened = !opened;
                break;
            case NAVIGATION_SETTINGS:
                setSelection(activeIdentifier);
                result.closeDrawer();
                break;
            case NAVIGATION_FEEDBACK:
                setSelection(activeIdentifier);
                result.closeDrawer();
                break;
            case NAVIGATION_APP_NAME:
                break;
            default:
                activeIdentifier = identifier;
                result.closeDrawer();
                break;
        }
        return true;
    }

    private void setSelection(int identifier) {
        result.setSelection(activeIdentifier);
        if (identifier == NAVIGATION_CURRENT_PLACE) {
            closeSubItems();
        }
    }

    private void closeSubItems() {
        for (int i = PositionManager.getInstance().getFavouritesList().size() - 1; i >= 0; i--) {
            result.removeItems(SUB_ITEMS_BASE_INDEX + i);
        }
        if (opened) {
            opened = false;
        }
        favorites.withSelectable(false);
        result.updateItem(favorites);
    }

    public void setNavigationItemClickListener(OnNavigationItemClickListener navigationItemClickListener) {
        this.navigationItemClickListener = navigationItemClickListener;
    }

    /**
     * Обновление Drawer badges
     */
    public void updateBadges() {
        PermissionChecker permissionChecker = new PermissionChecker();
        boolean isLocationPermissionGranted =
            permissionChecker.isPermissionGranted(activity, PERMISSION_REQUEST_FINE_LOCATION);

        currentPlace.withEnabled(isLocationPermissionGranted);
        result.updateItem(currentPlace);

        List<String> favCities = PositionManager.getInstance().getFavouritesList();
        if (favCities.isEmpty()) {
            favorites.withBadge("").withEnabled(false);
            result.updateItem(favorites);
        } else {
            favorites.withEnabled(true);
            result.updateItem(favorites);
            result.updateBadge(2, new StringHolder(String.valueOf(favCities.size())));
        }
    }

    public boolean closeDrawer() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
            return true;
        }
        return false;
    }

    public interface OnNavigationItemClickListener {

        void OnNavigationItemClicked(int identifier);
    }
}
