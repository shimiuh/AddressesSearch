package android.under_dash.addresses.search.models;

import android.under_dash.addresses.search.app.App;
import android.util.Log;

import io.objectbox.Box;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
class AddressListMap {
    public AddressListMap() {}
    public AddressListMap(int closestDistance) {
        this.closestDistance = closestDistance;
    }

    @Id
    public long id;
    public int closestDistance;

    public ToOne<AddressName> originAddressName;
    public ToOne<AddressName> destinationAddressName;


    public static void add(int closestDistance, AddressName originAddressName, AddressName destinationAddressName) {
        Box<AddressListMap> AddressListMapBox = App.getBox(AddressListMap.class);
        AddressListMap addressListMap = new AddressListMap(closestDistance);
        addressListMap.originAddressName.setAndPutTarget(originAddressName);
        addressListMap.destinationAddressName.setAndPutTarget(destinationAddressName);
        AddressListMapBox.put(addressListMap);
    }

}
