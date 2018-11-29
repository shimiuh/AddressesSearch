package android.under_dash.addresses.search.helpers;

import android.location.Location;
import android.under_dash.addresses.search.app.DistanceApiClient;

import java.util.HashMap;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpHelper {

    private static final String YOUR_API_KEY = "AIzaSyAOZZ2Xul6PmPxBUHxTZG7UurJOch_IDQ4";





    private void getDistanceInfo(String startPoint, String destination) {
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
                if (response.body() != null &&
                        response.body().getRows() != null &&
                        response.body().getRows().size() > 0 &&
                        response.body().getRows().get(0) != null &&
                        response.body().getRows().get(0).getElements() != null &&
                        response.body().getRows().get(0).getElements().size() > 0 &&
                        response.body().getRows().get(0).getElements().get(0) != null &&
                        response.body().getRows().get(0).getElements().get(0).getDistance() != null &&
                        response.body().getRows().get(0).getElements().get(0).getDuration() != null) {

                    Element element = response.body().getRows().get(0).getElements().get(0);
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
