package android.under_dash.addresses.search.helpers;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.models.objectBox.Address;
import android.under_dash.addresses.search.models.objectBox.AddressList;
import android.under_dash.addresses.search.models.objectBox.AddressMap;
import android.under_dash.addresses.search.models.room.dao.AddressDao;
import android.under_dash.addresses.search.models.room.dao.AddressListDao;
import android.under_dash.addresses.search.models.room.dao.AddressMapDao;

@Database(entities = {Address.class, AddressMap.class, AddressList.class }, version = 11, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase mInstance;
    private static final String DB_NAME = "institution.db";
    public abstract AddressDao institutionDao();
    public abstract AddressListDao searchListDao();
    public abstract AddressMapDao searchResDao();

    public static AppDatabase get(final Context context) {
        if (mInstance == null) {
            synchronized (AppDatabase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
                }
            }
        }
        return mInstance;
    }

    public void addAddressList(final AddressList... addressLists){
        App.getBackgroundHandler().post(() -> searchListDao().insertAll(addressLists));
    }

    public void addAddress(final Address... addresses){
        App.getBackgroundHandler().post(() -> institutionDao().insertAll(addresses));
    }
}
