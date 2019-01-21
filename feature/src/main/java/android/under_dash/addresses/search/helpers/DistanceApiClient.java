package android.under_dash.addresses.search.helpers;

import android.under_dash.addresses.search.models.DistanceResponse;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

//https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
public interface DistanceApiClient {
    @GET("maps/api/distancematrix/json")
    Call<DistanceResponse> getDistanceInfo(
            @QueryMap Map<String, String> parameters
    );
}
