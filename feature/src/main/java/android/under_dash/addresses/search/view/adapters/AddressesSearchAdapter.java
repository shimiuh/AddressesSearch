package android.under_dash.addresses.search.view.adapters;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.utils.Utils;
import android.under_dash.addresses.search.models.objectBox.Address;
import android.under_dash.addresses.search.models.SearchAddress;
import android.under_dash.addresses.search.view.adapters.multiChoice.MultiChoiceAdapter;
import android.under_dash.addresses.search.view.fragments.AddressResultFragment;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public  class AddressesSearchAdapter extends MultiChoiceAdapter<AddressesSearchAdapter.ViewHolder> {


    private final Activity mParentActivity;
    private List<SearchAddress> mValues = new ArrayList<>();
    private final boolean mTwoPane;

    public AddressesSearchAdapter(Activity parent, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
    }
    AddressesSearchAdapter(AddressSearchActivity parent, List<SearchAddress> items, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
        setData(items);
    }

    public void setData(List<SearchAddress> items) {
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
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(!TextUtils.isEmpty(mValues.get(position).address.name)) {
            holder.mName.setText(mValues.get(position).address.name);
        }
        holder.mAddress.setText(mValues.get(position).address.address);
        holder.mClosestDistance.setText(mValues.get(position).shortestDurationText);
        holder.itemView.setTag(mValues.get(position).address);

    }

    private void onClick(View view) {
        Address item = (Address) view.getTag();
        if(item == null){
            return;
        }
        int addFragmentTo = mTwoPane ? R.id.address_detail_container : android.R.id.content;
        Bundle arguments = new Bundle();
        arguments.putLong(AddressResultFragment.ARG_ITEM_ID, item.id);
        AddressResultFragment fragment = new AddressResultFragment();
        fragment.setArguments(arguments);//.addToBackStack(AddressResultFragment.TAG)
        ((AppCompatActivity)mParentActivity).getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment,AddressResultFragment.TAG).commit();
    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(ViewHolder holder, final int position) {
        return v -> {
            AddressesSearchAdapter.this.onClick(holder.itemView);
        };
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mName;
        final TextView mAddress;
        final TextView mClosestDistance;

        ViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mAddress = view.findViewById(R.id.address);
            mClosestDistance = view.findViewById(R.id.distance);
        }
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).address.id;
    }
}
