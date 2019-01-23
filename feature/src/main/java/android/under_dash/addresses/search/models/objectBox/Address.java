package android.under_dash.addresses.search.models.objectBox;


import android.under_dash.addresses.search.app.App;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import io.objectbox.Box;
import io.objectbox.Property;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.ToMany;

@Entity @androidx.room.Entity(tableName = "addresses")
public class Address {

    @Id @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "website")
    public String website;

    @ColumnInfo(name = "elevation")
    public double elevation;

    public ToMany<AddressMap> addressMaps;
    public ToMany<AddressList> addressLists;

    public Address() {}

    public Address(String name, String address, String website) {
        this.name = name;
        this.address = address;
        this.website = website;
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






    public static boolean add(String name, String str_address, String website, AddressList addressList) {
        boolean didAdd = false;
        Box<Address> box = App.getBox(Address.class);

        Address address = box.query().equal(Address_.name,name).and().equal(Address_.address,str_address).and().equal(Address_.website,website).build().findFirst();
        if(address == null) {
             address = new Address(name, str_address, website);
            didAdd = true;
        }

        addressList.addresses.add(address);
        //box.attach(address);
        address.addressLists.add(addressList);
        box.put(address);
        Log.d("shimi", "in add Address id = " + address.id + " addressNames size = " + address.addressLists.size() +
                " getAll().size = " + box.getAll().size() + " " + addressList.addresses.size());
        App.getBox(AddressList.class).put(addressList);
        return didAdd;
    }

    public static List<Address> getAllSearchSelected() {
        return getAllSelected(AddressList_.isSearchSelected);
    }

    public static List<Address> getAllResultSelected() {
        return getAllSelected(AddressList_.isResultSelected);
    }

    public static List<Address> getAllSelected(Property<AddressList> selected) {
        List<Address> addresses = new ArrayList<>();
        App.getBox(AddressList.class).query().equal(selected,true).build().find().forEach(addressName -> {
            addresses.addAll(addressName.addresses);
        });
        return addresses;
    }

    public static boolean isLinked(Address originAddress, Address destinationAddress) {
       // Log.i("shimi", "isLinked: test = "+test);
        QueryBuilder<Address> builder = App.getBox(Address.class).query();
        builder.link(Address_.addressMaps).equal(AddressMap_.origAddress,originAddress.address).and()
                .equal(AddressMap_.destAddress,destinationAddress.address);

        return builder.build().findFirst() != null;
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

}
