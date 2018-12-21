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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static final String TAG = "GeocodingLocation";

    public static String getLatLongFromLocation(final String locationAddress) {
        Geocoder geocoder = new Geocoder(App.get(), Locale.getDefault());
        String result = "";
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