package android.under_dash.addresses.search.models;


import android.under_dash.addresses.search.app.App;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.Property;
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
    public ToMany<AddressName> addressNames;

    public Address() {}

    public Address(String name, String address, String website) {
        this.name = name;
        this.address = address;
        this.website = website;
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

//    public AddressName getAddressName() {
//        return addressNames.getTarget();
//    }
//
//    public void setAddressName(AddressName addressName) {
//        this.addressNames.setTarget(addressName);
//    }

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

        Address address = box.query().equal(Address_.name,name).and().equal(Address_.address,str_address).and().equal(Address_.website,website).build().findFirst();
        if(address == null) {
             address = new Address(name, str_address, website);
            didAdd = true;
        }

        addressName.addresses.add(address);
        //box.attach(address);
        address.addressNames.add(addressName);
        box.put(address);
        Log.d("shimi", "in add Address id = " + address.id + " addressNames size = " + address.addressNames.size() +
                " getAll().size = " + box.getAll().size() + " " + addressName.addresses.size());
        App.getBox(AddressName.class).put(addressName);
        return didAdd;
    }

    public static List<Address> getAllSearchSelected() {
        return getAllSelected(AddressName_.isSearchSelected);
    }

    public static List<Address> getAllResultSelected() {
        return getAllSelected(AddressName_.isResultSelected);
    }

    public static List<Address> getAllSelected(Property<AddressName> selected) {
        List<Address> addresses = new ArrayList<>();
        App.getBox(AddressName.class).query().equal(selected,true).build().find().forEach(addressName -> {
            addresses.addAll(addressName.addresses);
        });
        return addresses;
    }

}
