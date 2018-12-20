package android.under_dash.addresses.search.app;

import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefManager {

    private static final String TAG = SharedPrefManager.class.getSimpleName();
    private SharedPreferences         mAppinfoPrefs;
    private final String             PREFS_NAME  = "PREFS_NAME";
    private static volatile SharedPrefManager sInstance = null;

    private static final String KRY_SEARCH_ID = "KRY_SEARCH_ID";
    private static final String KRY_RESULT_ID = "KRY_SEARCH_ID";

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
        return mAppinfoPrefs.getLong(KRY_RESULT_ID, 0);
    }
    public void setResultId(Long id) {
        mAppinfoPrefs.edit().putLong(KRY_RESULT_ID, id).apply();
        Log.d("shimi"," in setResultId id = "+id+"  getResultId() = "+getResultId());
    }

    public Long getSearchId() {
        return mAppinfoPrefs.getLong(KRY_SEARCH_ID, 0);
    }
    public void setSearchId(Long id) {
        mAppinfoPrefs.edit().putLong(KRY_SEARCH_ID, id).apply();
        Log.d("shimi"," in setSearchId id = "+id+"  getSearchId() = "+getSearchId());
    }
}
