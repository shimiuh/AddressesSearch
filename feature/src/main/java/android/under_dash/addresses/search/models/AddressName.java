package android.under_dash.addresses.search.models;

import android.support.annotation.NonNull;

import javax.annotation.Nonnull;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;

@Entity
public class AddressName {

    public AddressName() {}
    public AddressName(String name) {
        this.name = name;
    }

    @Id
    public long id;
    @Index @Unique
    public String name;
    public String fileLocation;

    public ToMany<Address> addresses;
    //public ToMany<AddressListMap> addressListMaps;

    public long getId() {
        return id;
    }

    public ToMany<Address> getAddresses() {
        return this.addresses;
    }
}
