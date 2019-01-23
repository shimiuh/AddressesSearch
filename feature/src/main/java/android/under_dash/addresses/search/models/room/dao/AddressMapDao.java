package android.under_dash.addresses.search.models.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import android.under_dash.addresses.search.models.objectBox.AddressMap;

import java.util.List;

@Dao
public interface AddressMapDao {

   @Query("SELECT * FROM address_maps")
   List<AddressMap> getAll();

    @Query("SELECT * FROM address_maps WHERE distance LIKE :name" +" LIMIT 1")
    AddressMap changeThisQuery(String name);

    @Insert
    void insertAll(AddressMap... addressMaps);

    @Delete
    void delete(AddressMap... addressMaps);

    @Query("DELETE FROM address_maps")
    void deleteAll();

//
//    @Query("SELECT * FROM institutions INNER JOIN search_results
//            ON search_results.searchListId = institutions.id INNER JOIN searchLists
//            ON searchLists.id = items_orders.item_id INNER JOIN customers
//            ON customers.id=orders.customer_id")
//            List<SearchList> getAll2();
}
