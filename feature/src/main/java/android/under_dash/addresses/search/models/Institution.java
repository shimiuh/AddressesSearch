package android.under_dash.addresses.search.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity (tableName = "institutions")
public class Institution {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;
    @Ignore
    public Institution(int id, String name, String address, String website) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.website = website;
    }
    public Institution() {
    }

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "website")
    public String website;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "map_distance")
    public Integer mapDistance;


}
