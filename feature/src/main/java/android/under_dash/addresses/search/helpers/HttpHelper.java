package android.under_dash.addresses.search.helpers;

import android.location.Location;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.app.DistanceApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.under_dash.addresses.search.app.RestUtil;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.Address_;
import android.under_dash.addresses.search.models.DistanceResponse;
import android.under_dash.addresses.search.models.Element;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.objectbox.Box;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpHelper {

    private static final String YOUR_API_KEY = "AIzaSyAOZZ2Xul6PmPxBUHxTZG7UurJOch_IDQ4";





    public static void getDistanceInfo(String startPoint, String destination) {
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
                                Log.d("shimi", "destinationAddress = |"+Utils.getLatLongFromLocation(destinationAddress,App.get())+"| address =|"+address.latLong+"|");
                            }
                        }
                    }
                    for (int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        String destinationAddress = list.get(i);

                        App.getBackgroundHandler().post(() -> {
                            //TODO: google is returning a edided address so we are not finding in DB
                            Address address = addressBox.query().equal(Address_.latLong, Utils.getLatLongFromLocation(destinationAddress,App.get())).build().findUnique();

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
                    //showTravelDistance("onResponse response = "+(response == null ? "null" : response.toString()));
                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
                //showTravelDistance("onFailure call = "+ (call == null ? "null" : call.toString()) );

            }
        });
    }


}
