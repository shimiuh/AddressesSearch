package android.under_dash.addresses.search.viewmodel;

import android.under_dash.addresses.search.models.Address;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddressViewModel extends ViewModel {

    private MutableLiveData<List<Address>> address;
    public LiveData<List<Address>> getAddresses() {
        if (address == null) {
            address = new MutableLiveData<List<Address>>();
            loadUsers();
        }
        return address;
    }

    private void loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
}
