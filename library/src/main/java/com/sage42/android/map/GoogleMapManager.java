package com.sage42.android.map;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapManager implements OnCameraChangeListener, GooglePlayServicesClient.ConnectionCallbacks,
                GooglePlayServicesClient.OnConnectionFailedListener
{
    public static final int   ANIMATION_DURATION = 2500;

    // default map settings
    public static final float STARTING_ZOOM      = 11f;
    public static final float DEFAULT_ZOOM       = 14f;
    public final int          mMapType           = GoogleMap.MAP_TYPE_NORMAL;

    // the map instance
    private GoogleMap         mMap;

    // Location API
    LocationClient            mLocationClient;

    public void init(final SupportMapFragment mapFragment)
    {
        if (this.mMap != null)
        {
            // map already init
            return;
        }

        // get map instance from supplied fragment
        this.mMap = mapFragment.getMap();

        // Check if we were successful in obtaining the map.
        this.mMap.setMyLocationEnabled(false);
        this.mapSettings();

        // listen and redraw pins
        this.mMap.setOnCameraChangeListener(this);
    }

    private void mapSettings()
    {
        // The Map is verified. It is now safe to manipulate the map.
        final UiSettings settings = this.mMap.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setMyLocationButtonEnabled(false); // We code in our own button
        settings.setRotateGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setTiltGesturesEnabled(false);
        settings.setZoomControlsEnabled(false); // we supply our own
        settings.setZoomGesturesEnabled(true);
    }

    @Override
    public void onCameraChange(final CameraPosition position)
    {
        // nothing yet
    }

    /**
     * This should be called by any fragments that use this class
     * 
     * @param activity
     */
    public void onAttach(final Activity activity)
    {
        // Initialize the locaiton client
        this.mLocationClient = new LocationClient(activity, this, this);
        this.mLocationClient.connect();
    }

    /**
     * This should be called by any fragments that use this class
     */
    public void onDetach()
    {

        if (this.mLocationClient != null)
        {
            this.mLocationClient.disconnect();
        }
    }

    public GoogleMap getMap()
    {
        return this.mMap;
    }

    /**
     * Center the map fragment on the current location
     */
    public void showMyLocation()
    {
        if (this.mLocationClient != null && this.mLocationClient.isConnected())
        {
            final Location location = this.mLocationClient.getLastLocation();
            if (location != null && this.mMap != null)
            {
                this.mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location
                                .getLongitude())));
            }
        }
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result)
    {
        // Do nothing
        // TODO: put GPS warning banner here?
    }

    @Override
    public void onConnected(final Bundle connectionHint)
    {
        // Do nothing
    }

    @Override
    public void onDisconnected()
    {
        // Do nothing
    }

}
