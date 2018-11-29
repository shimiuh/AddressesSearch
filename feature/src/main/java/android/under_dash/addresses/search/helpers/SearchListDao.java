package android.under_dash.addresses.search.helpers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.under_dash.addresses.search.models.SearchList;

import java.util.List;

@Dao
public interface SearchListDao {
    @Query("SELECT * FROM searchLists")
    List<SearchList> getAll();

    @Query("SELECT * FROM searchLists WHERE id IN (:userIds)")
    List<SearchList> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM searchLists WHERE address LIKE :name" +" LIMIT 1")
    SearchList findByName(String name);

    @Insert
    void insertAll(SearchList... searchList);

    @Delete
    void delete(SearchList... searchList);

    @Query("DELETE FROM searchLists")
    void deleteAll();
}
