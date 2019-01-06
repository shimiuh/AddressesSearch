package android.under_dash.addresses.search.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.Utils;
import android.under_dash.addresses.search.models.AddressName;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.objectbox.Box;


public  class AddressesListAdapter extends MultiChoiceAdapter<AddressesListAdapter.ViewHolder>{


    private static final int SELECTED_TYPE_SEARCH = 0;
    private static final int SELECTED_TYPE_RESULT = 1;
    private final Context mParentActivity;
    private int mSelectedType;
    private List<AddressName> mValues = new ArrayList<>();

    public AddressesListAdapter(Context context, int selectedType) {
        mParentActivity = context;
        mSelectedType = selectedType;
        setSingleClickMode(true);
    }

    public void setSelectedType(int selectedType) {
        mSelectedType = selectedType;
        notifyDataSetChanged();
    }

    public void setData(List<AddressName> items) {
        int currentSize = mValues.size()+1;
        //remove the current items
        mValues.clear();
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
        holder.mCheckBox.setChecked(isSelected);
        holder.itemView.setAlpha(1);
    }

    private void onClick(ViewHolder holder, final int position) {
        boolean isSelected =  getSelectedItemList().contains(position);
        if(mSelectedType == SELECTED_TYPE_SEARCH){
            mValues.get(position).setSearchSelected(isSelected);
        }else{
            mValues.get(position).setResultSelected(isSelected);
        }
        //Box<AddressName> box = App.getBox(AddressName.class).put();
    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(ViewHolder holder, final int position) {
        return v -> {
            AddressesListAdapter.this.onClick(holder, position);
        };
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mName;
        private final CheckBox mCheckBox;
        private final boolean mIsSelected = false;

        ViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.listName);
            mCheckBox = view.findViewById(R.id.listCheck);
            mCheckBox.setClickable(false);
        }
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }
}
