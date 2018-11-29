package android.under_dash.addresses.search.helpers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.under_dash.addresses.search.models.SearchResult;

import java.util.List;

@Dao
public interface SearchResDao {

   @Query("SELECT * FROM search_results")
   List<SearchResult> getAll();

    @Query("SELECT * FROM search_results WHERE walking_distance LIKE :name" +" LIMIT 1")
    SearchResult findBywalkingDistance(String name);

    @Insert
    void insertAll(SearchResult... searchList);

    @Delete
    void delete(SearchResult... searchList);

    @Query("DELETE FROM search_results")
    void deleteAll();

//
//    @Query("SELECT * FROM institutions INNER JOIN search_results
//            ON search_results.searchListId = institutions.id INNER JOIN searchLists
//            ON searchLists.id = items_orders.item_id INNER JOIN customers
//            ON customers.id=orders.customer_id")
//            List<SearchList> getAll2();
}
