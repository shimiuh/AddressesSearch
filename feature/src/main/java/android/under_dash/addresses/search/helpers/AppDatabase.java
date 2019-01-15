package android.under_dash.addresses.search.helpers;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.models.Institution;
import android.under_dash.addresses.search.models.SearchList;
import android.under_dash.addresses.search.models.SearchResult;

@Database(entities = {Institution.class, SearchList.class, SearchResult.class }, version = 11, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase mInstance;
    private static final String DB_NAME = "institution.db";
    public abstract InstitutionDao institutionDao();
    public abstract SearchListDao searchListDao();
    public abstract SearchResDao searchResDao();

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

    public void addSearchItem(final SearchList... searchList){
        App.getBackgroundHandler().post(() -> searchListDao().insertAll(searchList));
    }

    public void addInstitution(final Institution... institution){
        App.getBackgroundHandler().post(() -> institutionDao().insertAll(institution));
    }
}
