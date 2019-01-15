package android.under_dash.addresses.search.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName    = "search_results")
        //indices      = {@Index("name"),@Index(value = {"search_list_id", "institution_id"})},
//        foreignKeys  = {@ForeignKey(entity = SearchList.class, parentColumns = "id", childColumns = "search_list_id"),
//                        @ForeignKey(entity = Institution.class, parentColumns = "id", childColumns = "institution_id")})

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
