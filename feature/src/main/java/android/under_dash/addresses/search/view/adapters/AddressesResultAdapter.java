package android.under_dash.addresses.search.view.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.utils.Utils;
import android.under_dash.addresses.search.models.AddressMap;
import android.under_dash.addresses.search.view.adapters.multiChoice.MultiChoiceAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public  class AddressesResultAdapter extends MultiChoiceAdapter<AddressesResultAdapter.ViewHolder> {


    private List<AddressMap> mValues = new ArrayList<>();

    public AddressesResultAdapter() {
    }
    public AddressesResultAdapter(List<AddressMap> items) {
        setData(items);
    }
    public void setData(List<AddressMap> items) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_result_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.mIdView.setText(mValues.get(position).getDestinationAddress().name);
        holder.mContentView.setText(mValues.get(position).getDestinationAddress().address);
        holder.mDistance.setText(mValues.get(position).distanceText);
        holder.mDuration.setText(mValues.get(position).durationText);
        holder.itemView.setTag(mValues.get(position));
    }

    private void onClick(View view) {

    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(ViewHolder holder, final int position) {
        return v -> {
            AddressesResultAdapter.this.onClick(holder.itemView);
        };
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;
        final TextView mDuration;
        final TextView mDistance;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.name);
            mContentView = view.findViewById(R.id.address);
            mDuration = view.findViewById(R.id.duration);
            mDistance= view.findViewById(R.id.distance);
        }
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }
}
