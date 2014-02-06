package com.sage42.android.map;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

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
    private static final String              TAG          = GoogleMapManager.class.getSimpleName();

    // default map settings
    public static final float                DEFAULT_ZOOM = 14f;

    // the map instance
    private GoogleMap                        mMap;

    // Location API
    protected LocationClient                 mLocationClient;

    // callback/notify container
    private final IGoogleMapManagerCallbacks mCallback;

    public GoogleMapManager(final IGoogleMapManagerCallbacks callback)
    {
        super();
        this.mCallback = callback;
    }

    public void init(final SupportMapFragment mapFragment)
    {
        if (this.mMap != null)
        {
            // map already init
            if (BuildConfig.DEBUG)
            {
                Log.w(TAG, "Attempt to init map failed - already initiated"); //$NON-NLS-1$
            }
            return;
        }

        // get map instance from supplied fragment
        this.mMap = mapFragment.getMap();
        if (this.mMap == null)
        {
            if (BuildConfig.DEBUG)
            {
                Log.w(TAG, "Attempt to init map failed - no map found in supplied fragment"); //$NON-NLS-1$
            }
            return;
        }

        this.mapSettings();

        // listen and redraw pins
        this.mMap.setOnCameraChangeListener(this);
    }

    /**
     * These will need refactoring into some kind of callback or just plain removed
     */
    private void mapSettings()
    {
        // The Map is verified. It is now safe to manipulate the map.
        this.mMap.setMyLocationEnabled(true);

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
        if (this.mLocationClient == null)
        {
            this.mLocationClient = new LocationClient(activity, this, this);
            this.mLocationClient.connect();
        }
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
        this.mLocationClient = null;
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
        if ((this.mLocationClient != null) && this.mLocationClient.isConnected())
        {
            final Location location = this.mLocationClient.getLastLocation();
            if ((location != null) && (this.mMap != null))
            {
                this.showLocation(location);
            }
        }
    }

    /**
     * Center the map fragment on location
     */
    public void showLocation(final Location location)
    {
        if ((location != null) && (this.mMap != null))
        {
            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
        }
    }

    public LatLng getLastLocation()
    {
        if ((this.mLocationClient == null) || !this.mLocationClient.isConnected())
        {
            return null;
        }
        final Location location = this.mLocationClient.getLastLocation();
        if (location == null)
        {
            return null;
        }
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void zoomIn()
    {
        if (this.mMap == null)
        {
            if (BuildConfig.DEBUG)
            {
                Log.w(TAG, "Zoom In failed, no map"); //$NON-NLS-1$
            }
            return;
        }

        this.mMap.animateCamera(CameraUpdateFactory.zoomBy(1f));
    }

    public void zoomOut()
    {
        if (this.mMap == null)
        {
            if (BuildConfig.DEBUG)
            {
                Log.w(TAG, "Zoom Out failed, no map"); //$NON-NLS-1$
            }
            return;
        }

        this.mMap.animateCamera(CameraUpdateFactory.zoomBy(-1f));
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result)
    {
        // TODO: put GPS warning banner here?
        if (BuildConfig.DEBUG)
        {
            Log.w(TAG, "Connection to LocationClient has failed with: " + result.getErrorCode()); //$NON-NLS-1$
        }
    }

    @Override
    public void onConnected(final Bundle connectionHint)
    {
        if (BuildConfig.DEBUG)
        {
            Log.i(TAG, "Connection to LocationClient has successful."); //$NON-NLS-1$
        }
        // inform that the map is fully ready and connected to location services
        if (this.mCallback != null)
        {
            this.mCallback.onMapReady();
        }
    }

    @Override
    public void onDisconnected()
    {
        if (BuildConfig.DEBUG)
        {
            Log.w(TAG, "Connection to LocationClient has disconneced."); //$NON-NLS-1$
        }
    }

    /**
     * Callbacks/Events into containing activity/fragment
     */
    public interface IGoogleMapManagerCallbacks
    {
        void onMapReady();
    }

    /**
     * @return the locationClient
     */
    public LocationClient getLocationClient()
    {
        return this.mLocationClient;
    }

}
