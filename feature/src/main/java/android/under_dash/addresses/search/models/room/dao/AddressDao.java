package android.under_dash.addresses.search.models.room.dao;

import android.under_dash.addresses.search.models.objectBox.Address;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AddressDao {
    @Query("SELECT * FROM addresses")
    List<Address> getAll();

    @Query("SELECT * FROM addresses WHERE id IN (:userIds)")
    List<Address> changeThisQuery(int[] userIds);

    @Query("SELECT * FROM addresses WHERE name LIKE :name AND " + "address LIKE :address LIMIT 1")
    Address findByNameAndAddress(String name, String address);

    @Query("SELECT * FROM addresses WHERE name LIKE :name" +" LIMIT 1")
    Address findByName(String name);

    @Insert
    void insertAll(Address... institution);

    @Delete
    void delete(Address institution);

    @Query("DELETE FROM addresses")
    void deleteAll();
}
