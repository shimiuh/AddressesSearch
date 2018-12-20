package android.under_dash.addresses.search.models;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Address {
    @Id
    public long id;
    public String name;
    public String address;
    public String website;
    public String latLong;
    public ToMany<AddressMap> addressMaps;
    public ToOne<AddressName> addressName;

    public Address() {}

    public Address(String name, String address, String website, String latLong , AddressName addressName) {
        this.name = name;
        this.address = address;
        this.website = website;
        this.latLong = latLong;
        setAddressName(addressName);
    }

    public Address(Address addressToCopy) {
        this.id = addressToCopy.id;
        this.name = addressToCopy.name;
        this.address = addressToCopy.address;
        this.website = addressToCopy.website;
        this.latLong = addressToCopy.latLong;
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

    public String getLatLong() {return latLong; }

    public void setLatLong(String latLong) { this.latLong = latLong; }

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

    public Address result(){

        return (Address) this;
    }
    public Address search(){

        return (Address) this;
    }
}
