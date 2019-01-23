package android.under_dash.addresses.search.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.under_dash.addresses.search.utils.RestUtil;
import android.under_dash.addresses.search.app.SharedPrefManager;
import android.under_dash.addresses.search.models.objectBox.Address;
import android.under_dash.addresses.search.models.objectBox.AddressMap;
import android.under_dash.addresses.search.models.matrixMapApi.DistanceResponse;
import android.under_dash.addresses.search.models.matrixMapApi.Element;
import android.under_dash.addresses.search.models.matrixMapApi.Row;
import android.util.Log;
import org.apache.commons.collections4.ListUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsMatrixApiServiceJava extends AsyncTask<String, String, String> {


    private static final String TAG = MapsMatrixApiServiceJava.class.getSimpleName();
    private static final long MIN_API_CALL_INTERVAL = DateUtils.SECOND_IN_MILLIS*10;//A google restriction for sending a request one after the other
    private int mCount;
    private int mTotal;
    private final Runnable mOnDone;
    private List<Address> mStartPointAddresses;
    private List<Address> mDestinationAddresses;
    private ProgressDialog mDialog;
    private long mLastCallToApi;

//     Call to this job will look like this
//    GoogleMapsMatrixApiService.build(context, startPointAddresses, destinationAddresses, () ->
//          OnDone run();
//    }).execute();


    public static MapsMatrixApiServiceJava build(Context context, List<Address> startPointAddressNameId, List<Address> destinationAddressNameId, Runnable onDone){
        return new MapsMatrixApiServiceJava(context, startPointAddressNameId,  destinationAddressNameId,  onDone);
    }

    private MapsMatrixApiServiceJava(Context context, List<Address> startPointAddresses, List<Address> destinationAddresses, Runnable onDone) {
        this.mStartPointAddresses  = startPointAddresses;
        this.mDestinationAddresses = destinationAddresses;
        this.mOnDone = onDone;
        mLastCallToApi = 0;
        mCount = 0;
        mTotal = 0;
        mDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute(){
        mDialog.setTitle("Importing Data into Secure DataBase");
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.setIcon(android.R.drawable.ic_dialog_info);
        mDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Log.e(TAG, "inonProgressUpdate values[0] = "+values[0]);
        mDialog.setMessage("Please wait... "+values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        if(mStartPointAddresses.isEmpty() || mDestinationAddresses.isEmpty()){
            Log.e(TAG, "##### ONE LIST IS EMPTY ##### aborting job");
            return "";
        }
        Log.i(TAG, "doInBackground: mStartPointAddresses.size() = "+ mStartPointAddresses.size()+" mDestinationAddresses.size() = "+mDestinationAddresses.size());
        startJob(mStartPointAddresses, mDestinationAddresses);
        return "";
    }

    protected void onPostExecute(String data)
    {
        Log.i(TAG, "onPostExecute: ");
        if (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
        if(mOnDone != null){
            mOnDone.run();
        }
        mDialog = null;
    }

    private void startJob(List<Address> startPointList, List<Address> destinationList) {
        Log.d(TAG,"in getDistanceInfoAndAddInDb startPointList.size = "+startPointList.size()+" destinationList.size = "+destinationList.size());
        mCount = 0;
        int targetSize = 10;
        List<List<Address>> startPointListOfList = new ArrayList<>(ListUtils.partition(startPointList, targetSize));
        List<List<Address>> destinationListOfList = new ArrayList<>(ListUtils.partition(destinationList, targetSize));
        List<List<Address>> originDestinationListOfList = new ArrayList<>(destinationListOfList);

        mTotal = startPointListOfList.size() * destinationListOfList.size();
        publishProgress("Done with list partition");
        getDistanceInfoRecursion(startPointListOfList,destinationListOfList,originDestinationListOfList);

    }

    private void getDistanceInfoRecursion(final List<List<Address>> startPointListOfList,
                                                 final  List<List<Address>> destinationListOfList,
                                                 final List<List<Address>> originDestinationListOfList) {

        Log.d(TAG,"in getDistanceInfoRecursion sCount = "+mCount+" startPointListOfList.size = "+startPointListOfList.size()
                +" destinationListOfList.size = "+destinationListOfList.size()
                +" originDestinationListOfList.size = "+originDestinationListOfList.size());
        if(startPointListOfList.size() == 0 ){
            Log.d(TAG,"in getDistanceInfoRecursion END");
            return;
        }

        if(destinationListOfList.size() > 0) {
            mCount ++;
            List<Address> startPointList = startPointListOfList.get(0);
            List<Address> destinationList = destinationListOfList.get(0);
            destinationListOfList.remove(0);
            checkIfIntervalNeeded();
            getDistanceInfoAndAddInDb(startPointList, destinationList);
            getDistanceInfoRecursion(startPointListOfList, destinationListOfList, originDestinationListOfList);

        }else{
            startPointListOfList.remove(0);
            List<List<Address>> newDestinationListOfList = new ArrayList<>(originDestinationListOfList);
            getDistanceInfoRecursion(startPointListOfList, newDestinationListOfList,originDestinationListOfList);
        }
        publishProgress(mCount +" Done out of "+mTotal);
    }

    private void checkIfIntervalNeeded() {
        if(mLastCallToApi > 0 ) {
            long lastCallInterval = mLastCallToApi - System.currentTimeMillis();
            long interval = MIN_API_CALL_INTERVAL - lastCallInterval;
            Log.e(TAG, "in checkIfIntervalNeeded interval = "+interval+" lastCallInterval = "+lastCallInterval);
            if (interval > 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mLastCallToApi = System.currentTimeMillis();
    }

    private String getFormatDistanceInfo(List<Address> startPointList) {
        StringBuilder destination = new StringBuilder();
        for (Address address : startPointList) {
            if (address != null) {
                destination.append(address.address).append("|");
            }
        }
        return destination.toString();
    }


    private void getDistanceInfoAndAddInDb(List<Address> startPointList, List<Address> destinationList) {
        String startPoint  = getFormatDistanceInfo(startPointList);
        String destination = getFormatDistanceInfo(destinationList);
        Log.i(TAG, "in getDistanceInfoAndAddInDb: startPointList.size = "+startPointList.size()+" destinationList.size = "+destinationList.size());
        Map<String, String> mapQuery = new HashMap<>();
        //mapQuery.put("units", "imperial"); metric is default
        mapQuery.put("origins", startPoint);
        mapQuery.put("destinations", destination );//+ "|" + cities[1] + "|" + cities[2]
        mapQuery.put("mode", "walking");
        mapQuery.put("key", SharedPrefManager.get().getGoogleApiKey());
//        mapQuery.put("destinations[1]", cities[1]);
//        mapQuery.put("destinations[2]", cities[2]);
        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);


        try {
            DistanceResponse body = client.getDistanceInfo(mapQuery).execute().body();
            Log.d(TAG,"onResponse call = "+ (body == null ? "null" : body.getStatus()));
            if (body != null){
                addRespondToDb(body, startPointList, destinationList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addRespondToDb(DistanceResponse body, List<Address> startPointList, List<Address> destinationList) {
        List<Row> rows = body.getRows();
        Log.d(TAG, "addRespondToDb: rows = "+rows.size()+" "+body.getDestinationAddresses().size()+" "+body.getOriginAddresses().size());
        for (int i = 0; i < rows.size() ; i++){
            Address originAddress = startPointList.get(i);
            List<Element> elements = rows.get(i).getElements();
            Log.d(TAG, "addRespondToDb: elements = "+elements.size());
            for (int y = 0; y < elements.size(); y++) {
                Element element = elements.get(y);
                Address destinationAddress = destinationList.get(y);
                if(destinationAddress != null){
                    int       distance = element.getDistance().getValue();
                    int       duration = element.getDuration().getValue();
                    String    distanceText = element.getDuration().getText();
                    String    durationText = element.getDistance().getText();
                    AddressMap.add(distance, duration, distanceText, durationText, originAddress,  destinationAddress);
                    Log.d(TAG, "getStartLatLong = "+destinationAddress+" Distance = "+element.getDuration().getValue()+
                            " Duration = "+element.getDistance().getValue()+" (address != null) = "+(destinationAddress != null));
                }
            }
        }
    }


}