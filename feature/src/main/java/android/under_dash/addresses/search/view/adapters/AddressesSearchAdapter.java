package android.under_dash.addresses.search.view.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.helpers.HttpHelper;
import android.under_dash.addresses.search.helpers.Utils;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.view.fragments.AddressResultFragment;
import android.under_dash.addresses.search.view.activitys.AddressSearchActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;


public  class AddressesSearchAdapter extends MultiChoiceAdapter<AddressesSearchAdapter.ViewHolder> {


    private final Activity mParentActivity;
    private List<? extends Address> mValues = new ArrayList<>();
    private final boolean mTwoPane;

    public AddressesSearchAdapter(Activity parent, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
    }
    AddressesSearchAdapter(AddressSearchActivity parent, List<? extends Address> items, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
        setData(items);
    }

    public void setData(List<? extends Address> items) {
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
        Box<Address> addressResultBox = App.getBox(Address.class);
        List<Address> addressResultList = addressResultBox.getAll();//TODO: get only addressResultList

        Box<Address> addressSearchBox = App.getBox(Address.class);
        List<Address> addressSearchList = addressSearchBox.getAll();//TODO: get only addressSearchList

        HttpHelper.getDistanceInfoAndAddInDb(addressSearchList, addressResultList, () -> {



        });
        int addFragmentTo = mTwoPane ? R.id.address_detail_container : android.R.id.content;
        Bundle arguments = new Bundle();
        arguments.putString(AddressResultFragment.ARG_ITEM_ID, String.valueOf(item.id));
        AddressResultFragment fragment = new AddressResultFragment();
        fragment.setArguments(arguments);//.addToBackStack(AddressResultFragment.TAG)
        ((AppCompatActivity)mParentActivity).getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment,AddressResultFragment.TAG).commit();

//        Bundle arguments = new Bundle();
//        arguments.putString(AddressDetailListFragment.ARG_ITEM_ID, getIntent().getStringExtra(AddressDetailListFragment.ARG_ITEM_ID));
//        AddressDetailListFragment fragment = new AddressDetailListFragment();
//        fragment.setArguments(arguments);
//        getSupportFragmentManager().beginTransaction().add(R.id.address_detail_list_container, fragment).commit();

//        if (mTwoPane) {
//            Bundle arguments = new Bundle();
//            arguments.putString(AddressDetailFragment.ARG_ITEM_ID, String.valueOf(item.id));
//            AddressDetailFragment fragment = new AddressDetailFragment();
//            fragment.setArguments(arguments);
//            mParentActivity.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.address_detail_container, fragment)
//                    .commit();
//        } else {
//            Context context = view.getContext();
//            Intent intent = new Intent(context, AddressDetailActivity.class);
//            intent.putExtra(AddressDetailFragment.ARG_ITEM_ID, String.valueOf(item.id));
//            context.startActivity(intent);
//        }
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

    @Override
    public long getItemId(int position) {
        return mValues.get(position).id;
    }
}
