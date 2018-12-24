package android.under_dash.addresses.search.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
class ClosestByName {

    @Id
    public long id;
    public int closestDistance;
    public ToOne<AddressName> addressName;
}
