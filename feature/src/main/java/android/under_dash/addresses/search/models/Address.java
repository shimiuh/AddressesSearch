package android.under_dash.addresses.search.models;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class Address {
    @Id
    public long id;
    public String name;
    public String address;
    public String website;
    public String latLong;
    public int    distance;
    public int    duration;
    //public ToMany<Address> addresses;

    public Address() {}
    public Address(String name, String address, String website,String latLong) {
        this.name = name;
        this.address = address;
        this.website = website;
        this.latLong = latLong;
    }

    public Address(Address addressToCopy) {
        this.name = addressToCopy.name;
        this.address = addressToCopy.address;
        this.website = addressToCopy.website;
        this.latLong = addressToCopy.latLong;
        this.distance = addressToCopy.distance;
        this.duration = addressToCopy.duration;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                ", distance=" + distance +
                ", duration=" + duration +
                '}';
    }

    public AddressResultList result(){

        return (AddressResultList) this;
    }
    public AddressSearchList search(){

        return (AddressSearchList) this;
    }
}
