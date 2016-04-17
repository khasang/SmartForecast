package com.khasang.forecast.activities.etc;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

    private PrimaryDrawerItem currentPlace;
    private PrimaryDrawerItem favorites;
    private Drawer result;
    private boolean opened;
    private OnNavigationItemClickListener navigationItemClickListener;

    // Активный пункт, открытый в основном окне - это может быть текущая позиция или один из
    // избранных городов
    private int activeIdentifier = NAVIGATION_CURRENT_PLACE;

    // Массив идентификаторов избранных городов. Нужен для того, чтобы корректно удалить все старые города
    // после того, как пользователь изменил их
    private int[] favoriteCitiesIdentifiers;

    public NavigationDrawer(Activity activity, Toolbar toolbar) {
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
            case NAVIGATION_CITY_LIST:
                // TODO: нужен идентификатор города
                // Можно было бы не скрывать список городов, если один из них активен. Без идентификатора
                // это сделать сложно
                opened = false;
                result.closeDrawer();
                break;
            case NAVIGATION_SETTINGS:
            case NAVIGATION_FEEDBACK:
                result.closeDrawer();
                break;
            case NAVIGATION_FAVORITES:
                opened = !opened;
                updateFavorites();
                break;
            case NAVIGATION_APP_NAME:
                break;
            case NAVIGATION_CURRENT_PLACE:
            default:
                activeIdentifier = identifier;
                result.closeDrawer();
                break;
        }
        return true;
    }

    public void setNavigationItemClickListener(OnNavigationItemClickListener navigationItemClickListener) {
        this.navigationItemClickListener = navigationItemClickListener;
    }

    /**
     * Обновление Drawer badges
     */
    public void updateBadges(boolean locationPermissionEnabled) {
        currentPlace.withEnabled(locationPermissionEnabled);
        result.updateItem(currentPlace);

        updateFavorites();
        result.setSelection(activeIdentifier);
    }

    private void updateFavorites() {
        List<String> favCities = PositionManager.getInstance().getFavouritesList();
        removeFavorites();
        if (favCities.isEmpty()) {
            favorites.withBadge("").withEnabled(false);
        } else {
            favorites.withEnabled(true);
            result.updateBadge(2, new StringHolder(String.valueOf(favCities.size())));
            if (opened) {
                favoriteCitiesIdentifiers = new int[favCities.size()];
                for (int i = favCities.size() - 1; i >= 0; i--) {
                    int identifier = SUB_ITEMS_BASE_INDEX + i;
                    favoriteCitiesIdentifiers[i] = identifier;
                    String city = favCities.get(i).split(",")[0];
                    int position = result.getPosition(favorites);
                    result.addItemsAtPosition(position, new SecondaryDrawerItem().withLevel(2)
                        .withName(city)
                        .withIdentifier(identifier));
                }
            }
        }
        result.updateItem(favorites);
    }

    private void removeFavorites() {
        if (favoriteCitiesIdentifiers != null) {
            for (int favoriteCitiesIdentifier : favoriteCitiesIdentifiers) {
                result.removeItem(favoriteCitiesIdentifier);
            }
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
