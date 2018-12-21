package android.under_dash.addresses.search.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.SearchManager;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.old.AddressDetailActivity;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.dummy.DummyContent;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.under_dash.addresses.search.view.adapters.AddressesSearchAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar;

import java.util.ArrayList;

import io.objectbox.Box;

/**
 * A fragment representing a single Address detail screen.
 * This fragment is either contained in a {@link AddressSearchActivity}
 * in two-pane mode (on tablets) or a {@link AddressDetailActivity}
 * on handsets.
 */
public class AddressResultFragment extends Fragment_ {//implements AppBarLayout.OnOffsetChangedListener
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = AddressResultFragment.class.getSimpleName();
    private AddressesSearchAdapter mAdapter;
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
    public AddressResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
////            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
////
////            Activity activity = this.getActivity();
////            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
////            if (appBarLayout != null) {
////                appBarLayout.setTitle(mItem.content);
////            }
//        }
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
//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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


//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);


//        // Show the Up button in the action bar.
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, Toolbar toolbar) {



        mAdapter = new AddressesSearchAdapter(getActivity(), false);//DummyContent.ITEMS
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
                AddressName resultAddressName = App.getBox(AddressName.class).get(SearchManager.get().getResultId());
                mItemAnimation.getAnimation().reset();
                mRecyclerView.setLayoutAnimation(mItemAnimation);
                mAdapter.setData(resultAddressName.addresses);
                mRecyclerView.scheduleLayoutAnimation();
                mSwipeLayout.setRefreshing(false);
                //mRecyclerView.invalidate();
                //mRecyclerView.requestLayout();
            }
        }, 2000); // Delay in millis

    }

    @Override
    public void onResume() {
        super.onResume();
        //mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //mAppBarLayout.removeOnOffsetChangedListener(this);
    }

//    @Override
//    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        //mSwipeRefresh.setEnabled(verticalOffset == 0);
////        if (mAppBarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(mAppBarLayout)) {
////            mSwipeRefresh.setEnabled(false);
////        } else {
////            mSwipeRefresh.setEnabled(true);
////        }
//    }
}
