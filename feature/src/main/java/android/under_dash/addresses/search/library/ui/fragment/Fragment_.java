package android.under_dash.addresses.search.library.ui.fragment;

//import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_ extends Fragment {

    private ViewModel_ mViewModel;

    public static Fragment_ newInstance() {
        return new Fragment_();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return this.onCreateView(inflater,container,savedInstanceState);//inflater.inflate(R.layout.fragment_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         //mViewModel = ViewModelProviders.of(this).get(ViewModel_.class);
        // TODO: Use the ViewModel
    }

}
