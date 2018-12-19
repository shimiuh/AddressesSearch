package android.under_dash.addresses.search.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;

@Entity
public class AddressName {

    public AddressName(String name) {
        this.name = name;
    }

    @Id
    public long id;
    @Index
    public String name;
    public String fileLocation;
    public ToMany<Address> addresses;
}
