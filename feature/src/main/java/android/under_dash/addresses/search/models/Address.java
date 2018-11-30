package android.under_dash.addresses.search.models;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Address {
    @Id
    public long id;
}
