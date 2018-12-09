package android.under_dash.addresses.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressResultList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;

import java.util.ArrayList;
import java.util.List;


public  class AddressesSearchAdapter extends MultiChoiceAdapter<AddressesSearchAdapter.ViewHolder> {


    private final AddressListActivity mParentActivity;
    private List<? extends Address> mValues = new ArrayList<>();
    private final boolean mTwoPane;

    AddressesSearchAdapter(AddressListActivity parent, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
    }
    AddressesSearchAdapter(AddressListActivity parent, List<? extends Address> items, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
        setData(items);
    }

    public void setData(List<? extends Address> items) {
        this.mValues = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.mIdView.setText(mValues.get(position).name);
        holder.mContentView.setText(mValues.get(position).address);
        holder.itemView.setTag(mValues.get(position));
    }

    private void onClick(View view) {
        Address item = (Address) view.getTag();
        if(item == null){
            return;
        }
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(AddressDetailFragment.ARG_ITEM_ID, String.valueOf(item.id));
            AddressDetailFragment fragment = new AddressDetailFragment();
            fragment.setArguments(arguments);
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.address_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, AddressDetailActivity.class);
            intent.putExtra(AddressDetailFragment.ARG_ITEM_ID, String.valueOf(item.id));
            context.startActivity(intent);
        }
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
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = (TextView) view.findViewById(R.id.id_text);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }
}
