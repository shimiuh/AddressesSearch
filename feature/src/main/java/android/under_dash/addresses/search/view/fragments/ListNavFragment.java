package android.under_dash.addresses.search.view.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.under_dash.addresses.search.R;
import android.under_dash.addresses.search.library.ui.fragment.Fragment_;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListNavFragment extends Fragment_ {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_nav_fragment, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
