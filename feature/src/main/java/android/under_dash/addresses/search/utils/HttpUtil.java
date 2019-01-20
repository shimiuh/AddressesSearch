package android.under_dash.addresses.search.utils;

import android.location.Address;
import android.location.Geocoder;
import android.under_dash.addresses.search.app.App;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {



    // Call must be of the Ui thread
    public static Address getLatLongFromLocation(final String locationAddress) {
        Geocoder geocoder = new Geocoder(App.get(), Locale.getDefault());
        Address result = new Address( Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationAddress, 5);
            if (addressList != null && addressList.size() > 0) {
                result = addressList.get(0);
//                StringBuilder sb = new StringBuilder();
//                sb.append(address.getLatitude()).append(",").append(address.getLongitude());
//                result = sb.toString();
            }
        } catch (Exception e) {
            Log.e("shimi", "Unable to connect to Geocoder", e);
        }
        return result;
    }

    // Call must be of the Ui thread
    public static double getElevation(final String locationAddress) {
        Address address = getLatLongFromLocation(locationAddress);
        return getElevation(address.getLongitude(), address.getLatitude());
    }
    // Call must be of the Ui thread
    public static double getElevation(double longitude, double latitude) {
        final double[] result = {Double.NaN};
        OkHttpClient httpClient = new OkHttpClient();
        String url = "http://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(latitude)
                + "," + String.valueOf(longitude)
                + "&sensor=true";
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = httpClient.newCall(request).execute();
            String respStr = response.toString();
            String tagOpen = "<elevation>";
            String tagClose = "</elevation>";
            if (respStr.contains(tagOpen)) {
                int start = respStr.indexOf(tagOpen) + tagOpen.length();
                int end = respStr.indexOf(tagClose);
                String value = respStr.substring(start, end);
                result[0] = Double.parseDouble(value);//(double)(Double.parseDouble(value)*3.2808399); // convert from meters to feet
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
// code based of https://stackoverflow.com/questions/1995998/get-altitude-by-longitude-and-latitude-in-android

        return result[0];
    }


}
