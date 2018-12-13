package android.under_dash.addresses.search.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.under_dash.addresses.search.app.App;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static final String TAG = "GeocodingLocation";

    public static String getLatLongFromLocation(final String locationAddress) {
        Geocoder geocoder = new Geocoder(App.get(), Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationAddress, 5);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLatitude()).append(",").append(address.getLongitude());
                result = sb.toString();
            }
        } catch (Exception e) {
            Log.e("shimi", "Unable to connect to Geocoder", e);
        }
        return result;
    }

    public static boolean isBadPosSize(List mList, int position) {
        return mList == null || position < 0 || position >= mList.size();
    }
}