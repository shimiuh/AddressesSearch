package android.under_dash.addresses.search.app;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class SharedPrefManager {

    private static final String TAG = SharedPrefManager.class.getSimpleName();
    private SharedPreferences         mAppinfoPrefs;
    private final String             PREFS_NAME  = "PREFS_NAME";
    private static volatile SharedPrefManager sInstance = null;

    private static final String KRY_SEARCH_ID = "KRY_SEARCH_ID";
    private static final String KRY_RESULT_ID = "KRY_SEARCH_ID";
    private static final String GOOGLE_API_KEY = "GOOGLE_API_KEY";

    private SharedPrefManager() {
        mAppinfoPrefs = App.getAppContext().getSharedPreferences(PREFS_NAME, 0);
    }

    synchronized public static SharedPrefManager get(){
        if(sInstance == null){
            synchronized(SharedPrefManager.class) {
                if (sInstance == null) {
                    sInstance = new SharedPrefManager();
                }
            }
        }
        return sInstance;
    }

    public void clearAll() {
        mAppinfoPrefs.edit().clear().apply();
    }



    public Long getResultId() {
        return mAppinfoPrefs.getLong(KRY_RESULT_ID, 1);
    }
    public void setResultId(Long id) {
        mAppinfoPrefs.edit().putLong(KRY_RESULT_ID, id).apply();
        Log.d("shimi"," in setResultId id = "+id+"  getResultId() = "+getResultId());
    }

    public Long getSearchId() {
        return mAppinfoPrefs.getLong(KRY_SEARCH_ID, 2);
    }
    public void setSearchId(Long id) {
        mAppinfoPrefs.edit().putLong(KRY_SEARCH_ID, id).apply();
        Log.d("shimi"," in setSearchId id = "+id+"  getSearchId() = "+getSearchId());
    }

    public void setGoogleApiKey(String key) {
        mAppinfoPrefs.edit().putString(GOOGLE_API_KEY, key).apply();
        Log.d("shimi"," in setGoogleApiKey key = "+key);
    }

    public String getGoogleApiKey() {
        String key = mAppinfoPrefs.getString(GOOGLE_API_KEY, "");
        try {
            key = new CsvReader().read(new File(Constants.GOOGLE_API_KEY_PATH), StandardCharsets.UTF_8).getRow(0).getField(0);
        } catch (Exception e) { e.printStackTrace();}
        setGoogleApiKey(key);
        return key;
    }
}
