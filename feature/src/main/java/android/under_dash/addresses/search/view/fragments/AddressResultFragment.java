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
import android.under_dash.addresses.search.old.AddressDetailActivity;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.dummy.DummyContent;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private AppBarLayout mAppBarLayout;
    private SwipeRefreshLayout mSwipeRefresh;
    private CollapsingToolbarLayout mCollapsingToolbar;

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



        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.address_detail)).setText(mItem.details);
        }


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshResultList);
        mCollapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);



            if (mCollapsingToolbar != null) {
                mCollapsingToolbar.setTitle("Result's");
            }

//        // Show the Up button in the action bar.
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
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
