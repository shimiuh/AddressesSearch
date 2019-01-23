package android.under_dash.addresses.search.view.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.utils.Utils;
import android.under_dash.addresses.search.models.objectBox.Address;
import android.under_dash.addresses.search.view.adapters.multiChoice.MultiChoiceAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public  class DisplayAddressesAdapter extends MultiChoiceAdapter<DisplayAddressesAdapter.ViewHolder> {


    private List<Address> mValues = new ArrayList<>();

    public DisplayAddressesAdapter() {
    }
    DisplayAddressesAdapter(List<Address> items) {
        setData(items);
    }
    public void setData(List<Address> items) {
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
        if(!TextUtils.isEmpty(mValues.get(position).name)) {
            holder.mName.setText(mValues.get(position).name);
        }
        holder.mAddress.setText(mValues.get(position).address);
        holder.itemView.setTag(mValues.get(position).address);

    }

    private void onClick(View view) {
        Address item = (Address) view.getTag();
        if(item == null){
            return;
        }
    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(ViewHolder holder, final int position) {
        return v -> {
            DisplayAddressesAdapter.this.onClick(holder.itemView);
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
        return mValues.get(position).id;
    }
}
