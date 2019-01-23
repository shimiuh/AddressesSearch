package android.under_dash.addresses.search.helpers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import android.under_dash.addresses.search.models.room.Institution;

import java.util.List;

@Dao
public interface InstitutionDao {
    @Query("SELECT * FROM institutions")
    List<Institution> getAll();

    @Query("SELECT * FROM institutions WHERE id IN (:userIds)")
    List<Institution> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM institutions WHERE name LIKE :name AND " + "address LIKE :address LIMIT 1")
    Institution findByNameAndAddress(String name, String address);

    @Query("SELECT * FROM institutions WHERE name LIKE :name" +" LIMIT 1")
    Institution findByName(String name);

    @Insert
    void insertAll(Institution... institution);

    @Delete
    void delete(Institution institution);

    @Query("DELETE FROM institutions")
    void deleteAll();
}
