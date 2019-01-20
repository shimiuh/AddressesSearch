package android.under_dash.addresses.search.view.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.utils.UiUtils;
import android.under_dash.addresses.search.utils.Utils;
import android.under_dash.addresses.search.models.AddressName;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;


public  class TagAdapter  extends RecyclerView.Adapter<TagAdapter.ViewHolder>{


    private int mSelectedType;
    private final AddressesListAdapter.OnSelectionChange mOnSelectionChange;
    private List<AddressName> mValues = new ArrayList<>();

    public TagAdapter(int selectedType, AddressesListAdapter.OnSelectionChange onSelectionChange) {
        mSelectedType = selectedType;
        mOnSelectionChange = onSelectionChange;
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
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(position == 0){
            ((FlexboxLayoutManager.LayoutParams) holder.itemView.getLayoutParams()).setMarginStart(UiUtils.pixToDp(115));
        }
        holder.mName.setText(mValues.get(position).name);
        holder.itemView.setTag(mValues.get(position));
    }

    private void onClick(ViewHolder holder, final int position) {
        //Box<AddressName> box = App.getBox(AddressName.class).put();
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void setItemSelected(int selectedPosition) {
        if(mSelectedType == AddressesListAdapter.SELECTED_TYPE_SEARCH){
            mValues.get(selectedPosition).setSearchSelected(false);
        }else{
            mValues.get(selectedPosition).setResultSelected(false);
        }
        if(mOnSelectionChange != null){
            mOnSelectionChange.onSelectionUpdate();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mName;
        private final View mRemove;
        ViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.tag);
            mRemove = view.findViewById(R.id.remove);
            mRemove.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                setItemSelected(pos);
                remove(pos);
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }
}
