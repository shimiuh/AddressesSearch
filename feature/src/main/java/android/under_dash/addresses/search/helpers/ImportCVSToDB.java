package android.under_dash.addresses.search.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.under_dash.addresses.search.app.App;
import android.under_dash.addresses.search.models.objectBox.Address;
import android.under_dash.addresses.search.models.objectBox.AddressList;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.nio.charset.StandardCharsets;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class ImportCVSToDB extends AsyncTask<String, String, String> {

    private final Runnable mOnDone;
    AddressList mAddressName;
    Context mContext;
    File mFile = null;
    private ProgressDialog mDialog;

    public ImportCVSToDB(Context context, File file, AddressList addressName, Runnable onDone) {
        this.mContext=context;
        this.mFile=file;
        this.mAddressName = addressName;
        this.mOnDone = onDone;
    }

    @Override
    protected void onPreExecute()
    {
        mDialog =new ProgressDialog(mContext);
        mDialog.setTitle("Importing Data into Secure DataBase");
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.setIcon(android.R.drawable.ic_dialog_info);
        mDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mDialog.setMessage("Please wait... "+values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {

        String data="";
        Log.d(getClass().getName(), mFile.toString());

        try{
            CsvReader csvReader = new CsvReader();
            CsvContainer csv = csvReader.read(mFile, StandardCharsets.UTF_8);
            for (CsvRow row : csv.getRows()) {

                String acc_name = row.getField(0);
                String acc_website = row.getField(1);
                String acc_address = row.getField(2);

                //String latLong = Utils.getLatLongFromLocation(acc_address);
                Address.add(acc_name,acc_address,acc_website,mAddressName);
                publishProgress(mAddressName.addresses.size()+" Done out of "+csv.getRowCount());
            }
            Log.d("shimi", "DB added  size = "+mAddressName.addresses.size()+" "+App.getBox(Address.class).getAll().size());
            return data="added";

        } catch (Exception e) {
            Log.e("shimi", "Error for importing file");
        }
        return data="";

    }

    protected void onPostExecute(String data)
    {

        if (mDialog.isShowing()){
            mDialog.dismiss();
        }
        if(mOnDone != null){
            mOnDone.run();
        }
        if (data.length()!=0){
            Toast.makeText(mContext, "File is built Successfully! size = "+App.getBox(Address.class).getAll().size(), Toast.LENGTH_LONG).show();
            //Log.d("shimi", "File is built Successfully!"+"\n"+data+"\n institutionDB = "+AppDatabase.get(mContext).institutionDao().getAll().toString());
        }else{
            Toast.makeText(mContext, "File fail to build", Toast.LENGTH_SHORT).show();
        }
    }


}