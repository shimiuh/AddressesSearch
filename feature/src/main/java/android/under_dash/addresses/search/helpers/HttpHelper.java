package android.under_dash.addresses.search.helpers;

import android.os.Looper;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.app.DistanceApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.under_dash.addresses.search.app.RestUtil;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.Address_;
import android.under_dash.addresses.search.models.DistanceResponse;
import android.under_dash.addresses.search.models.Element;
import android.under_dash.addresses.search.models.Row;
import android.util.Log;

import org.apache.commons.collections4.ListUtils;

import io.objectbox.Box;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpHelper {

    private static final String YOUR_API_KEY = "AIzaSyAOZZ2Xul6PmPxBUHxTZG7UurJOch_IDQ4";
    private static int sCount = 0;
    private static final String TAG = HttpHelper.class.getSimpleName();

    public static void getDistanceInfoAndAddInDb(List<? extends Address> startPointList, List<? extends Address> destinationList) {
        getDistanceInfoAndAddInDb(startPointList,destinationList,null);
    }

    public static void getDistanceInfoAndAddInDb(List<? extends Address> startPointList, List<? extends Address> destinationList, Runnable onDone) {

        if(Looper.myLooper() == Looper.getMainLooper()){
            App.getBackgroundHandler().post(() -> {
                getDistanceInfoAndAddInDb(startPointList, destinationList);
            });
            return;
        }

        int targetSize = 25;
        List<? extends List<? extends Address>> startPointListOfList = new ArrayList<>(ListUtils.partition(startPointList, targetSize));
        List<? extends List<? extends Address>> destinationListOfList = new ArrayList<>(ListUtils.partition(destinationList, targetSize));

//        List<? extends List<? extends Address>> startPointListOfList = ListUtils.partition(startPointList, targetSize);
//        List<? extends List<? extends Address>> destinationListOfList = ListUtils.partition(destinationList, targetSize);

        List<List<? extends Address>> originDestinationListOfList = new ArrayList<>();
        originDestinationListOfList.addAll(destinationListOfList);
        sCount = 0;
        getDistanceInfoRecursion(startPointListOfList,destinationListOfList,originDestinationListOfList,onDone);

    }

    private static void getDistanceInfoRecursion(final List<? extends List<? extends Address>> startPointListOfList,
                        final  List<? extends List<? extends Address>> destinationListOfList,
                        final List<? extends List<? extends Address>> originDestinationListOfList, Runnable onDone) {
        sCount ++;
        Log.d(TAG,"in getDistanceInfoRecursion sCount = "+sCount+" startPointListOfList.size = "+startPointListOfList.size()
                +" destinationListOfList.size = "+destinationListOfList.size()
                +" originDestinationListOfList.size = "+originDestinationListOfList.size());
        if(startPointListOfList.size() == 0 ){
            if(onDone != null){
                onDone.run();
            }
            Log.d(TAG,"in getDistanceInfoRecursion END");
            return;
        }

        if(destinationListOfList.size() > 0) {
            List<? extends Address> startPointList = startPointListOfList.get(0);
            List<? extends Address> destinationList = destinationListOfList.get(0);
            destinationListOfList.remove(0);
            //TODO: move next line in next call

            getDistanceInfoAndAddInDb(getFormatDistanceInfo(startPointList), getFormatDistanceInfo(destinationList), () -> {
                getDistanceInfoRecursion(startPointListOfList, destinationListOfList,originDestinationListOfList,onDone);
            });
        }else{
            startPointListOfList.remove(0);
            List<List<? extends Address>>  newDestinationListOfList = new ArrayList<>();
            newDestinationListOfList.addAll(originDestinationListOfList);
            getDistanceInfoRecursion(startPointListOfList, newDestinationListOfList,originDestinationListOfList,onDone);
        }
    }

    private static String getFormatDistanceInfo(List<? extends Address> startPointList) {
        StringBuilder destination = new StringBuilder();
        for (Address address : startPointList) {
            if (address != null) {
                destination.append(address.latLong).append("|");
            }
        }
        return destination.toString();
    }


    public static void getDistanceInfoAndAddInDb(String startPoint, String destination, Runnable onDone) {

        // http://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY
        //https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "imperial");
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
                if (body!= null){
                    addRespondToDb(body);
                }
            }
            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
                Log.d("shimi","onFailure call = "+ (call == null ? "null" : call.toString()));
            }

            private void addRespondToDb(DistanceResponse body) {

                if(Looper.myLooper() == Looper.getMainLooper()){
                    App.getBackgroundHandler().post(() -> {
                        addRespondToDb(body);
                    });
                    return;
                }

                List<String> originAddressesList = body.getOriginAddresses();
                List<String> destinationAddressesList = body.getDestinationAddresses();
                List<Row> rows = body.getRows();
                for (int i = 0; i < rows.size() ; i++){
                    String originAddress = originAddressesList.get(i);
                    List<Element> elements = rows.get(i).getElements();

                    for (int y = 0; y < elements.size(); y++) {
                        String destinationAddress = destinationAddressesList.get(y);
                        Element element = elements.get(y);

                        App.getBackgroundHandler().post(() -> {
                            //TODO: google is returning a eddied address so we are not finding in DB thus i am converting to latLong
                            Box<Address> addressBox = App.get().getBox(Address.class);
                            Address address = addressBox.query().equal(Address_.latLong, Utils.getLatLongFromLocation(destinationAddress)).build().findUnique();

                            if(address != null){
                                address.setDuration(element.getDuration().getValue());
                                address.setDistance(element.getDistance().getValue());
                                addressBox.put(address);
                                Log.d("shimi", "getStartLatLong = "+destinationAddress+" Distance = "+element.getDuration().getValue()+
                                        " Duration = "+element.getDistance().getValue()+" (address != null) = "+(address != null));
                            }
                        });
                    }

                }
                if(onDone != null) {
                    onDone.run();
                }

            }
        });
    }







    public static void getDistanceInfoAndAddInDbOld(String startPoint, String destination, Runnable onDone) {

        // http://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY
        //https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "imperial");
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
                if (body!= null){
                    //addRespondToDb(body);
                }
                if (body!= null &&
                        body.getRows() != null &&
                        body.getRows().size() > 0 &&
                        body.getRows().get(0) != null &&
                        body.getRows().get(0).getElements() != null &&
                        body.getRows().get(0).getElements().size() > 0 &&
                        body.getRows().get(0).getElements().get(0) != null &&
                        body.getRows().get(0).getElements().get(0).getDistance() != null &&
                        body.getRows().get(0).getElements().get(0).getDuration() != null) {

                    List<Element> elements = body.getRows().get(0).getElements();

                    List<String> list = body.getDestinationAddresses();

                    Box<Address> addressBox = App.get().getBox(Address.class);
                    List<Address> addresses = addressBox.getAll();
                    Log.d("shimi","Rows size = "+body.getRows().size()+" Elements().size() = "+elements.size()+" list = "+elements.size()+" addressBox "+addresses.size());

                    if(addresses != null) {
                        Log.d("shimi", "in Http addresses.size() = "+addresses.size());
                        for (int i = 0; i < addresses.size(); i++) {
                            Address address = addresses.get(i);
                            //String startLatLong = elements.get(i).getStartLatLong();
                            String destinationAddress = list.get(i);

                            if (address != null) {
                                Log.d("shimi", "destinationAddress = |"+Utils.getLatLongFromLocation(destinationAddress)+"| address =|"+address.latLong+"|");
                            }
                        }
                    }
                    for (int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        String destinationAddress = list.get(i);

                        App.getBackgroundHandler().post(() -> {
                            //TODO: google izs returning a edided address so we are not finding in DB
                            Address address = addressBox.query().equal(Address_.latLong, Utils.getLatLongFromLocation(destinationAddress)).build().findUnique();

                            if(address != null){
                                address.setDuration(element.getDuration().getValue());
                                address.setDistance(element.getDistance().getValue());
                                addressBox.put(address);
                                Log.d("shimi", "getStartLatLong = "+destinationAddress+" Distance = "+element.getDuration().getValue()+
                                        " Duration = "+element.getDistance().getValue()+" (address != null) = "+(address != null));
                            }
                        });
                    }

                    // showTravelDistance("onResponse "+element.getDistance().getText() + "\n" + element.getDuration().getText());
                }else{
                    Log.d("shimi","onResponse response = "+(response == null ? "null" : response.toString()));
                    //showTravelDistance("onResponse response = "+(response == null ? "null" : response.toString()));
                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
                Log.d("shimi","onFailure call = "+ (call == null ? "null" : call.toString()));

                //showTravelDistance("onFailure call = "+ (call == null ? "null" : call.toString()) );

            }

            private void addRespondToDb(DistanceResponse body) {

                if(Looper.myLooper() == Looper.getMainLooper()){
                    App.getBackgroundHandler().post(() -> {
                        addRespondToDb(body);
                    });
                    return;
                }

                List<String> originAddressesList = body.getOriginAddresses();
                List<String> destinationAddressesList = body.getDestinationAddresses();
                List<Row> rows = body.getRows();
                for (int i = 0; i < rows.size() ; i++){
                    String originAddress = originAddressesList.get(i);
                    List<Element> elements = rows.get(i).getElements();

                    for (int y = 0; y < elements.size(); y++) {
                        String destinationAddress = destinationAddressesList.get(y);
                        Element element = elements.get(y);

                        App.getBackgroundHandler().post(() -> {
                            //TODO: google is returning a eddied address so we are not finding in DB thus i am converting to latLong
                            Box<Address> addressBox = App.get().getBox(Address.class);
                            Address address = addressBox.query().equal(Address_.latLong, Utils.getLatLongFromLocation(destinationAddress)).build().findUnique();

                            if(address != null){
                                address.setDuration(element.getDuration().getValue());
                                address.setDistance(element.getDistance().getValue());
                                addressBox.put(address);
                                Log.d("shimi", "getStartLatLong = "+destinationAddress+" Distance = "+element.getDuration().getValue()+
                                        " Duration = "+element.getDistance().getValue()+" (address != null) = "+(address != null));
                            }
                        });
                    }

                }
                if(onDone != null) {
                    onDone.run();
                }

            }
        });
    }


}
