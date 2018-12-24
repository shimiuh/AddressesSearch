package android.under_dash.addresses.search.models;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class SearchAddress implements Comparable<SearchAddress>{

    public Address address;
    public int closestDistance;
    public String shortestDurationText;

    public SearchAddress(Address address, int distance, String shortestDurationText) {
        this.address = address;
        this.closestDistance = distance;
        this.shortestDurationText = shortestDurationText;
    }

    @Override
    public int compareTo(@NonNull SearchAddress address) {
        return SearchAddress.Comparators.DISTANCE.compare(this, address);
    }

    public static class Comparators {
        public static final Comparator<SearchAddress> DISTANCE = (SearchAddress o1, SearchAddress o2) -> Integer.compare(o1.closestDistance, o2.closestDistance);
    }

    public static SearchAddress make(Address address, int distance, String shortestDurationText) {
        return new SearchAddress(address, distance, shortestDurationText);
    }
}
