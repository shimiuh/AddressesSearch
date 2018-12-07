package android.under_dash.addresses.search.models;

import io.objectbox.annotation.Entity;

@Entity
public class AddressResultList extends Address {

    public AddressResultList(String name, String address, String website, String latLong) {
        super(name, address, website, latLong);
    }
}
