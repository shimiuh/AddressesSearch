package android.under_dash.addresses.search.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName    = "search_results",
        //indices      = {@Index("name"),@Index(value = {"search_list_id", "institution_id"})},
        foreignKeys  = {@ForeignKey(entity = SearchList.class, parentColumns = "id", childColumns = "search_list_id"),
                        @ForeignKey(entity = Institution.class, parentColumns = "id", childColumns = "institution_id")})

public class SearchResult {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "search_list_id")
    public long searchListId;

    @ColumnInfo(name = "institution_id")
    public long institutionId;

    @ColumnInfo(name = "walking_distance")
    public String walkingDistance;

    @ColumnInfo(name = "driving_distance")
    public String drivingDistance;
}
