package android.under_dash.addresses.search.models.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import android.under_dash.addresses.search.models.objectBox.AddressList;

import java.util.List;

@Dao
public interface AddressListDao {
    @Query("SELECT * FROM address_lists")
    List<AddressList> getAll();

    @Query("SELECT * FROM address_lists WHERE id IN (:userIds)")
    List<AddressList> changeThisQuery(int[] userIds);

    @Query("SELECT * FROM address_lists WHERE isSearchSelected LIKE :name" +" LIMIT 1")
    AddressList findByName(String name);

    @Insert
    void insertAll(AddressList... addressLists);

    @Delete
    void delete(AddressList... addressLists);

    @Query("DELETE FROM address_lists")
    void deleteAll();
}
