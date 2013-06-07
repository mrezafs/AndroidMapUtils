package com.sage42.android.map.markers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.sage42.android.map.DistanceUtils;

/**
 * https://developers.google.com/maps/articles/toomanymarkers#distancebasedclustering
 * 
 * @author Corey Scott (corey.scott@sage42.com)
 *
 */
public class DistanceBasedClustering<T extends IMapLocation>
{
    public Map<LatLng, List<T>> reduce(final List<T> markers, final double minDistanceBetweenClusters)
    {
        final Map<LatLng, List<T>> output = new HashMap<LatLng, List<T>>();

        for (final T thisMarker : markers)
        {
            // check existing clusters for a center point that is within the min distance
            LatLng found = null;
            for (final LatLng centerpoint : output.keySet())
            {
                // calc distance from current marker to this point
                final double distance = DistanceUtils.distanceInMetersFromLatLngs(centerpoint, thisMarker.getLatLng());
                if (distance <= minDistanceBetweenClusters)
                {
                    // cluster found add to cluster
                    found = centerpoint;
                    break;
                }
            }

            if (found == null)
            {
                // start new cluster
                output.put(thisMarker.getLatLng(), new ArrayList<T>());
                output.get(thisMarker.getLatLng()).add(thisMarker);
            }
            else
            {
                // cluster found add to cluster
                output.get(found).add(thisMarker);
            }

        }

        return output;
    }
}
