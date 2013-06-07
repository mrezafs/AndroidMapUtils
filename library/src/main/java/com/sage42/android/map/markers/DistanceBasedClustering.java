package com.sage42.android.map.markers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.sage42.android.map.DistanceUtils;

/**
 * Copyright (C) 2013- Sage 42 App Sdn Bhd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Reference: https://developers.google.com/maps/articles/toomanymarkers#distancebasedclustering
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
