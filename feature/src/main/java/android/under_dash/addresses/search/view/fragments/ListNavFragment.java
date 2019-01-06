package android.under_dash.addresses.search.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.SearchManager;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressMap;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.models.SearchAddress;
import android.under_dash.addresses.search.view.adapters.AddressesListAdapter;
import android.under_dash.addresses.search.view.adapters.AddressesSearchAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

public class ListNavFragment extends Fragment_ {


    private AddressesListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private LayoutAnimationController mItemAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_nav_fragment, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int resId = R.anim.layout_animation_fall_down;
        mItemAnimation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        mRecyclerView = view.findViewById(R.id.addresses_list);
        mSwipeLayout = view.findViewById(R.id.swipeRefreshAddressList);
        // Adding Listener
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateSearchListData();
            }
        });

        setupRecyclerView(mRecyclerView);

        //RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.search_list);
        //FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(view.getContext());
        //layoutManager.setFlexWrap(FlexWrap.WRAP);
        //layoutManager.setFlexDirection(FlexDirection.ROW);
        //layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        //recyclerView.setLayoutManager(layoutManager);



    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {//, Toolbar toolbar



        mAdapter = new AddressesListAdapter(getContext(),0);//DummyContent.ITEMS
        mAdapter.setHasStableIds(true);
//        MultiChoiceToolbar multiChoiceToolbar = new MultiChoiceToolbar.Builder(getActivity(), toolbar).setDefaultIcon(R.drawable.ic_delete_24dp, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity().getApplicationContext(), " multiChoiceToolbar clicked",Toast.LENGTH_SHORT).show();
//            }
//        }).build();
 //       mAdapter.setMultiChoiceToolbar(multiChoiceToolbar);
        //adapter.setSingleClickMode(true);
        recyclerView.setAdapter(mAdapter);
        updateSearchListData();
    }

    public void getSelected(){
        for (int i = 0; i< mRecyclerView.getChildCount(); i++){
            View v  = mRecyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
        }
    }

    private void updateSearchListData() {

        mSwipeLayout.setRefreshing(true);
        mAdapter.setData(new ArrayList<AddressName>());
        List<AddressName> addressNames = App.getBox(AddressName.class).getAll();
        Log.i("shimi", "in updateSearchListData:  addressNames.size= "+addressNames.size());
        mItemAnimation.getAnimation().reset();
        mRecyclerView.setLayoutAnimation(mItemAnimation);
        mAdapter.setData(addressNames);//searchAddressName.addresses
        mRecyclerView.scheduleLayoutAnimation();
        mSwipeLayout.setRefreshing(false);
    }

}
