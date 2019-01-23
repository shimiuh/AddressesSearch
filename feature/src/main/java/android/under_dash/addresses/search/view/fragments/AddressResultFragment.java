package android.under_dash.addresses.search.view.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.SearchManager;
import android.under_dash.addresses.search.models.objectBox.Address;
import android.under_dash.addresses.search.models.objectBox.AddressMap;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.view.adapters.AddressesResultAdapter;
import android.under_dash.addresses.search.view.adapters.multiChoice.MultiChoiceToolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.relation.ToMany;

public class AddressResultFragment extends Fragment_ {//implements AppBarLayout.OnOffsetChangedListener
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = AddressResultFragment.class.getSimpleName();
    private AddressesResultAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private LayoutAnimationController mItemAnimation;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddressResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.address_result_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi(View view) {
        mRecyclerView = view.findViewById(R.id.address_list);
        mSwipeLayout = view.findViewById(R.id.swipeRefreshResultList);
        // Adding Listener
        mSwipeLayout.setRefreshing(true);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateListData();
            }
        });
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        setupRecyclerView(mRecyclerView, toolbar);
        int resId = R.anim.layout_animation_fall_down;
        mItemAnimation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, Toolbar toolbar) {

        mAdapter = new AddressesResultAdapter();
        mAdapter.setHasStableIds(true);
        MultiChoiceToolbar multiChoiceToolbar = new MultiChoiceToolbar.Builder((AppCompatActivity)getActivity(), toolbar).setDefaultIcon(R.drawable.ic_delete_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), " multiChoiceToolbar clicked",Toast.LENGTH_SHORT).show();
            }
        }).build();
        mAdapter.setMultiChoiceToolbar(multiChoiceToolbar);
        recyclerView.setAdapter(mAdapter);
        updateListData();
    }

    private void updateListData() {
        mAdapter.setData(new ArrayList<AddressMap>());
        App.getUiHandler().postDelayed(() -> {
            Long id = null;
            Bundle bundle = getArguments();
            if(bundle != null && bundle.containsKey(ARG_ITEM_ID)) {
                id = bundle.getLong(ARG_ITEM_ID);
                Address searchAddress = App.getBox(Address.class).get(id);
                List<AddressMap> listMap = new ArrayList<>();
                ToMany<AddressMap> searchAddressMaps = searchAddress.addressMaps;
                Log.i("shimi", "run: in set DATA getResultId = "+SearchManager.get().getResultId());
                //TODO: check if you can query this list
                searchAddressMaps.forEach(addressMap -> {
                    addressMap.originAddress.getTarget().addressNames.forEach(addressName -> {
                        Log.i("shimi", "run: addressName.id = "+addressName.id);
                        if(addressName.isResultSelected && !listMap.contains(addressMap)){
                            listMap.add(addressMap);
                        }
                    });

                });
                Log.i("shimi", "in set DATA listMap.size() = "+listMap.size());
                listMap.sort(AddressMap.Comparators.DURATION);
                mItemAnimation.getAnimation().reset();
                mRecyclerView.setLayoutAnimation(mItemAnimation);

                mAdapter.setData(listMap);
                mRecyclerView.scheduleLayoutAnimation();
                mSwipeLayout.setRefreshing(false);
            }else{
                Log.i("shimi", "run: bundle == null");
            }
        }, 500); // Delay in millis

    }
}
