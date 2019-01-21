package android.under_dash.addresses.search.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.utils.RestUtil;
import android.under_dash.addresses.search.app.SharedPrefManager;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressMap;
import android.under_dash.addresses.search.models.DistanceResponse;
import android.under_dash.addresses.search.models.Element;
import android.under_dash.addresses.search.models.Row;
import android.util.Log;

import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleMapsMatrixApiService extends AsyncTask<String, String, String> {


    private static final String TAG = GoogleMapsMatrixApiService.class.getSimpleName();
    private int mCount = 0;//Just for log TODO: remove for release
    private int mTotal = 0;//Just for log TODO: remove for release
    private final Runnable mOnDone;
    private List<Address> mStartPointAddresses;
    private List<Address> mDestinationAddresses;
    private ProgressDialog mDialog;
    private boolean mIsJobInProgress;
    private long mLastCallToApi;

//     Call to this job will look like this
//    GoogleMapsMatrixApiService.build(context, startPointAddresses, destinationAddresses, () -> {
//    }).execute();


    public static GoogleMapsMatrixApiService build(Context context, List<Address> startPointAddressNameId, List<Address> destinationAddressNameId, Runnable onDone){
        return new GoogleMapsMatrixApiService(context, startPointAddressNameId,  destinationAddressNameId,  onDone);
    }

    private GoogleMapsMatrixApiService(Context context, List<Address> startPointAddresses, List<Address> destinationAddresses, Runnable onDone) {
        this.mStartPointAddresses  = startPointAddresses;
        this.mDestinationAddresses = destinationAddresses;
        this.mOnDone = onDone;
        mLastCallToApi = 0;
        mDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute()
    {
        mDialog.setTitle("Importing Data into Secure DataBase");
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.setIcon(android.R.drawable.ic_dialog_info);
        mDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
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
        getDistanceInfoAndAddInDb(mStartPointAddresses, mDestinationAddresses);
        mIsJobInProgress = true;
        while (mIsJobInProgress){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
        if (data.length()!=0){
            Log.d(TAG,"File is built Successfully! size = "+App.getBox(Address.class).getAll().size());
        }else{
            Log.d(TAG,"File fail to build");
        }
        mDialog = null;
    }

    private void getDistanceInfoAndAddInDb(List<Address> startPointList, List<Address> destinationList) {
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
            mIsJobInProgress = false;
            return;
        }

        if(destinationListOfList.size() > 0) {
            mCount ++;
            List<Address> startPointList = startPointListOfList.get(0);
            List<Address> destinationList = destinationListOfList.get(0);
            destinationListOfList.remove(0);
            long lastCallInterval = mLastCallToApi - System.currentTimeMillis();
            if(lastCallInterval < 10000) {
                App.getBackgroundHandler().postDelayed(() -> {
                    mLastCallToApi = System.currentTimeMillis();
                    getDistanceInfoAndAddInDb(startPointList, destinationList, () -> {
                        getDistanceInfoRecursion(startPointListOfList, destinationListOfList, originDestinationListOfList);
                    });
                },lastCallInterval+200);

            }
        }else{
            startPointListOfList.remove(0);
            List<List<Address>> newDestinationListOfList = new ArrayList<>(originDestinationListOfList);
            getDistanceInfoRecursion(startPointListOfList, newDestinationListOfList,originDestinationListOfList);
        }
        publishProgress(mCount +" Done out of "+mTotal);
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


    private void getDistanceInfoAndAddInDb(List<Address> startPointList, List<Address> destinationList, Runnable onDone) {
        String startPoint  = getFormatDistanceInfo(startPointList);
        String destination = getFormatDistanceInfo(destinationList);
        Log.i(TAG, "in getDistanceInfoAndAddInDb: startPointList.size = "+startPointList.size()+" destinationList.size = "+destinationList.size());
        //https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
        Map<String, String> mapQuery = new HashMap<>();
        //mapQuery.put("units", "imperial"); metric is default
        mapQuery.put("origins", startPoint);
        mapQuery.put("destinations", destination );//+ "|" + cities[1] + "|" + cities[2]
        mapQuery.put("mode", "walking");
        mapQuery.put("key", SharedPrefManager.get().getGoogleApiKey());
//        mapQuery.put("destinations[1]", cities[1]);
//        mapQuery.put("destinations[2]", cities[2]);
        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);

        Call<DistanceResponse> call = client.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<DistanceResponse> call, @NonNull Response<DistanceResponse> response) {
                DistanceResponse body = response.body();
                Log.d(TAG,"onResponse call = "+ (body == null ? "null" : body.getStatus()));
                if (body!= null){
                    addRespondToDb(body);
                }
            }
            @Override
            public void onFailure(@NonNull Call<DistanceResponse> call, @NonNull Throwable t) {
                Log.d(TAG,"onFailure call = "+ call.toString()+ " t.getMessage = " +t.getMessage());
                if(onDone != null) {
                    onDone.run();
                }

            }

            private void addRespondToDb(DistanceResponse body) {
                List<Row> rows = body.getRows();
                Log.d(TAG, "addRespondToDb: rows = "+rows.size()+" "+body.getDestinationAddresses().size()+" "+body.getOriginAddresses().size());
                for (int i = 0; i < rows.size() ; i++){
                    Address originAddress = startPointList.get(i); //addressBox.query().equal(Address_.latLong, originLatLong).build().findUnique();
                    List<Element> elements = rows.get(i).getElements();
                    Log.d(TAG, "addRespondToDb: elements = "+elements.size());
                    for (int y = 0; y < elements.size(); y++) {
                        Element element = elements.get(y);
                        //element.getStatus() TODO: ONLY ADD IF STATUS OK
                        Address destinationAddress = destinationList.get(y); //addressBox.query().equal(Address_.latLong, destinationLatLong).build().findUnique();
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
                if(onDone != null) {
                    onDone.run();
                }
            }
        });
    }


}