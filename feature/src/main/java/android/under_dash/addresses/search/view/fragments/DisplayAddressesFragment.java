package android.under_dash.addresses.search.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.dummy.DummyContent;
import android.under_dash.addresses.search.helpers.SearchManager;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressMap;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.old.AddressDetailActivity;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.under_dash.addresses.search.view.adapters.AddressesResultAdapter;
import android.under_dash.addresses.search.view.adapters.DisplayAddressesAdapter;
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

/**
 * A fragment representing a single Address detail screen.
 * This fragment is either contained in a {@link AddressSearchActivity}
 * in two-pane mode (on tablets) or a {@link AddressDetailActivity}
 * on handsets.
 */
public class DisplayAddressesFragment extends Fragment_ {//implements AppBarLayout.OnOffsetChangedListener
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = DisplayAddressesFragment.class.getSimpleName();
    private DisplayAddressesAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private LayoutAnimationController mItemAnimation;

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private SwipeRefreshLayout mSwipeRefresh;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DisplayAddressesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.address_result_fragment, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        mAdapter = new DisplayAddressesAdapter(getActivity());//DummyContent.ITEMS
        mAdapter.setHasStableIds(true);
        MultiChoiceToolbar multiChoiceToolbar = new MultiChoiceToolbar.Builder((AppCompatActivity)getActivity(), toolbar).setDefaultIcon(R.drawable.ic_delete_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), " multiChoiceToolbar clicked",Toast.LENGTH_SHORT).show();
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
                Long id = null;
                Bundle bundle = getArguments();
                if(bundle != null && bundle.containsKey(ARG_ITEM_ID)) {
                    id = bundle.getLong(ARG_ITEM_ID);
                    AddressName searchAddress = App.getBox(AddressName.class).get(id);
                    mItemAnimation.getAnimation().reset();
                    mRecyclerView.setLayoutAnimation(mItemAnimation);

                    mAdapter.setData(searchAddress.addresses);
                    mRecyclerView.scheduleLayoutAnimation();
                    mSwipeLayout.setRefreshing(false);
                    //mRecyclerView.invalidate();
                    //mRecyclerView.requestLayout();
                }else{
                    Log.i("shimi", "run: bundle == null");
                }
            }
        }, 500); // Delay in millis

    }

}
