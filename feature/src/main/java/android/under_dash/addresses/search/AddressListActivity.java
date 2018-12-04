package android.under_dash.addresses.search;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.HttpHelper;
import android.under_dash.addresses.search.helpers.MyCSVFileReader;
import android.under_dash.addresses.search.library.Activity_;
import android.under_dash.addresses.search.models.Address;
import android.util.Log;
import android.view.View;

import android.under_dash.addresses.search.dummy.DummyContent;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

/**
 * An activity representing a list of Addresses. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AddressDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AddressListActivity extends Activity_ {

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        requestPermissions();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Log.d("shimi", "in fab click");
                MyCSVFileReader.openDialogToReadCSV(AddressListActivity.this);
            }
        });
        View detailContainer = findViewById(R.id.address_detail_container);
        View parent = findViewById(R.id.parent);


        if (findViewById(R.id.address_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.

        }
        ConstraintSet set = new ConstraintSet();
        int orientation = getResources().getConfiguration().orientation;
        if (true||orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            detailContainer.setVisibility(View.VISIBLE);
            //set.constrainPercentWidth(R.id.address_list,0.5f);
            //set.constrainPercentWidth(R.id.address_detail_container,0.5f);
            mTwoPane = true;
        } else {
            // In portrait
            detailContainer.setVisibility(View.GONE);
            //set.constrainPercentWidth(R.id.address_list,1);
            mTwoPane = false;
        }
        //set.applyTo((ConstraintLayout) parent);


        View recyclerView = findViewById(R.id.address_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        //initAddressData();

    }

    private void initAddressData() {

        App.getBackgroundHandler().post(() -> {
            Box<Address> addressBox = getBox(Address.class);
            List<Address> addresses = addressBox.getAll();
            //StringBuilder destination = new StringBuilder();
            List<String> destination = new ArrayList<>();
            if(addresses != null && addresses.size() > 0 && addresses.get(0).getDuration() == 0) {

                //
                for (Address address : addresses) {
                    if (address != null) {
                        destination.add(address.latLong);
                        //destination.append(address.latLong).append("|");
                        //Log.d("shimi", .toString());
                    }
                }

                Log.d("shimi", "in create addresses.size() = "+addresses.size()+ "  destination = "+destination.toString());
                HttpHelper.getDistanceInfoAndAddInDb(destination,destination);
                //HttpHelper.getDistanceInfoAndAddInDb("New+York+City,NY",destination.toString(),null);
            }else{
                if(addresses != null) {
                    Log.d("shimi", "in create else addresses.size() = "+addresses.size());
                    for (Address address : addresses) {
                        if (address != null) {
                            Log.d("shimi", " Distance = "+address.getDistance()+" Duration = "+address.getDuration()+" latLong ="+address.latLong);
                        }
                    }
                }
            }
        });

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        MultiChoiceAdapter adapter = new AddressesSearchAdapter(this, DummyContent.ITEMS, mTwoPane);
        //adapter.setSingleClickMode(true);
        recyclerView.setAdapter(adapter);
    }



    public void requestPermissions(){

        Dexter.withActivity(this).withPermissions(PERMISSIONS).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

//        if(!hasPermissions(AddressListActivity.this, PERMISSIONS)){
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCurrentCoordinate() {
        Location location = getCurrentLocation();
        if (location == null) return "";
        return location.getLatitude() + "," + location.getLongitude();
    }

    private Location getCurrentLocation() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return null;
        }
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

//        Criteria criteria = new Criteria();
//        String bestProvider = lm.getBestProvider(criteria, false);
//        Location location = lm.getLastKnownLocation(bestProvider);
        return location;
    }
}
