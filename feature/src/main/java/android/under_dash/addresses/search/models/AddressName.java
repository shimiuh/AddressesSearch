package android.under_dash.addresses.search.models;

import android.support.annotation.NonNull;
import android.under_dash.addresses.search.app.App;

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
    public boolean isSearchSelected;
    public boolean isResultSelected;

    public ToMany<Address> addresses;
    //public ToMany<AddressListMap> addressListMaps;

    public long getId() {
        return id;
    }

    public ToMany<Address> getAddresses() {
        return this.addresses;
    }

    public void setSearchSelected(boolean searchSelected) {
        isSearchSelected = searchSelected;
        update();
    }

    public void setResultSelected(boolean resultSelected) {
        isResultSelected = resultSelected;
        update();
    }

    public void update() {
        App.getBoxStore().runInTx(() -> App.getBox(AddressName.class).put(this) );
    }

}
