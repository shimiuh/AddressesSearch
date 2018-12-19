package android.under_dash.addresses.search.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.models.Address;
import android.under_dash.addresses.search.models.AddressName;
import android.under_dash.addresses.search.models.Institution;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.nio.charset.StandardCharsets;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class ImportCVSToSQLiteDB extends AsyncTask<String, String, String> {

    AddressName mAddressName;
    Context mContext;
    File mFile = null;
    private ProgressDialog dialog;

    public ImportCVSToSQLiteDB(Context context, File file, AddressName addressName) {
        this.mContext=context;
        this.mFile=file;
        this.mAddressName = addressName;
    }

    @Override
    protected void onPreExecute()
    {
        dialog=new ProgressDialog(mContext);
        dialog.setTitle("Importing Data into SecureIt DataBase");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String data="";
        Log.d(getClass().getName(), mFile.toString());

        App.getBoxStore().runInTxAsync(() -> {
            App.get().getBox(Address.class).removeAll();//TODO: remove only addresses by mAddressName
        }, (result, error) -> {});

        try{
            CsvReader csvReader = new CsvReader();
            CsvContainer csv = csvReader.read(mFile, StandardCharsets.UTF_8);
            for (CsvRow row : csv.getRows()) {



                String acc_name = row.getField(0);
                String acc_website = row.getField(1);
                String acc_address = row.getField(2);

                Institution institution = new Institution(0,acc_name,acc_website,acc_address);
                //AppDatabase.get(mContext).addInstitution(institution);
                App.getBoxStore().runInTxAsync(() -> {
                    String latLong = Utils.getLatLongFromLocation(acc_address);
                    //Log.e("shimi", "latLong = "+latLong);
                    App.get().getBox(Address.class).put(new Address(acc_name,acc_address,acc_website,latLong,mAddressName));

                }, (result, error) -> {
                    // transaction is done! do something?
                });
                //data=data+"Account_name:"+acc_name  +"  Account_web:"+acc_website+"\n" +"  Account_address:"+acc_address+"\n";
                //Log.d("shimi", "doInBackground: "+"Read line: " + row);

            }
            Log.d("shimi", "DB added  size = "+App.get().getBox(Address.class).getAll().size());
            return data="added";

        } catch (Exception e) {
            Log.e("shimi", "Error for importing file");
        }
        return data="";

    }

    protected void onPostExecute(String data)
    {

        if (dialog.isShowing()){
            dialog.dismiss();
        }

        if (data.length()!=0){
            Toast.makeText(mContext, "File is built Successfully! size = "+App.get().getBox(Address.class).getAll().size(), Toast.LENGTH_LONG).show();
            //Log.d("shimi", "File is built Successfully!"+"\n"+data+"\n institutionDB = "+AppDatabase.get(mContext).institutionDao().getAll().toString());
        }else{
            Toast.makeText(mContext, "File fail to build", Toast.LENGTH_SHORT).show();
        }
    }


}