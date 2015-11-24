package com.khasang.forecast;

/**
 * Created by novoselov on 24.11.2015.
 */
public class LocationFactory {
    private Location location;

    public LocationFactory() {
    }

    public Location createLocation() {
        return createLocation(Location.LOCATION_TYPE.GPS_LOCATION, new Coordinate());
    }

    public Location createLocation(Location.LOCATION_TYPE lType, Coordinate coordinate) {
        location = new Location();
        location.ltype = lType;
        switch (lType) {
            case GPS_LOCATION:
                location.coordinate = LocationManager.getGPSCoordinate();
                break;
            case STATIC_LOCATION:
                location.coordinate = coordinate;
                break;
            default:
                break;
        }
        return location;
    }
}

