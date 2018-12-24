package android.under_dash.addresses.search.view.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.helpers.Utils;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressMap;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.under_dash.addresses.search.view.fragments.AddressResultFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;

import java.util.ArrayList;
import java.util.List;


public  class AddressesResultAdapter extends MultiChoiceAdapter<AddressesResultAdapter.ViewHolder> {


    private final Activity mParentActivity;
    private List<AddressMap> mValues = new ArrayList<>();
    private final boolean mTwoPane;

    public AddressesResultAdapter(Activity parent, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
    }
    public AddressesResultAdapter(AddressSearchActivity parent, List<AddressMap> items, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
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
        }
        notifyDataSetChanged();
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
            mIdView = (TextView) view.findViewById(R.id.id_text);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDuration = (TextView) view.findViewById(R.id.duration);
            mDistance= (TextView) view.findViewById(R.id.distance);
        }
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }
}