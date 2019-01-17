package android.under_dash.addresses.search.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.under_dash.addresses.search.app.App;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

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

    public static boolean isBadPosSize(List mList, int position) {
        return mList == null || position < 0 || position >= mList.size();
    }

    public static String convertCents(int cost) {
       return NumberFormat.getCurrencyInstance(Locale.US).format(cost / 100.0);
    }

    //for without "Resource leak: '<unassigned Closeable value>' is never closed" warning, something like this
    public boolean copyAppDbToDownloadFolder() throws IOException {
        try {
            String currDate = new Date(System.currentTimeMillis()).toString();
            File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DATABASE_BUCKUP_NAME" + currDate + ".db");
            File currentDB = App.get().getDatabasePath("DATABASE_NAME");
            if (currentDB.exists()) {
                FileInputStream fis = new FileInputStream(currentDB);
                FileOutputStream fos = new FileOutputStream(backupDB);
                fos.getChannel().transferFrom(fis.getChannel(), 0, fis.getChannel().size());
                // or fis.getChannel().transferTo(0, fis.getChannel().size(), fos.getChannel());
                fis.close();
                fos.close();
                Log.i("Database successfully", " copied to download folder");
                return true;
            }
        } catch (IOException e) {
            Log.d("Copying Database", "fail, reason:", e);
        }
        return false;
    }
}