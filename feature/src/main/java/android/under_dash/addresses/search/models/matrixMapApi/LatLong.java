package android.under_dash.addresses.search.models.matrixMapApi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
class LatLong {

    @JsonProperty("lat")
    private Integer lat;
    @JsonProperty("lng")
    private Integer lng;
    @JsonProperty("lat")
    public Integer getLat() {
        return lat;
    }
    @JsonProperty("lat")
    public void setLat(Integer lat) {
        this.lat = lat;
    }
    @JsonProperty("lng")
    public Integer getLng() {
        return lng;
    }
    @JsonProperty("lng")
    public void setLng(Integer lng) {
        this.lng = lng;
    }
}
