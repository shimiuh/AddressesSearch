package android.under_dash.addresses.search.models;


import android.under_dash.addresses.search.app.App;
import android.util.Log;
import io.objectbox.Box;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Address {
    @Id
    public long id;
    public String name;
    public String address;
    public String website;
    public ToMany<AddressMap> addressMaps;
    //public ToMany<ClosestByName> closestByName;
    public ToOne<AddressName> addressName;

    public Address() {}

    public Address(String name, String address, String website) {
        this.name = name;
        this.address = address;
        this.website = website;
    }

    public Address(Address addressToCopy) {
        this.id = addressToCopy.id;
        this.name = addressToCopy.name;
        this.address = addressToCopy.address;
        this.website = addressToCopy.website;
        this.addressMaps = addressToCopy.addressMaps;
        this.addressName = addressToCopy.addressName;
    }

    public ToMany<AddressMap> getAddressMaps() {
        return addressMaps;
    }

    public void setAddressMap(AddressMap addressMap) {
        this.addressMaps.add(addressMap);
    }
    public AddressMap getAddressMap(long id) {
        return addressMaps.getById(id);
    }

    public AddressName getAddressName() {
        return addressName.getTarget();
    }

    public void setAddressName(AddressName addressName) {
        this.addressName.setTarget(addressName);
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                '}';
    }

    public static boolean add(String name, String str_address, String website, AddressName addressName) {
        boolean didAdd = false;
        Box<Address> box = App.getBox(Address.class);
//        Address oldAddress = box.query().equal(Address_.address, str_address).build().findUnique();
//        if(oldAddress == null) {
//
//        }
        didAdd = true;
        Address address = new Address(name, str_address, website);
        addressName.addresses.add(address);
        //box.attach(address);
        address.addressName.setAndPutTarget(addressName);
        box.put(address);
        Log.d("shimi", "in add Address id = " + address.id + " addressName = " + address.addressName.getTarget().name +
                " getAll().size = " + box.getAll().size() + " " + addressName.addresses.size());
        App.getBox(AddressName.class).put(addressName);
        return didAdd;
    }
}
