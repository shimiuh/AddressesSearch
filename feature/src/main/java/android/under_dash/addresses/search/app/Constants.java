package android.under_dash.addresses.search.app;

import android.Manifest;
import android.under_dash.addresses.search.R;

public class Constants {

    public static final int    DIALOG_EDIT_TEXT_ID = R.id.addresses_list;
    public static final float  COST_PER_ELEMENT    = 00.5f;
    public static final String GOOGLE_API_KEY_PATH = "/storage/emulated/0/Documents/GoogleApiKey.csv";
    public static int PERMISSION_ALL = 1;
    public static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

}
