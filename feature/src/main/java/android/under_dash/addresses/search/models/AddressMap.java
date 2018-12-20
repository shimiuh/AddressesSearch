package android.under_dash.addresses.search.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class AddressMap {

    public AddressMap() {}

    @Id
    public long id;
    @Index
    public String originLatLong;
    @Index
    public String destinationLatLong;
    public int    distance;
    public int    duration;
}
