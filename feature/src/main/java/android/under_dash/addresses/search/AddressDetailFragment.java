package android.under_dash.addresses.search;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.under_dash.addresses.search.dummy.DummyContent;

/**
 * A fragment representing a single Address detail screen.
 * This fragment is either contained in a {@link AddressListActivity}
 * in two-pane mode (on tablets) or a {@link AddressDetailActivity}
 * on handsets.
 */
public class AddressDetailFragment extends Fragment_ {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddressDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_address_detail, container, false);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            if (getArguments().containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mItem.content);
                }
            }

            //Bundle arguments = new Bundle();
            //arguments.putString(AddressDetailListFragment.ARG_ITEM_ID, getActivity().getIntent().getStringExtra(AddressDetailListFragment.ARG_ITEM_ID));
            AddressDetailListFragment fragment = new AddressDetailListFragment();
            fragment.setArguments(getArguments());
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.address_detail_list_container, fragment)
                    .commit();
        }

        return rootView;
    }
}
