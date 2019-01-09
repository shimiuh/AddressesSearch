package android.under_dash.addresses.search.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.models.AddressName_;
import android.under_dash.addresses.search.view.adapters.AddressesListAdapter;
import android.under_dash.addresses.search.view.adapters.TagAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class ListNavFragment extends Fragment_ implements AddressesListAdapter.OnSelectionChange {


    private AddressesListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private LayoutAnimationController mItemAnimation;
    private TagAdapter mSearchTagAdapter;
    private TagAdapter mResultTagAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_nav_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int resId = R.anim.layout_animation_fall_down;
        mItemAnimation = AnimationUtils.loadLayoutAnimation(getContext(), resId);

        mRecyclerView = view.findViewById(R.id.addresses_list);
        mSwipeLayout = view.findViewById(R.id.swipeRefreshAddressList);
        mSwipeLayout.setOnRefreshListener(() -> {
            updateSearchListData();
            updateTagData();
        });

        setupRecyclerView(mRecyclerView);
        setTagLists(view);

    }

    private void setTagLists(View view) {

        RecyclerView tagSearchRecycler= view.findViewById(R.id.tag_search_list);
        FlexboxLayoutManager layoutManager = getFlexboxLayoutManager(view.getContext());
        tagSearchRecycler.setLayoutManager(layoutManager);
        mSearchTagAdapter = new TagAdapter(view.getContext(),AddressesListAdapter.SELECTED_TYPE_SEARCH,this);
        tagSearchRecycler.setAdapter(mSearchTagAdapter);

        RecyclerView tagResultRecycler= view.findViewById(R.id.tag_result_list);
        layoutManager = getFlexboxLayoutManager(view.getContext());
        tagResultRecycler.setLayoutManager(layoutManager);
        mResultTagAdapter = new TagAdapter(view.getContext(),AddressesListAdapter.SELECTED_TYPE_RESULT,this);
        tagResultRecycler.setAdapter(mResultTagAdapter);

        updateTagData();

    }

    private void updateTagData() {
        Box<AddressName> box = App.getBox(AddressName.class);
        List<AddressName> searchList = box.query().equal(AddressName_.isSearchSelected, true).build().find();
        List<AddressName> resultList = box.query().equal(AddressName_.isResultSelected, true).build().find();
        resultList.add(new AddressName("Test"));
        resultList.add(new AddressName("Check"));
        resultList.add(new AddressName("To"));
        resultList.add(new AddressName("Why This Day"));

        searchList.add(new AddressName("Test"));
        searchList.add(new AddressName("Check"));
        searchList.add(new AddressName("To"));
        searchList.add(new AddressName("Why This Day"));
        searchList.add(new AddressName("Test"));
        searchList.add(new AddressName("Check"));
        searchList.add(new AddressName("To"));
        searchList.add(new AddressName("Why This Day"));

        mSearchTagAdapter.setData(searchList);
        mResultTagAdapter.setData(resultList);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        mAdapter = new AddressesListAdapter(getContext(),AddressesListAdapter.SELECTED_TYPE_SEARCH,this);
        mAdapter.setHasStableIds(true);
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

    @Override
    public void onSelectionUpdate() {
        updateTagData();
    }

    private FlexboxLayoutManager getFlexboxLayoutManager(Context context) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        return layoutManager;
    }
}
