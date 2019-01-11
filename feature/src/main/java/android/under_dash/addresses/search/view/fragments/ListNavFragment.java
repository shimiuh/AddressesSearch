package android.under_dash.addresses.search.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.ImportCVSToSQLiteDB;
import android.under_dash.addresses.search.helpers.MyCSVFileReader;
import android.under_dash.addresses.search.helpers.SearchManager;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.models.AddressName_;
import android.under_dash.addresses.search.utils.DialogUtil;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.under_dash.addresses.search.view.adapters.AddressesListAdapter;
import android.under_dash.addresses.search.view.adapters.TagAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.File;
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
    private RecyclerView mTagSearchRecycler;
    private RecyclerView mTagResultRecycler;
    private CheckBox mSearchTagCheckBox;
    private CheckBox mResultTagCheckBox;

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
        mTagSearchRecycler= view.findViewById(R.id.tag_search_list);
        mTagResultRecycler = view.findViewById(R.id.tag_result_list);
        mSwipeLayout = view.findViewById(R.id.swipeRefreshAddressList);
        mResultTagCheckBox = view.findViewById(R.id.result_tag_check_box);
        mSearchTagCheckBox = view.findViewById(R.id.search_tag_check_box);
        view.findViewById(R.id.add_list_file).setOnClickListener(v -> {
            addListDialog();
        });

        setupRecyclerView();
        setTagLists(view);


        mSwipeLayout.setOnRefreshListener(this::updateData);

        mSearchTagCheckBox.setChecked(true);
        mSearchTagCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                if(mResultTagCheckBox.isChecked()) {
                    mResultTagCheckBox.setChecked(false);
                }
            }else{
                if(!mResultTagCheckBox.isChecked()) {
                    mResultTagCheckBox.setChecked(true);
                }
            }
            updateData();
        });

        mSearchTagCheckBox.setChecked(false);
        mResultTagCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                if(mSearchTagCheckBox.isChecked()) {
                    mSearchTagCheckBox.setChecked(false);
                }
            }else{
                if(!mSearchTagCheckBox.isChecked()) {
                    mSearchTagCheckBox.setChecked(true);
                }
            }
            updateData();
        });

    }

    private void addListDialog() {

        DialogUtil.show(getActivity(),R.string.set_address_name, R.string.set_address_name,R.string.set_address_name,
                (dialog, listName) -> {
                    if(TextUtils.isEmpty(listName)){
                        dialog.dismiss();
                        return;
                    }
                    Log.i("shimi", "in addListDialog : name = -"+listName+"-"+listName.length());
                    List<AddressName> addressName = App.getBox(AddressName.class).find(AddressName_.name,listName);
                    //AddressName address = App.getBox(AddressName.class).query().equal(AddressName_.name,listName).build().findFirst();

                    if(addressName.size() > 0 ){
                        DialogUtil.show(getContext(),R.string.this_list_exsisets, R.string.this_list_exsisets_mesage,
                                R.string.this_list_exsisets_action,R.string.this_list_exsisets_regret,(dialog2, which) -> {
                                    importAddressesFromFile(addressName.get(0));
                                },(dialog2, which) -> {
                                    dialog.dismiss();
                                    addListDialog();
                                });
                    }else{
                        AddressName name = new AddressName(listName);
                        name.update();
                        importAddressesFromFile(name);
                    }

                }, R.string.set_address_name);

    }

    private void importAddressesFromFile(AddressName addressName) {
        MyCSVFileReader.openDialogToReadCSV(getActivity(), pathFile -> {
            Log.d("shimi", "in fab fab_result_list resultAddressName = "+addressName.name+" getResultId = "+SearchManager.get().getResultId());
            new ImportCVSToSQLiteDB(getActivity(),pathFile,addressName, () -> {
                updateData();
            }).execute();
        });
    }

    private void updateData() {
        int selectedType = getSelectedTagType();
        updateSearchListData(selectedType);
        updateTagData(selectedType);
    }

    private int getSelectedTagType() {
        int selectedType;
        if(mResultTagCheckBox.isChecked()){
            selectedType = AddressesListAdapter.SELECTED_TYPE_RESULT;
        }else if(mSearchTagCheckBox.isChecked()){
            selectedType = AddressesListAdapter.SELECTED_TYPE_SEARCH;
        }else{
            mSearchTagCheckBox.setChecked(true);
            selectedType = AddressesListAdapter.SELECTED_TYPE_SEARCH;
        }
        return selectedType;
    }

    private void setTagLists(View view) {

        mSearchTagAdapter = new TagAdapter(view.getContext(),AddressesListAdapter.SELECTED_TYPE_SEARCH,this);
        mTagSearchRecycler.setAdapter(mSearchTagAdapter);


        mResultTagAdapter = new TagAdapter(view.getContext(),AddressesListAdapter.SELECTED_TYPE_RESULT,this);
        mTagResultRecycler.setAdapter(mResultTagAdapter);

        updateTagData(AddressesListAdapter.SELECTED_TYPE_SEARCH);

    }

    private void updateTagData(int selectedType) {
        if(selectedType == AddressesListAdapter.SELECTED_TYPE_SEARCH){
            updateSearchTagData();
        }else{
            updateResultTagData();
        }
    }

    private void updateSearchTagData() {
        Box<AddressName> box = App.getBox(AddressName.class);
        List<AddressName> searchList = box.query().equal(AddressName_.isSearchSelected, true).build().find();
        mItemAnimation.getAnimation().reset();
        mTagSearchRecycler.setLayoutAnimation(mItemAnimation);
        mSearchTagAdapter.setSelectedType(AddressesListAdapter.SELECTED_TYPE_SEARCH);
        mSearchTagAdapter.setData(searchList);
        mTagSearchRecycler.scheduleLayoutAnimation();
    }

    private void updateResultTagData() {
        Box<AddressName> box = App.getBox(AddressName.class);
        List<AddressName> resultList = box.query().equal(AddressName_.isResultSelected, true).build().find();
        mItemAnimation.getAnimation().reset();
        mTagResultRecycler.setLayoutAnimation(mItemAnimation);
        mResultTagAdapter.setSelectedType(AddressesListAdapter.SELECTED_TYPE_RESULT);
        mResultTagAdapter.setData(resultList);
        mTagResultRecycler.scheduleLayoutAnimation();
    }


    private void setupRecyclerView() {

        mAdapter = new AddressesListAdapter(getContext(),AddressesListAdapter.SELECTED_TYPE_SEARCH,this);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
        updateSearchListData(AddressesListAdapter.SELECTED_TYPE_SEARCH);
    }

    public void getSelected(){
        for (int i = 0; i< mRecyclerView.getChildCount(); i++){
            View v  = mRecyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
        }
    }

    private void updateSearchListData(int selectedType) {

        mSwipeLayout.setRefreshing(true);
        List<AddressName> addressNames = App.getBox(AddressName.class).getAll();
        Log.i("shimi", "in updateSearchListData:  addressNames.size= "+addressNames.size());
        mItemAnimation.getAnimation().reset();
        mRecyclerView.setLayoutAnimation(mItemAnimation);
        mAdapter.setSelectedType(selectedType);
        mAdapter.setData(addressNames);//searchAddressName.addresses
        mRecyclerView.scheduleLayoutAnimation();
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void onSelectionUpdate() {
        updateData();
    }
}
