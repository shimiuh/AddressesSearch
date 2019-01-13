package android.under_dash.addresses.search.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.app.Constants;
import android.under_dash.addresses.search.app.DistanceApiClient;
import android.under_dash.addresses.search.app.RestUtil;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressMap;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.models.AddressName_;
import android.under_dash.addresses.search.models.Address_;
import android.under_dash.addresses.search.models.DistanceResponse;
import android.under_dash.addresses.search.models.Element;
import android.under_dash.addresses.search.models.Institution;
import android.under_dash.addresses.search.models.Row;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.collections4.ListUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import io.objectbox.Box;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleMapsMatrixApiService extends AsyncTask<String, String, String> {


    private static final String TAG = GoogleMapsMatrixApiService.class.getSimpleName();
    private static final String YOUR_API_KEY = "AIzaSyAOZZ2Xul6PmPxBUHxTZG7UurJOch_IDQ4";

    private int mCount = 0;
    private int mTotal = 0;
    private final Runnable mOnDone;
    private List<Address> mStartPointAddresses;
    private List<Address> mDestinationAddresses;
    private Context mContext;
    private ProgressDialog mDialog;
    private boolean isGettingInfo;
    private long mLastCallToApi;


    public static GoogleMapsMatrixApiService build(Context context, List<Address> startPointAddressNameId, List<Address> destinationAddressNameId, Runnable onDone){
        return new GoogleMapsMatrixApiService(context, startPointAddressNameId,  destinationAddressNameId,  onDone);
    }

    public GoogleMapsMatrixApiService(Context context, List<Address> startPointAddresses, List<Address> destinationAddresses, Runnable onDone) {
        this.mContext = context;
        this.mStartPointAddresses  = startPointAddresses;
        this.mDestinationAddresses = destinationAddresses;
        this.mOnDone = onDone;
        mLastCallToApi = 0;
    }

    @Override
    protected void onPreExecute()
    {
        mDialog =new ProgressDialog(mContext);
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

        String data="";

        if(mStartPointAddresses.isEmpty() || mDestinationAddresses.isEmpty()){
            Log.e(TAG, "##### ONE LIST IS EMPTY ##### aborting calling");
            return "";
        }


        Log.i(TAG, "doInBackground: mStartPointAddresses.size() = "+ mStartPointAddresses.size()+" mDestinationAddresses.size() = "+mDestinationAddresses.size());
        getDistanceInfoAndAddInDb(mStartPointAddresses, mDestinationAddresses);
        isGettingInfo = true;
        while (isGettingInfo){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return data="";

    }

    protected void onPostExecute(String data)
    {
        Log.i(TAG, "onPostExecute: ");
        if (mDialog.isShowing()){
            mDialog.dismiss();
        }
        if(mOnDone != null){
            mOnDone.run();
        }
        if (data.length()!=0){
            Toast.makeText(mContext, "File is built Successfully! size = "+App.getBox(Address.class).getAll().size(), Toast.LENGTH_LONG).show();
            //Log.d("shimi", "File is built Successfully!"+"\n"+data+"\n institutionDB = "+AppDatabase.get(mContext).institutionDao().getAll().toString());
        }else{
            Toast.makeText(mContext, "File fail to build", Toast.LENGTH_SHORT).show();
        }
        mContext = null;
    }

    private void getDistanceInfoAndAddInDb(List<Address> startPointList, List<Address> destinationList) {
        Log.d(TAG,"in getDistanceInfoAndAddInDb startPointList.size = "+startPointList.size()+" destinationList.size = "+destinationList.size());

//        if(Looper.myLooper() == Looper.getMainLooper()){
//            App.getBackgroundHandler().post(() -> {
//                getDistanceInfoAndAddInDb(startPointList, destinationList);
//            });
//            return;
//        }

        int targetSize = 10;
        List<List<Address>> startPointListOfList = new ArrayList<>(ListUtils.partition(startPointList, targetSize));
        List<List<Address>> destinationListOfList = new ArrayList<>(ListUtils.partition(destinationList, targetSize));

        List<List<Address>> originDestinationListOfList = new ArrayList<>();
        originDestinationListOfList.addAll(destinationListOfList);
        mCount = 0;
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
            isGettingInfo = false;
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
            List<List<Address>>  newDestinationListOfList = new ArrayList<>();
            newDestinationListOfList.addAll(originDestinationListOfList);
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
        Log.i("shimi", "in getDistanceInfoAndAddInDb: startPointList.size = "+startPointList.size()+" destinationList.size = "+destinationList.size());

        // http://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY
        //https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
        Map<String, String> mapQuery = new HashMap<>();
        //mapQuery.put("units", "imperial"); metric is default
        mapQuery.put("origins", startPoint);
        mapQuery.put("destinations", destination );//+ "|" + cities[1] + "|" + cities[2]
        mapQuery.put("mode", "walking");
        mapQuery.put("key", YOUR_API_KEY);
//        mapQuery.put("destinations[1]", cities[1]);
//        mapQuery.put("destinations[2]", cities[2]);
        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);

        Call<DistanceResponse> call = client.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                DistanceResponse body = response.body();
                Log.d(TAG,"onResponse call = "+ (body == null ? "null" : body.getStatus()));
                if (body!= null){
                    addRespondToDb(body);
                }
            }
            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
                Log.d(TAG,"onFailure call = "+ (call == null ? "null" : call.toString()+ " t.getMessage = " +t.getMessage()));
                if(onDone != null) {
                    onDone.run();
                }

            }

            private void addRespondToDb(DistanceResponse body) {

//                if(Looper.myLooper() == Looper.getMainLooper()){
//                    App.getBackgroundHandler().post(() -> {
//                        addRespondToDb(body);
//                    });
//                    return;
//                }

                //String originLatLong = "";
                //String destinationLatLong = "";


                //List<String> originAddressesList = body.getOriginAddresses();
                //List<String> destinationAddressesList = body.getDestinationAddresses();
                List<Row> rows = body.getRows();
                Log.d(TAG, "addRespondToDb: rows = "+rows.size()+" "+body.getDestinationAddresses().size()+" "+body.getOriginAddresses().size());
               // Box<Address> addressBox = App.getBox(Address.class);
                for (int i = 0; i < rows.size() ; i++){
                    //String originAddressString = originAddressesList.get(i);
                    //originLatLong = Utils.getLatLongFromLocation(originAddressString);
                    Address originAddress = startPointList.get(i); //addressBox.query().equal(Address_.latLong, originLatLong).build().findUnique();
                    List<Element> elements = rows.get(i).getElements();
                    Log.d(TAG, "addRespondToDb: elements = "+elements.size());
                    for (int y = 0; y < elements.size(); y++) {
                        //String destinationAddressString = destinationAddressesList.get(y);
                        //destinationLatLong = Utils.getLatLongFromLocation(destinationAddressString);
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