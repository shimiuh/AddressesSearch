package android.under_dash.addresses.search.view.activitys;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.under_dash.addresses.search.app.Constants;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.models.AddressName_;
import android.under_dash.addresses.search.old.AddressDetailActivity;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.DbUtils;
import android.under_dash.addresses.search.helpers.HttpHelper;
import android.under_dash.addresses.search.helpers.ImportCVSToSQLiteDB;
import android.under_dash.addresses.search.helpers.MyCSVFileReader;
import android.under_dash.addresses.search.helpers.Utils;
import android.under_dash.addresses.search.library.Activity_;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.view.adapters.AddressesSearchAdapter;
import android.under_dash.addresses.search.view.fragments.AddressResultFragment;
import android.util.Log;
import android.view.View;

import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rany.albeg.wein.springfabmenu.SpringFabMenu;

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
public class AddressSearchActivity extends Activity_ {

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
    private AddressesSearchAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private LayoutAnimationController mItemAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAddressNameList();
        setContentView(R.layout.activity_address_list);
        requestPermissions();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        SpringFabMenu fab = (SpringFabMenu)findViewById(R.id.fab);
        fab.setOnSpringFabMenuItemClickListener(new SpringFabMenu.OnSpringFabMenuItemClickListener() {
            @Override
            public void onSpringFabMenuItemClick(View view) {
                int id = view.getId();

                if(id == R.id.fab_clip){
                    addAddressFromClip();
                    Toast.makeText(getApplicationContext(), " clicked!",Toast.LENGTH_SHORT).show();
                }else if(id == R.id.fab_result_list){
                    Log.d("shimi", "in fab fab_result_list");
                    MyCSVFileReader.openDialogToReadCSV(AddressSearchActivity.this, pathFile -> {
                        AddressName AddressName = App.getBox(AddressName.class).query().equal(AddressName_.name,Constants.ADDRESS_RESULT).build().findUnique();
                        new ImportCVSToSQLiteDB(AddressSearchActivity.this,pathFile,AddressName).execute();
                    });
                }else if(id == R.id.fab_search_list){
                    Log.d("shimi", "in fab fab_search_list");
                    MyCSVFileReader.openDialogToReadCSV(AddressSearchActivity.this, pathFile -> {
                        AddressName AddressName = App.getBox(AddressName.class).query().equal(AddressName_.name,Constants.ADDRESS_SEARCH).build().findUnique();
                        new ImportCVSToSQLiteDB(AddressSearchActivity.this,pathFile,AddressName).execute();
                    });
                }else if(id == R.id.fab_swap){
                    Log.d("shimi", "in fab fab_swap");
                    DbUtils.swapLists(() -> {
                        updateListData();
                    });
                }
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
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            detailContainer.setVisibility(View.VISIBLE);
            set.constrainPercentWidth(R.id.address_list,0.5f);
            //set.constrainPercentWidth(R.id.address_detail_container,0.5f);
            mTwoPane = true;
        } else {
            // In portrait
            detailContainer.setVisibility(View.GONE);
            set.constrainPercentWidth(R.id.address_list,1);
            mTwoPane = false;
        }
        //set.applyTo((ConstraintLayout) parent);

        mTwoPane = false;
        mRecyclerView = findViewById(R.id.address_list);
        mSwipeLayout = findViewById(R.id.swipeRefreshAddressList);
        // Adding Listener
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateListData();
            }
        });

        setupRecyclerView(mRecyclerView, toolbar);

        int resId = R.anim.layout_animation_fall_down;
        mItemAnimation = AnimationUtils.loadLayoutAnimation(this, resId);

        //initAddressData();

    }

    private void initAddressNameList() {
        App.getBackgroundHandler().post(() -> {
            if(App.getBox(AddressName.class).getAll().size() == 0) {
                App.getBox(AddressName.class).put(new AddressName(Constants.ADDRESS_RESULT));
                App.getBox(AddressName.class).put(new AddressName(Constants.ADDRESS_SEARCH));
            }
        });
    }

    private void addAddressFromClip() {

        if(Looper.myLooper() != Looper.getMainLooper()){
            App.getBackgroundHandler().post(this::addAddressFromClip);
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if(clip != null){
            String address = clip.getItemAt(0).toString();
            Box<Address> searchBox = getBox(Address.class);
            String latLong = Utils.getLatLongFromLocation(address);
            //searchBox.put(new Address("",address,"",latLong)); TODO: add addressName
        }




    }

    private void removeAddress(Address address) {

        if(Looper.myLooper() != Looper.getMainLooper()){
            App.getBackgroundHandler().post(() -> removeAddress(address));
            return;
        }

        Box<Address> searchBox = getBox(Address.class);
        searchBox.remove(address);

    }

    private void initAddressData() {

        App.getBackgroundHandler().post(() -> {
            Box<Address> addressBox = getBox(Address.class);
            List<Address> addresses = addressBox.getAll();
            //StringBuilder destination = new StringBuilder();
            List<String> destination = new ArrayList<>();
            if(addresses != null && addresses.size() > 0 ){//&& addresses.get(0).getDuration() == 0) {

                //
                for (Address address : addresses) {
                    if (address != null) {
                        destination.add(address.latLong);
                        //destination.append(address.latLong).append("|");
                        //Log.d("shimi", .toString());
                    }
                }

                Log.d("shimi", "in create addresses.size() = "+addresses.size()+ "  destination = "+destination.toString());
                HttpHelper.getDistanceInfoAndAddInDb(addresses, addresses);
                //HttpHelper.getDistanceInfoAndAddInDb("New+York+City,NY",destination.toString(),null);
            }else{
                if(addresses != null) {
                    Log.d("shimi", "in create else addresses.size() = "+addresses.size());
                    for (Address address : addresses) {
                        if (address != null) {
                            //Log.d("shimi", " Distance = "+address.getDistance()+" Duration = "+address.getDuration()+" latLong ="+address.latLong);
                        }
                    }
                }
            }
        });

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, Toolbar toolbar) {



        mAdapter = new AddressesSearchAdapter(this, mTwoPane);//DummyContent.ITEMS
        mAdapter.setHasStableIds(true);
        MultiChoiceToolbar multiChoiceToolbar = new MultiChoiceToolbar.Builder(this, toolbar).setDefaultIcon(R.drawable.ic_delete_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), " multiChoiceToolbar clicked",Toast.LENGTH_SHORT).show();
            }
        }).build();
        mAdapter.setMultiChoiceToolbar(multiChoiceToolbar);
        //adapter.setSingleClickMode(true);
        recyclerView.setAdapter(mAdapter);
        updateListData();
    }

    private void updateListData() {
        mAdapter.setData(new ArrayList<Address>());
        App.getUiHandler().postDelayed(new Runnable() {
            @Override public void run() {
                Box<Address> addressBox = getBox(Address.class);
                mItemAnimation.getAnimation().reset();
                mRecyclerView.setLayoutAnimation(mItemAnimation);
                mAdapter.setData(addressBox.getAll());
                mRecyclerView.scheduleLayoutAnimation();
                mSwipeLayout.setRefreshing(false);
                //mRecyclerView.invalidate();
                //mRecyclerView.requestLayout();
            }
        }, 2000); // Delay in millis

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

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(AddressResultFragment.TAG);
        if(fragment != null ){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            //getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }
}
