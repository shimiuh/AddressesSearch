package android.under_dash.addresses.search.models.objectBox;

import android.under_dash.addresses.search.app.App;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;

@Entity @androidx.room.Entity(tableName = "address_lists")
public class AddressList {

    public AddressList() {}
    public AddressList(String name) {
        this.name = name;
    }

    @Id @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    public long id;

    @Index @Unique @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "fileLocation")
    public String fileLocation;

    @ColumnInfo(name = "isSearchSelected")
    public boolean isSearchSelected;

    @ColumnInfo(name = "isResultSelected")
    public boolean isResultSelected;

    public ToMany<Address> addresses;

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
        App.getBoxStore().runInTx(() -> App.getBox(AddressList.class).put(this) );
    }

    public void remove() {
        App.getBoxStore().runInTx(() -> App.getBox(AddressList.class).remove(this ));
    }
}
