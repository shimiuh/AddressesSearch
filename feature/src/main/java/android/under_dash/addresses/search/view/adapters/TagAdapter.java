package android.under_dash.addresses.search.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.helpers.UiUtils;
import android.under_dash.addresses.search.helpers.Utils;
import android.under_dash.addresses.search.models.AddressName;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.obsez.android.lib.filechooser.internals.UiUtil;

import java.util.ArrayList;
import java.util.List;


public  class TagAdapter  extends RecyclerView.Adapter<TagAdapter.ViewHolder>{


    private final Context mContext;
    private int mSelectedType;
    private final AddressesListAdapter.OnSelectionChange mOnSelectionChange;
    private List<AddressName> mValues = new ArrayList<>();

    public TagAdapter(Context context,int selectedType, AddressesListAdapter.OnSelectionChange onSelectionChange) {
        mContext = context;
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
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position == 0){
            ((FlexboxLayoutManager.LayoutParams) holder.itemView.getLayoutParams()).setMarginStart(UiUtils.pixToDp(90));
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

    private void setItemSelected(int selectedPosition, boolean isSelected) {
        if(mSelectedType == AddressesListAdapter.SELECTED_TYPE_SEARCH){
            mValues.get(selectedPosition).setSearchSelected(isSelected);
        }else{
            mValues.get(selectedPosition).setResultSelected(isSelected);
        }
        if(mOnSelectionChange != null){
            mOnSelectionChange.onSelectionUpdate();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mName;
        private final View mRemove;
        private final boolean mIsSelected = false;

        ViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.tag);
            mRemove = view.findViewById(R.id.remove);
            mRemove.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                setItemSelected(pos, false);
                remove(pos);
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }
}
