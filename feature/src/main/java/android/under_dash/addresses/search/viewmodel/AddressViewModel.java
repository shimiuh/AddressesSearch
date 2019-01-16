package android.under_dash.addresses.search.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.under_dash.addresses.search.models.Address;

import java.util.List;

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
