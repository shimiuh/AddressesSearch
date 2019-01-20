package android.under_dash.addresses.search.utils;

import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();


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