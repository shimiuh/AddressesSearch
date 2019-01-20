package android.under_dash.addresses.search.old;

import android.app.Activity;
import android.os.Bundle;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.under_dash.addresses.search.dummy.DummyContent;

/**
 * A fragment representing a single Address detail screen.
 * This fragment is either contained in a {@link AddressSearchActivity}
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.address_result_fragment, container, false);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            if (getArguments().containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

                Activity activity = this.getActivity();
            }

            //Bundle arguments = new Bundle();
            //arguments.putString(AddressDetailListFragment.ARG_ITEM_ID, getActivity().getIntent().getStringExtra(AddressDetailListFragment.ARG_ITEM_ID));
//            AddressResultFragment fragment = new AddressResultFragment();
//            fragment.setArguments(getArguments());
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .add(R.id.address_detail_list_container, fragment)
//                    .commit();
        }

        return rootView;
    }
}
