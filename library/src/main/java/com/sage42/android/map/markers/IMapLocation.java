package com.sage42.android.map.markers;

import com.google.android.gms.maps.model.LatLng;

public interface IMapLocation
{
    /**
     * @return Marker location
     */
    public LatLng getLatLng();

    /**
     * @return unique id
     */
    public String getId();
}
