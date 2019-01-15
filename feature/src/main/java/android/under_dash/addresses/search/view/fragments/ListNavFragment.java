package android.under_dash.addresses.search.view.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.app.Constants;
import android.under_dash.addresses.search.helpers.ImportCVSToSQLiteDB;
import android.under_dash.addresses.search.helpers.MyCSVFileReader;
import android.under_dash.addresses.search.helpers.SearchManager;
import android.under_dash.addresses.search.helpers.Utils;
import android.under_dash.addresses.search.helpers.Work;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressMap_;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.models.AddressName_;
import android.under_dash.addresses.search.models.Address_;
import android.under_dash.addresses.search.utils.DialogUtil;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.under_dash.addresses.search.view.adapters.AddressesListAdapter;
import android.under_dash.addresses.search.view.adapters.TagAdapter;
import android.under_dash.addresses.search.view.customUI.LoadingTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    private LoadingTextView mCostTextView;
    List<Address> mSearchAddressesForFetch = new ArrayList<>();
    List<Address> mResultAddressesForFetch = new ArrayList<>();

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
        mCostTextView = view.findViewById(R.id.cost);
        mRecyclerView = view.findViewById(R.id.addresses_list);
        mTagSearchRecycler= view.findViewById(R.id.tag_search_list);
        mTagResultRecycler = view.findViewById(R.id.tag_result_list);
        mSwipeLayout = view.findViewById(R.id.swipeRefreshAddressList);
        mResultTagCheckBox = view.findViewById(R.id.result_tag_check_box);
        mSearchTagCheckBox = view.findViewById(R.id.search_tag_check_box);
        view.findViewById(R.id.add_list_file).setOnClickListener(v -> {
            addListDialog();
        });
        view.findViewById(R.id.add_list_web).setOnClickListener(v -> {
            addListDialog();
        });


        view.findViewById(R.id.start_compare).setOnClickListener(v -> {
            if(mSearchAddressesForFetch.size()> 0 && mResultAddressesForFetch.size() > 0) {
                ((AddressSearchActivity) getActivity()).searchListes(mSearchAddressesForFetch, mResultAddressesForFetch);
            }else {
                int textId  = mResultAddressesForFetch.size() > 0 ? R.string.no_search_to_do :R.string.add_result_for_search;
                Toast.makeText(getContext(),textId,Toast.LENGTH_SHORT).show();
            }
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
                        AddressName newAddressName = new AddressName(listName);
                        newAddressName.update();
                        importAddressesFromFile(newAddressName);
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
        mCostTextView.setLoading(true);

        Work.job(this::getSelectedListCost).onUiDelayed(cost -> {
            mCostTextView.setLoading(false);
            mCostTextView.setText(getString(R.string.price,Utils.convertCents((int)cost)));
        },1);

    }
    private List<List<Address>> getSelectedListsNotMapped() {
        List<Address> searchAddresses = Address.getAllSearchSelected();
        List<Address> resultAddresses = Address.getAllResultSelected();
        return new ArrayList<List<Address>>();

    }


    private int getSelectedListCost() {
        mSearchAddressesForFetch.clear();
        mResultAddressesForFetch.clear();
        AtomicInteger elements = new AtomicInteger();
        List<Address> searchAddresses = Address.getAllSearchSelected();
        List<Address> resultAddresses = Address.getAllResultSelected();
        searchAddresses.forEach(address -> {
            resultAddresses.forEach(address1 -> {
                if(!Address.isLinked(address,address1)){
                    elements.getAndIncrement();
                    if(!mSearchAddressesForFetch.contains(address)) {
                        mSearchAddressesForFetch.add(address);
                    }
                    if(!mResultAddressesForFetch.contains(address1)) {
                        mResultAddressesForFetch.add(address1);
                    }

                }

            });
        });
        //elements.set((int) ((resultAddresses.size() * searchAddresses.size()) * Constants.COST_PER_ELEMENT));
        long total = (int) (Constants.COST_PER_ELEMENT * elements.get());
        return (int) (total + (total * 0.1f));
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

    @Override
    public void onShowList(AddressName AddressName) {
        Bundle arguments = new Bundle();
        arguments.putLong(AddressResultFragment.ARG_ITEM_ID, AddressName.id);
        DisplayAddressesFragment fragment = new DisplayAddressesFragment();
        fragment.setArguments(arguments);//.addToBackStack(AddressResultFragment.TAG)
        ((AppCompatActivity)getActivity()).getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment,DisplayAddressesFragment.TAG).addToBackStack(DisplayAddressesFragment.TAG).commit();

    }
}
