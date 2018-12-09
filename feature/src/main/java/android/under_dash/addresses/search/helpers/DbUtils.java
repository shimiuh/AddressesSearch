package android.under_dash.addresses.search.helpers;

import android.os.Looper;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.models.AddressResultList;
import android.under_dash.addresses.search.models.AddressSearchList;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class DbUtils {

    public static void swapLists(Runnable onDone) {
        if(Looper.getMainLooper() != Looper.myLooper()){
            App.getBackgroundHandler().post(() -> swapLists(onDone));
            return;
        }

        Box<AddressSearchList> searchBox = App.getBox(AddressSearchList.class);
        Box<AddressResultList> resultBox = App.getBox(AddressResultList.class);
        List<AddressSearchList> searchList = searchBox.getAll();
        List<AddressResultList> resultList = resultBox.getAll();
        searchBox.removeAll();
        resultBox.removeAll();
        List<AddressSearchList> newSearchList = new ArrayList<>();
        resultList.forEach(addressResult -> {
            newSearchList.add(new AddressSearchList(addressResult));
        });

        List<AddressResultList> newResultList = new ArrayList<>();
        searchList.forEach(addressSearchList -> {
            newResultList.add(new AddressResultList(addressSearchList));
        });
        resultBox.put(newResultList);
        searchBox.put(newSearchList);
        if(onDone != null){
            onDone.run();
        }
    }
}
