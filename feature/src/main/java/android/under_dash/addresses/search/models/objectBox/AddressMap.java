package android.under_dash.addresses.search.models.objectBox;

import androidx.annotation.NonNull;
import android.under_dash.addresses.search.app.App;
import android.util.Log;

import java.util.Comparator;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import io.objectbox.Box;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity @androidx.room.Entity(tableName = "address_maps")
public class AddressMap implements Comparable<AddressMap> {



    @Id @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "distance")
    public int          distance;

    @ColumnInfo(name = "duration")
    public int          duration;

    @ColumnInfo(name = "distanceText")
    public String       distanceText;

    @ColumnInfo(name = "durationText")
    public String       durationText;

    @ColumnInfo(name = "origAddress")
    public String       origAddress;

    @ColumnInfo(name = "destAddress")
    public String       destAddress;

    public ToOne<Address> originAddress;
    public ToOne<Address> destinationAddress;

    public AddressMap() {}
    public AddressMap(int distance, int duration, String distanceText,String durationText,String origAddress, String destAddress) {//String originLatLong, String destinationLatLong,
        this .distance = distance;
        this .duration = duration;
        this .distanceText = distanceText;
        this .durationText = durationText;
        this.origAddress = origAddress;
        this.destAddress = destAddress;
    }

    public Address getOriginAddress() {
        return this.originAddress.getTarget();
    }

    public Address getDestinationAddress() {
        return this.destinationAddress.getTarget();
    }


    public static AddressMap add(int distance, int duration, String distanceText,String durationText, Address originAddress, Address destinationAddress) {
        Box<Address> addressBox = App.getBox(Address.class);
        Box<AddressMap> addressMapBox = App.getBox(AddressMap.class);

        AddressMap addressMap = new AddressMap(distance, duration, distanceText, durationText, originAddress.address, destinationAddress.address);
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

    @Override
    public int compareTo(@NonNull AddressMap addressMap) {
        return Comparators.DURATION.compare(this, addressMap);
    }

    public static class Comparators {
        public static final Comparator<AddressMap> DISTANCE = (AddressMap o1, AddressMap o2) -> Integer.compare(o1.distance, o2.distance);
        public static final Comparator<AddressMap> DURATION = (AddressMap o1, AddressMap o2) -> Integer.compare(o1.duration, o2.duration);
    }
}
