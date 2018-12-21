package android.under_dash.addresses.search.models;

import android.under_dash.addresses.search.app.App;
import android.util.Log;

import io.objectbox.Box;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

@Entity
public class AddressMap {

    public AddressMap() {}
    public AddressMap(int distance, int duration) {//String originLatLong, String destinationLatLong,
//        this .originLatLong = originLatLong;
//        this .destinationLatLong = destinationLatLong;
        this .distance = distance;
        this .duration = duration;
    }

    @Id
    public long id;
    public ToOne<Address> originAddress;
    public ToOne<Address> destinationAddress;
//    @Index
//    public String originLatLong;
//    @Index
//    public String destinationLatLong;
    public int    distance;
    public int    duration;
    //String originLatLong, String destinationLatLong,
    public static AddressMap add(int distance, int duration, Address originAddress, Address destinationAddress) {
        Box<Address> addressBox = App.getBox(Address.class);
        Box<AddressMap> addressMapBox = App.getBox(AddressMap.class);

        AddressMap addressMap = new AddressMap(distance, duration);
        addressMapBox.put(addressMap);
        originAddress.addressMaps.add(addressMap);
        destinationAddress.addressMaps.add(addressMap);

        addressMap.originAddress.setAndPutTarget(originAddress);
        addressMap.destinationAddress.setAndPutTarget(destinationAddress);
        addressMapBox.put(addressMap);
        addressBox.put(originAddress);
        addressBox.put(destinationAddress);

        Log.d("shimi","in add Address id = "+addressMap.id+" originAddressId= "+addressMap.originAddress.getTarget().id+
                " destinationAddressId= "+addressMap.destinationAddress.getTarget().id+
                "\n addressMapBox.size = "+addressMapBox.getAll().size()+" "+originAddress.addressMaps.size()+
                " distance = "+addressMap.distance+" duration = "+addressMap.duration);
        return addressMap;
    }
}
