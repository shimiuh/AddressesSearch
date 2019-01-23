package android.under_dash.addresses.search.helpers;

import android.under_dash.addresses.search.app.SharedPrefManager;

public class SearchManager {


    private static volatile SearchManager sInstance;
    private long mSearchId;
    private long mResultId;



    private SearchManager(){ }

    synchronized public static SearchManager get(){
        if(sInstance == null){
            synchronized(SearchManager.class) {
                if (sInstance == null) {
                    sInstance = new SearchManager();
                }
            }
        }
        return sInstance;
    }

    public long getSearchId() {
        if(mSearchId == 0){
            mSearchId = SharedPrefManager.get().getSearchId();
        }
//        if(mSearchId == 0){
//            setSearchId(App.getBox(AddressName.class).query().equal(AddressName_.name,Constants.ADDRESS_SEARCH).build().findUnique().getId());
//        }
        return mSearchId;
    }

    public void setSearchId(long mSearchId) {
        this.mSearchId = mSearchId;
        SharedPrefManager.get().setSearchId(this.mSearchId);
    }

    public long getResultId() {
        if(mResultId == 0){
            mResultId = SharedPrefManager.get().getResultId();
        }
        return mResultId;
    }

    public void setResultId(long mResultId) {
        this.mResultId = mResultId;
        SharedPrefManager.get().setResultId(this.mResultId);
    }

    public void swapLists() {
        long resultId = getResultId();
        setResultId(getSearchId());
        setSearchId(resultId);
    }



//    public static void swapLists(Runnable onDone) {
//        if(Looper.getMainLooper() != Looper.myLooper()){
//            App.getBackgroundHandler().post(() -> swapLists(onDone));
//            return;
//        }
//
//        AddressName resultAddressName = App.getBox(AddressName.class).query().equal(AddressName_.name,Constants.ADDRESS_RESULT).build().findUnique();
//        List<Address> resultList = resultAddressName.getAddresses();
//
//        AddressName searchAddressName = App.getBox(AddressName.class).query().equal(AddressName_.name,Constants.ADDRESS_SEARCH).build().findUnique();
//        List<Address> searchList = searchAddressName.getAddresses();
//
//        searchList.forEach(address -> {
//            address.setAddressName(resultAddressName);
//        });
//
//        resultList.forEach(address -> {
//            address.setAddressName(searchAddressName);
//        });
//        if(onDone != null){
//            onDone.run();
//        }
//    }
}
