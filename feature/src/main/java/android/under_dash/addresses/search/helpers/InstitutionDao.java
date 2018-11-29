package android.under_dash.addresses.search.helpers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.under_dash.addresses.search.models.Institution;

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
