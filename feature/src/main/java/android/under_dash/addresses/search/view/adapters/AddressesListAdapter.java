package android.under_dash.addresses.search.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.Utils;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.view.adapters.multiChoice.MultiChoiceAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.objectbox.Box;


public  class AddressesListAdapter extends MultiChoiceAdapter<AddressesListAdapter.ViewHolder> implements MultiChoiceAdapter.Listener {


    public static final int SELECTED_TYPE_SEARCH = 0;
    public static final int SELECTED_TYPE_RESULT = 1;
    private final Context mParentActivity;
    private final OnSelectionChange mOnSelectionChange;
    private int mSelectedType;
    private List<AddressName> mValues = new ArrayList<>();
    int mCreationPosition;

    public interface OnSelectionChange{
        void onSelectionUpdate();
    }

    public AddressesListAdapter(Context context, int selectedType, OnSelectionChange onSelectionChange) {
        mParentActivity = context;
        mSelectedType = selectedType;
        mOnSelectionChange = onSelectionChange;
        setSingleClickMode(true);
        setMultiChoiceSelectionListener(this);
    }

    public void setSelectedType(int selectedType) {
        mSelectedType = selectedType;
        notifyDataSetChanged();
    }

    public void setData(List<AddressName> items) {
        int currentSize = mValues.size()+1;
        //remove the current items
        mValues.clear();
        mCreationPosition = 0;
        //tell the recycler view that all the old items are gone
        notifyItemRangeRemoved(0, currentSize+1);

        //add all the new items
        //Collections.reverse(cardItemList); //we want to display newest first
        mValues = items;
        //tell the recycler view how many new items we added +1 for the footer
        notifyItemRangeInserted(0, mValues.size()+1);
    }

    public void remove(int pos) {
        if (!Utils.isBadPosSize(mValues,pos)) {
            mValues.remove(pos);
            notifyItemRemoved(pos);
            notifyItemChanged(mValues.size()+1);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.mName.setText(mValues.get(position).name);
        holder.itemView.setTag(mValues.get(position));
        boolean isSelected =  mSelectedType == SELECTED_TYPE_SEARCH ? mValues.get(position).isSearchSelected : mValues.get(position).isResultSelected;
        holder.mName.setChecked(isSelected);
        holder.itemView.setAlpha(1);
        setSelect(position, isSelected);
    }



//    private void onClick(ViewHolder holder, final int position) {
//        boolean isSelected =  getSelectedItemList().contains(position);
//        Log.d("shimi", "onClick() called with: mSelectedType = [" + mSelectedType + "], isSelected = [" + isSelected + "]");
//        if(mSelectedType == SELECTED_TYPE_SEARCH){
//            mValues.get(position).setSearchSelected(isSelected);
//        }else{
//            mValues.get(position).setResultSelected(isSelected);
//        }
//        //Box<AddressName> box = App.getBox(AddressName.class).put();
//    }
//
//    @Override
//    protected View.OnClickListener defaultItemViewClickListener(ViewHolder holder, final int position) {
//        return v -> {
//            Log.d("shimi", "defaultItemViewClickListener() called with: position = [" + position + "]");
//            AddressesListAdapter.this.onClick(holder, position);
//        };
//    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void OnItemSelected(int selectedPosition, int itemSelectedCount, int allItemCount) {
        setItemSelected(selectedPosition,true);
    }

    @Override
    public void OnItemDeselected(int deselectedPosition, int itemSelectedCount, int allItemCount) {
        setItemSelected(deselectedPosition,false);
    }

    @Override
    public void OnSelectAll(int itemSelectedCount, int allItemCount) {

    }

    @Override
    public void OnDeselectAll(int itemSelectedCount, int allItemCount) {

    }

    private void setItemSelected(int selectedPosition, boolean isSelected) {

//        boolean isSelectedDB = mSelectedType == SELECTED_TYPE_SEARCH ? mValues.get(selectedPosition).isSearchSelected : mValues.get(selectedPosition).isResultSelected;
//
//        if(isSelectedDB && !isSelected){
//            setSelect(selectedPosition, isSelectedDB);
//            return;
//        }
        if(mSelectedType == SELECTED_TYPE_SEARCH){
            mValues.get(selectedPosition).setSearchSelected(isSelected);
        }else{
            mValues.get(selectedPosition).setResultSelected(isSelected);
        }
        if(mOnSelectionChange != null){
            mOnSelectionChange.onSelectionUpdate();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox mName;
        private final ImageButton mRemove;
        private final ImageButton mShow;
        private final boolean mIsSelected = false;

        ViewHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.listName);
            mRemove = view.findViewById(R.id.listRemove);
            mRemove.setOnClickListener(v -> {

            });
            mShow = view.findViewById(R.id.listShow);
            mShow.setOnClickListener(v -> {

            });
            view.setOnClickListener(v -> {
                Log.d("shimi", "ViewHolder() called with: position = [" + getAdapterPosition() + "]");
                //AddressesListAdapter.this.onClick(holder, position);
            });
            int position = mCreationPosition;
            Log.d("shimi", "ViewHolder() called with: position = [" + getAdapterPosition() + "] mCreationPosition = "+mCreationPosition+" ");
            if (!Utils.isBadPosSize(mValues,position)) {
                boolean isSelectedDB = mSelectedType == SELECTED_TYPE_SEARCH ? mValues.get(position).isSearchSelected : mValues.get(position).isResultSelected;
                setSelect(position, isSelectedDB);
            }
            mCreationPosition++;
        }
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }
}
