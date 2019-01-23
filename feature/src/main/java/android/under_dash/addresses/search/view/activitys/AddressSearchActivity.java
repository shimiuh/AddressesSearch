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
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.under_dash.addresses.search.app.Constants;
import android.under_dash.addresses.search.models.objectBox.AddressList;
import android.under_dash.addresses.search.network.MapsMatrixApiService;
import android.under_dash.addresses.search.helpers.SearchManager;
import android.under_dash.addresses.search.helpers.Work;
import android.under_dash.addresses.search.models.objectBox.AddressMap;
import android.under_dash.addresses.search.models.SearchAddress;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.ImportCVSToDB;
import android.under_dash.addresses.search.helpers.CSVFileReader;
import android.under_dash.addresses.search.library.Activity_;
import android.under_dash.addresses.search.models.objectBox.Address;
import android.under_dash.addresses.search.view.adapters.AddressesSearchAdapter;
import android.under_dash.addresses.search.view.adapters.multiChoice.MultiChoiceToolbar;
import android.under_dash.addresses.search.view.fragments.AddressResultFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rany.albeg.wein.springfabmenu.SpringFabMenu;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;


public class AddressSearchActivity extends Activity_ {


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private AddressesSearchAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private LayoutAnimationController mItemAnimation;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //App.getBox(AddressName.class).removeAll();
        //Log.i("shimi", "onCreate: -"+SharedPrefManager.get().getGoogleApiKey()+"-");
       // tempInitAddressNameList();
        initUi();
    }

    private void initUi() {
        setContentView(R.layout.activity_address_list);
        requestPermissions();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.dialog_ok, R.string.dialog_ok) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        SpringFabMenu fab = (SpringFabMenu)findViewById(R.id.fab);
        fab.setOnSpringFabMenuItemClickListener(new SpringFabMenu.OnSpringFabMenuItemClickListener() {
            @Override
            public void onSpringFabMenuItemClick(View view) {
                int id = view.getId();

                if(id == R.id.fab_clip){
                    if(true){
                        searchLists(App.getBox(AddressList.class).get(SearchManager.get().getSearchId()).addresses,
                                App.getBox(AddressList.class).get(SearchManager.get().getResultId()).addresses);
                        return;
                    }
                    addAddressFromClip();
                    Toast.makeText(getApplicationContext(), " clicked!",Toast.LENGTH_SHORT).show();
                }else if(id == R.id.fab_result_list){

                    CSVFileReader.openDialogToReadCSV(AddressSearchActivity.this, pathFile -> {
                        AddressList resultAddressName = App.getBox(AddressList.class).get(SearchManager.get().getResultId());
                        Log.d("shimi", "in fab fab_result_list resultAddressName = "+resultAddressName.name+" getResultId = "+SearchManager.get().getResultId());
                        new ImportCVSToDB(AddressSearchActivity.this,pathFile,resultAddressName, () -> {
                            updateSearchListData();
                        }).execute();
                    });
                }else if(id == R.id.fab_search_list){

                    CSVFileReader.openDialogToReadCSV(AddressSearchActivity.this, pathFile -> {
                        AddressList searchAddressName = App.getBox(AddressList.class).get(SearchManager.get().getSearchId());
                        Log.d("shimi", "in fab fab_search_list searchAddressName = "+searchAddressName.name+" getSearchId = "+SearchManager.get().getSearchId());
                        new ImportCVSToDB(AddressSearchActivity.this,pathFile,searchAddressName, () -> {
                            updateSearchListData();
                        }).execute();
                    });
                }else if(id == R.id.fab_swap){
                    Log.d("shimi", "in fab fab_swap");
                    SearchManager.get().swapLists();
                    updateSearchListData();
                }
            }
        });

        View detailContainer = findViewById(R.id.address_detail_container);

        ConstraintSet set = new ConstraintSet();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            detailContainer.setVisibility(View.VISIBLE);
            set.constrainPercentWidth(R.id.address_list,0.5f);
            mTwoPane = true;
        } else {
            // In portrait
            detailContainer.setVisibility(View.GONE);
            set.constrainPercentWidth(R.id.address_list,1);
            mTwoPane = false;
        }

        mTwoPane = false;
        mRecyclerView = findViewById(R.id.address_list);
        mSwipeLayout = findViewById(R.id.swipeRefreshAddressList);
        // Adding Listener
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateSearchListData();
            }
        });

        setupRecyclerView(mRecyclerView, toolbar);

        int resId = R.anim.layout_animation_fall_down;
        mItemAnimation = AnimationUtils.loadLayoutAnimation(this, resId);
        //initAddressData();
    }

    public void searchLists(List<Address> startPointAddresses, List<Address> destinationAddresses) {
        App.getBox(AddressList.class).getAll().forEach(addressName -> {
            Log.i("shimi", "in AddressName.class).getAll().forEach : name "+addressName.name+"  id = "+addressName.id);
        });

        Log.i("shimi", "in GoogleMapsMatrixApiService.build: getSearchId "+SearchManager.get().getSearchId()+"  getResultId = "+SearchManager.get().getResultId());
        MapsMatrixApiService.build(AddressSearchActivity.this,startPointAddresses,destinationAddresses,() -> {
            Log.i("shimi", "in GoogleMapsMatrixApiService onDone AddressMap size = " +App.getBox(AddressMap.class).getAll().size());
        }).execute();
    }

    private void tempInitAddressNameList() {
        App.getBackgroundHandler().post(() -> {
            if(App.getBox(AddressList.class).getAll().size() == 0) {
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Stores in NY")));
                SearchManager.get().setSearchId(App.getBox(AddressList.class).put(new AddressList("Shops")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Toys")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Food mark st")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Chabad")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Signings")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Churches")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Mask")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("Try me out")));
                SearchManager.get().setResultId(App.getBox(AddressList.class).put(new AddressList("All")));
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
        recyclerView.setAdapter(mAdapter);
        updateSearchListData();
    }

    private void updateSearchListData() {

        mSwipeLayout.setRefreshing(true);
        mAdapter.setData(new ArrayList<SearchAddress>());
        List<SearchAddress> searchAddress = new ArrayList<>();
        Work.job(() -> {
            List<Address> addresses =  Address.getAllSearchSelected();
            if(addresses.size()<=0){
                return false;
            }
            addresses.forEach(address -> {
                List<AddressMap> listMap = new ArrayList<>();
                List<AddressMap> searchAddressMaps = address.addressMaps;
                if(searchAddressMaps.size() > 0){
                    //TODO: figure a query and avoid Big O 2
                    searchAddressMaps.forEach(addressMap -> {
                        addressMap.originAddress.getTarget().addressLists.forEach(addressName -> {
                            Log.i("shimi", "run: addressName.id = " + addressName.id);
                            if (addressName.isResultSelected && !listMap.contains(addressMap)) {
                                listMap.add(addressMap);
                            }
                        });
                    });
                    if(listMap.size() > 0 ) {
                        listMap.sort(AddressMap.Comparators.DURATION);
                        searchAddress.add(SearchAddress.make(address, listMap.get(0).distance, listMap.get(0).durationText));//listMap.get(0).distance
                        searchAddress.sort(SearchAddress.Comparators.DISTANCE);
                    }
                }

            });
            return true;
        }).onUiDelayed(object -> {
            if((boolean)object) {
                Log.i("shimi", "in updateSearchListData:  searchAddress.size= " + searchAddress.size());
                mItemAnimation.getAnimation().reset();
                mRecyclerView.setLayoutAnimation(mItemAnimation);
                mAdapter.setData(searchAddress);//searchAddressName.addresses
                mRecyclerView.scheduleLayoutAnimation();
            }
            mSwipeLayout.setRefreshing(false);
        },500);

    }


    public void requestPermissions(){

        Dexter.withActivity(this).withPermissions(Constants.PERMISSIONS).withListener(new MultiplePermissionsListener() {
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
        return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(AddressResultFragment.TAG);
        if(fragment != null ){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        if(mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
