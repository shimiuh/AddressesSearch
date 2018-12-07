package android.under_dash.addresses.search.models;

import io.objectbox.annotation.Entity;

@Entity
public class AddressSearchList extends Address {

    public AddressSearchList(String name, String address, String website,String latLong) {
        super(name, address, website, latLong);
    }
}